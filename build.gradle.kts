plugins {
    kotlin("jvm") version "2.3.0"
    id("org.jetbrains.intellij.platform") version "2.5.0"
}

group = "valentyn.deshel"
version = "1.0.0"

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaUltimate("2024.3.1")
    }
}

kotlin {
    jvmToolchain(17)
}

intellijPlatform {
    pluginConfiguration {
        id.set("valentyn.deshel.studylogger")
        name.set("Study Integrity Logger")
        version.set(project.version.toString())

        ideaVersion {
            sinceBuild.set("243")
        }
    }
}