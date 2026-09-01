/* SPDX-License-Identifier: MIT */

package io.github.janguenter.bluemap.productivemetalworks.adapter.bluemap523;

import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.TileModel;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.texture.Texture;
import de.bluecolored.bluemap.core.util.Direction;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import de.bluecolored.bluemap.core.world.block.ExtendedBlock;

/** Emits textured fluid layers, bars, and connected full-cube window faces. */
final class OverlayEmitter {

    private static final int WHITE = 0xFFFF_FFFF;
    private static final float WINDOW_INSET = 0.001F;

    private final ResourcePack resourcePack;
    private final TextureGallery textures;

    OverlayEmitter(ResourcePack resourcePack, TextureGallery textures) {
        this.resourcePack = resourcePack;
        this.textures = textures;
    }

    void surface(
            BlockNeighborhood block,
            TileModelView target,
            Key texture,
            int tint,
            float y,
            float x0,
            float z0,
            float x1,
            float z1,
            Color mapColor
    ) {
        quad(block, target, texture, Direction.UP,
                x0, y, z1, x1, y, z1, x1, y, z0, x0, y, z0,
                x0, z0, x1, z1, tint);
        addMapColor(texture, tint, mapColor);
    }

    @SuppressWarnings("checkstyle:ParameterNumber")
    void fluidBox(
            BlockNeighborhood block,
            TileModelView target,
            Key texture,
            int tint,
            float x0,
            float y0,
            float z0,
            float x1,
            float y1,
            float z1,
            Color mapColor
    ) {
        if (!(x1 > x0 && y1 > y0 && z1 > z0)) {
            return;
        }
        quad(block, target, texture, Direction.UP,
                x0, y1, z1, x1, y1, z1, x1, y1, z0, x0, y1, z0,
                x0, z0, x1, z1, tint);
        quad(block, target, texture, Direction.NORTH,
                x1, y0, z0, x0, y0, z0, x0, y1, z0, x1, y1, z0,
                x0, y0, x1, y1, tint);
        quad(block, target, texture, Direction.SOUTH,
                x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1,
                x0, y0, x1, y1, tint);
        quad(block, target, texture, Direction.WEST,
                x0, y0, z0, x0, y0, z1, x0, y1, z1, x0, y1, z0,
                z0, y0, z1, y1, tint);
        quad(block, target, texture, Direction.EAST,
                x1, y0, z1, x1, y0, z0, x1, y1, z0, x1, y1, z1,
                z0, y0, z1, y1, tint);
        addMapColor(texture, tint, mapColor);
    }

