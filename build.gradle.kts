@file:Suppress("ConvertLambdaToReference")

val modId: String by project
val modName: String by project
val modDescription: String by project
val modAuthor: String by project
val modVersion: String by project
val modLicense: String by project
val modSourcesURL: String by project
val modIssuesURL: String by project

val minecraftVersion: String by project
val fabricVersion: String by project
val mixinVersion: String by project
val clothConfigVersion: String by project
val modmenuVersion: String by project
val immediatelyfastVersion: String by project

val modNameStripped = modName.replace(" ", "")

plugins {
	id("net.fabricmc.fabric-loom") version "1.16-SNAPSHOT"
	id("maven-publish")
}

repositories {
	mavenCentral()
	maven {
		url = uri("https://maven.shedaniel.me/")
	}
	maven {
		url = uri("https://maven.terraformersmc.com/")
	}
	maven {
		url = uri("https://api.modrinth.com/maven")
	}
}

base {
	archivesName.set(modNameStripped)
}

version = "$minecraftVersion+v$modVersion"
group = "me.$modAuthor.$modId"

dependencies {
	minecraft("com.mojang:minecraft:$minecraftVersion")
	implementation("net.fabricmc:fabric-loader:$fabricVersion")

	// Fabric API
	implementation("net.fabricmc.fabric-api:fabric-api:0.150.0+26.1.2")

	// Cloth Config
	implementation("me.shedaniel.cloth:cloth-config-fabric:$clothConfigVersion") {
		exclude(group = "net.fabricmc.fabric-api")
	}

	// ModMenu (optional)
	compileOnlyApi("com.terraformersmc:modmenu:$modmenuVersion")

	compileOnly("net.fabricmc:sponge-mixin:$mixinVersion")
	implementation("com.google.code.findbugs:jsr305:3.0.2")
	implementation("org.jetbrains:annotations:24.1.0")
}

tasks.processResources {
	inputs.property("version", project.version)

	filesMatching("fabric.mod.json") {
		expand("version" to project.version)
	}
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
	options.release.set(25)
}

java {
	withSourcesJar()

	toolchain {
		languageVersion.set(JavaLanguageVersion.of(25))
	}
}

tasks.jar {
	from("LICENSE") {
		rename { "${it}_${base.archivesName.get()}" }
	}
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])
		}
	}

	repositories {
		// Add publishing repositories here if needed
	}
}