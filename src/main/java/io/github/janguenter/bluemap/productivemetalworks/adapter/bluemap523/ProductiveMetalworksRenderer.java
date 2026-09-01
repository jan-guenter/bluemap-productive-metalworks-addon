/* SPDX-License-Identifier: MIT */

package io.github.janguenter.bluemap.productivemetalworks.adapter.bluemap523;

import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.MaxCapacityReachedException;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.map.hires.block.BlockRenderer;
import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.util.Direction;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import de.bluecolored.bluemap.core.world.block.ExtendedBlock;
import io.github.janguenter.bluemap.productivemetalworks.activation.AddonRuntime;
import io.github.janguenter.bluemap.productivemetalworks.adapter.bluemap523
        .ProductiveMetalworksBlockEntityData.Capacitor;
import io.github.janguenter.bluemap.productivemetalworks.adapter.bluemap523
        .ProductiveMetalworksBlockEntityData.Casting;
import io.github.janguenter.bluemap.productivemetalworks.adapter.bluemap523
        .ProductiveMetalworksBlockEntityData.Controller;
import io.github.janguenter.bluemap.productivemetalworks.adapter.bluemap523
        .ProductiveMetalworksBlockEntityData.Fluid;
import io.github.janguenter.bluemap.productivemetalworks.adapter.bluemap523
        .ProductiveMetalworksBlockEntityData.MultiData;
import io.github.janguenter.bluemap.productivemetalworks.adapter.bluemap523
        .ProductiveMetalworksBlockEntityData.Position;
import io.github.janguenter.bluemap.productivemetalworks.adapter.bluemap523
        .ProductiveMetalworksBlockEntityData.Tank;
import io.github.janguenter.bluemap.productivemetalworks.adapter.bluemap523
        .ProductiveMetalworksBlockEntityData.Tap;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/** Keeps JSON hosts intact, then restores stable client-only block-entity layers. */
final class ProductiveMetalworksRenderer implements BlockRenderer {

    private static final ThreadLocal<Boolean> STOCK_FALLBACK =
            ThreadLocal.withInitial(() -> Boolean.FALSE);

    private final ResourcePack resourcePack;
    private final TextureGallery textures;
    private final RenderSettings settings;
    private final AddonRuntime runtime;
    private final VariantRendererCatalog catalog;
    private final OverlayEmitter emitter;
    private final Map<BlockRendererType, BlockRenderer> hosts = new IdentityHashMap<>();

    ProductiveMetalworksRenderer(
            ResourcePack resourcePack,
            TextureGallery textures,
            RenderSettings settings,
            AddonRuntime runtime,
            VariantRendererCatalog catalog
    ) {
        this.resourcePack = resourcePack;
        this.textures = textures;
        this.settings = settings;
        this.runtime = runtime;
        this.catalog = catalog;
        this.emitter = new OverlayEmitter(resourcePack, textures);
    }

    @Override
    public void render(
            BlockNeighborhood block,
            Variant variant,
            TileModelView target,
            Color mapColor
    ) {
        int safeStart = target.getStart();
        try {
            if (!renderOwned(block, variant, target, mapColor)) {
                stock(block, variant, target, mapColor);
            }
        } catch (MaxCapacityReachedException exception) {
            throw exception;
        } catch (Error error) {
            throwIfFatal(error);
            reset(target, safeStart);
            runtime.inactive("renderer-" + error.getClass().getSimpleName());
            stockSafely(block, variant, target, mapColor, safeStart);
        } catch (RuntimeException exception) {
            reset(target, safeStart);
            runtime.inactive("renderer-" + exception.getClass().getSimpleName());
            stockSafely(block, variant, target, mapColor, safeStart);
        }
    }

