package de.raindancer.modules.warp.store;

import de.raindancer.core.ui.hologram.ScreenKind;
import de.raindancer.core.ui.hologram.Screens;
import de.raindancer.modules.warp.model.Warp;
import de.raindancer.modules.warp.rules.ScreenLineRule;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.function.Predicate;

/** The warps' screens for Core's {@code /screen}: the warps anybody may use, by category, and everybody's own. */
public final class WarpScreens {

    public static final List<String> KINDS = List.of("warps-list", "warps-categories", "warps-you");
    private static final MiniMessage MINI = MiniMessage.miniMessage();
    private static final int LISTED = 10;
    private static final int TOP = 5;

    private final WarpCatalogue warps;
    private final Predicate<Warp> closedForRent;
    private final ScreenLineRule rule = new ScreenLineRule();

    public WarpScreens(WarpCatalogue warps, Predicate<Warp> closedForRent) {
        this.warps = warps;
        this.closedForRent = closedForRent;
    }

    public void register(Screens screens, Plugin owner) {
        screens.register(owner, ScreenKind.everyone("warps-list", "Warps",
                "The warps anybody may use.", Material.ENDER_PEARL, this::list));
        screens.register(owner, ScreenKind.everyone("warps-categories", "Warp categories",
                "How many public warps each category holds.", Material.BOOKSHELF, this::categories));
        screens.register(owner, ScreenKind.personal("warps-you", "Your warps",
                "Everybody's own warps.", Material.COMPASS, this::you));
    }

    private Component list() {
        List<Warp> open = rule.publicOnes(warps.all(), closedForRent);
        StringBuilder text = new StringBuilder("<gold><bold>Warps</bold></gold>");
        if (open.isEmpty()) {
            return MINI.deserialize(text + "\n<gray>No warps yet");
        }
        rule.list(open, LISTED).forEach(line -> text.append('\n').append(line));
        text.append("\n<dark_gray>/warp");
        return MINI.deserialize(text.toString());
    }

    private Component categories() {
        StringBuilder text = new StringBuilder("<gold><bold>Warp categories</bold></gold>");
        List<ScreenLineRule.Shelf> top = rule.categories(rule.publicOnes(warps.all(), closedForRent), TOP);
        if (top.isEmpty()) {
            return MINI.deserialize(text + "\n<gray>No warps yet");
        }
        for (ScreenLineRule.Shelf shelf : top) {
            text.append("\n<white>").append(MINI.escapeTags(shelf.category()))
                    .append(" <dark_gray>· <gray>").append(shelf.warps())
                    .append(shelf.warps() == 1 ? " warp" : " warps");
        }
        return MINI.deserialize(text.toString());
    }

    private Component you(ScreenKind.Viewer viewer) {
        List<Warp> mine = rule.ownedBy(viewer.id(), warps.all());
        StringBuilder text = new StringBuilder("<gold><bold>Your warps</bold></gold>");
        if (mine.isEmpty()) {
            return MINI.deserialize(text + "\n<gray>None yet");
        }
        text.append("\n<gray>Warps <white>").append(mine.size());
        rule.list(mine, LISTED - 1).forEach(line -> text.append('\n').append(line));
        return MINI.deserialize(text.toString());
    }
}
