package me.yamakaja.runtimetransformer;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import me.yamakaja.runtimetransformer.agent.Agent;

import java.io.*;
import java.lang.management.ManagementFactory;

/**
 * Created by Yamakaja on 19.05.17.
 */
public class RuntimeTransformer {

    private Class<?>[] transformers;

    public RuntimeTransformer(Class<?>... transformers) {
        this.transformers = transformers;

        File agentFile = saveAgentJar();
        attachAgent(agentFile);
    }

    private void attachAgent(File agentFile) {
            try {
                String pid = ManagementFactory.getRuntimeMXBean().getName();
                VirtualMachine vm = VirtualMachine.attach(pid.substring(0, pid.indexOf('@')));
                vm.loadAgent(agentFile.getAbsolutePath());
                vm.detach();

                Agent.getInstance().process(transformers);

            } catch (AttachNotSupportedException | IOException | AgentLoadException | AgentInitializationException e) {
                e.printStackTrace();
            }
    }

    private File saveAgentJar() {
        File agentFile = null;
        try {
            agentFile = File.createTempFile("agent", ".jar");
            agentFile.deleteOnExit();

            OutputStream writer = new FileOutputStream(agentFile);
            InputStream reader = RuntimeTransformer.class.getResourceAsStream("/agent.jar");

            byte[] buffer = new byte[1024];
            int count;

            while ((count = reader.read(buffer)) > 0)
                writer.write(buffer, 0, count);

            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return agentFile;
    }
}