    private boolean renderOwned(
            BlockNeighborhood block,
            Variant variant,
            TileModelView target,
            Color mapColor
    ) {
        if (!runtime.active() || catalog == null) {
            return false;
        }
        String blockId = block.getBlockState().getId().getFormatted();
        Integer moltenTint = ProductiveMetalworksCatalog.fluidTint(blockId);
        if (moltenTint != null) {
            emitter.moltenFluid(block, target, moltenTint, mapColor);
            return true;
        }
        if (ProductiveMetalworksCatalog.window(blockId)) {
            return window(block, variant, target, mapColor);
        }

        stock(block, variant, target, mapColor);
        int overlayStart = target.getTileModel().size();
        try {
            if (ProductiveMetalworksCatalog.CASTING_TABLE.equals(blockId)
                    || ProductiveMetalworksCatalog.CASTING_BASIN.equals(blockId)) {
                casting(blockId, block, target, mapColor);
            } else if (ProductiveMetalworksCatalog.tank(blockId)) {
                tank(block, target, mapColor);
            } else if (ProductiveMetalworksCatalog.capacitor(blockId)) {
                capacitor(block, target);
            } else if (ProductiveMetalworksCatalog.FOUNDRY_TAP.equals(blockId)) {
                tap(block, target, mapColor);
            } else if (ProductiveMetalworksCatalog.controller(blockId)) {
                controller(block, target, mapColor);
            }
        } catch (MaxCapacityReachedException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            reset(target, overlayStart);
        }
        return true;
    }

    private void casting(
            String blockId,
            BlockNeighborhood block,
            TileModelView target,
            Color mapColor
    ) {
        if (!(block.getBlockEntity() instanceof Casting data)) {
            return;
        }
        Fluid fluid = data.fluid();
        Integer tint = fluid == null ? null : ProductiveMetalworksCatalog.fluidTint(fluid.id());
        if (tint == null) {
            return;
        }
        float base = ProductiveMetalworksCatalog.CASTING_TABLE.equals(blockId)
                ? 15F / 16F : 1F / 16F;
        float travel = ProductiveMetalworksCatalog.CASTING_TABLE.equals(blockId)
                ? 0.7F / 16F : 14.01F / 16F;
        emitter.surface(block, target, ProductiveMetalworksCatalog.MOLTEN_TEXTURE, tint,
                base + data.fill() * travel, 0.001F, 0.001F, 0.999F, 0.999F,
                mapColor);
    }

    private void tank(BlockNeighborhood block, TileModelView target, Color mapColor) {
        if (!(block.getBlockEntity() instanceof Tank data)) {
            return;
        }
        Fluid fluid = data.fluid();
        Integer tint = fluid == null ? null : ProductiveMetalworksCatalog.fluidTint(fluid.id());
        if (tint == null) {
            return;
        }
        float fluidY = 0.5F / 16F + data.fill() * 15.01F / 16F;
        emitter.fluidBox(block, target, ProductiveMetalworksCatalog.MOLTEN_TEXTURE, tint,
                0.01F, 0.001F, 0.01F, 0.99F, fluidY, 0.99F, mapColor);
    }

    private void capacitor(BlockNeighborhood block, TileModelView target) {
        if (!(block.getBlockEntity() instanceof Capacitor data)) {
            return;
        }
        Direction facing;
        try {
            facing = Direction.fromString(block.getBlockState().getProperties().get("facing"));
        } catch (IllegalArgumentException | NullPointerException exception) {
            return;
        }
        emitter.energyBar(block, target, facing, data.fill());
    }

    private void tap(BlockNeighborhood block, TileModelView target, Color mapColor) {
        if (!(block.getBlockEntity() instanceof Tap tap) || !tap.active()) {
            return;
        }
        Fluid fluid = neighborFluid(block.getNeighborBlock(0, -1, 0));
        if (fluid == null) {
            int[] behind = behind(block.getBlockState().getProperties().get("facing"));
            fluid = behind == null ? null
                    : neighborFluid(block.getNeighborBlock(behind[0], 0, behind[1]));
        }
        Integer tint = fluid == null ? null : ProductiveMetalworksCatalog.fluidTint(fluid.id());
        if (tint == null) {
            return;
        }
        boolean basinBelow = ProductiveMetalworksCatalog.CASTING_BASIN.equals(
                block.getNeighborBlock(0, -1, 0).getBlockState().getId().getFormatted()
        );
        emitter.fluidBox(block, target, ProductiveMetalworksCatalog.MOLTEN_FLOW_TEXTURE, tint,
                6.5F / 16F, basinBelow ? -0.99F : 0F, 6.5F / 16F,
                9.5F / 16F, 5.5F / 16F, 9.5F / 16F, mapColor);
    }

