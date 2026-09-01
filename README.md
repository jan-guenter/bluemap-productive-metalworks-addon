# BlueMap Productive Metalworks Add-on

A Java 21 BlueMap add-on for the exact `productivemetalworks-1.21.1-1.15.1` profile in All the Mons
`1.2.0` / Minecraft `1.21.1`.

Status: unpublished `0.1.0-alpha.2` BlueMap 5.23 migration candidate. It
preserves the owner-accepted alpha.1 renderer, profile, gallery, and fallback
behavior while moving shared compatibility helpers into the pinned Adapter API.

## Build

Clone with `--recurse-submodules`, or initialize an existing checkout with
`git submodule update --init --recursive -- tooling/bluemap-addon-toolkit
modules/bluemap-addon-adapter-api`. The settings preflight accepts only the
committed toolkit and Adapter API pins and rejects uninitialized, changed, or
dirty submodule checkouts.

```bash
gradle --no-daemon -PbluemapSourcePath=../bluemap-backport clean check build
```

`check` is the quick Java/checkstyle/archive gate. `prototypeCheck` additionally
requires every exact candidate JAR property and validates the comparison
gallery. See `provenance/upstreams.json` for immutable artifact identities and
the [execution guide](docs/EXECUTION.md) for the prototype-to-release loop.

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
