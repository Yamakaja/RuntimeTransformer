package me.yamakaja.runtimetransformer.plugin.transformer;

import me.yamakaja.runtimetransformer.annotation.Inject;
import me.yamakaja.runtimetransformer.annotation.InjectionType;
import me.yamakaja.runtimetransformer.annotation.Transform;
import net.minecraft.server.v1_11_R1.EntityLiving;
import net.minecraft.server.v1_11_R1.World;
import org.bukkit.Bukkit;

/**
 * Created by Yamakaja on 19.05.17.
 */
@Transform(EntityLiving.class)
public abstract class EntityLivingTransformer extends EntityLiving {

    private EntityLivingTransformer(World world) {
        super(world);
    }

    @Inject(InjectionType.INSERT)
    public void setHealth(float f) {
        Bukkit.broadcastMessage(super.getName() + ": " + super.getHealth() + " -> " + f);
    }

}
