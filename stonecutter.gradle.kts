plugins {
    id("dev.kikugie.stonecutter")
    id("me.modmuss50.mod-publish-plugin")
}
stonecutter active "26.1"

// This base mod version is used by the subprojects (for each minecraft version), which add the MC version as a suffix.
rootProject.version = "${property("mod.version")}${localBuildVersionSuffix().get()}"

// If we are not performing a release build (i.e. building in CI), try to get some info about the state of the Git repo
// to add to the version (so local builds can be easily distinguished from released builds).
private fun localBuildVersionSuffix(): Provider<String> {
    // git will give the (abbreviated) commit hash, suffixed with "-dirty" if the working dir is dirty.
    // If we can't exec git, fallback to just "-local"
    val gitCommitProvider = providers.exec {
        commandLine("git", "describe", "--always", "--dirty", "--exclude", "*")
    }.standardOutput.asText.map {
        "-${it.trim()}"
    }.orElse("-local")

    return providers.gradleProperty("publish.release").map {
        // If this is called, the "publish.release" property is present.
        // Return an empty string.
        return@map ""
    }.orElse(gitCommitProvider)
}

val readmeText = rootProject.providers.fileContents(rootProject.layout.projectDirectory.file("README.md")).asText
val changelogText = rootProject.providers.fileContents(rootProject.layout.projectDirectory.file("CHANGELOG.md")).asText

// The root project creates a GitHub release, to which the subprojects upload the versioned mod jars.
// Modrinth uploads are handled by each subproject individually.
publishMods {
    version = rootProject.version.toString()
    changelog = changelogText
    type = STABLE

    displayName = rootProject.version.toString()

    dryRun = !hasProperty("publish.release")

    github {
        accessToken = providers.environmentVariable("GITHUB_TOKEN")

        repository = providers.environmentVariable("GITHUB_REPOSITORY_ID").orElse(providers.gradleProperty("publish.github.repository"))
        commitish = providers.environmentVariable("GITHUB_REF").orElse(providers.gradleProperty("publish.github.commitish"))

        tagName = rootProject.version.toString()

        allowEmptyFiles = true
    }
}

tasks.named("publishMods") {
    onlyIf {
        changelogText.isPresent && readmeText.isPresent
    }
}