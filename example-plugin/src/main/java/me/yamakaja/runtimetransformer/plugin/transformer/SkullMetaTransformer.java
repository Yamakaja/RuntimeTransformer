package me.yamakaja.runtimetransformer.plugin.transformer;

import com.google.common.base.Predicate;
import com.mojang.authlib.GameProfile;
import me.yamakaja.runtimetransformer.annotation.CallParameters;
import me.yamakaja.runtimetransformer.annotation.Inject;
import me.yamakaja.runtimetransformer.annotation.InjectionType;
import me.yamakaja.runtimetransformer.annotation.TransformByName;
import net.minecraft.server.v1_11_R1.GameProfileSerializer;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import net.minecraft.server.v1_11_R1.TileEntitySkull;

import javax.annotation.Nullable;

/**
 * Created by Yamakaja on 3/3/18.
 */
@TransformByName("org.bukkit.craftbukkit.v1_11_R1.inventory.CraftMetaSkull")
public class SkullMetaTransformer {

    private GameProfile profile;

    @CallParameters(
            type = CallParameters.Type.SPECIAL,
            owner = "org/bukkit/craftbukkit/v1_11_R1/inventory/CraftMetaItem",
            name = "applyToItem",
            desc = "(Lnet/minecraft/server/v1_11_R1/NBTTagCompound;)V"
    )
    private native void super_applyToItem(NBTTagCompound tag);

    @Inject(InjectionType.OVERRIDE)
    void applyToItem(final NBTTagCompound tag) {
        super_applyToItem(tag);
        if (this.profile != null) {
            NBTTagCompound owner = new NBTTagCompound();
            GameProfileSerializer.serialize(owner, this.profile);
            tag.set("SkullOwner", owner);
            System.out.println("Set owner to " + owner);
            TileEntitySkull.b(this.profile, new Predicate<GameProfile>() {
                @Override
                public boolean apply(@Nullable GameProfile gameProfile) {
                    NBTTagCompound newOwner = new NBTTagCompound();
                    GameProfileSerializer.serialize(newOwner, gameProfile);
                    tag.set("SkullOwner", newOwner);
                    System.out.println("Received game profile!");
                    return false;
                }
            });
        }

    }

}
