/* SPDX-License-Identifier: MIT */

package io.github.janguenter.bluemap.productivemetalworks.adapter.bluemap522;

import de.bluecolored.bluemap.core.util.Direction;
import de.bluecolored.bluemap.core.world.LightData;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;

/** Samples the owning block and the world-facing side of an emitted face. */
final class FaceLighting {

    private FaceLighting() {
    }

    static Sample sample(BlockNeighborhood block, Direction direction) {
        var offset = direction.toVector();
        LightData own = block.getLightData();
        LightData faced = block.getNeighborBlock(
                offset.getX(), offset.getY(), offset.getZ()
        ).getLightData();
        return new Sample(
                Math.max(own.getSkyLight(), faced.getSkyLight()),
                Math.max(own.getBlockLight(), faced.getBlockLight())
        );
    }

    record Sample(int sunlight, int blocklight) {
    }
}
