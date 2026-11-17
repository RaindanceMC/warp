package de.raindancer.modules.warp.listener;

import de.raindancer.core.platform.util.Scheduling;
import de.raindancer.modules.warp.WarpServices;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

/** Prime's warp token, looked for a few seconds after somebody joins (WarpTokens decides). */
public final class PrimeTokenListener implements IWarpListener {

    private final WarpServices services;

    public PrimeTokenListener(WarpServices services) {
        this.services = services;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Scheduling.entityLater(services.plugin(), player, 100, () -> services.tokens().primeCheck(player));
    }

    @Override
    public void forget(UUID player) {
        // Remembers nobody: what was given is on the player.
    }

    @Override
    public String describe() {
        return "Prime's warp token, once per Prime";
    }
}
