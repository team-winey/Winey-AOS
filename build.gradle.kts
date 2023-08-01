// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(ClassPathPlugins.gradle)
        classpath(ClassPathPlugins.kotlinGradlePlugin)
        classpath(ClassPathPlugins.kotlinSerialization)
        classpath(ClassPathPlugins.hilt)
        classpath(ClassPathPlugins.oss)
    }
}

plugins {
    id(Plugins.ktlint) version Versions.ktlintVersion
}

allprojects {
    apply {
        plugin(Plugins.ktlint)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
