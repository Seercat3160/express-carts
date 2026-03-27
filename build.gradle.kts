plugins {
    id("net.fabricmc.fabric-loom")
    id("me.modmuss50.mod-publish-plugin")
}

version = "${rootProject.version}+${sc.current.version}"
group = property("mod.group").toString()

base.archivesName = "${property("mod.id")}-fabric"

repositories {
    maven("https://maven.nucleoid.xyz")

    // https://github.com/xpple/BetterConfig/
    maven("https://maven.xpple.dev/maven2")
}

dependencies {
    fun modInclude(dep: Any): Dependency? = implementation(include(dep)!!)

    minecraft("com.mojang:minecraft:${sc.current.version}")
    implementation("net.fabricmc:fabric-loader:${property("deps.fabric.loader")}")

    implementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric.api")}")

    modInclude("eu.pb4:polymer-core:${property("deps.polymer")}")
    modInclude("eu.pb4:polymer-resource-pack:${property("deps.polymer")}")
    modInclude("xyz.nucleoid:server-translations-api:${property("deps.server_translations")}")

    modInclude("dev.xpple:betterconfig-fabric:${property("deps.betterconfig")}")
}

tasks {
    processResources {
        val props = mapOf(
            "id" to project.property("mod.id"),
            "version" to project.version,
            "minecraft" to sc.current.version,
            "fabricloader" to project.property("deps.fabric.loader"),
            "fabricapi" to project.property("deps.fabric.api"),
            "java" to requiredJava.majorVersion
        )

        inputs.properties(props)

        filesMatching("fabric.mod.json") {
            expand(props)
        }

        val mixinJava = "JAVA_${requiredJava.majorVersion}"
        filesMatching("*.mixins.json") { expand("java" to mixinJava) }
    }

    jar {
        from("LICENSE") {
            rename { "${it}_${base.archivesName.get()}" }
        }
    }

    // Builds the version into a shared folder in `build/libs/${mod version}/`
    register<Copy>("buildAndCollect") {
        group = "build"
        from(jar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }
}

loom {
    splitEnvironmentSourceSets()

    mods {
        register(property("mod.id").toString()) {
            sourceSet(sourceSets["main"])
        }
    }

    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json") // Useful for interface injection
//    accessWidenerPath = sc.process(
//        rootProject.file("src/main/resources/template.ct"),
//        "build/processed.ct"
//    )
    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1") // Adds names to lambdas - useful for mixins
    }

    runConfigs.all {
        ideConfigGenerated(true)
        vmArgs("-Dmixin.debug.export=true") // Exports transformed classes for debugging
        runDir = "../../run" // Shares the run directory between versions
    }

}

val requiredJava = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    sc.current.parsed >= "1.20.6" -> JavaVersion.VERSION_21
    sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
    sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
    else -> JavaVersion.VERSION_1_8
}

java {
    withSourcesJar()

    sourceCompatibility = requiredJava
    targetCompatibility = requiredJava
}

val readmeText = rootProject.providers.fileContents(rootProject.layout.projectDirectory.file("README.md")).asText
val changelogText = rootProject.providers.fileContents(rootProject.layout.projectDirectory.file("CHANGELOG.md")).asText

publishMods {
    changelog = changelogText
    type = STABLE
    file = tasks.jar.flatMap { it.archiveFile }
    displayName = "${rootProject.version} for ${sc.current.version} Fabric"

    dryRun = !hasProperty("publish.release")

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_API_KEY")
        projectId = providers.gradleProperty("publish.modrinth.id")
        minecraftVersions.addAll(property("publish.modrinth.minecraft").toString().split(' '))

        projectDescription = readmeText

        requires("fabric-api")
        embeds("polymer")
        embeds("betterconfig")
    }

    github {
        accessToken = providers.environmentVariable("GITHUB_TOKEN")

        // We want to publish only one GitHub release per mod version, containing all the different Minecraft version jars
        parent(project(":").tasks.named("publishGithub"))
    }
}

tasks.named("publishMods") {
    onlyIf {
        changelogText.isPresent && readmeText.isPresent
    }
}