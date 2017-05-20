package me.yamakaja.runtimetransformer.plugin;

import me.yamakaja.runtimetransformer.RuntimeTransformer;
import me.yamakaja.runtimetransformer.plugin.transformer.CraftServerTransformer;
import me.yamakaja.runtimetransformer.plugin.transformer.EntityLivingTransformer;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Yamakaja on 19.05.17.
 */
public class ExampleTransformationPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

        new RuntimeTransformer(
                EntityLivingTransformer.class,
                CraftServerTransformer.class
        );

    }

}
