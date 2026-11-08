package de.raindancer.modules.warp.rules;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class FavouriteRuleTest {

    private final FavouriteRule rule = new FavouriteRule();

    @Test
    @DisplayName("pinning is a toggle, case does not matter, and the line round-trips")
    void pinningAndTheLine() {
        Set<String> pinned = rule.toggled(Set.of(), "Spawn");
        assertThat(rule.isPinned(pinned, "spawn")).isTrue();
        assertThat(rule.fromLine(rule.asLine(pinned))).containsExactly("spawn");
        assertThat(rule.toggled(pinned, "SPAWN")).isEmpty();
        assertThat(rule.fromLine(null)).isEmpty();
        assertThat(rule.fromLine(" , ,")).isEmpty();
    }

    @Test
    @DisplayName("pinned warps come first in the order they were pinned, the rest as they came")
    void pinnedComeFirst() {
        Set<String> pinned = rule.toggled(rule.toggled(Set.of(), "mall"), "arena");
        List<String> arranged = rule.arranged(List.of("spawn", "arena", "farm", "mall"), pinned, name -> name);
        assertThat(arranged).containsExactly("mall", "arena", "spawn", "farm");
    }

    @Test
    @DisplayName("past the most, the oldest pin gives way")
    void theOldestPinGivesWay() {
        Set<String> pinned = Set.of();
        for (int i = 0; i <= FavouriteRule.MOST; i++) {
            pinned = rule.toggled(pinned, "w" + i);
        }
        assertThat(pinned).hasSize(FavouriteRule.MOST).doesNotContain("w0").contains("w" + FavouriteRule.MOST);
    }
}
