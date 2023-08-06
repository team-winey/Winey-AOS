import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id(ModulePlugins.androidApplication)
    id(ModulePlugins.kotlinAndroid)
    id(ModulePlugins.kotlinParcelize)
    id(ModulePlugins.kotlinKapt)
    id(ModulePlugins.kotlinSerialization)
    id(ModulePlugins.hilt)
    id(ModulePlugins.oss)
}

android {
    namespace = DefaultConfig.packageName
    compileSdk = DefaultConfig.compileSdk

    defaultConfig {
        applicationId = DefaultConfig.packageName
        minSdk = DefaultConfig.minSdk
        targetSdk = DefaultConfig.targetSdk
        versionCode = DefaultConfig.versionCode
        versionName = DefaultConfig.versionName

        buildConfigField(
            "String",
            "AUTH_BASE_URL",
            gradleLocalProperties(rootDir).getProperty("auth.base.url"),
        )
        buildConfigField(
            "String",
            "KAKAO_NATIVE_KEY",
            gradleLocalProperties(rootDir).getProperty("kakao.native.key"),
        )

        manifestPlaceholders["KAKAO_NATIVE_KEY"] = gradleLocalProperties(rootDir).getProperty("kakao.native.key.quote")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = Versions.javaVersion
        targetCompatibility = Versions.javaVersion
    }

    kotlinOptions {
        jvmTarget = Versions.jvmVersion
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}

dependencies {
    KotlinDependencies.run {
        implementation(kotlin)
        implementation(coroutines)
        implementation(jsonSerialization)
        implementation(dateTime)
    }

    AndroidXDependencies.run {
        implementation(coreKtx)
        implementation(appCompat)
        implementation(constraintLayout)
        implementation(startup)
        implementation(hilt)
        implementation(activity)
        implementation(fragment)
        implementation(legacy)
        implementation(security)
        implementation(lifecycleKtx)
        implementation(lifecycleLiveDataKtx)
        implementation(lifecycleViewModelKtx)
        implementation(lifecycleJava8)
        implementation(splashScreen)
        implementation(pagingRuntime)
        implementation(workManager)
        implementation(hiltWorkManager)
        implementation(exif)
        implementation(dataStore)
        implementation(dataStoreCore)
    }

    TestDependencies.run {
        testImplementation(jUnit)
        androidTestImplementation(androidTest)
        androidTestImplementation(espresso)
    }

    implementation(MaterialDesignDependencies.materialDesign)

    KaptDependencies.run {
        kapt(hiltAndroidCompiler)
        kapt(hiltWorkManagerCompiler)
    }

    ThirdPartyDependencies.run {
        implementation(coil)
        implementation(platform(okHttpBom))
        implementation(okHttp)
        implementation(okHttpLoggingInterceptor)
        implementation(retrofit)
        implementation(retrofitJsonConverter)
        implementation(timber)
        implementation(ossLicense)
        implementation(progressView)
        implementation(balloon)
        implementation(lottie)
        implementation(circleImageView)
        implementation(kakao)

        debugImplementation(flipper)
        debugImplementation(flipperNetwork)
        debugImplementation(flipperLeakCanary)
        debugImplementation(soloader)
    }
}
