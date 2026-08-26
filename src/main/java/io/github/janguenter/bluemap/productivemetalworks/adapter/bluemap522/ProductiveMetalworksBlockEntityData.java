/* SPDX-License-Identifier: MIT */

package io.github.janguenter.bluemap.productivemetalworks.adapter.bluemap522;

import de.bluecolored.bluemap.core.world.mca.blockentity.MCABlockEntity;
import de.bluecolored.bluenbt.NBTName;

import java.util.List;

/** Narrow BlueNBT projections of stable ProductiveLib and Metalworks save tags. */
public final class ProductiveMetalworksBlockEntityData {

    private ProductiveMetalworksBlockEntityData() {
    }

    /** NeoForge 1.21.1 FluidStack payload. Components are intentionally skipped. */
    public static final class Fluid {

        @NBTName("id")
        private String id;

        @NBTName("amount")
        private int amount;

        public Fluid() {
        }

        Fluid(String id, int amount) {
            this.id = id;
            this.amount = amount;
        }

        String id() {
            return id;
        }

        int amount() {
            return amount;
        }

        boolean validOwned() {
            return amount > 0 && ProductiveMetalworksCatalog.fluidTint(id) != null;
        }
    }

    /** ProductiveLib FluidTank wrapper written below the outer {@code fluid} tag. */
    public static final class FluidTank {

        @NBTName("Fluid")
        private Fluid fluid;

        public FluidTank() {
        }

        FluidTank(Fluid fluid) {
            this.fluid = fluid;
        }

        Fluid fluid() {
            return fluid;
        }
    }

    /** Casting table and basin state. */
    public static final class Casting extends MCABlockEntity {

        @NBTName("fluid")
        private FluidTank fluid;

        @NBTName("maxAmount")
        private int maxAmount;

        public Casting() {
        }

        Casting(Fluid fluid, int maxAmount) {
            this.fluid = new FluidTank(fluid);
            this.maxAmount = maxAmount;
        }

        Fluid fluid() {
            Fluid selected = fluid == null ? null : fluid.fluid();
            return selected != null && selected.validOwned()
                    && maxAmount > 0 && selected.amount() <= maxAmount ? selected : null;
        }

        float fill() {
            Fluid selected = fluid();
            return selected == null ? 0F : (float) selected.amount() / (float) maxAmount;
        }
    }

    /** Single-fluid, 4,000 mB foundry tank state. */
    public static final class Tank extends MCABlockEntity {

        @NBTName("fluid")
        private FluidTank fluid;

        public Tank() {
        }

        Tank(Fluid fluid) {
            this.fluid = new FluidTank(fluid);
        }

        Fluid fluid() {
            Fluid selected = fluid == null ? null : fluid.fluid();
            return selected != null && selected.validOwned() && selected.amount() <= 4_000
                    ? selected : null;
        }

        float fill() {
            Fluid selected = fluid();
            return selected == null ? 0F : (float) selected.amount() / 4_000F;
        }
    }

    /** Active tap packet state. The numeric fluid registry id is not interpreted. */
    public static final class Tap extends MCABlockEntity {

        @NBTName("isActive")
        private boolean active;

        @NBTName("fluidId")
        private int fluidId;

        public Tap() {
        }

        Tap(boolean active, int fluidId) {
            this.active = active;
            this.fluidId = fluidId;
        }

        boolean active() {
            return active && fluidId > 0;
        }
    }

    /** Fixed 40,000 FE capacitor state. */
    public static final class Capacitor extends MCABlockEntity {

        @NBTName("energy")
        private int energy;

        public Capacitor() {
        }

        Capacitor(int energy) {
            this.energy = energy;
        }

        float fill() {
            return energy < 0 || energy > 40_000 ? 0F : (float) energy / 40_000F;
        }
    }

    /** Assembled controller fluid list and bounded multiblock dimensions. */
    public static final class Controller extends MCABlockEntity {

        @NBTName("fluid")
        private List<Fluid> fluids;

        @NBTName("multiData")
        private MultiData multiblock;

        public Controller() {
        }

        Controller(List<Fluid> fluids, MultiData multiblock) {
            this.fluids = fluids;
            this.multiblock = multiblock;
        }

        List<Fluid> fluids() {
            if (fluids == null || fluids.isEmpty() || fluids.size() > 20) {
                return List.of();
            }
            long total = 0L;
            for (Fluid fluid : fluids) {
                if (fluid == null || !fluid.validOwned()) {
                    return List.of();
                }
                total += fluid.amount();
            }
            return total <= 90_000L ? List.copyOf(fluids) : List.of();
        }

        MultiData multiblock() {
            return multiblock != null && multiblock.valid() ? multiblock : null;
        }
    }

    /** ProductiveLib 0.2.0 MultiBlockData fields used by its client renderer. */
    public static final class MultiData {

        @NBTName("height")
        private int height;

        @NBTName("volume")
        private int volume;

        @NBTName("controller")
        private long controller;

        @NBTName("corner1")
        private long corner1;

        @NBTName("corner2")
        private long corner2;

        public MultiData() {
        }

        MultiData(int height, int volume, long controller, long corner1, long corner2) {
            this.height = height;
            this.volume = volume;
            this.controller = controller;
            this.corner1 = corner1;
            this.corner2 = corner2;
        }

        int height() {
            return height;
        }

        Position controller() {
            return Position.unpack(controller);
        }

        Position corner1() {
            return Position.unpack(corner1);
        }

        Position corner2() {
            return Position.unpack(corner2);
        }

        private boolean valid() {
            Position base = controller();
            Position first = corner1();
            Position second = corner2();
            int width = Math.abs(first.x() - second.x()) - 1;
            int depth = Math.abs(first.z() - second.z()) - 1;
            return height >= 2 && height <= 16
                    && width >= 1 && width <= 16
                    && depth >= 1 && depth <= 16
                    && volume > 0 && volume <= 4_096
                    && Math.abs(first.y() - base.y()) <= height
                    && Math.abs(second.y() - base.y()) <= height;
        }
    }

    record Position(int x, int y, int z) {

        static Position unpack(long packed) {
            int x = (int) (packed >> 38);
            int y = (int) (packed << 52 >> 52);
            int z = (int) (packed << 26 >> 38);
            return new Position(x, y, z);
        }
    }
}
