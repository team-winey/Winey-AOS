import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id(ModulePlugins.androidApplication)
    id(ModulePlugins.kotlinAndroid)
    id(ModulePlugins.kotlinParcelize)
    id(ModulePlugins.kotlinKapt)
    id(ModulePlugins.kotlinSerialization)
    id(ModulePlugins.hilt)
    id(ModulePlugins.googleService)
    id(ModulePlugins.firebaseAppDistribution)
    id(ModulePlugins.firebaseCrashlytics)
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
            gradleLocalProperties(rootDir).getProperty("auth.base.url")
        )
        buildConfigField(
            "String",
            "KAKAO_NATIVE_KEY",
            gradleLocalProperties(rootDir).getProperty("kakao.native.key")
        )
        buildConfigField(
            "String",
            "AMPLITUDE_API_KEY",
            gradleLocalProperties(rootDir).getProperty("amplitude.api.key")
        )

        manifestPlaceholders["KAKAO_NATIVE_KEY"] = gradleLocalProperties(rootDir).getProperty("kakaoNativeKey")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            keyAlias = gradleLocalProperties(rootDir).getProperty("keyAlias")
            keyPassword = gradleLocalProperties(rootDir).getProperty("keyPassword")
            storeFile = file("winey.jks")
            storePassword = gradleLocalProperties(rootDir).getProperty("storePassword")
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
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
        viewBinding = true
        dataBinding = true
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
        implementation(swipeRefreshLayout)
        implementation(hilt)
        implementation(activity)
        implementation(fragment)
        implementation(legacy)
        implementation(lifecycleKtx)
        implementation(lifecycleLiveDataKtx)
        implementation(lifecycleViewModelKtx)
        implementation(lifecycleJava8)
        implementation(lifecycleService)
        implementation(splashScreen)
        implementation(pagingRuntime)
        implementation(exif)
        implementation(dataStore)
        implementation(dataStoreCore)
    }

    TestDependencies.run {
        testImplementation(jUnit)
        androidTestImplementation(androidTest)
        androidTestImplementation(espresso)
    }

    KaptDependencies.run {
        kapt(hiltAndroidCompiler)
    }

    GoogleDependencies.run {
        implementation(materialDesign)
        implementation(inAppUpdate)
        implementation(inAppUpdateKtx)
    }

    ThirdPartyDependencies.run {
        implementation(coil)
        implementation(platform(okHttpBom))
        implementation(okHttp)
        implementation(okHttpLoggingInterceptor)
        implementation(retrofit)
        implementation(retrofitJsonConverter)
        implementation(timber)
        implementation(progressView)
        implementation(lottie)
        implementation(circleImageView)
        implementation(kakaoLogin)
        implementation(amplitude)
    }

    FirebaseDependencies.run {
        implementation(platform(bom))
        implementation(messaging)
        implementation(analytics)
        implementation(crashlytics)
    }
}
