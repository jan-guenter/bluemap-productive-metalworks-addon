#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Compact Productive Metalworks stable-render comparison cases."""

from __future__ import annotations

from dataclasses import dataclass


NAMESPACE = "productivemetalworks_gallery"
ENVELOPE = (159, 99, 159, 173, 103, 169)


@dataclass(frozen=True)
class Placement:
    case_id: str
    label: str
    x: int
    y: int
    z: int
    block_state: str
    expected: str
    block_nbt: str = ""


IRON_500 = (
    '{fluid:{Fluid:{id:"productivemetalworks:molten_iron",amount:500}},'
    "maxAmount:1000,coolingTime:0}"
)
IRON_750 = (
    '{fluid:{Fluid:{id:"productivemetalworks:molten_iron",amount:750}},'
    "maxAmount:1000,coolingTime:0}"
)


PLACEMENTS = (
    Placement("table-empty", "empty casting table", 160, 100, 160,
              "productivemetalworks:casting_table[facing=north]", "stock-only"),
    Placement("table-filled", "half-full casting table", 162, 100, 160,
              "productivemetalworks:casting_table[facing=north]", "molten-surface",
              IRON_500),
    Placement("basin-filled", "three-quarter casting basin", 164, 100, 160,
              "productivemetalworks:casting_basin[facing=north]", "molten-surface",
              IRON_750),
    Placement("tank-empty", "empty red tank", 166, 100, 160,
              "productivemetalworks:red_foundry_tank[facing=north]", "stock-only"),
    Placement("tank-half", "half-full blue tank", 168, 100, 160,
              "productivemetalworks:blue_foundry_tank[facing=north]",
              "molten-volume-half",
              '{fluid:{Fluid:{id:"productivemetalworks:molten_copper",amount:2000}}}'),
    Placement("tank-full", "full green tank", 170, 100, 160,
              "productivemetalworks:green_foundry_tank[facing=north]",
              "molten-volume-full",
              '{fluid:{Fluid:{id:"productivemetalworks:molten_emerald",amount:4000}}}'),
    Placement("capacitor-empty", "empty black capacitor", 160, 100, 163,
              "productivemetalworks:black_foundry_capacitor[facing=north]",
              "stock-only", "{energy:0}"),
    Placement("capacitor-half", "half-full red capacitor", 162, 100, 163,
              "productivemetalworks:red_foundry_capacitor[facing=north]",
              "power-bar-half", "{energy:20000}"),
    Placement("capacitor-full", "full green capacitor", 164, 100, 163,
              "productivemetalworks:green_foundry_capacitor[facing=north]",
              "power-bar-full", "{energy:40000}"),
    Placement("tap-basin", "active tap destination basin", 168, 100, 163,
              "productivemetalworks:casting_basin[facing=north]", "molten-surface",
              IRON_750),
    Placement("tap-active", "active molten tap", 168, 101, 163,
              "productivemetalworks:foundry_tap[facing=north]", "molten-stream",
              "{isActive:1b,fluidId:1}"),
    Placement("molten-iron", "molten iron tint", 160, 100, 166,
              "productivemetalworks:molten_iron", "exact-fluid-tint"),
    Placement("molten-emerald", "molten emerald tint", 162, 100, 166,
              "productivemetalworks:molten_emerald", "exact-fluid-tint"),
    Placement("molten-redstone", "molten redstone tint", 164, 100, 166,
              "productivemetalworks:molten_redstone", "exact-fluid-tint"),
    Placement("molten-refined-obsidian", "molten refined obsidian tint",
              166, 100, 166, "productivemetalworks:molten_refined_obsidian",
              "exact-fluid-tint"),
    Placement("window-red-lower", "mixed window red lower", 169, 100, 166,
              "productivemetalworks:red_foundry_window[facing=north]",
              "shared-faces-culled"),
    Placement("window-blue-lower", "mixed window blue lower", 170, 100, 166,
              "productivemetalworks:blue_foundry_window[facing=north]",
              "shared-faces-culled"),
    Placement("window-green-upper", "mixed window green upper", 169, 101, 166,
              "productivemetalworks:green_foundry_window[facing=north]",
              "shared-faces-culled"),
    Placement("window-yellow-upper", "mixed window yellow upper", 170, 101, 166,
              "productivemetalworks:yellow_foundry_window[facing=north]",
              "shared-faces-culled"),
)
