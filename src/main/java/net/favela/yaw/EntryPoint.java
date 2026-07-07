package net.favela.yaw;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.favela.yaw.api.loader.Loader;

/**
 * @see Loader
 */
public class EntryPoint implements ClientModInitializer {

    /** The mod's technical ID: namespace for Identifier, the name of the logger, and the search key in the loader. */
    public static final String MOD_ID = "favelayaw";

    @Override
    public void onInitializeClient() {
        Loader.load();
    }

    private static ModMetadata metadata() {
        return FabricLoader.getInstance()
                .getModContainer(MOD_ID)
                .orElseThrow(() -> new IllegalStateException("Mod container not found: " + MOD_ID))
                .getMetadata();
    }

    /** Display name from fabric.mod.json (← gradle.properties: mod_name). */
    public static String name() {
        return metadata().getName();
    }

    /** Version from fabric.mod.json (← gradle.properties: mod_version). */
    public static String version() {
        return metadata().getVersion().getFriendlyString();
    }
}