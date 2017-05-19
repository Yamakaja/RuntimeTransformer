package me.yamakaja.runtimetransformer.agent;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Yamakaja on 19.05.17.
 */
public class Agent {

    private static Pattern pattern = Pattern.compile("net\\.minecraft\\.server\\.(v[\\d_R]*)\\.EntityLiving");

    public static void agentmain(String agentArgument, Instrumentation instrumentation) {

        instrumentation.addTransformer(new EntityClassFileTransformer(), true);

        String version = null;
        Class<?> resultClass = null;

        for (Class<?> clazz : instrumentation.getAllLoadedClasses()) {
            if (pattern.matcher(clazz.getName()).matches()) {
                Matcher matcher = pattern.matcher(clazz.getName());
                matcher.find();
                version = matcher.group(1);
                resultClass = clazz;
                break;
            }
        }

        if (version == null) {
            throw new RuntimeException("No version of EntityLiving could be found!");
        }

        System.out.println("Determined version: " + version);

        try {
            instrumentation.retransformClasses(resultClass);
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        }

    }

}
