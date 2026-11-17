package de.raindancer.modules.warp;

import de.raindancer.core.data.settings.Describe;
import de.raindancer.core.data.settings.In;
import de.raindancer.core.data.settings.Key;
import de.raindancer.core.data.settings.Range;
import de.raindancer.core.data.settings.Settings;
import de.raindancer.core.data.settings.Title;
import de.raindancer.core.data.settings.Topic;
import de.raindancer.core.world.teleport.Companions;
import org.bukkit.Material;

/**
 * What an owner can decide about warps.
 *
 * <p>The record <em>is</em> the schema: the file, its comments, its validation and the
 * {@code /settings} screens all come from it, so there is nothing to keep in step and no second list
 * to forget. {@link #DEFAULTS} is real Java rather than literals in a yaml, and every component has
 * a {@code with…} of its own — a positional constructor with twelve components is a mis-ordering
 * waiting to happen, and two swapped {@code int}s compile perfectly.
 */
@Settings(id = "warps", topics = {
        @Topic(path = "warps", title = "Warps", icon = Material.LODESTONE),
        @Topic(path = "warps/travelling", title = "Going somewhere", icon = Material.ENDER_PEARL),
        @Topic(path = "warps/making", title = "Making them", icon = Material.COMPASS),
})
public record WarpSettings(

        @In("warps/travelling") @Title("Stand still for") @Range(min = 0, max = 60)
        @Describe("Seconds somebody must stand still before a warp takes them, so that running "
                + "away from a fight through a warp makes it harder. Zero sends them at once.")
        @Key("warmup-seconds")
        int warmupSeconds,

        @In("warps/travelling") @Title("Wait between warps") @Range(min = 0, max = 3600)
        @Describe("Seconds between one player's warps. One wait for all warps rather than one per "
                + "warp: with a wait per warp, hopping between two of them would cost nothing, "
                + "which is the same as having none. Zero switches it off.")
        @Key("cooldown-seconds")
        int cooldownSeconds,

        @In("warps/travelling") @Title("Being hurt cancels the wait")
        @Describe("Whether taking damage gives up on a warp somebody is standing still for. Worth "
                + "thinking about together with the wait above: mobs at spawn and a five-second "
                + "wait is a warp nobody can complete, and players "
                + "report it as broken.")
        @Key("hurt-cancels-warmup")
        boolean hurtCancelsWarmup,

        @In("warps/travelling") @Title("Look for somewhere safe to land")
        @Describe("Whether arriving looks for solid ground near the warp rather than dropping "
                + "somebody exactly where it was set. A warp set on a boat, or one whose ground has "
                + "since been mined out, is otherwise a fall.")
        @Key("safe-arrival")
        boolean safeArrival,

        @In("warps/travelling") @Title("How far to look for it") @Range(min = 1, max = 32)
        @Describe("Blocks. Also how much of the world has to be brought in to find out, so a large "
                + "number is a pause for everybody on the server, not only the person warping.")
        @Key("safe-arrival-radius")
        int safeArrivalRadius,

        @In("warps/making") @Title("Warps one server may have") @Range(min = 1, max = 5000)
        @Describe("A ceiling, so that a script cannot fill the place store. Reaching it refuses the "
                + "next one with a line saying so rather than quietly doing nothing.")
        @Key("most-warps")
        int mostWarps,

        @In("warps/making") @Title("Longest a warp's name may be") @Range(min = 3, max = 48)
        @Describe("Characters. A name longer than this cannot be read in the menu it appears in, "
                + "and the button that opens it is what people look for.")
        @Key("longest-name")
        int longestName,

        @In("warps/travelling") @Title("Bring what you are leading")
        @Describe("Whether a dog on your lead, the boat you are towing and whatever is riding with "
                + "you come along. Off, a warp takes the player and leaves everything they were "
                + "holding on to standing where they were.")
        @Key("bring-what-you-lead")
        boolean bringWhatYouLead,

        @In("warps/travelling") @Title("Bring your animals standing nearby")
        @Describe("Whether your own tame animals come along as well, even the ones not on a lead. "
                + "Friendlier, and it costs a search around the player on every warp. Never brings "
                + "somebody else's animals, and never brings a wild mob, so a warp taken at a mob "
                + "farm does not bring the farm along.")
        @Key("bring-nearby-pets")
        boolean bringNearbyPets,

        @In("warps/travelling") @Title("How far your animals may be") @Range(min = 1, max = 32)
        @Describe("Blocks. Only used when the setting above is on. A lead is never limited by this: "
                + "something on a lead comes however far it has drifted.")
        @Key("bring-radius")
        int bringRadius,

        @In("warps/travelling") @Title("Most that may come at once") @Range(min = 1, max = 20)
        @Describe("A ceiling on how many animals travel with one player. A hundred entities "
                + "teleported at once is a pause for everybody on the server. What is on a lead is brought first when this bites.")
        @Key("bring-at-most")
        int bringAtMost,

        @In("warps/making") @Title("Group warps into categories")
        @Describe("Whether the warp menu offers the categories page. Off, every warp is on one "
                + "list, which suits a small server but not one with many warps.")
        @Key("use-categories")
        boolean useCategories,

        @In("warps/making") @Title("Warps one player may set") @Range(min = 0, max = 100)
        @Describe("How many warps of their own somebody with rainswarps.warp.create may set. A warp "
                + "token is one more on top, whatever this says, and staff have no limit. Zero lets "
                + "only tokens and staff make warps.")
        @Key("most-own-warps")
        int mostOwnWarps,

        @In("warps/making") @Title("Price of making a warp")
        @Describe("Money, written like 25 or 1.5k, taken when a player makes a warp. Moving or re-making "
                + "one they already have is free, as is a warp made with a warp token, and staff with "
                + "rainswarps.warp.manage or rainswarps.bypass.fees pay nothing. 0 makes it free, which is "
                + "the default and needs no economy plugin.")
        @Key("create-price")
        String createPrice,

        @In("warps/making") @Title("Rent per week")
        @Describe("Money a player-owned warp costs every week, taken from its owner when it falls due; "
                + "the first week is included. Unpaid, the warp is closed to everybody but its owner and "
                + "staff until the owner pays with /warp rent pay. Nothing is ever deleted for not "
                + "paying. Only warps made while rent is on are charged, and never staff's. 0 switches "
                + "rent off, which is the default.")
        @Key("rent-per-week")
        String rentPerWeek,

        @In("warps/making") @Title("Most an owner may charge to visit")
        @Describe("Money. Owners may set a visit fee on their own warp up to this much (/warp fee). "
                + "0 means owners may not charge, which is the default.")
        @Key("most-visit-fee")
        String mostVisitFee,

        @In("warps/making") @Title("Server's cut of a visit fee (percent)") @Range(min = 0, max = 100)
        @Describe("How much of every visit fee goes to the server instead of the owner. The visitor "
                + "pays the fee; the owner receives it minus this share.")
        @Key("visit-fee-server-cut-percent")
        int visitFeeServerCutPercent,

        @In("warps/making") @Title("A warp token for every new Prime")
        @Describe("Somebody who becomes Prime gets one warp token the next time they are on: one warp of "
                + "their own, free. A renewal is the same Prime and gives none; buying it again after it ran "
                + "out gives another.")
        @Key("prime-token")
        boolean primeToken) {

    /** Without the Prime token switch, which is on. */
    public WarpSettings(int warmupSeconds, int cooldownSeconds, boolean hurtCancelsWarmup, boolean safeArrival,
                        int safeArrivalRadius, int mostWarps, int longestName, boolean bringWhatYouLead,
                        boolean bringNearbyPets, int bringRadius, int bringAtMost, boolean useCategories,
                        int mostOwnWarps, String createPrice, String rentPerWeek, String mostVisitFee,
                        int visitFeeServerCutPercent) {
        this(warmupSeconds, cooldownSeconds, hurtCancelsWarmup, safeArrival, safeArrivalRadius, mostWarps,
                longestName, bringWhatYouLead, bringNearbyPets, bringRadius, bringAtMost, useCategories,
                mostOwnWarps, createPrice, rentPerWeek, mostVisitFee, visitFeeServerCutPercent, true);
    }

    public static final WarpSettings DEFAULTS = new WarpSettings(
            3, 15, true, true, 8, 200, 24, true, false, 8, 10, true, 3,
            "0", "0", "0", 0);

    // ------------------------------------------------------------------ read back safely

    /**
     * The warm-up, clamped.
     *
     * <p>The store clamps what it reads from the file, but a {@code WarpSettings} can also be built
     * in code — by a test, or by a host handing in its own — and the clamp is what stops a negative
     * warm-up becoming a countdown that never ends.
     */
    public int warmup() {
        return Math.max(0, Math.min(60, warmupSeconds));
    }

    public int cooldown() {
        return Math.max(0, Math.min(3600, cooldownSeconds));
    }

    public int arrivalRadius() {
        return Math.max(1, Math.min(32, safeArrivalRadius));
    }

    public int nameLimit() {
        return Math.max(3, Math.min(48, longestName));
    }

    public int ownWarpLimit() {
        return Math.max(0, Math.min(100, mostOwnWarps));
    }

    public WarpSettings withMostOwnWarps(int most) {
        return new WarpSettings(warmupSeconds, cooldownSeconds, hurtCancelsWarmup, safeArrival, safeArrivalRadius, mostWarps, longestName, bringWhatYouLead, bringNearbyPets, bringRadius, bringAtMost, useCategories, most, createPrice, rentPerWeek, mostVisitFee, visitFeeServerCutPercent, primeToken);
    }

    public int warpLimit() {
        return Math.max(1, Math.min(5000, mostWarps));
    }

    /**
     * What travels with the player, as the policy Core's teleport takes.
     *
     * <p>Built here rather than stored, because two booleans in a config file and a three-way policy
     * in code are the same decision written twice — and the copy in the file is the one an owner
     * edits. The clamping is Companions' own, so a radius typed as a thousand is the largest
     * sensible search rather than an exception at the first warp.
     */
    public Companions companions() {
        if (!bringWhatYouLead) {
            return Companions.NOBODY;
        }
        Companions policy = bringNearbyPets
                ? Companions.WHAT_YOU_LEAD_AND_NEARBY_PETS
                : Companions.WHAT_YOU_LEAD;
        return policy.within(bringRadius).atMost(bringAtMost);
    }

    // ------------------------------------------------------------------ one component at a time

    public WarpSettings withWarmupSeconds(int seconds) {
        return new WarpSettings(seconds, cooldownSeconds, hurtCancelsWarmup, safeArrival, safeArrivalRadius, mostWarps, longestName, bringWhatYouLead, bringNearbyPets, bringRadius, bringAtMost, useCategories, mostOwnWarps, createPrice, rentPerWeek, mostVisitFee, visitFeeServerCutPercent, primeToken);
    }

    public WarpSettings withCooldownSeconds(int seconds) {
        return new WarpSettings(warmupSeconds, seconds, hurtCancelsWarmup, safeArrival, safeArrivalRadius, mostWarps, longestName, bringWhatYouLead, bringNearbyPets, bringRadius, bringAtMost, useCategories, mostOwnWarps, createPrice, rentPerWeek, mostVisitFee, visitFeeServerCutPercent, primeToken);
    }

    public WarpSettings withHurtCancelsWarmup(boolean cancels) {
        return new WarpSettings(warmupSeconds, cooldownSeconds, cancels, safeArrival, safeArrivalRadius, mostWarps, longestName, bringWhatYouLead, bringNearbyPets, bringRadius, bringAtMost, useCategories, mostOwnWarps, createPrice, rentPerWeek, mostVisitFee, visitFeeServerCutPercent, primeToken);
    }

    public WarpSettings withSafeArrival(boolean safe) {
        return new WarpSettings(warmupSeconds, cooldownSeconds, hurtCancelsWarmup, safe, safeArrivalRadius, mostWarps, longestName, bringWhatYouLead, bringNearbyPets, bringRadius, bringAtMost, useCategories, mostOwnWarps, createPrice, rentPerWeek, mostVisitFee, visitFeeServerCutPercent, primeToken);
    }

    public WarpSettings withSafeArrivalRadius(int radius) {
        return new WarpSettings(warmupSeconds, cooldownSeconds, hurtCancelsWarmup, safeArrival, radius, mostWarps, longestName, bringWhatYouLead, bringNearbyPets, bringRadius, bringAtMost, useCategories, mostOwnWarps, createPrice, rentPerWeek, mostVisitFee, visitFeeServerCutPercent, primeToken);
    }

    public WarpSettings withMostWarps(int most) {
        return new WarpSettings(warmupSeconds, cooldownSeconds, hurtCancelsWarmup, safeArrival, safeArrivalRadius, most, longestName, bringWhatYouLead, bringNearbyPets, bringRadius, bringAtMost, useCategories, mostOwnWarps, createPrice, rentPerWeek, mostVisitFee, visitFeeServerCutPercent, primeToken);
    }

    public WarpSettings withLongestName(int longest) {
        return new WarpSettings(warmupSeconds, cooldownSeconds, hurtCancelsWarmup, safeArrival, safeArrivalRadius, mostWarps, longest, bringWhatYouLead, bringNearbyPets, bringRadius, bringAtMost, useCategories, mostOwnWarps, createPrice, rentPerWeek, mostVisitFee, visitFeeServerCutPercent, primeToken);
    }

    public WarpSettings withBringWhatYouLead(boolean bring) {
        return new WarpSettings(warmupSeconds, cooldownSeconds, hurtCancelsWarmup, safeArrival, safeArrivalRadius, mostWarps, longestName, bring, bringNearbyPets, bringRadius, bringAtMost, useCategories, mostOwnWarps, createPrice, rentPerWeek, mostVisitFee, visitFeeServerCutPercent, primeToken);
    }

    public WarpSettings withBringNearbyPets(boolean bring) {
        return new WarpSettings(warmupSeconds, cooldownSeconds, hurtCancelsWarmup, safeArrival, safeArrivalRadius, mostWarps, longestName, bringWhatYouLead, bring, bringRadius, bringAtMost, useCategories, mostOwnWarps, createPrice, rentPerWeek, mostVisitFee, visitFeeServerCutPercent, primeToken);
    }

    public WarpSettings withBringRadius(int radius) {
        return new WarpSettings(warmupSeconds, cooldownSeconds, hurtCancelsWarmup, safeArrival, safeArrivalRadius, mostWarps, longestName, bringWhatYouLead, bringNearbyPets, radius, bringAtMost, useCategories, mostOwnWarps, createPrice, rentPerWeek, mostVisitFee, visitFeeServerCutPercent, primeToken);
    }

    public WarpSettings withBringAtMost(int most) {
        return new WarpSettings(warmupSeconds, cooldownSeconds, hurtCancelsWarmup, safeArrival, safeArrivalRadius, mostWarps, longestName, bringWhatYouLead, bringNearbyPets, bringRadius, most, useCategories, mostOwnWarps, createPrice, rentPerWeek, mostVisitFee, visitFeeServerCutPercent, primeToken);
    }

    public WarpSettings withUseCategories(boolean use) {
        return new WarpSettings(warmupSeconds, cooldownSeconds, hurtCancelsWarmup, safeArrival, safeArrivalRadius, mostWarps, longestName, bringWhatYouLead, bringNearbyPets, bringRadius, bringAtMost, use, mostOwnWarps, createPrice, rentPerWeek, mostVisitFee, visitFeeServerCutPercent, primeToken);
    }

    public WarpSettings withCreatePrice(String price) {
        return new WarpSettings(warmupSeconds, cooldownSeconds, hurtCancelsWarmup, safeArrival, safeArrivalRadius, mostWarps, longestName, bringWhatYouLead, bringNearbyPets, bringRadius, bringAtMost, useCategories, mostOwnWarps, price, rentPerWeek, mostVisitFee, visitFeeServerCutPercent, primeToken);
    }

    public WarpSettings withRentPerWeek(String rent) {
        return new WarpSettings(warmupSeconds, cooldownSeconds, hurtCancelsWarmup, safeArrival, safeArrivalRadius, mostWarps, longestName, bringWhatYouLead, bringNearbyPets, bringRadius, bringAtMost, useCategories, mostOwnWarps, createPrice, rent, mostVisitFee, visitFeeServerCutPercent, primeToken);
    }

    public WarpSettings withMostVisitFee(String most) {
        return new WarpSettings(warmupSeconds, cooldownSeconds, hurtCancelsWarmup, safeArrival, safeArrivalRadius, mostWarps, longestName, bringWhatYouLead, bringNearbyPets, bringRadius, bringAtMost, useCategories, mostOwnWarps, createPrice, rentPerWeek, most, visitFeeServerCutPercent, primeToken);
    }

    public WarpSettings withVisitFeeServerCutPercent(int percent) {
        return new WarpSettings(warmupSeconds, cooldownSeconds, hurtCancelsWarmup, safeArrival, safeArrivalRadius, mostWarps, longestName, bringWhatYouLead, bringNearbyPets, bringRadius, bringAtMost, useCategories, mostOwnWarps, createPrice, rentPerWeek, mostVisitFee, percent, primeToken);
    }

    public WarpSettings withPrimeToken(boolean give) {
        return new WarpSettings(warmupSeconds, cooldownSeconds, hurtCancelsWarmup, safeArrival, safeArrivalRadius, mostWarps, longestName, bringWhatYouLead, bringNearbyPets, bringRadius, bringAtMost, useCategories, mostOwnWarps, createPrice, rentPerWeek, mostVisitFee, visitFeeServerCutPercent, give);
    }

    /** The cut, clamped: a percentage outside 0 to 100 would pay the owner a negative amount. */
    public int visitCutPercent() {
        return Math.max(0, Math.min(100, visitFeeServerCutPercent));
    }
}
