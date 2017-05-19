package me.yamakaja.runtimetransformer.agent;

import me.yamakaja.runtimetransformer.annotation.InjectionType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;
import java.util.Optional;

/**
 * Created by Yamakaja on 19.05.17.
 */
public class MethodJob {

    private InjectionType type;
    private MethodNode targetNode;
    private MethodNode transformerNode;
    private MethodNode resultNode;

    public MethodJob(InjectionType type, MethodNode transformerNode) {
        this.type = type;
        this.transformerNode = transformerNode;
    }

    public MethodJob(InjectionType type, MethodNode targetNode, MethodNode transformerNode) {
        this(type, transformerNode);
        this.targetNode = targetNode;
    }

    public boolean hasTarget() {
        return targetNode != null;
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
    }

    private void append() {
        targetNode.instructions.add(transformerNode.instructions);
        resultNode = targetNode;
    }

    private void insert() {
        InsnList trInsns = transformerNode.instructions;

        if (trInsns.get(trInsns.size() - 2).getOpcode() == Opcodes.RETURN)
            trInsns.remove(trInsns.get(trInsns.size() - 2));

        if (trInsns.get(trInsns.size() - 3).getOpcode() == Opcodes.ACONST_NULL && trInsns.get(trInsns.size() - 1).getOpcode() == Opcodes.ATHROW) { // Remove "throw null" method exit
            trInsns.remove(trInsns.get(trInsns.size() - 2));
            trInsns.remove(trInsns.get(trInsns.size() - 2));
        }

        targetNode.instructions.insert(trInsns);

        resultNode = targetNode;
    }

    private void override() {
        resultNode = transformerNode;
    }

    public MethodNode getResultNode() {
        return resultNode;
    }

    public void apply(ClassNode node) {
        for (int i = 0; i < node.methods.size(); i++) {
            if (!(((MethodNode)node.methods.get(i)).name.equals(targetNode.name) &&
                    ((MethodNode)node.methods.get(i)).desc.equals(targetNode.desc)))
                continue;

            node.methods.set(i, getResultNode());
        }

    }
}
