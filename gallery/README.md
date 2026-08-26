# Productive Metalworks staging gallery

This generated gallery is the bounded fixture used for the exact Productive
Metalworks `1.21.1-1.15.1` comparison. Its 19 cases cover casting surfaces,
empty and filled tanks, capacitor gauges, an active tap, four molten-fluid
tints with naturally scheduled level states, and a connected mixed-dye window
cluster. The clear envelope is x `159..173`, y `99..103`, z `159..169`.

Stable commands:

```bash
python gallery/generate.py
python gallery/generate.py --check
python gallery/lint.py
bash gallery/package.sh /tmp/productivemetalworks-gallery.zip
```

Keep gallery generation deterministic, bounded, synthetic where practical, and
free of candidate assets or captured meshes.
