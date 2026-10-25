package de.raindancer.modules.warp;

import de.raindancer.core.world.poi.Poi;
import de.raindancer.modules.warp.model.Warp;
import de.raindancer.modules.warp.rules.ScreenLineRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("What the warps' screens say")
class ScreenLineRuleTest {

    private final ScreenLineRule rule = new ScreenLineRule();

    private Warp warp(String name, String permission, String category, UUID owner) {
        Poi.Builder building = Poi.builder(name, "world", 0, 64, 0).kind("warp");
        if (permission != null) {
            building.tag(Warp.TAG_PERMISSION, permission);
        }
        if (category != null) {
            building.tag(Warp.TAG_CATEGORY, category);
        }
        if (owner != null) {
            building.owner(owner);
        }
        return new Warp(building.build());
    }

    @Test
    @DisplayName("a public warp has no permission on it and is not shut for rent; shown in alphabetical order")
    void publicOnes() {
        Warp spawn = warp("spawn", null, null, null);
        Warp arena = warp("Arena", null, null, null);
        Warp staff = warp("staff", "rainswarps.warp.staff", null, null);
        Warp shut = warp("shop", null, null, null);

        assertThat(rule.publicOnes(List.of(spawn, staff, shut, arena), warp -> warp == shut))
                .containsExactly(arena, spawn);
    }

    @Test
    @DisplayName("the list shows what fits, then how many more there are")
    void list() {
        List<Warp> warps = List.of(warp("a", null, null, null), warp("b", null, null, null),
                warp("c", null, null, null));

        assertThat(rule.list(warps, 2)).containsExactly("<white>a", "<white>b", "<gray>+1 more");
        assertThat(rule.list(warps, 3)).containsExactly("<white>a", "<white>b", "<white>c");
        assertThat(rule.list(List.of(), 3)).isEmpty();
    }

    @Test
    @DisplayName("tags in a warp's label cannot style the screen")
    void escaped() {
        assertThat(rule.list(List.of(warp("<red>x", null, null, null)), 3)).containsExactly("<white>\\<red>x");
    }

    @Test
    @DisplayName("categories are counted, the biggest first; warps in none are 'Other'")
    void categories() {
        List<ScreenLineRule.Shelf> shelves = rule.categories(List.of(
                warp("a", null, "Shops", null), warp("b", null, "Shops", null), warp("c", null, "Farms", null),
                warp("d", null, null, null), warp("e", null, null, null), warp("f", null, null, null)), 5);

        assertThat(shelves).extracting(ScreenLineRule.Shelf::category).containsExactly("Other", "Shops", "Farms");
        assertThat(shelves.get(0).warps()).isEqualTo(3);
        assertThat(rule.categories(List.of(), 5)).isEmpty();
    }

    @Test
    @DisplayName("a player's own warps are the ones they own")
    void yours() {
        UUID ann = UUID.randomUUID();
        UUID bob = UUID.randomUUID();
        Warp mine = warp("mine", null, null, ann);
        Warp his = warp("his", null, null, bob);

        assertThat(rule.ownedBy(ann, List.of(mine, his))).containsExactly(mine);
        assertThat(rule.ownedBy(ann, List.of())).isEmpty();
    }
}
