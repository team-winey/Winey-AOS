// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.gradleVersion}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlinVersion}")
        classpath(ClassPathPlugins.hilt)
        classpath(ClassPathPlugins.oss)
        classpath("org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlinSerializationJsonVersion}")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}