import java.util.*

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.0.0-Beta4"
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
    type.set("IU")
}

//includeTransitiveDependencies = true
dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1-Beta")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
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
        if (p.containsKey("AndroidStudioLocation")) {
            runIde {
                ideDir.set(file(p.getProperty("AndroidStudioLocation")))
            }
        }
    }
}
