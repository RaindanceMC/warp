package de.raindancer.modules.warp;

import de.raindancer.core.RainsCore;
import de.raindancer.core.content.pack.JarPack;
import de.raindancer.core.platform.log.LogChannel;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/** The warp token's look, offered to Core's resource pack. Drawn by art/warp_token.py. */
public final class WarpPack {

    public static final String ROOT = "/de/raindancer/modules/warp/pack/";
    public static final List<String> FILES = List.of(
            "pack.mcmeta",
            "assets/rainswarps/items/token.json",
            "assets/rainswarps/models/item/token.json",
            "assets/rainswarps/textures/item/token_crystal.png",
            "assets/rainswarps/textures/item/token_crystal.png.mcmeta",
            "assets/rainswarps/textures/item/token_gem.png",
            "assets/rainswarps/textures/item/token_gold.png",
            "assets/rainswarps/textures/item/token_gold.png.mcmeta",
            "assets/rainswarps/textures/item/token_sky.png",
            "assets/rainswarps/textures/item/token_sky.png.mcmeta");

    private static final String OWNER = "Warps";
    private static final String NAME = "warp-token";

    private WarpPack() {
    }

    public static void contribute(Plugin plugin, RainsCore core, LogChannel log) {
        try {
            JarPack.offer(plugin, core.resourcePacks(), WarpPack.class, ROOT, FILES, OWNER, NAME, "The warp token");
        } catch (IOException failure) {
            log.warn("The warps pack could not be written ({}); warp tokens look like nether stars.",
                    failure.getMessage());
        }
    }

    public static void withdraw(RainsCore core) {
        core.resourcePacks().withdraw(OWNER.toLowerCase(Locale.ROOT) + ":" + NAME);
    }
}
