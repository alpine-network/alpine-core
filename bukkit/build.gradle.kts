plugins {
    id("core.blossom-conventions")
    id("core.hangar-conventions")
}

dependencies {
    // Internal dependencies
    implementation(libs.localelib)
    implementation(libs.dbcp2)

    // Consumer dependencies
    api(platform(libs.adventure.bom))
    api(libs.adventure.api)
    api(libs.adventure.text.minimessage)
    api(libs.adventure.text.serializer.plain)
    api(libs.annotations)
    api(libs.litecommands.adventure.platform)
    api(libs.xseries)

    // Bukkit platform dependencies
    api(libs.adventure.platform.bukkit)
    api(libs.configlib.spigot)
    api(libs.litecommands.bukkit)
    compileOnlyApi(libs.gson)
    compileOnlyApi(libs.guava)
    compileOnly(libs.spigot.api) {
        exclude(libs.gson.get().group)
        exclude(libs.guava.get().group)
        exclude("junit")
        exclude("org.yaml", "snakeyaml")
    }

    // Server plugins
    compileOnly(libs.placeholderapi)
    compileOnly(libs.vault.api)

    // Testing dependencies
    testImplementation(libs.testng)
    testImplementation(libs.lang)

    // Code generation
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

sourceSets {
    main {
        blossom {
            javaSources {
                property("group", project.group.toString())
                property("name", rootProject.name)
                property("version", project.version.toString())
            }
        }
    }
}

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}-${project.version}.jar")
    }
    processResources {
        expandProperties("plugin.yml")
    }
    javadoc {
        val v = libs.versions
        applyLinks(
            "https://docs.oracle.com/en/java/javase/11/docs/api/",
            "https://hub.spigotmc.org/javadocs/spigot/",
            "https://jd.advntr.dev/platform/bukkit/${v.adventureBukkit.get()}",
            "https://lib.alpn.cloud/javadoc/alpine-public/dev/tomwmth/configlib-spigot/${v.configlib.get()}/raw/",
            "https://repo.panda-lang.org/javadoc/releases/dev/rollczi/litecommands-bukkit/${v.litecommands.get()}/raw/",
        )
    }
}