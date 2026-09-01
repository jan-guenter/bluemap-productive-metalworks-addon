# BlueMap Productive Metalworks Add-on

A Java 21 BlueMap add-on for the exact `productivemetalworks-1.21.1-1.15.1` profile in All the Mons
`1.2.0` / Minecraft `1.21.1`.

Status: owner-accepted `0.1.0-alpha.2` BlueMap 5.23 release candidate. It
preserves the owner-accepted alpha.1 renderer, profile, gallery, and fallback
behavior while moving shared compatibility helpers into the pinned Adapter API.
The exact production JAR is 122,988 bytes with SHA-256
`712bd40aa0918d091988cf4472c3b7c6111419d577ea7282a11648f19b1610c4`.

## Build

Clone with `--recurse-submodules`, or initialize the toolkit and Adapter API
submodules in an existing checkout. The settings preflight accepts only the
committed pins and rejects uninitialized, changed, or dirty submodule
checkouts.

```bash
gradle --no-daemon -PbluemapSourcePath=../bluemap-backport clean check build
```

`check` is the quick Java/checkstyle/archive gate. `prototypeCheck` additionally
requires every exact candidate JAR property and validates the comparison
gallery. See `provenance/upstreams.json` for immutable artifact identities and
the [execution guide](docs/EXECUTION.md) for the prototype-to-release loop.

The exact BlueMap checkout is commit
`7e07f4e74ec1e92a6ead9aa1e66054af3e133aac` with API commit
`285c9a60eff3ac2b0cab308ce1058d1565be0971`. Exactly four Adapter API helpers
are compiled from commit `e81f08bc4bfbf02d810ec8949a019130e2e61634`,
source tree `2f974c9bb2ba13888d69682f86f30f58922d30eb`; no module JAR is installed,
bundled, or nested.

## Install

Place the production JAR in BlueMap's add-on pack directory and restart the
BlueMap JVM. Removal plus one restart restores stock behavior; the add-on
creates no custom world state.

Set `-Dbluemap.productivemetalworks.disabled=true` to leave the exact profile inactive.

## Scope boundary

The exact profile restores connected mixed-dye foundry windows; casting, tank,
capacitor, tap, and controller overlays from persisted data; and tint, animated
installed textures, level geometry, slopes, and shared-face culling for all 44
Productive Metalworks molten fluids. Unsupported artifacts and unknown overlay
data retain stock rendering. Transient particles and unpersisted client state
remain excluded.

No Productive Metalworks binary, source, class, asset, captured mesh, or gallery is
bundled in the add-on.
