plugins {
    id("core.build-logic")
}

subprojects {
    apply {
        plugin("core.base-conventions")
        plugin("core.shadow-conventions")
        plugin("core.maven-conventions")
    }
}