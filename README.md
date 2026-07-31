<div align="center">

# Alpine Core
A modern framework for developing Minecraft server plugins.

<!-- modrinth_exclude.start -->
[![Available on Hangar](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/hangar_vector.svg)](https://hangar.papermc.io/Alpine/AlpineCore)
[![Available on Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/project/UGeZQ9hY)
<!-- modrinth_exclude.end -->
[![Read the Docs](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/documentation/gitbook_vector.svg)](https://lib.alpn.cloud/javadoc/snapshots/co/crystaldev/alpinecore/latest)
</div>

<!-- modrinth_exclude.start -->
> [!WARNING]
> AlpineCore is currently in an early stage of development and breaking changes **will** occur.
<!-- modrinth_exclude.end -->

## What is AlpineCore?
Inspired by [MassiveCore](https://gitlab.massivecraft.team/massivecraft/MassiveCore), AlpineCore has a few key goals:
- Reduce boilerplate
- Integrate modern technologies
  - [Adventure](https://github.com/PaperMC/adventure)
  - [LiteCommands](https://github.com/Rollczi/LiteCommands)
  - [ConfigLib](https://github.com/tomwmth/ConfigLib)
- Maintain high compatibility
  - Minecraft 1.8.8 through 26.2
  - Spigot, Paper, Purpur, and Folia
  - Runs on Java 8 servers

## Distributions
AlpineCore ships as two archives. **Install exactly one!** They both register the plugin name `AlpineCore`, so the server will likely throw an error.

| Archive                 | Servers                      | Minecraft        | Server Java |
|-------------------------|------------------------------|------------------|-------------|
| **`AlpineCore-Bukkit`** | Spigot, Paper, Purpur, Folia | `1.8.8` - `26.2` | 8+          |
| `AlpineCore-Paper`      | Paper, Folia                 | `1.21.4`+        | 21+         |

**If you are unsure, use `AlpineCore-Bukkit`.** It supports every version that AlpineCore targets, including the newest ones, and is the right choice on any Spigot server.

`AlpineCore-Paper` is a smaller, native build for modern Paper: it is compiled directly against the Paper API and uses the server's own Adventure and region scheduler instead of bundling its own. 

Prefer it if you run Paper or Folia `1.21.4` or newer, and want the leaner archive.

## Systems
AlpineCore consists of the following core systems:
- Engines
  - Extended from `AlpineEngine`
  - An event listener that is automatically registered
- Integrations
  - Extended from `AlpineIntegration`
  - Engines that only activate under configurable conditions, such as the presence of an external plugin
- Configurations
  - Extended from `AlpineConfig`
  - A collection of settings that is automatically registered and persisted
  - Uses a fork of [Exlll's ConfigLib](https://github.com/Exlll/ConfigLib) to provide a smooth configuration experience for both developers and administrators
  - Pre-written integrations with common configuration use cases, including messages compatible with [Kyori's Adventure](https://github.com/KyoriPowered/adventure)
- Storage
  - Extended from `AlpineStore`
  - Handles persistent key + data pairs backed by a configurable storage system
- Commands
  - Extended from `AlpineCommand`
  - A server command that is automatically registered
    - Includes a more convenient API for registering completions and conditions
  - Uses [LiteCommands](https://github.com/Rollczi/LiteCommands) to enable the efficient creation of complex command structures
- User Interfaces
  - Provides an advanced inventory GUI framework designed for simplicity, with recipe-like slot mask configuration for element placement
  - Allows the efficient creation of interactive user interfaces in the plugin
  - Utilizes `UIHandler` to initialize the menu to handle user interactions
- Teleportation
  - Managed by `TeleportManager`
  - Provides a centralized system for point-to-point teleportation
    - Curate a teleportation sequence with the `TeleportTask` builder
    - Handle the lifecycle of a teleportation request, from initialization to the execution
    - Add a countdown, event handlers, & cancellation policies
- Events
  - Extended from `AlpineEvent`
  - A generic Bukkit event, minus the boilerplate

## For Developers
You depend on the same artifact regardless of which archive your users install. 
The API is identical across both, so you do not target a distribution.

> [!IMPORTANT]
> **Building against AlpineCore 0.5.0+ requires a Java 21 toolchain**, even for plugins that target 1.8.8 servers. 
> Adventure 5 is compiled for Java 21, and Gradle and Maven will refuse to resolve it on an older JDK.
>
> This does not change what your plugin *runs* on. 
> Compile on 21 and, if you need Java 8 support, run [JvmDowngrader](https://github.com/unimined/JvmDowngrader) over your own output.
> This is the process used for `AlpineCore-Bukkit`.

To use AlpineCore, you must add it as a dependency to your project:

<details>
<summary>Gradle (Kotlin DSL)</summary>

```kotlin
repositories {
    maven("https://lib.alpn.cloud/releases")
}

dependencies {
    compileOnly("co.crystaldev:alpinecore:0.5.0")
}
```
</details>


<details>
<summary>Gradle (Groovy DSL)</summary>

```groovy
repositories {
    maven {
        url 'https://lib.alpn.cloud/releases'
    }
}

dependencies {
    compileOnly 'co.crystaldev:alpinecore:0.5.0'
}
```
</details>

<details>
<summary>Maven</summary>

```xml
<repositories>
  <repository>
    <name>Alpine Public</name>
    <url>https://lib.alpn.cloud/releases</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>co.crystaldev</groupId>
    <artifactId>alpinecore</artifactId>
    <version>0.5.0</version>
  </dependency>
</dependencies>
```
</details>

<!-- modrinth_exclude.start -->
All classes and methods that are part of the API should have Javadocs. If one does not, open an issue.

> [!TIP]
> Check out the [example plugin]([example](https://github.com/alpine-network/example-plugin/)) in this repository to help you get started.

> [!IMPORTANT]
> AlpineCore must be added as a plugin on any server using your plugin. **DO NOT** shade it.
>
> The same goes for Adventure and LiteCommands. AlpineCore exposes both across its own API, so
> bundling your own copy will collide with the one already on the classpath.
> 
> Depend on AlpineCore and use what it provides.

## Server Admins
Download the latest version [here](https://github.com/alpine-network/alpine-core/releases/latest),
and see [Distributions](#distributions) for which of the two archives to install. **Install exactly one**.

`AlpineCore-Bukkit` has been verified on `1.8.8`, `1.21.1`, and `26.2`, and supports everything in between. 
`AlpineCore-Paper` has been verified on `1.21.1`, `1.21.11`, `26.2`.

## License
AlpineCore is licensed under the Mozilla Public License v2.0. For information regarding your requirements in the use of this library, please see [Mozilla's FAQ](https://www.mozilla.org/en-US/MPL/2.0/FAQ/).

## Special Thanks To
![YourKit-Logo](https://www.yourkit.com/images/yklogo.png)

YourKit supports open source projects with innovative and intelligent tools for monitoring and profiling Java and .NET applications.

YourKit is the creator of <a href="https://www.yourkit.com/java/profiler/">YourKit Java Profiler</a>, <a href="https://www.yourkit.com/dotnet-profiler/">YourKit .NET Profiler</a>, and <a href="https://www.yourkit.com/youmonitor/">YourKit YouMonitor</a>.

We thank YourKit for supporting open source projects with its full-featured Java Profiler.
<!-- modrinth_exclude.end -->