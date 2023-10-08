object ClassPathPlugins {
    const val gradle = "com.android.tools.build:gradle:${Versions.gradleVersion}"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}"
    const val hilt = "com.google.dagger:hilt-android-gradle-plugin:${Versions.hiltVersion}"
    const val oss = "com.google.android.gms:oss-licenses-plugin:${Versions.ossPluginVersion}"
}

object ProjectPlugins {
    const val ktlint = "org.jlleitschuh.gradle.ktlint"
    const val kotlinSerialization = "org.jetbrains.kotlin.plugin.serialization"
}

object ModulePlugins {
    const val androidApplication = "com.android.application"
    const val kotlinAndroid = "org.jetbrains.kotlin.android"
    const val kotlinKapt = "kotlin-kapt"
    const val kotlinParcelize = "kotlin-parcelize"
    const val kotlinSerialization = "kotlinx-serialization"
    const val hilt = "dagger.hilt.android.plugin"
    const val oss = "com.google.android.gms.oss-licenses-plugin"
}
