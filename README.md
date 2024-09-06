# Alpine Core
> Current version: 0.4.2

A lightweight library for creating modern yet widely compatible Minecraft server plugins.

> This library is currently in an early state of development and breaking changes **will** occur.

Inspired by [MassiveCore](https://gitlab.massivecraft.team/massivecraft/MassiveCore), the project has a few key goals:
- Reduce boilerplate
- Integrate modern technologies
  - [Adventure](https://github.com/KyoriPowered/adventure) 
  - [LiteCommands](https://github.com/Rollczi/LiteCommands)
  - [ConfigLib](https://github.com/tomwmth/ConfigLib)
- Maintain high compatibility
  - Java 8 minimum
  - Minecraft 1.8.8 minimum
  - Spigot minimum

### Systems
The project consists of the following core systems:
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

### For Developers
The library can be added as a dependency to your Gradle buildscript like so:

```
repositories {
    maven {
        name 'Alpine Public'
        url 'https://lib.alpn.cloud/alpine-public'
    }
}

dependencies {
    compileOnly 'co.crystaldev:alpinecore:0.4.2'
}
```

All classes and methods that are part of the API should have Javadocs. If one does not, open an issue. There is also a very basic example plugin located in this repository.

Keep in mind that using this library will require it to be added as a plugin on any server using your plugin. **DO NOT** shade it into your own plugin.

**WARNING:** It is recommended that projects using this library as a dependency use Gradle 8.2+. Lower versions are known to occasionally exhibit odd transitive dependency issues.

### For Server Admins
Any plugin built using this library will require you to add it as a plugin to your server. On its own it does nothing.

The plugin has been explicitly verified to work on releases ranging from `1.8.8` to `1.21.1`.

### License
This library is licensed under the Mozilla Public License v2.0. For information regarding your requirements in the use of this library, please see [Mozilla's FAQ](https://www.mozilla.org/en-US/MPL/2.0/FAQ/).

### Special Thanks To
![YourKit-Logo](https://www.yourkit.com/images/yklogo.png)

YourKit supports open source projects with innovative and intelligent tools for monitoring and profiling Java and .NET applications.

YourKit is the creator of <a href="https://www.yourkit.com/java/profiler/">YourKit Java Profiler</a>, <a href="https://www.yourkit.com/dotnet-profiler/">YourKit .NET Profiler</a>, and <a href="https://www.yourkit.com/youmonitor/">YourKit YouMonitor</a>.

We thank YourKit for supporting open source projects with its full-featured Java Profiler.
