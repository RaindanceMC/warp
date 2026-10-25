package de.raindancer.modules.warp.rules;

import de.raindancer.modules.warp.model.Warp;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

/** What the warps' screens pick out, count and say. */
public final class ScreenLineRule implements IWarpRule {

    public static final String NO_CATEGORY = "Other";

    public record Shelf(String category, int warps) {
    }

    /** What anybody may use: no permission on it and not shut for unpaid rent. Alphabetical. */
    public List<Warp> publicOnes(Collection<Warp> warps, Predicate<Warp> closedForRent) {
        return warps.stream()
                .filter(warp -> warp.permission().isEmpty() && !closedForRent.test(warp))
                .sorted(Comparator.comparing(Warp::label, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public List<Warp> ownedBy(UUID owner, Collection<Warp> warps) {
        return warps.stream()
                .filter(warp -> warp.owner().filter(owner::equals).isPresent())
                .sorted(Comparator.comparing(Warp::label, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    /** One line per warp, up to {@code shown}, then how many more there are. */
    public List<String> list(List<Warp> warps, int shown) {
        List<String> lines = new ArrayList<>();
        for (Warp warp : warps.stream().limit(shown).toList()) {
            lines.add("<white>" + MiniMessage.miniMessage().escapeTags(warp.label()));
        }
        if (warps.size() > shown) {
            lines.add("<gray>+" + (warps.size() - shown) + " more");
        }
        return lines;
    }

    public List<Shelf> categories(Collection<Warp> warps, int limit) {
        Map<String, Integer> counted = new HashMap<>();
        warps.forEach(warp -> counted.merge(warp.category().orElse(NO_CATEGORY), 1, Integer::sum));
        return counted.entrySet().stream()
                .map(entry -> new Shelf(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingInt(Shelf::warps).reversed().thenComparing(Shelf::category))
                .limit(limit).toList();
    }

    @Override
    public String describe() {
        return "what the warps' screens pick out, count and say";
    }
}
