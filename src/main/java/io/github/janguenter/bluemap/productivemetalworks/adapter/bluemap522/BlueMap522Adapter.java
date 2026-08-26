/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.productivemetalworks.adapter.bluemap522;

import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.block.BlockRenderer;
import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.mca.blockentity.BlockEntityType;
import io.github.janguenter.bluemap.productivemetalworks.activation.AddonRuntime;

import java.util.List;

/** BlueMap 5.22 renderer, resource extension, and narrow BlueNBT registration. */
public final class BlueMap522Adapter {

    private static final AddonRuntime RUNTIME = AddonRuntime.INSTANCE;
    private static final BlockRendererType RENDERER = new BlockRendererType.Impl(
            Key.parse("bluemap_productivemetalworks:stable_overlays"),
            BlueMap522Adapter::createRenderer
    );
    private static final ResourcePack.Extension<ProfileResourceExtension> EXTENSION =
            new ProfileResourceExtensionType(RENDERER, RUNTIME);
    private static final List<BlockEntityType> BLOCK_ENTITIES = List.of(
            new BlockEntityType.Impl(
                    Key.parse("productivemetalworks:casting"),
                    ProductiveMetalworksBlockEntityData.Casting.class
            ),
            new BlockEntityType.Impl(
                    Key.parse("productivemetalworks:foundry_tank"),
                    ProductiveMetalworksBlockEntityData.Tank.class
            ),
            new BlockEntityType.Impl(
                    Key.parse("productivemetalworks:foundry_tap"),
                    ProductiveMetalworksBlockEntityData.Tap.class
            ),
            new BlockEntityType.Impl(
                    Key.parse("productivemetalworks:foundry_capacitor"),
                    ProductiveMetalworksBlockEntityData.Capacitor.class
            ),
            new BlockEntityType.Impl(
                    Key.parse("productivemetalworks:foundry_controller"),
                    ProductiveMetalworksBlockEntityData.Controller.class
            )
    );

    private BlueMap522Adapter() {
    }

    /** Installs all identities atomically enough to fail closed on any collision. */
    public static synchronized boolean install() {
        if (!RegistryGuard.canRegister(BlockRendererType.REGISTRY, RENDERER)
                || !RegistryGuard.canRegister(ResourcePack.Extension.REGISTRY, EXTENSION)) {
            RUNTIME.fail("registry-collision");
            return false;
        }
        for (BlockEntityType type : BLOCK_ENTITIES) {
            if (!RegistryGuard.canRegister(BlockEntityType.REGISTRY, type)) {
                RUNTIME.fail("block-entity-registry-collision");
                return false;
            }
        }
        if (!RegistryGuard.register(BlockRendererType.REGISTRY, RENDERER)
                || !RegistryGuard.register(ResourcePack.Extension.REGISTRY, EXTENSION)) {
            RUNTIME.fail("registry-registration-failed");
            return false;
        }
        for (BlockEntityType type : BLOCK_ENTITIES) {
            if (!RegistryGuard.register(BlockEntityType.REGISTRY, type)) {
                RUNTIME.fail("block-entity-registry-registration-failed");
                return false;
            }
        }
        return true;
    }

    private static BlockRenderer createRenderer(
            ResourcePack pack,
            TextureGallery gallery,
            RenderSettings settings
    ) {
        try {
            return new ProductiveMetalworksRenderer(
                    pack, gallery, settings, RUNTIME, ProfileResourceExtension.catalog(pack)
            );
        } catch (RuntimeException exception) {
            RUNTIME.inactive("renderer-construction-"
                    + exception.getClass().getSimpleName());
            return BlockRendererType.DEFAULT.create(pack, gallery, settings);
        }
    }
}
