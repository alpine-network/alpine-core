import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    alias(libs.plugins.blossom)
    alias(libs.plugins.shadow)
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
    maven("https://repo.helpch.at/releases/")
}

configurations {
    configureEach {
        exclude("org.slf4j")
        exclude("junit")
    }
}

dependencies {
    // Provided/optional dependencies
    compileOnly(libs.spigot.api)
    compileOnly(libs.vault.api)
    compileOnly(libs.placeholderapi)
    compileOnly(libs.gson)

    // Bundled dependencies
    shade(this, libs.annotations)
    shade(this, libs.configlib.spigot)
    shade(this, libs.xseries)
    shade(this, libs.commons.dbcp2)
    shade(this, libs.localelib)

    shade(this, libs.bundles.litecommands)
    shade(this, libs.bundles.adventure)

    // Testing dependencies
    testImplementation(libs.testng)
    testImplementation(libs.commons.lang)

    // Code generation
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
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

tasks.shadowJar {
    dependsOn("jar")
    outputs.upToDateWhen { false }

    // Add shaded dependencies
    configurations.clear()
    configurations.add(project.configurations.getByName("shadow"))

    // Rename shaded jar
    archiveFileName.set("${project.properties["plugin_name"]}-$version-shaded.jar")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.getByName<Jar>("sourcesJar") {
    archiveFileName.set("${project.properties["plugin_name"]}-$version-sources.jar")
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
                        if (dependency.name != "LocaleLib") {
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
    }
    repositories {
        maven {
            name = "AlpineCloud"
            url = uri("https://lib.alpn.cloud" + if (isSnapshot()) "/snapshots" else "/alpine-public")
            credentials {
                username = System.getenv("ALPINE_MAVEN_NAME")
                password = System.getenv("ALPINE_MAVEN_SECRET")
            }
        }
    }
}

tasks.javadoc {
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

tasks.register("writeVersion") {
    doLast {
        File(".version").writeText(compileVersion())
    }
}

subprojects {
    tasks.withType<Javadoc> {
        enabled = false
    }
}

fun isSnapshot(): Boolean {
    return (project.findProperty("snapshot") as? String)?.toBoolean() ?: false
}

fun compileGroup(): String {
    return "${project.properties["maven_group"]}.${project.properties["maven_artifact"]}"
}

fun compileVersion(): String {
    val major = project.properties["version_major"]
    val minor = project.properties["version_minor"]
    val patch = project.properties["version_patch"]
    return "${major}.${minor}.${patch}${if (isSnapshot()) "-SNAPSHOT" else ""}"
}

fun <T> shade(scope: DependencyHandlerScope, dependency: Provider<T?>) where T : Any {
    scope.implementation(dependency)
    scope.api(dependency)
    scope.shadow(dependency)
}