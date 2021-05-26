package me.yamakaja.runtimetransformer;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Yamakaja on 19.05.17.
 */
public class RuntimeTransformer {

    private static final String ATTACH_MOD_PATH = "jmods/jdk.attach.jmod";

    public RuntimeTransformer(Class<?>... transformers) {
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

        File javaHome = new File(System.getProperty("java.home"));
        if (systemClassLoader instanceof URLClassLoader) {
            URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();

            try {
                Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                method.setAccessible(true);

                File toolsJar = new File(javaHome, "lib/tools.jar");
                if (!toolsJar.exists())
                    throw new RuntimeException("Not running with JDK!");

                method.invoke(urlClassLoader, toolsJar.toURI().toURL());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } else {
            Path attachMod = javaHome.toPath().resolve(ATTACH_MOD_PATH);
            if (Files.notExists(attachMod)) {
                throw new RuntimeException("Not running with JDK!");
            }
        }

        TransformerUtils.attachAgent(TransformerUtils.saveAgentJar(), transformers);
    }

}
