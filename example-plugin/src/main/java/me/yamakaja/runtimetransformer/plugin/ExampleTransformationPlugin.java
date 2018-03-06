package me.yamakaja.runtimetransformer.plugin;

import me.yamakaja.runtimetransformer.RuntimeTransformer;
import me.yamakaja.runtimetransformer.plugin.transformer.CraftServerTransformer;
import me.yamakaja.runtimetransformer.plugin.transformer.EntityLivingTransformer;
import me.yamakaja.runtimetransformer.plugin.transformer.SkullMetaTransformer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Yamakaja on 19.05.17.
 */
public class ExampleTransformationPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

        new RuntimeTransformer(
                EntityLivingTransformer.class,
                CraftServerTransformer.class,
                SkullMetaTransformer.class
        );

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ItemStack stack = ((Player) sender).getInventory().getItemInMainHand();
        SkullMeta itemMeta = (SkullMeta) stack.getItemMeta();
        itemMeta.setOwner("Yamakaja");
        stack.setItemMeta(itemMeta);
        ((Player) sender).getInventory().setItemInMainHand(stack);
        return true;
    }
}
