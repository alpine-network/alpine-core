plugins {
    idea
    `kotlin-dsl`
}

dependencies {
    implementation(libs.blossom.plugin)
    implementation(libs.idea.ext.plugin)
    implementation(libs.hangar.publish.plugin)
    implementation(libs.maven.publish.plugin)
    implementation(libs.minotaur.publish.plugin)
    implementation(libs.shadow.plugin)
    implementation(libs.spotless.plugin)

    // https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}