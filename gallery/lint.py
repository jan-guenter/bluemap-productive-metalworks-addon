#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Lint the compact generated gallery without starting Minecraft."""

from __future__ import annotations

import json
from pathlib import Path
import re
import sys

sys.dont_write_bytecode = True
import cases
import generate


ROOT = Path(__file__).resolve().parent


def main() -> int:
    for relative, payload in generate.generated_files().items():
        path = ROOT / relative
        if not path.is_file() or path.read_bytes() != payload:
            raise ValueError(f"generated file differs: {relative}")
    json.loads((ROOT / "datapack/pack.mcmeta").read_text(encoding="utf-8"))
    if not 8 <= len(cases.PLACEMENTS) <= 24:
        raise ValueError("gallery case count escaped its compact bound")
    if len({row.case_id for row in cases.PLACEMENTS}) != len(cases.PLACEMENTS):
        raise ValueError("gallery case ids are not unique")
    bounds = cases.ENVELOPE
    for row in cases.PLACEMENTS:
        if not (bounds[0] <= row.x <= bounds[3]
                and bounds[1] <= row.y <= bounds[4]
                and bounds[2] <= row.z <= bounds[5]):
            raise ValueError(f"placement escaped the envelope: {row.case_id}")
        if row.block_nbt and not (
            row.block_nbt.startswith("{") and row.block_nbt.endswith("}")
        ):
            raise ValueError(f"invalid inline block NBT: {row.case_id}")
    functions = "\n".join(
        path.read_text(encoding="utf-8")
        for path in sorted(
            (ROOT / f"datapack/data/{cases.NAMESPACE}/function").glob("*.mcfunction")
        )
    )
    if len(re.findall(r"^setblock ", functions, re.MULTILINE)) != len(
        cases.PLACEMENTS
    ):
        raise ValueError("setblock count differs from the case roster")
    for forbidden in ("summon ", "data merge", "op ", "deop ", "stop "):
        if forbidden in functions.lower():
            raise ValueError(f"forbidden gallery command: {forbidden}")
    print(f"gallery lint passed: {len(cases.PLACEMENTS)} bounded cases")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (OSError, ValueError) as error:
        print(f"gallery lint failed: {error}", file=sys.stderr)
        raise SystemExit(1)
