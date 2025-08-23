<div align="center">

# Alpine Core
A modern framework for developing Minecraft server plugins.

<!-- modrinth_exclude.start -->
[![Available on Hangar](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/hangar_vector.svg)](https://hangar.papermc.io/Alpine/AlpineCore)
[![Available on Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/project/UGeZQ9hY)
<!-- modrinth_exclude.end -->
[![Read the Docs](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/documentation/gitbook_vector.svg)](https://lib.alpn.cloud/javadoc/releases/co/crystaldev/alpinecore/0.4.9/raw/index.html)
</div>

<!-- modrinth_exclude.start -->
> [!WARNING]
> AlpineCore is currently in an early stage of development and breaking changes **will** occur.
<!-- modrinth_exclude.end -->

## What is AlpineCore?
Inspired by [MassiveCore](https://gitlab.massivecraft.team/massivecraft/MassiveCore), AlpineCore has a few key goals:
- Reduce boilerplate
- Integrate modern technologies
  - [Adventure](https://github.com/KyoriPowered/adventure) 
  - [LiteCommands](https://github.com/Rollczi/LiteCommands)
  - [ConfigLib](https://github.com/tomwmth/ConfigLib)
- Maintain high compatibility
  - Java 8 minimum
  - Minecraft 1.8.8 minimum
  - Spigot minimum

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
  - Utilizes a fork of [Exlll's ConfigLib](https://github.com/Exlll/ConfigLib) to provide a smooth configuration experience for both developers and administrators
  - Pre-written integrations with common configuration use cases, including messages compatible with [Kyori's Adventure](https://github.com/KyoriPowered/adventure)
- Storage
  - Extended from `AlpineStore`
  - Handles persistent key + data pairs backed by a configurable storage system
- Commands
  - Extended from `AlpineCommand`
  - A server command that is automatically registered
    - Includes a more convenient API for registering completions and conditions
  - Utilizes [LiteCommands](https://github.com/Rollczi/LiteCommands) to enable the efficient creation of complex command structures
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
To use AlpineCore, you must add it as a dependency to your project:

<details>
<summary>Gradle (Kotlin DSL)</summary>

```kotlin
repositories {
    maven("https://lib.alpn.cloud/releases")
}

dependencies {
    compileOnly("co.crystaldev:alpinecore:0.4.9")
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
    compileOnly 'co.crystaldev:alpinecore:0.4.9'
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
    <version>0.4.9</version>
  </dependency>
</dependencies>
```
</details>

<!-- modrinth_exclude.start -->
All classes and methods that are part of the API should have Javadocs. If one does not, open an issue.

> [!TIP]
> Check out our [basic example plugin](https://github.com/alpine-network/example-plugin) to help you get started.

> [!IMPORTANT]
> AlpineCore must be added as a plugin on any server using your plugin. **DO NOT** shade it.

## Server Admins
AlpineCore has been explicitly verified to work on all versions from `1.8.8` to `1.21.x`.
You can download the latest version [here](https://github.com/alpine-network/alpine-core/releases/latest).

## License
AlpineCore is licensed under the Mozilla Public License v2.0. For information regarding your requirements in the use of this library, please see [Mozilla's FAQ](https://www.mozilla.org/en-US/MPL/2.0/FAQ/).

## Special Thanks To
![YourKit-Logo](https://www.yourkit.com/images/yklogo.png)

YourKit supports open source projects with innovative and intelligent tools for monitoring and profiling Java and .NET applications.

YourKit is the creator of <a href="https://www.yourkit.com/java/profiler/">YourKit Java Profiler</a>, <a href="https://www.yourkit.com/dotnet-profiler/">YourKit .NET Profiler</a>, and <a href="https://www.yourkit.com/youmonitor/">YourKit YouMonitor</a>.

We thank YourKit for supporting open source projects with its full-featured Java Profiler.
<!-- modrinth_exclude.end -->