    private void controller(BlockNeighborhood block, TileModelView target, Color mapColor) {
        if (!"true".equals(block.getBlockState().getProperties().get("attached"))
                || !(block.getBlockEntity() instanceof Controller data)) {
            return;
        }
        MultiData multiblock = data.multiblock();
        List<Fluid> fluids = data.fluids();
        if (multiblock == null || fluids.isEmpty()) {
            return;
        }
        Position base = multiblock.controller();
        if (base.x() != block.getX() || base.y() != block.getY() || base.z() != block.getZ()) {
            return;
        }
        Position first = multiblock.corner1();
        Position second = multiblock.corner2();
        float x0 = Math.min(first.x(), second.x()) - base.x() + 1F + 0.001F;
        float x1 = Math.max(first.x(), second.x()) - base.x() - 0.001F;
        float z0 = Math.min(first.z(), second.z()) - base.z() + 1F + 0.001F;
        float z1 = Math.max(first.z(), second.z()) - base.z() - 0.001F;
        float offsetY = -(multiblock.height() - (first.y() - base.y()) - 1F);
        long total = fluids.stream().mapToLong(Fluid::amount).sum();
        float filled = (float) total / 90_000F;
        float y = offsetY + 0.0001F;
        for (Fluid fluid : fluids) {
            float next = y + filled * (multiblock.height()
                    * (float) fluid.amount() / (float) total - 1F / 16F);
            Integer tint = ProductiveMetalworksCatalog.fluidTint(fluid.id());
            if (tint == null || next <= y) {
                return;
            }
            emitter.fluidBox(block, target, ProductiveMetalworksCatalog.MOLTEN_TEXTURE, tint,
                    x0, y, z0, x1, next, z1, mapColor);
            y = next;
        }
    }

    private boolean window(
            BlockNeighborhood block,
            Variant variant,
            TileModelView target,
            Color mapColor
    ) {
        var model = variant.getModel().getResource(resourcePack.getModels()::get);
        if (model == null) {
            return false;
        }
        model.applyParent(resourcePack.getModels());
        var variable = model.getTextures().get("front");
        var path = variable == null ? null
                : variable.getTexturePath(model.getTextures()::get);
        if (path == null || resourcePack.getTextures().get(path) == null) {
            return false;
        }
        for (Direction direction : Direction.values()) {
            var offset = direction.toVector();
            String neighbor = block.getNeighborBlock(
                    offset.getX(), offset.getY(), offset.getZ()
            ).getBlockState().getId().getFormatted();
            if (!ProductiveMetalworksCatalog.window(neighbor)) {
                emitter.windowFace(block, target, path, direction, mapColor);
            }
        }
        return true;
    }

    private static Fluid neighborFluid(ExtendedBlock block) {
        if (block.getBlockEntity() instanceof Casting casting) {
            return casting.fluid();
        }
        if (block.getBlockEntity() instanceof Tank tank) {
            return tank.fluid();
        }
        if (block.getBlockEntity() instanceof Controller controller) {
            List<Fluid> fluids = controller.fluids();
            return fluids.isEmpty() ? null : fluids.getFirst();
        }
        return null;
    }

    private static int[] behind(String facing) {
        if (facing == null) {
            return null;
        }
        return switch (facing) {
            case "north" -> new int[]{0, 1};
            case "south" -> new int[]{0, -1};
            case "west" -> new int[]{1, 0};
            case "east" -> new int[]{-1, 0};
            default -> null;
        };
    }

    private void stock(
            BlockNeighborhood block,
            Variant variant,
            TileModelView target,
            Color mapColor
    ) {
        if (STOCK_FALLBACK.get()) {
            return;
        }
        STOCK_FALLBACK.set(Boolean.TRUE);
        try {
            BlockRendererType type = catalog == null
                    ? BlockRendererType.DEFAULT : catalog.original(variant);
            hosts.computeIfAbsent(
                    type, found -> found.create(resourcePack, textures, settings)
            ).render(block, variant, target, mapColor);
        } finally {
            STOCK_FALLBACK.set(Boolean.FALSE);
        }
    }

    private void stockSafely(
            BlockNeighborhood block,
            Variant variant,
            TileModelView target,
            Color mapColor,
            int safeStart
    ) {
        try {
            stock(block, variant, target, mapColor);
        } catch (Error error) {
            throwIfFatal(error);
            reset(target, safeStart);
        } catch (RuntimeException exception) {
            reset(target, safeStart);
        }
    }

    private static void reset(TileModelView target, int start) {
        target.getTileModel().reset(start);
        target.initialize(start);
    }

    @SuppressWarnings("removal")
    private static void throwIfFatal(Error error) {
        if (error instanceof OutOfMemoryError outOfMemory) {
            throw outOfMemory;
        }
        if (error instanceof ThreadDeath threadDeath) {
            throw threadDeath;
        }
    }
}