    void moltenFluid(
            BlockNeighborhood block,
            TileModelView target,
            int tint,
            Color mapColor
    ) {
        BlockState state = block.getBlockState();
        float northWest;
        float southWest;
        float northEast;
        float southEast;
        int level = state.getLiquidLevel();
        if (level < 8 && !(level == 0 && sameFluid(state, block.getNeighborBlock(0, 1, 0)))) {
            northWest = cornerHeight(block, state, -1, -1);
            southWest = cornerHeight(block, state, -1, 0);
            northEast = cornerHeight(block, state, 0, -1);
            southEast = cornerHeight(block, state, 0, 0);
        } else {
            northWest = 1F;
            southWest = 1F;
            northEast = 1F;
            southEast = 1F;
        }

        fluidFace(block, target, state, ProductiveMetalworksCatalog.MOLTEN_TEXTURE,
                Direction.DOWN,
                0F, 0F, 0F, 1F, 0F, 0F, 1F, 0F, 1F, 0F, 0F, 1F, tint);
        boolean top = fluidFace(block, target, state,
                flowingSurface(block, state)
                        ? ProductiveMetalworksCatalog.MOLTEN_FLOW_TEXTURE
                        : ProductiveMetalworksCatalog.MOLTEN_TEXTURE,
                Direction.UP,
                0F, southWest, 1F, 1F, southEast, 1F,
                1F, northEast, 0F, 0F, northWest, 0F, tint);
        fluidFace(block, target, state, ProductiveMetalworksCatalog.MOLTEN_FLOW_TEXTURE,
                Direction.NORTH,
                1F, 0F, 0F, 0F, 0F, 0F,
                0F, northWest, 0F, 1F, northEast, 0F, tint);
        fluidFace(block, target, state, ProductiveMetalworksCatalog.MOLTEN_FLOW_TEXTURE,
                Direction.SOUTH,
                0F, 0F, 1F, 1F, 0F, 1F,
                1F, southEast, 1F, 0F, southWest, 1F, tint);
        fluidFace(block, target, state, ProductiveMetalworksCatalog.MOLTEN_FLOW_TEXTURE,
                Direction.WEST,
                0F, 0F, 0F, 0F, 0F, 1F,
                0F, southWest, 1F, 0F, northWest, 0F, tint);
        fluidFace(block, target, state, ProductiveMetalworksCatalog.MOLTEN_FLOW_TEXTURE,
                Direction.EAST,
                1F, 0F, 1F, 1F, 0F, 0F,
                1F, northEast, 0F, 1F, southEast, 1F, tint);

        if (top) {
            addMapColor(ProductiveMetalworksCatalog.MOLTEN_TEXTURE, tint, mapColor);
        }
    }

    @SuppressWarnings("checkstyle:ParameterNumber")
    private boolean fluidFace(
            BlockNeighborhood block,
            TileModelView target,
            BlockState state,
            Key texture,
            Direction direction,
            float ax, float ay, float az,
            float bx, float by, float bz,
            float cx, float cy, float cz,
            float dx, float dy, float dz,
            int tint
    ) {
        var offset = direction.toVector();
        ExtendedBlock neighbor = block.getNeighborBlock(
                offset.getX(), offset.getY(), offset.getZ()
        );
        if (sameFluid(state, neighbor)
                || (direction != Direction.UP && neighbor.getProperties().isCulling())) {
            return false;
        }
        quad(block, target, texture, direction,
                ax, ay, az, bx, by, bz, cx, cy, cz, dx, dy, dz,
                0F, 0F, 1F, 1F, tint);
        return true;
    }

    private static float cornerHeight(
            BlockNeighborhood block,
            BlockState state,
            int x,
            int z
    ) {
        for (int dx = x; dx <= x + 1; dx++) {
            for (int dz = z; dz <= z + 1; dz++) {
                if (sameFluid(state, block.getNeighborBlock(dx, 1, dz))) {
                    return 1F;
                }
            }
        }

        float sum = 0F;
        int count = 0;
        for (int dx = x; dx <= x + 1; dx++) {
            for (int dz = z; dz <= z + 1; dz++) {
                ExtendedBlock neighbor = block.getNeighborBlock(dx, 0, dz);
                BlockState neighborState = neighbor.getBlockState();
                if (sameFluid(state, neighbor)) {
                    if (neighborState.getLiquidLevel() == 0) {
                        return 14F / 16F;
                    }
                    sum += baseHeight(neighborState.getLiquidLevel());
                    count++;
                } else if (neighborState.isAir()) {
                    count++;
                }
            }
        }
        return sum == 0F || count == 0 ? 3F / 16F : sum / count;
    }

    static float baseHeight(int level) {
        return level >= 8 ? 1F : (14F - level * 1.9F) / 16F;
    }

    private static boolean flowingSurface(BlockNeighborhood block, BlockState state) {
        float own = baseHeight(state.getLiquidLevel());
        if (own > 0.8F) {
            return false;
        }
        return differentFluidHeight(block, state, own, -1, 0)
                || differentFluidHeight(block, state, own, 1, 0)
                || differentFluidHeight(block, state, own, 0, -1)
                || differentFluidHeight(block, state, own, 0, 1);
    }

