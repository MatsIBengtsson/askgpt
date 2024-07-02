/* This file has been modified by Mats Bengtsson.
Original file is part of the Nerdy Things AskGPT project.
*/
import java.util.*

plugins {
    id("java")
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.serialization") version "1.9.24"
    id("org.jetbrains.intellij") version "1.17.2"
}

group = "io.nerdythings.askgpt"
version = "1.2"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.2.5")
       type.set("IU") // Target IDE Platform
}

//includeTransitiveDependencies = true
dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.json:json:20230618")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("222")
        untilBuild.set("250.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }


    val propFile = project.rootProject.file("local.properties")
    if (propFile.exists()) {
        val p = Properties()
        p.load(propFile.reader())
        val pycharmLocation = p.getProperty("PyCharmLocation")
        val pyCharmProjectDir = p.getProperty("PyCharmProjectDir")
        val ideaLocation = p.getProperty("IdeaLocation")
        val ideaProjectDir = p.getProperty("IdeaProjectDir")

        if (pycharmLocation != null && pyCharmProjectDir != null) {
            register<org.jetbrains.intellij.tasks.RunIdeTask>("runPycharmIde") {
                ideDir.set(file(pycharmLocation))
                args = listOf(file(pyCharmProjectDir).absolutePath)
            }
        }

        if (ideaLocation != null && ideaProjectDir != null) {
            register<org.jetbrains.intellij.tasks.RunIdeTask>("runIdeaIde") {
                ideDir.set(file(ideaLocation))
                args = listOf(file(ideaProjectDir).absolutePath)
            }
        }
        if (p.containsKey("AndroidStudioLocation")) {
            runIde {
                ideDir.set(file(p.getProperty("AndroidStudioLocation")))
            }
        }
    }

    // Add configurations for running PyCharm and IDEA
    register("runPycharm") {
        dependsOn("runPycharmIde")
        description = "Run the IDE with PyCharm"
        group = "IDE"
    }

    register("runIdea") {
        dependsOn("runIdeaIde")
        description = "Run the IDE with IntelliJ IDEA"
        group = "IDE"
    }
}
