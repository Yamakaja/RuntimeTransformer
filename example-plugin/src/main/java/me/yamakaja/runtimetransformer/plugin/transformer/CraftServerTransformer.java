package me.yamakaja.runtimetransformer.plugin.transformer;

import me.yamakaja.runtimetransformer.annotation.Inject;
import me.yamakaja.runtimetransformer.annotation.InjectionType;
import me.yamakaja.runtimetransformer.annotation.Transform;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;

/**
 * Created by Yamakaja on 20.05.17.
 */
@Transform(CraftServer.class)
public class CraftServerTransformer {

    @Inject(InjectionType.OVERRIDE)
    public String getVersion() {
        return "TEST VERSION";
    }

}
