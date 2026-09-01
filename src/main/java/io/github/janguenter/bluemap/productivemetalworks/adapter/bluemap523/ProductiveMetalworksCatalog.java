/* SPDX-License-Identifier: MIT */

package io.github.janguenter.bluemap.productivemetalworks.adapter.bluemap523;

import de.bluecolored.bluemap.core.util.Key;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/** Exact stable-render roster and molten-fluid tint table for 1.15.1. */
final class ProductiveMetalworksCatalog {

    static final Key MOLTEN_TEXTURE = Key.parse(
            "productivemetalworks:block/fluid/molten_metal"
    );
    static final Key MOLTEN_FLOW_TEXTURE = Key.parse(
            "productivemetalworks:block/fluid/molten_metal_flow"
    );
    static final Key POWER_TEXTURE = Key.parse("productivemetalworks:block/power_level");
    static final String CASTING_TABLE = "productivemetalworks:casting_table";
    static final String CASTING_BASIN = "productivemetalworks:casting_basin";
    static final String FOUNDRY_TAP = "productivemetalworks:foundry_tap";

    private static final String[] DYES = {
        "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink",
        "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red",
        "black"
    };
    private static final Map<String, Integer> FLUID_TINTS = createFluidTints();
    private static final Set<String> TANKS = dyed("foundry_tank");
    private static final Set<String> CAPACITORS = dyed("foundry_capacitor");
    private static final Set<String> CONTROLLERS = dyed("foundry_controller");
    private static final Set<String> WINDOWS = dyed("foundry_window");
    private static final Set<String> CUSTOM_BLOCKS = createCustomBlocks();

    private ProductiveMetalworksCatalog() {
    }

    static Integer fluidTint(String fluidId) {
        return FLUID_TINTS.get(fluidId);
    }

    static boolean moltenBlock(String blockId) {
        return FLUID_TINTS.containsKey(blockId);
    }

    static boolean tank(String blockId) {
        return TANKS.contains(blockId);
    }

    static boolean capacitor(String blockId) {
        return CAPACITORS.contains(blockId);
    }

    static boolean controller(String blockId) {
        return CONTROLLERS.contains(blockId);
    }

    static boolean window(String blockId) {
        return WINDOWS.contains(blockId);
    }

    static Set<String> customBlocks() {
        return CUSTOM_BLOCKS;
    }

    static Set<Key> usedTextures() {
        LinkedHashSet<Key> result = new LinkedHashSet<>();
        result.add(MOLTEN_TEXTURE);
        result.add(MOLTEN_FLOW_TEXTURE);
        result.add(POWER_TEXTURE);
        for (String dye : DYES) {
            result.add(Key.parse("productivemetalworks:block/" + dye
                    + "_foundry_window_front"));
        }
        return Set.copyOf(result);
    }

    private static Set<String> dyed(String suffix) {
        LinkedHashSet<String> result = new LinkedHashSet<>();
        for (String dye : DYES) {
            result.add("productivemetalworks:" + dye + '_' + suffix);
        }
        return Set.copyOf(result);
    }

    private static Set<String> createCustomBlocks() {
        LinkedHashSet<String> result = new LinkedHashSet<>();
        result.add(CASTING_TABLE);
        result.add(CASTING_BASIN);
        result.add(FOUNDRY_TAP);
        result.addAll(TANKS);
        result.addAll(CAPACITORS);
        result.addAll(CONTROLLERS);
        result.addAll(WINDOWS);
        result.addAll(FLUID_TINTS.keySet());
        return Set.copyOf(result);
    }

    private static Map<String, Integer> createFluidTints() {
        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();
        put(result, "molten_heavy_core", -10262666);
        put(result, "molten_amethyst", -3170061);
        put(result, "molten_glowstone", -271756);
        put(result, "molten_redstone", -6023160);
        put(result, "molten_obsidian", -15725540);
        put(result, "molten_glass", -3085591);
        put(result, "molten_emerald", -15213214);
        put(result, "molten_diamond", -11801114);
        put(result, "molten_lapis", -14927728);
        put(result, "molten_quartz", -1120546);
        put(result, "molten_carbon", -15990783);
        put(result, "molten_ender", -15704495);
        put(result, "molten_ancient_debris", -11916253);
        put(result, "molten_shulker_shell", -6985579);
        put(result, "molten_blaze", -210688);
        put(result, "molten_slime", -11104434);
        put(result, "molten_magma_cream", -1476573);
        put(result, "molten_wax", -18424);
        put(result, "meat", -176537);
        put(result, "molten_iron", -3892115);
        put(result, "molten_copper", -3774656);
        put(result, "molten_gold", -1918680);
        put(result, "molten_netherite", -14277082);
        put(result, "molten_aluminum", -1842205);
        put(result, "molten_lead", -8614714);
        put(result, "molten_nickel", -5658236);
        put(result, "molten_osmium", -4142627);
        put(result, "molten_platinum", -4868609);
        put(result, "molten_silver", -5971737);
        put(result, "molten_tin", -8882056);
        put(result, "molten_uranium", -8460424);
        put(result, "molten_zinc", -4868683);
        put(result, "molten_iridium", -4144960);
        put(result, "molten_steel", -9868951);
        put(result, "molten_invar", -3158065);
        put(result, "molten_electrum", -1055310);
        put(result, "molten_bronze", -2520515);
        put(result, "molten_brass", -738747);
        put(result, "molten_enderium", -15834012);
        put(result, "molten_lumium", -9602);
        put(result, "molten_signalum", -2392299);
        put(result, "molten_constantan", -2444402);
        put(result, "molten_refined_glowstone", -5133738);
        put(result, "molten_refined_obsidian", -10138487);
        return Map.copyOf(result);
    }

    private static void put(Map<String, Integer> target, String name, int tint) {
        target.put("productivemetalworks:" + name, tint);
    }
}
