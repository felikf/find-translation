plugins {
    id("org.jetbrains.intellij") version "1.16.1"
    kotlin("jvm") version "1.9.24"
}

group = "cz.findtranslation"
version = "0.1.0"

repositories {
    mavenCentral()
}

intellij {
    version.set("2024.1")
    type.set("IU")
    plugins.set(listOf("JavaScript", "com.intellij.modules.json", "org.jetbrains.plugins.yaml"))
}

dependencies {
    implementation(kotlin("stdlib"))
}

tasks {
    patchPluginXml {
        sinceBuild.set("241")
        untilBuild.set("242.*")
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}
