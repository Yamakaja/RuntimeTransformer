package me.yamakaja.runtimetransformer;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;

import me.yamakaja.runtimetransformer.agent.Agent;

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

        OutputStream out = null;
        InputStream in = null;
        try {
            agentFile = File.createTempFile("agent", ".jar");
            agentFile.deleteOnExit();

            out = new FileOutputStream(agentFile);
            in = RuntimeTransformer.class.getResourceAsStream("/agent.jar");

            byte[] buffer = new byte[1024];
            int count;

            while ((count = in.read(buffer)) > 0)
                out.write(buffer, 0, count);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {}
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {}
            }
        }

        return agentFile;
    }
}
