package de.raindancer.modules.warp.rules;

import de.raindancer.core.social.support.PrimeMembers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.TreeSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PrimeTokenRuleTest {

    private final PrimeTokenRule rule = new PrimeTokenRule();
    private final Instant now = Instant.parse("2026-11-21T16:00:00Z");
    private final Instant since = now.minus(Duration.ofDays(3));

    private Optional<PrimeMembers.Member> member(Instant until) {
        return Optional.of(new PrimeMembers.Member(UUID.randomUUID(), since, until, new TreeSet<>()));
    }

    @Test
    @DisplayName("a Prime member who has had no token for this Prime gets one")
    void firstToken() {
        assertThat(rule.due(true, member(now.plus(Duration.ofDays(28))), now, 0L)).isTrue();
    }

    @Test
    @DisplayName("a renewal is the same Prime: no second token")
    void renewalGivesNone() {
        assertThat(rule.due(true, member(now.plus(Duration.ofDays(58))), now, since.toEpochMilli())).isFalse();
    }

    @Test
    @DisplayName("buying Prime again after it ran out is a new Prime, and a new token")
    void comingBack() {
        assertThat(rule.due(true, member(now.plus(Duration.ofDays(28))), now,
                since.minus(Duration.ofDays(90)).toEpochMilli())).isTrue();
    }

    @Test
    @DisplayName("nobody who is not Prime right now, and nobody when the server switched it off")
    void notPrimeOrOff() {
        assertThat(rule.due(true, Optional.empty(), now, 0L)).isFalse();
        assertThat(rule.due(true, member(now.minus(Duration.ofDays(1))), now, 0L)).isFalse();
        assertThat(rule.due(false, member(now.plus(Duration.ofDays(28))), now, 0L)).isFalse();
    }
}
