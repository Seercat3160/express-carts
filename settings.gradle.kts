pluginManagement {
	repositories {
		maven("https://maven.fabricmc.net/")
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
	}

	plugins {
		id("net.fabricmc.fabric-loom").version(providers.gradleProperty("plugins.loom")).apply(false)
		id("net.fabricmc.fabric-loom-remap").version(providers.gradleProperty("plugins.loom")).apply(false)
		id("me.modmuss50.mod-publish-plugin").version(providers.gradleProperty("plugins.mod-publish-plugin")).apply(false)
		id("dev.kikugie.stonecutter").version(providers.gradleProperty("plugins.stonecutter")).apply(false)
	}
}

plugins {
	id("dev.kikugie.stonecutter")
}

stonecutter {
	create(rootProject) {
		versions("1.21.11").buildscript("build.obfuscated.gradle.kts")
		versions("26.1").buildscript("build.gradle.kts")
		vcsVersion = "26.1"
	}
}

rootProject.name = "express-carts"
