plugins {
    id(Plugins.androidApplication)
    id(Plugins.kotlinAndroid)
    id(Plugins.kotlinParcelize)
    id(Plugins.kotlinKapt)
    id(Plugins.hiltPlugin)
    id(Plugins.ossLicensesPlugin)
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
        buildConfig = true
        dataBinding = true
        viewBinding = true
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
        implementation(fragment)
        implementation(startup)
        implementation(legacy)
        implementation(security)
        implementation(hilt)
        implementation(lifeCycleKtx)
        implementation(lifecycleJava8)
        implementation(splashScreen)
        implementation(pagingRuntime)
        implementation(workManager)
        implementation(hiltWorkManager)
    }

    KaptDependencies.run {
        kapt(hiltCompiler)
        kapt(hiltWorkManagerCompiler)
    }

    implementation(MaterialDesignDependencies.materialDesign)

    TestDependencies.run {
        testImplementation(jUnit)
        androidTestImplementation(androidTest)
        androidTestImplementation(espresso)
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
        debugImplementation(flipper)
        debugImplementation(flipperNetwork)
        debugImplementation(flipperLeakCanary)
        debugImplementation(leakCanary)
        debugImplementation(soloader)
    }
}