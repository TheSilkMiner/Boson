# Boson
**Build Status for branch `1.12.2`:** [![Build Status](https://travis-ci.org/TheSilkMiner/Boson.svg?branch=1.12.2)](https://travis-ci.org/TheSilkMiner/Boson)

## Description
Boson is a (not so) small library mod designed to ease the creation of mods by providing some custom utilities and interfaces that other mods can rely on for their own libraries.
Written in Kotlin, mainly for Kotlin users.

## Functionality
### User Facing
- Loading screen has now a bigger resolution, telling you when and which registers are populated;
- More informative tooltip, with NBT viewing, Ore dictionary entries, recipe information etc;

### Developer Side
- `FMLLoadComplete` has now a new companion event that fires before the mod switches to `AVAILABLE`;
- Object Holders now notify when they are populated;
- A new, DSL-based configuration framework;
- A new OreDictionary system, based on 1.14 tags, that allows for a finer control and non-convention based names;
- Automatic file loading from configurable locations, be it other mods, or other directories (JSON mainly);
- Easier and more Object-Oriented SQL management;
