# Chunk Stories

![alt text](http://chunkstories.xyz/img/github_header.png "Header screenshot")

### A Voxel Games Framework

Chunk Stories is a fully-featured voxel game engine written in Java, aiming to replace Minecraft/MCP as a modding framework to build mods and total conversions on. It features almost everything the original game could do, but is built in a modular and open fashion : everything is either engine or content. Mods are, by design, first-class citizens, and multiple of them can be loaded at the same time, without requiring a game restart. A server can host it's own custom mods and have the clients download them upon connection. It's basically gmod for Minecraft.

The project consists of an API, an implementation of that API in the form of client and server executables and everything else is merely mods loaded at runtime. A base mod called "chunkstories-core" or just "core" is bundled with executables to give content creators a solid base upon which to build the gamemode of their dreams. It comes with most basic bloc types a Minecraft clone is expected to have: dirt, stone, wood, glass, chests, doors etc.

### Why yet another Minecraft clone ?

It may have occured to you that a while ago, some company of the name 'Microsoft' spent more than what a few countries GPD to aquire the rights to the videogame 'Minecraft', to the astonishment of observers and fears of some faitfull players. More recently their strategy has become increasingly clear: they are using the brand to promote their other products, and are phasing out the relativly open, and beloved so-called "Java Edition". Replacing it is effectively a port of the C++/C# console remake, exclusive to the platforms Microsoft *wants* you to use.

Chunk Stories is free software and runs on any platform someone can be bothered to port the implementation to.

#### You didn't answer the question. There already are multiple open minecraft clones!

For the author, Chunk Stories is and always has been a side project, made for fun and to become a better programmer. This is the main quest. The author also has the smug belief that other clones are flawed in some way, and he can do better, especially in the mods handling department. ( [Yes, that](https://xkcd.com/927/) )

To this end, Chunk Stories borrows heavily from one of two best things about the Minecraft modding ecosystem: The Bukkit-style of plugins, and the server-downloadable "resource packs", mixing the two into the idea of a "Mod".

### Engine Features

 * Configuration files allow to redefine almost anything using a simplistic syntax
 * Customizable network packets, world generation, inputs, GUI*, fonts, localization, ...
 * Support for basically loading everything at runtime, including downloading on server connection
 * You only deal with named definitions, voxels|items|entities|whatever IDs are allocated automatically and dynamically
 * Up to 65535 voxel types, with 8-bit metadata and optional support for [even more data](http://chunkstories.xyz/wiki)
 * Component-based entity system
 * Heightmap representation for unloaded terrain, rendering up to 1km
 * Modern-ish renderer in GL3.3 core, supporting deferred rendering, reloading shaders, instanced rendering, particles, SSR, bloom, you name it
 * Built-in support for AABB physics, skeletal animation with hitscan hit detection and more
 * Works on Windows and Linux ( OSX may work but unsupported )

\* some stuff *might* not be quite done right now

### Building chunkstories

*This is for building `chunkstories`, the core engine. If you are only looking to write mods, you do not have to mess with this at all and should rather follow the [mods creation guide](http://chunkstories.xyz/wiki/doku.php?id=mod_setup) on the project Wiki !*

#### Setup

First you need to clone both `chunkstories-api` and `chunkstories-core` as both are needed to compile this. You can try to build from the artifacts in the repo, but only those used in released versions of the games are guaranteed to be present.
 * `git clone` both `chunkstories-api` and `chunkstories-core`
 * in the chunkstories-api folder: `./gradlew install` or `gradlew.exe install`on Windows
 * in the chunkstories-core folder: `./gradlew install` or `gradlew.exe install`on Windows

The local maven repository on your computer (.m2 folder) now contains copies of both the api and core content the chunkstories engine requires. These are not automatically rebuilt when building the implementation as they are completly seperate projects, so keep that in mind.

#### Gradle Tasks

 * `./gradlew client:shadowJar` builds the Client executable (chunkstories.jar)
 * `./gradlew server:shadowJar` builds the Server executable (server.jar)
 * `./gradlew converter:shadowJar` builds the Map converter executable (converter.jar)
 * `./gradlew launcher:createExe` builds the launcher executables (.exe and .jar as well)
 * `./gradlew buildAll` builds all of the above

### Links

 * To learn how to play the game and register an account, please visit http://chunkstories.xyz
 * You can find a lot more information on the game wiki, including guides to writing mods, at http://chunkstories.xyz/wiki/
 * You can find videos and dev logs on the lead developer youtube channel: http://youtube.com/Hugobros3
 * We have a discord where anyone can discuss with the devs: https://discord.gg/wudd4pe
 * You can get support either by opening a issue on this project or by visiting the subreddit over at https://reddit.com/r/chunkstories

### License

The chunkstories **implementation** is released under LGPL, see LICENSE.MD
