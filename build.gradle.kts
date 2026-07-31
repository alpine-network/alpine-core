plugins {
    id("core.build-logic")
}

subprojects {
    apply {
        plugin("core.base-conventions")
        plugin("core.maven-conventions")
        plugin("core.spotless-conventions")
    }
}