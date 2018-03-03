package me.yamakaja.runtimetransformer.plugin.transformer;

import me.yamakaja.runtimetransformer.annotation.Inject;
import me.yamakaja.runtimetransformer.annotation.InjectionType;
import me.yamakaja.runtimetransformer.annotation.TransformByName;
import net.minecraft.server.v1_11_R1.NBTTagCompound;

/**
 * Created by Yamakaja on 3/3/18.
 */
@TransformByName("org.bukkit.craftbukkit.v1_11_R1.inventory.CraftMetaSkull")
public class SkullMetaTransformer {

    @Inject(InjectionType.OVERRIDE)
    public String getOwner() {
        return "Nobody";
    }

}
