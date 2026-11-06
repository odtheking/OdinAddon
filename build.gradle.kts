import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("net.fabricmc.fabric-loom")
    kotlin("jvm")
}

group = property("maven_group") as String
version = property("mod_version") as String

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    maven("https://api.modrinth.com/maven")
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    implementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")

    runtimeOnly("me.djtheredstoner:DevAuth-fabric:${property("devauth_version")}")

    implementation("maven.modrinth:odin:${property("odin_version")}")
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/odinaddon.accesswidener")
    runConfigs.named("client") {
        generateRunConfig.set(true)
        jvmArguments.addAll(
            "-Dmixin.debug.export=true",
            "-Ddevauth.enabled=true",
            "-Ddevauth.account=main",
            "-Dfabric.log.disableAnsi=false",
            "-XX:StackShadowPages=32",
            "-XX:+AllowEnhancedClassRedefinition",
        )
    }

    runConfigs.named("server") {
        generateRunConfig.set(false)
    }
}

afterEvaluate {
    loom.runs.named("client") {
        jvmArguments.add("-javaagent:${configurations.compileClasspath.get().find { it.name.contains("sponge-mixin") }}")
    }
}

tasks {
    named<JavaExec>("runClient") {
        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(25)
            vendor = JvmVendorSpec.JETBRAINS
        }
    }

    processResources {
        filesMatching("fabric.mod.json") {
            expand(getProperties())
        }
    }

    compileKotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_25
        }
    }

    compileJava {
        sourceCompatibility = "25"
        targetCompatibility = "25"
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked"))
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}