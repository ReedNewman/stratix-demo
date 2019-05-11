import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

val kotlinVersion = plugins.getPlugin(KotlinPluginWrapper::class.java).kotlinPluginVersion

plugins {
	kotlin("jvm") version "1.3.30"
}

group = "com.stratix"
version = "0.0.1-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	implementation(kotlin("stdlib"))
	implementation(kotlin("stdlib-jdk8"))
	implementation(kotlin("reflect"))
}
/*
compileKotlin {
	kotlinOptions {
		freeCompilerArgs = ['-Xjsr305=strict']
		jvmTarget = '1.8'
	}
}

compileTestKotlin {
	kotlinOptions {
		freeCompilerArgs = ['-Xjsr305=strict']
		jvmTarget = '1.8'
	}
}
 */
