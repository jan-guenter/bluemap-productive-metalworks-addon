/* SPDX-License-Identifier: MIT */

package io.github.janguenter.bluemap.productivemetalworks.adapter.bluemap523;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class OverlayEmitterTest {

    @Test
    void usesPinnedBlueMapFluidLevelHeights() {
        assertEquals(14F / 16F, OverlayEmitter.baseHeight(0), 0.00001F);
        assertEquals((14F - 1.9F) / 16F, OverlayEmitter.baseHeight(1), 0.00001F);
        assertEquals((14F - 7F * 1.9F) / 16F, OverlayEmitter.baseHeight(7), 0.00001F);
        assertEquals(1F, OverlayEmitter.baseHeight(8), 0.00001F);
        assertEquals(1F, OverlayEmitter.baseHeight(15), 0.00001F);
    }
}
