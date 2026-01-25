plugins {
    java
    id("com.gradleup.shadow") version "8.3.0"
}

group = "me.lukiiy"
version = "1.0-b1.2_01"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(files("lib/cb-b1.2_01.jar"))
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        mergeServiceFiles()
        minimize()
    }

    jar { enabled = false }

    build { dependsOn("shadowJar") }

    processResources {
        val props = mapOf("version" to version)

        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

val targetJava = 8

java {
    val javaVersion = JavaVersion.toVersion(targetJava)

    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion

    if (JavaVersion.current() < javaVersion) toolchain.languageVersion.set(JavaLanguageVersion.of(targetJava))
}