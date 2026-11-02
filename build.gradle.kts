import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("net.fabricmc.fabric-loom")
    kotlin("jvm")
    `maven-publish`
    idea
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

group = property("maven_group") as String
version = property("mod_version") as String

repositories {
    google()
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    implementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
    
    runtimeOnly("me.djtheredstoner:DevAuth-fabric:${property("devauth_version")}")
    implementation("com.github.stivais:Commodore:${property("commodore_version")}")
    
    implementation("com.github.odtheking:odinFabric:${property("odin_version")}")
    //implementation(files("libs/${File("libs").listFiles()?.get(0)?.name}"))
    //swap to local if jitpack breaks again
    
    property("minecraft_lwjgl_version").let { lwjglVersion ->
        implementation("org.lwjgl:lwjgl-nanovg:$lwjglVersion")
        
        listOf("windows", "linux", "macos", "macos-arm64").forEach { os ->
            implementation("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-$os")
        }
    }
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/odinaddon.accesswidener")
    runConfigs.named("client") {
        isIdeConfigGenerated = true
        vmArgs.addAll(
            arrayOf(
                "-Dmixin.debug.export=true",
                "-Ddevauth.enabled=true",
                "-Ddevauth.account=main",
                "-XX:+AllowEnhancedClassRedefinition"
            )
        )
    }
    
    runConfigs.named("server") {
        isIdeConfigGenerated = false
    }
}

afterEvaluate {
    loom.runs.named("client") {
        vmArg("-javaagent:${configurations.compileClasspath.get().find { it.name.contains("sponge-mixin") }}")
    }
}

tasks {
    processResources {
        val props = mapOf(
            "version" to version,
            "minecraft_version" to project.property("minecraft_version"),
            "loader_version" to project.property("loader_version"),
            "odin_version" to project.property("odin_version")
        )
        
        inputs.properties(props)
        
        filesMatching("fabric.mod.json") {
            expand(props)
        }
    }
    
    compileKotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_25
            freeCompilerArgs.add("-Xlambdas=class") //Commodore
        }
    }
    
    compileJava {
        sourceCompatibility = "25"
        targetCompatibility = "25"
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked"))
    }
}

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 25
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    
    withSourcesJar()
}

fabricApi {
    configureDataGeneration {
        client = true
    }
}