# RaindanceMC/warp

- **`warp-module`** — Rain's Flexible Modules — Warps: Named places anybody can be sent to, with a menu to pick one from and a warm-up to stand through. The places themselves are RainsCore's — a warp is a POI of kind "warp", so a ghast line can fly to one and deleting a world takes its warps with it. What is here is who may use which, how a server groups them, and the screens.
- **`warp-standalone`** — Rain's Warps: Rain's Warps, as a plugin of its own. Nothing here but a build file and a descriptor: the warps module and the standard wrapper are shaded together, and that is the whole plugin.

Part of [RaindanceMC](https://github.com/RaindanceMC); built on [RainsCore](https://github.com/RaindanceMC/RainsCore) and the shared parent in [framework](https://github.com/RaindanceMC/framework). Split out of `rains-flexible-modules` on 16.10.2026, history kept.

## Build

```
mvn install
```

Needs Java 25 and read access to `packages.tstieh.de` (server ids `packages-releases` / `packages-snapshots` in `~/.m2/settings.xml`).

## Plugins

- `warp-standalone/target/RainsWarps-1.9.2.jar` — also published as `de.raindancer.modules:warp-standalone:1.9.2`

## Versions

A module's version is a property in this repository's root `pom.xml`, a plugin's is `plugin.version` in its own pom. CI publishes every version that is not in the registry yet on the next push to master.
