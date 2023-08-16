// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(ClassPathPlugins.gradle)
        classpath(ClassPathPlugins.kotlinGradlePlugin)
        classpath(ClassPathPlugins.hilt)
        classpath(ClassPathPlugins.oss)
    }
}

plugins {
    id(ProjectPlugins.ktlint) version Versions.ktlintVersion
    id(ProjectPlugins.kotlinSerialization) version Versions.kotlinVersion
}

allprojects {
    apply {
        plugin(ProjectPlugins.ktlint)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
