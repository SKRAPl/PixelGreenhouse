# PixelGreenhouse

A Forge mod for Minecraft 1.12.2 that adds a functional Greenhouse block for growing Pixelmon apricorns automatically, powered by furnace fuel.

## Requirements
- Minecraft: 1.12.2
- Forge: 14.23.5.2860 (as used in this repo)
- Pixelmon Reforged: 8.4.3 (the build uses a local JAR in `libs/`)
- Java 8 (JDK 8)

## Features
- **Greenhouse block** with GUI and internal inventory.
- **Fuel-based growth**: consumes any furnace fuel.
- **Apricorn processing**: accepts Pixelmon Apricorn items in input slots and periodically produces more.
- **Hopper support**:
  - Top: input slots (apricorns)
  - Sides: fuel slot
  - Bottom: output slots
- **Facing**: block rotates to face the player on placement.

## Crafting
Recipe (shaped):
```
GGG
G G
III
```
- `G` = glass
- `I` = iron ingot
- Result: `pixelgreenhouse:greenhouse`

## How to use
1. Place the Greenhouse.
2. Right‑click to open the GUI.
3. Put apricorns into input slots (6 slots: indices 0–5).
4. Add furnace fuel to the fuel slot (index 6).
5. Outputs appear in output slots (indices 7–12).
6. Growth takes time (internally tick‑based) and yields a small random amount per cycle.

## Installation
- Download/build the mod JAR and place it in your `mods` folder together with Pixelmon Reforged 8.4.3.
- Start the game with Forge 1.12.2.

## Credits
- Author: PolkaX
- Thanks to Pixelmon Reforged team.
