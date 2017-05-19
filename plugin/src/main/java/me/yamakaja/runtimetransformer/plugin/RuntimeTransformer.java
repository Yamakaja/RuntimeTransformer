package me.yamakaja.runtimetransformer.plugin;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * Created by Yamakaja on 19.05.17.
 */
public class RuntimeTransformer extends JavaPlugin {

    @Override
    public void onEnable() {
        attachAgentToJVM();
    }

    private static void attachAgentToJVM() {
        try {
            String pid = ManagementFactory.getRuntimeMXBean().getName();
            VirtualMachine vm = VirtualMachine.attach(pid.substring(0, pid.indexOf('@')));
            vm.loadAgent(new File("agent.jar").getAbsolutePath());
            vm.detach();
        } catch (AttachNotSupportedException | IOException | AgentLoadException | AgentInitializationException e) {
            e.printStackTrace();
        }
    }

}
