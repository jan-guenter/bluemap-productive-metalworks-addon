/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.productivemetalworks.adapter.bluemap523;

import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePackExtension;
import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.util.Key;
import io.github.janguenter.bluemap.productivemetalworks.activation.AddonRuntime;
import io.github.janguenter.bluemap.productivemetalworks.profile.ExactArtifactDetector;
import io.github.janguenter.bluemap.productivemetalworks.profile.ProductiveMetalworks1151Profile;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/** Exact-artifact admission, installed-resource preflight, and target-only wrapping. */
final class ProfileResourceExtension implements ResourcePackExtension {

    private static final Map<ResourcePack, VariantRendererCatalog> CATALOGS =
            new WeakHashMap<>();

    private final ResourcePack resourcePack;
    private final BlockRendererType renderer;
    private final AddonRuntime runtime;

    ProfileResourceExtension(
            ResourcePack resourcePack,
            BlockRendererType renderer,
            AddonRuntime runtime
    ) {
        this.resourcePack = resourcePack;
        this.renderer = renderer;
        this.runtime = runtime;
    }

    @Override
    public void loadResources(Iterable<Path> roots) {
        if (Boolean.getBoolean("bluemap.productivemetalworks.disabled")) {
            runtime.inactive("operator-disabled");
            return;
        }
        Path artifact = ExactArtifactDetector.findExact(
                roots, ProductiveMetalworks1151Profile.ARTIFACTS.getFirst()
        );
        if (artifact == null) {
            runtime.inactive("exact-artifact-missing-or-duplicate");
            return;
        }
        if (!ProductiveMetalworks1151Profile.matchesProductiveLib(artifact)) {
            runtime.inactive("embedded-productivelib-mismatch");
            return;
        }
        runtime.activate();
    }

    @Override
    public Set<Key> collectUsedTextureKeys() {
        return runtime.active() ? ProductiveMetalworksCatalog.usedTextures() : Set.of();
    }

    @Override
    public void bake() {
        if (!runtime.active()) {
            return;
        }
        for (Key texture : ProductiveMetalworksCatalog.usedTextures()) {
            if (resourcePack.getTextures().get(texture) == null) {
                runtime.inactive("required-texture-missing");
                return;
            }
        }
        VariantRendererCatalog catalog = VariantRendererCatalog.wrap(resourcePack, renderer);
        synchronized (CATALOGS) {
            CATALOGS.put(resourcePack, catalog);
        }
        System.out.println("BlueMap Productive Metalworks add-on active: wrapped "
                + catalog.size() + " exact stable-render variants.");
    }

    static VariantRendererCatalog catalog(ResourcePack resourcePack) {
        synchronized (CATALOGS) {
            return CATALOGS.get(resourcePack);
        }
    }
}