    private static boolean differentFluidHeight(
            BlockNeighborhood block,
            BlockState state,
            float own,
            int dx,
            int dz
    ) {
        ExtendedBlock neighbor = block.getNeighborBlock(dx, 0, dz);
        return sameFluid(state, neighbor)
                && Math.abs(baseHeight(neighbor.getBlockState().getLiquidLevel()) - own) > 0.0001F;
    }

    private static boolean sameFluid(BlockState state, ExtendedBlock neighbor) {
        return state.getId().equals(neighbor.getBlockState().getId());
    }

    void energyBar(
            BlockNeighborhood block,
            TileModelView target,
            Direction direction,
            float fill
    ) {
        if (!(fill > 0F && fill <= 1F)) {
            return;
        }
        float horizontal0 = 5F / 16F;
        float horizontal1 = 11F / 16F;
        float y0 = 2F / 16F;
        float y1 = (2F + 12F * fill) / 16F;
        float outside = 0.0001F;
        switch (direction) {
            case NORTH -> quad(block, target, ProductiveMetalworksCatalog.POWER_TEXTURE,
                    direction,
                    horizontal1, y0, -outside, horizontal0, y0, -outside,
                    horizontal0, y1, -outside, horizontal1, y1, -outside,
                    0F, 0F, 6F / 16F, 12F / 16F, WHITE);
            case SOUTH -> quad(block, target, ProductiveMetalworksCatalog.POWER_TEXTURE,
                    direction,
                    horizontal0, y0, 1F + outside, horizontal1, y0, 1F + outside,
                    horizontal1, y1, 1F + outside, horizontal0, y1, 1F + outside,
                    0F, 0F, 6F / 16F, 12F / 16F, WHITE);
            case WEST -> quad(block, target, ProductiveMetalworksCatalog.POWER_TEXTURE,
                    direction,
                    -outside, y0, horizontal1, -outside, y0, horizontal0,
                    -outside, y1, horizontal0, -outside, y1, horizontal1,
                    0F, 0F, 6F / 16F, 12F / 16F, WHITE);
            case EAST -> quad(block, target, ProductiveMetalworksCatalog.POWER_TEXTURE,
                    direction,
                    1F + outside, y0, horizontal0, 1F + outside, y0, horizontal1,
                    1F + outside, y1, horizontal1, 1F + outside, y1, horizontal0,
                    0F, 0F, 6F / 16F, 12F / 16F, WHITE);
            default -> {
                // Capacitors accept horizontal facings only.
            }
        }
    }

    void windowFace(
            BlockNeighborhood block,
            TileModelView target,
            Key texture,
            Direction direction,
            Color mapColor
    ) {
        outerWindowFace(block, target, texture, direction);
        innerWindowFace(block, target, texture, direction);
        if (direction == Direction.UP) {
            addMapColor(texture, WHITE, mapColor);
        }
    }

    private void outerWindowFace(
            BlockNeighborhood block,
            TileModelView target,
            Key texture,
            Direction direction
    ) {
        switch (direction) {
            case DOWN -> quad(block, target, texture, direction,
                    0F, 0F, 0F, 1F, 0F, 0F, 1F, 0F, 1F, 0F, 0F, 1F,
                    0F, 0F, 1F, 1F, WHITE);
            case UP -> quad(block, target, texture, direction,
                    0F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 0F, 0F, 1F, 0F,
                    0F, 0F, 1F, 1F, WHITE);
            case NORTH -> quad(block, target, texture, direction,
                    1F, 0F, 0F, 0F, 0F, 0F, 0F, 1F, 0F, 1F, 1F, 0F,
                    0F, 0F, 1F, 1F, WHITE);
            case SOUTH -> quad(block, target, texture, direction,
                    0F, 0F, 1F, 1F, 0F, 1F, 1F, 1F, 1F, 0F, 1F, 1F,
                    0F, 0F, 1F, 1F, WHITE);
            case WEST -> quad(block, target, texture, direction,
                    0F, 0F, 0F, 0F, 0F, 1F, 0F, 1F, 1F, 0F, 1F, 0F,
                    0F, 0F, 1F, 1F, WHITE);
            case EAST -> quad(block, target, texture, direction,
                    1F, 0F, 1F, 1F, 0F, 0F, 1F, 1F, 0F, 1F, 1F, 1F,
                    0F, 0F, 1F, 1F, WHITE);
        }
    }

