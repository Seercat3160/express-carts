import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("net.fabricmc.fabric-loom-remap")
    id("me.modmuss50.mod-publish-plugin")
}

// Functions to make properties more pleasant to use
class ModProps {
    fun get(key: String): String {
        return providers.gradleProperty("mod.$key").get()
    }

    val id: String = get("id")
    val version: String = providers.gradleProperty("mod.version").zip(localBuildVersionSuffix()) { version, suffix ->
        "$version$suffix"
    }.get()
    val group: String = get("group")
}

val mod = ModProps()

fun dep(key: String): String {
    return providers.gradleProperty("deps.$key").get()
}

val releaseBuild = hasProperty("publish.release")

version = "${mod.version}+${dep("minecraft")}"
group = mod.group

base.archivesName = "${mod.id}-fabric"

repositories {
    maven("https://maven.nucleoid.xyz")

    // https://github.com/xpple/BetterConfig/
    maven("https://maven.xpple.dev/maven2")
}

loom {
    splitEnvironmentSourceSets()

    mods {
        register(mod.id) {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${dep("minecraft")}")
    modImplementation("net.fabricmc:fabric-loader:${dep("fabric.loader")}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${dep("fabric.api")}")

    mappings(loom.officialMojangMappings())

    modImplementation(include("eu.pb4:polymer-core:${dep("polymer")}")!!)
    modImplementation(include("eu.pb4:polymer-resource-pack:${dep("polymer")}")!!)
    modImplementation(include("xyz.nucleoid:server-translations-api:${dep("server_translations")}")!!)

    modImplementation(include("dev.xpple:betterconfig-fabric:${dep("betterconfig")}")!!)

    implementation(annotationProcessor(include("io.github.llamalad7:mixinextras-fabric:${dep("mixinextras")}")!!)!!)
}

tasks {
    processResources {
        val props = mapOf(
            "version" to project.version,
            "minecraft_version" to dep("minecraft"),
            "loader_version" to dep("fabric.loader")
        )

        inputs.properties(props)

        filesMatching("fabric.mod.json") {
            expand(props)
        }
    }

    jar {
        from("LICENSE") {
            rename { "${it}_${base.archivesName.get()}" }
        }
    }
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

val changelogFile = file("CHANGELOG.md")
val changelogText = if (changelogFile.canRead()) changelogFile.readText() else ""
val readmeFile = file("README.md")
val readmeText = if (readmeFile.canRead()) readmeFile.readText() else ""

publishMods {
    changelog = changelogText
    type = STABLE
    file = tasks.named<RemapJarTask>("remapJar").flatMap { it.archiveFile }
    displayName = "${mod.version} for ${dep("minecraft")} Fabric"

    dryRun = !releaseBuild

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_API_KEY")
        projectId = providers.gradleProperty("publish.modrinth.id")
        minecraftVersions.add(providers.gradleProperty("publish.modrinth.minecraft"))

        projectDescription = readmeText

        requires("fabric-api")
        embeds("polymer")
        embeds("betterconfig")
    }

    github {
        accessToken = providers.environmentVariable("GITHUB_TOKEN")
        repository = providers.environmentVariable("GITHUB_REPOSITORY_ID").orElse(providers.gradleProperty("publish.github.repository"))
        commitish = providers.environmentVariable("GITHUB_REF").orElse(providers.gradleProperty("publish.github.commitish"))
    }
}

tasks.named("publishMods") {
    onlyIf {
        // We don't require mayRelease == true, as in that case we set dryRun = true for publishMods
        !changelogText.isBlank() && !readmeText.isBlank()
    }
}

// If we are not performing a release build (i.e. building in CI), try to get some info about the state of the Git repo
// to add to the version (so local builds can be easily distinguished from released builds).
private fun localBuildVersionSuffix(): Provider<String> {
    // git will give the (abbreviated) commit hash, suffixed with "-dirty" if the working dir is dirty.
    // If we can't exec git, fallback to just "-local"
    val gitCommitProvider = providers.exec {
        commandLine("git", "describe", "--always", "--dirty", "--exclude", "*")
    }.standardOutput.asText.map {
        "-$it"
    }.orElse("-local")

    return providers.gradleProperty("publish.release").map {
        // If this is called, the "publish.release" property is present.
        // Return an empty string.
        return@map ""
    }.orElse(gitCommitProvider)
}