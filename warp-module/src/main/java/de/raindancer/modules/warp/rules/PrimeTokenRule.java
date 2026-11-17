package de.raindancer.modules.warp.rules;

import de.raindancer.core.social.support.PrimeMembers;

import java.time.Instant;
import java.util.Optional;

/**
 * Whether a Prime member is owed their warp token. One per Prime, told apart by when it began: a renewal keeps
 * {@link PrimeMembers.Member#since()}, buying again after it ran out starts a new one.
 */
public final class PrimeTokenRule implements IWarpRule {

    /** @param givenFor the start of the Prime a token was last given for, in epoch millis; 0 for never */
    public boolean due(boolean enabled, Optional<PrimeMembers.Member> member, Instant now, long givenFor) {
        return enabled && member.isPresent() && member.get().activeAt(now)
                && member.get().since().toEpochMilli() != givenFor;
    }

    @Override
    public String describe() {
        return "one warp token for each Prime, not for each renewal";
    }
}