    private void innerWindowFace(
            BlockNeighborhood block,
            TileModelView target,
            Key texture,
            Direction side
    ) {
        float low = WINDOW_INSET;
        float high = 1F - WINDOW_INSET;
        switch (side) {
            case DOWN -> quad(block, target, texture, Direction.UP,
                    0F, low, 1F, 1F, low, 1F, 1F, low, 0F, 0F, low, 0F,
                    0F, 0F, 1F, 1F, WHITE);
            case UP -> quad(block, target, texture, Direction.DOWN,
                    0F, high, 0F, 1F, high, 0F, 1F, high, 1F, 0F, high, 1F,
                    0F, 0F, 1F, 1F, WHITE);
            case NORTH -> quad(block, target, texture, Direction.SOUTH,
                    0F, 0F, low, 1F, 0F, low, 1F, 1F, low, 0F, 1F, low,
                    0F, 0F, 1F, 1F, WHITE);
            case SOUTH -> quad(block, target, texture, Direction.NORTH,
                    1F, 0F, high, 0F, 0F, high, 0F, 1F, high, 1F, 1F, high,
                    0F, 0F, 1F, 1F, WHITE);
            case WEST -> quad(block, target, texture, Direction.EAST,
                    low, 0F, 1F, low, 0F, 0F, low, 1F, 0F, low, 1F, 1F,
                    0F, 0F, 1F, 1F, WHITE);
            case EAST -> quad(block, target, texture, Direction.WEST,
                    high, 0F, 0F, high, 0F, 1F, high, 1F, 1F, high, 1F, 0F,
                    0F, 0F, 1F, 1F, WHITE);
        }
    }

    private void addMapColor(Key textureKey, int tint, Color mapColor) {
        Texture texture = resourcePack.getTextures().get(textureKey);
        if (texture == null) {
            return;
        }
        Color sampled = new Color().set(texture.getColorPremultiplied());
        sampled.multiply(new Color().set(tint)).premultiplied();
        mapColor.add(sampled);
    }

    @SuppressWarnings("checkstyle:ParameterNumber")
    private void quad(
            BlockNeighborhood block,
            TileModelView target,
            Key texture,
            Direction direction,
            float ax, float ay, float az,
            float bx, float by, float bz,
            float cx, float cy, float cz,
            float dx, float dy, float dz,
            float u0, float v0, float u1, float v1,
            int argb
    ) {
        int start = target.add(2);
        TileModel model = target.getTileModel();
        model.setPositions(start, ax, ay, az, bx, by, bz, cx, cy, cz);
        model.setPositions(start + 1, ax, ay, az, cx, cy, cz, dx, dy, dz);
        model.setUvs(start, u0, v1, u1, v1, u1, v0);
        model.setUvs(start + 1, u0, v1, u1, v0, u0, v0);
        int material = textures.get(texture);
        float red = (argb >>> 16 & 0xFF) / 255F;
        float green = (argb >>> 8 & 0xFF) / 255F;
        float blue = (argb & 0xFF) / 255F;
        FaceLighting.Sample light = FaceLighting.sample(block, direction);
        for (int index = start; index < start + 2; index++) {
            model.setMaterialIndex(index, material);
            model.setColor(index, red, green, blue);
            model.setAOs(index, 1F, 1F, 1F);
            model.setSunlight(index, light.sunlight());
            model.setBlocklight(index, light.blocklight());
        }
    }
}
