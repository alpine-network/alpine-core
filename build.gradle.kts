import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("net.kyori.blossom") version "1.2.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = this.compileGroup()
version = this.compileVersion()

val properties = mapOf(
    "pluginVersion" to version,
    "pluginName" to project.properties["plugin_name"],
    "pluginDescription" to project.properties["plugin_description"],
    "pluginWebsite" to project.properties["plugin_website"],
    "group" to group
)

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
    maven("https://lib.alpn.cloud/alpine-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.panda-lang.org/releases")
}

configurations {
    configureEach {
        exclude("org.slf4j")
        exclude("junit")
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:${project.properties["spigot_version"]}")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("com.google.guava:guava:33.0.0-jre")
    compileOnly("com.google.code.gson:gson:2.10.1")
    testImplementation("org.testng:testng:7.5.1") // v7.6+ requires JDK 11
    testImplementation("commons-lang:commons-lang:2.6")

    shade(this, "org.apache.commons:commons-dbcp2:2.12.0")

    shade(this, "org.jetbrains:annotations:24.1.0")
    shade(this, "de.exlll:configlib-spigot:4.2.0")
    shade(this, "com.github.cryptomorin:XSeries:9.9.0")

    val liteCommands = "3.4.0"
    shade(this, "dev.rollczi:litecommands-bukkit:$liteCommands")
    shade(this, "dev.rollczi:litecommands-adventure-platform:$liteCommands")

    val adventure = "4.16.0"
    shade(this, "net.kyori:adventure-platform-bukkit:4.3.0")
    shade(this, "net.kyori:adventure-api:$adventure")
    shade(this, "net.kyori:adventure-text-minimessage:$adventure")
    shade(this, "net.kyori:adventure-text-serializer-plain:$adventure")

    val lombok = "org.projectlombok:lombok:1.18.30"
    compileOnly(lombok)
    annotationProcessor(lombok)
}

blossom {
    properties.forEach {
        replaceToken("\${${it.key}}", it.value)
    }
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<Jar> {
    // Rename JAR
    archiveFileName.set("${project.properties["plugin_name"]}-$version.jar")

    // Add exclusions
    exclude("META-INF/versions/")
    exclude("META-INF/maven/")

    manifest {
        attributes(
            "Manifest-Version" to "1.0",
            "Created-By" to "Gradle",
            "Built-JDK" to "${System.getProperty("java.version")} (${System.getProperty("java.vendor")})",
            "Built-By" to System.getProperty("user.name"),
            "Built-Date" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date()),
            "Name" to project.group.toString().replace(".", "/"),
            "Implementation-Title" to project.properties["plugin_name"],
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "Crystal Development, LLC.",
            "Specification-Title" to project.properties["plugin_name"],
            "Specification-Version" to project.version,
            "Specification-Vendor" to "Crystal Development, LLC.",
        )
    }
}

tasks.getByName<Jar>("sourcesJar") {
    archiveFileName.set("${project.properties["plugin_name"]}-$version-sources.jar")
}

tasks.withType<ShadowJar> {
    dependsOn("jar")
    outputs.upToDateWhen { false }

    // Add shaded dependencies
    configurations.clear()
    configurations.add(project.configurations.getByName("shadow"))

    // Rename shaded jar
    archiveFileName.set("${project.properties["plugin_name"]}-$version-shaded.jar")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    inputs.properties(properties)

    filesMatching("plugin.yml") {
        expand(properties)
    }

    from(".") {
        include("LICENSE")
        into("META-INF/")
    }
}

tasks.withType<Test> {
    useTestNG()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            // Set the unshaded JAR as the main artifact
            artifact(tasks["jar"])

            // Add sources as secondary artifact
            artifact(tasks["sourcesJar"]) {
                classifier = "sources"
            }

            pom {
                name.set(project.properties["plugin_name"] as String)
                description.set(project.properties["plugin_description"] as String)
                url.set(project.properties["plugin_website"] as String)

                groupId = project.properties["maven_group"] as String
                artifactId = project.properties["maven_artifact"] as String
                version = compileVersion()
                packaging = "jar"

                withXml {
                    val dependenciesNode = asNode().appendNode("dependencies")

                    project.configurations["api"].allDependencies.forEach { dependency ->
                        val dependencyNode = dependenciesNode.appendNode("dependency")
                        dependencyNode.appendNode("groupId", dependency.group)
                        dependencyNode.appendNode("artifactId", dependency.name)
                        dependencyNode.appendNode("version", dependency.version)
                        dependencyNode.appendNode("scope", "compile")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            name = "AlpineCloud"
            url = uri("https://lib.alpn.cloud/alpine-public")
            credentials {
                username = System.getenv("ALPINE_MAVEN_NAME")
                password = System.getenv("ALPINE_MAVEN_SECRET")
            }
        }
    }
}

tasks.withType<Javadoc> {
    options {
        this as StandardJavadocDocletOptions
        stylesheetFile = File(projectDir, "/dracula-stylesheet.css")
    }
    setDestinationDir(File(projectDir, "/docs/"))
    include(project.group.toString().replace(".", "/") + "/framework/**/*.java")
    include(project.group.toString().replace(".", "/") + "/integration/**/*.java")
    include(project.group.toString().replace(".", "/") + "/util/**/*.java")
    include(project.group.toString().replace(".", "/") + "/AlpinePlugin.java")
}

subprojects {
    tasks.withType<Javadoc> {
        enabled = false
    }
}

fun compileGroup(): String {
    return "${project.properties["maven_group"]}.${project.properties["maven_artifact"]}"
}

fun compileVersion(): String {
    val major = project.properties["version_major"]
    val minor = project.properties["version_minor"]
    val patch = project.properties["version_patch"]
    val preRelease = project.properties["version_pre_release"]
    return "${major}.${minor}.${patch}${if (preRelease == "none") "" else preRelease}"
}

fun shade(scope: DependencyHandlerScope, dependency: String) {
    scope.implementation(dependency)
    scope.api(dependency)
    scope.shadow(dependency)
}