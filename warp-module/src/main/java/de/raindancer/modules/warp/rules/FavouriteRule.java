package de.raindancer.modules.warp.rules;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * A player's pinned warps: which, how they come first in the list, and how the set is kept as one line in the
 * player's own data. Knows nothing about Minecraft.
 */
public final class FavouriteRule implements IWarpRule {

    /** At most this many pinned, so the top of the list stays a top and not the whole list again. */
    public static final int MOST = 9;

    public Set<String> fromLine(String line) {
        Set<String> names = new LinkedHashSet<>();
        if (line == null || line.isBlank()) {
            return names;
        }
        for (String name : line.split(",")) {
            if (!name.isBlank()) {
                names.add(name.strip().toLowerCase(Locale.ROOT));
            }
        }
        return names;
    }

    public String asLine(Set<String> names) {
        return String.join(",", names);
    }

    /** The set with {@code name} pinned or unpinned; pinning past {@link #MOST} drops the oldest pin. */
    public Set<String> toggled(Set<String> names, String name) {
        Set<String> now = new LinkedHashSet<>(names);
        String key = name == null ? "" : name.strip().toLowerCase(Locale.ROOT);
        if (key.isEmpty()) {
            return now;
        }
        if (!now.remove(key)) {
            now.add(key);
            while (now.size() > MOST) {
                now.remove(now.iterator().next());
            }
        }
        return now;
    }

    public boolean isPinned(Set<String> names, String name) {
        return name != null && names.contains(name.strip().toLowerCase(Locale.ROOT));
    }

    /** Pinned warps first, in the order they were pinned, the rest as they came. */
    public <T> List<T> arranged(List<T> warps, Set<String> names, java.util.function.Function<T, String> nameOf) {
        List<T> first = new ArrayList<>();
        List<T> rest = new ArrayList<>();
        for (String pinned : names) {
            for (T warp : warps) {
                if (pinned.equals(nameOf.apply(warp).toLowerCase(Locale.ROOT))) {
                    first.add(warp);
                }
            }
        }
        for (T warp : warps) {
            if (!first.contains(warp)) {
                rest.add(warp);
            }
        }
        first.addAll(rest);
        return first;
    }

    @Override
    public String describe() {
        return "which warps a player pinned to the top of the list";
    }
}
