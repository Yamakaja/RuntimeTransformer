package me.yamakaja.runtimetransformer.agent;

import me.yamakaja.runtimetransformer.annotation.InjectionType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

/**
 * Created by Yamakaja on 19.05.17.
 */
public class MethodJob {

    private InjectionType type;
    private MethodNode transformerNode;
    private MethodNode resultNode;

    private String owner;
    private String transformer;
    private String superClass;

    public MethodJob(InjectionType type, String owner, String transformer, String superClass, MethodNode transformerNode) {
        this.type = type;
        this.transformerNode = transformerNode;
        this.owner = owner;
        this.transformer = transformer;
        this.superClass = superClass;

        transformerNode.name = transformerNode.name.endsWith("_INJECTED")
                ? transformerNode.name.substring(0, transformerNode.name.length() - 9)
                : transformerNode.name;
    }

    public void process() {
        switch (type) {
            case OVERRIDE:
                override();
                break;
            case INSERT:
                insert();
                break;
            case APPEND:
                append();
                break;
        }

        transformInvocations();
    }

    private void transformInvocations() {
        for (Iterator<AbstractInsnNode> it = (Iterator<AbstractInsnNode>) resultNode.instructions.iterator(); it.hasNext(); ) {
            AbstractInsnNode insn = it.next();

            if (insn instanceof MethodInsnNode) {
                MethodInsnNode methodInsn = (MethodInsnNode) insn;

                if (methodInsn.getOpcode() == Opcodes.INVOKESPECIAL && methodInsn.owner.equals(this.owner))
                    methodInsn.owner = this.superClass;
                else if (methodInsn.owner.equals(transformer)) {
                    methodInsn.owner = this.owner;
                }

                if (methodInsn.name.endsWith("_INJECTED")) {
                    methodInsn.name = methodInsn.name.substring(0, methodInsn.name.length() - 9);
                }

            }

            if (insn instanceof FieldInsnNode) {
                FieldInsnNode fieldInsn = (FieldInsnNode) insn;

                if (fieldInsn.owner.equals(transformer))
                    fieldInsn.owner = this.owner;
            }

        }
    }

    private void append() {
        if (!this.resultNode.desc.endsWith("V"))
            throw new RuntimeException("Can't append to non-void method!");

        InsnList list = resultNode.instructions;

        AbstractInsnNode node = list.getLast();

        if (node instanceof LabelNode) {
            node = node.getPrevious();
        }

        if (!(node.getOpcode() == Opcodes.RETURN))
            throw new RuntimeException("Method " + this.resultNode.name + " in " + this.owner + " doesn't end with return opcode?!");

        list.remove(node);

        list.add(transformerNode.instructions);

        resultNode.instructions.add(transformerNode.instructions);
    }

    private void insert() {
        InsnList trInsns = transformerNode.instructions;

        AbstractInsnNode node = trInsns.getLast();

        while (true) {
            if (node == null)
                break;

            if (node instanceof LabelNode) {
                node = node.getPrevious();
                continue;
            } else if (node.getOpcode() == Opcodes.RETURN) {
                trInsns.remove(node);
            } else if (node.getOpcode() == Opcodes.ATHROW && node.getPrevious().getOpcode() == Opcodes.ACONST_NULL) {
                AbstractInsnNode prev = node.getPrevious();
                trInsns.remove(node);
                trInsns.remove(prev);
            }

            break;
        }

        resultNode.instructions.insert(trInsns);
    }

    private void override() {
        resultNode = transformerNode;
    }

    public MethodNode getResultNode() {
        return resultNode;
    }

    public void apply(ClassNode node) {
        for (int i = 0; i < node.methods.size(); i++) {
            if (!(transformerNode.name.equals(((MethodNode) node.methods.get(i)).name)
                    && transformerNode.desc.equals(((MethodNode) node.methods.get(i)).desc)))
                continue;

            resultNode = ((MethodNode) node.methods.get(i));

            process();

            node.methods.set(i, getResultNode());
            return;
        }

        throw new RuntimeException("Target method node not found! Transformer: " + transformer);
    }
}
