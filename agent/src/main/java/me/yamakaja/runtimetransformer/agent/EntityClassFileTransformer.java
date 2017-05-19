package me.yamakaja.runtimetransformer.agent;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.regex.Pattern;

/**
 * Created by Yamakaja on 18.05.17.
 */
public class EntityClassFileTransformer implements ClassFileTransformer {

    private Pattern pattern = Pattern.compile("net/minecraft/server/(v[\\d_R]*)/EntityLiving");

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        boolean visitingEntityLiving = pattern.matcher(className).matches();
        if (!visitingEntityLiving)
            return classfileBuffer;

        System.out.println("Transforming EntityLiving!");

        ClassWriter writer;
        try {
            ClassReader reader = new ClassReader(classfileBuffer);

            writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            ClassVisitor visitor = new EntityClassVisitor(Opcodes.ASM5, writer);

            reader.accept(visitor, 0);

        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return writer.toByteArray();
    }


}
