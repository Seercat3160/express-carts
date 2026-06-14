pluginManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.fabricmc.net/") { name = "Fabric" }
		maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
	}

	plugins {
		id("me.modmuss50.mod-publish-plugin").version(providers.gradleProperty("plugins.mod-publish-plugin")).apply(false)
		id("dev.kikugie.stonecutter").version(providers.gradleProperty("plugins.stonecutter")).apply(false)
		id("dev.kikugie.loom-back-compat").version(providers.gradleProperty("plugins.loom-back-compat")).apply(false)
	}
}

plugins {
	id("dev.kikugie.stonecutter")
	id("dev.kikugie.loom-back-compat")
}

stonecutter {
	create(rootProject) {
		versions("1.21.11", "26.1", "26.2")
		vcsVersion = "26.2"
	}
}

rootProject.name = "express-carts"
