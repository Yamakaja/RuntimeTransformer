package me.yamakaja.runtimetransformer.agent;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Yamakaja on 18.05.17.
 */
public class EntityClassVisitor extends ClassVisitor {

    private String className;

    private Pattern pattern = Pattern.compile("net/minecraft/server/(v[\\d_R]*)/EntityLiving");

    public EntityClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (name.endsWith("setHealth")) {
            Matcher matcher = this.pattern.matcher(className);
            matcher.find();
            return new EntityClassMethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions), matcher.group(1));
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
