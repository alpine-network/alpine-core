plugins {
    id("core.build-logic")
}

val published = setOf("alpinecore-common", "alpinecore-bukkit", "alpinecore-paper")

subprojects {
    apply {
        plugin("core.base-conventions")
        plugin("core.spotless-conventions")
    }

    if (name in published) {
        apply(plugin = "core.maven-conventions")
    }
}
