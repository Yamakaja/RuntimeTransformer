package me.yamakaja.runtimetransformer.agent;

import me.yamakaja.runtimetransformer.annotation.Inject;
import me.yamakaja.runtimetransformer.annotation.InjectionType;
import me.yamakaja.runtimetransformer.annotation.Transform;
import me.yamakaja.runtimetransformer.util.MethodUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by Yamakaja on 19.05.17.
 */
public class AgentJob {

    private final List<MethodJob> methodJobs;
    private Class<?> transformer;
    private Class<?> toTransform;
    private Class<?>[] interfaces;

    public AgentJob(Class<?> transformer) {
        this.transformer = transformer;
        interfaces = transformer.getInterfaces();
//
//        Class<?> superClass = transformer.getSuperclass();
//        while (superClass != Object.class)
//            superClass = superClass.getSuperclass();

        if (!transformer.isAnnotationPresent(Transform.class))
            throw new RuntimeException("Transform annotation not present on transformer!");

        Transform transform = transformer.getAnnotation(Transform.class);
        toTransform = transform.value();

        ClassNode transformerNode = new ClassNode(Opcodes.ASM5);
        ClassReader transformerReader;

        try {
            transformerReader = new ClassReader(transformer.getResource(transformer.getSimpleName() + ".class").openStream());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load class file of " + toTransform.getSimpleName(), e);
        }

        transformerReader.accept(transformerNode, 0);

        Method[] methods = transformer.getDeclaredMethods();

        methodJobs = new ArrayList<>(methods.length);

        Arrays.stream(methods).filter(method -> method.isAnnotationPresent(Inject.class))
                .forEach(method -> {

                    InjectionType type = method.getAnnotation(Inject.class).value();

                    Optional<MethodNode> transformerMethodNode = ((List<MethodNode>) transformerNode.methods).stream()
                            .filter(node -> node != null && method.getName().equals(node.name) && MethodUtils.getSignature(method).equals(node.desc)).findAny();

                    if (!transformerMethodNode.isPresent())
                        throw new RuntimeException("Transformer method node not found! (WTF?)");

                    methodJobs.add(new MethodJob(type, toTransform.getName().replace('.', '/'),
                            transformer.getName().replace('.', '/'),
                            toTransform.getSuperclass().getName().replace('.', '/'),
                            transformerMethodNode.get()));

                });
    }

    public List<MethodJob> getMethodJobs() {
        return methodJobs;
    }

    public Class<?> getToTransform() {
        return toTransform;
    }

    public Class<?>[] getInterfaces() {
        return interfaces;
    }

    public Class<?> getTransformer() {
        return transformer;
    }

    public void apply(ClassNode node) {
        for (MethodJob methodJob : methodJobs) {
            methodJob.apply(node);
        }
    }
}
