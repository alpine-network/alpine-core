plugins {
    id("java")
}

group = "dev.tomwmth.exampleplugin"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://lib.alpn.cloud/alpine-public/")
    maven("https://repo.viaversion.com/")
    maven("https://repo.panda-lang.org/releases")
    maven("https://jitpack.io/")
}

dependencies {
    compileOnly(libs.spigot.api)
    compileOnly("com.viaversion:viaversion-api:5.0.2")
    implementation(rootProject)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

tasks.withType<Jar>().configureEach {
    // Rename jar
//    archiveFileName.set("ExamplePlugin-$version.jar")

    // Add exclusions
    exclude("META-INF/versions/")
    exclude("META-INF/maven/")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}