object KotlinDependencies {
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinVersion}"
    const val coroutines =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutinesAndroidVersion}"
    const val jsonSerialization =
        "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinSerializationJsonVersion}"
    const val dateTime = "org.jetbrains.kotlinx:kotlinx-datetime:${Versions.kotlinDateTimeVersion}"
}

object AndroidXDependencies {
    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtxVersion}"
    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompatVersion}"
    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayoutVersion}"
    const val startup = "androidx.startup:startup-runtime:${Versions.appStartUpVersion}"
    const val hilt = "com.google.dagger:hilt-android:${Versions.hiltVersion}"
    const val activity = "androidx.activity:activity-ktx:${Versions.activityKtxVersion}"
    const val fragment = "androidx.fragment:fragment-ktx:${Versions.fragmentKtxVersion}"
    const val legacy = "androidx.legacy:legacy-support-v4:${Versions.legacySupportVersion}"
    const val security = "androidx.security:security-crypto:${Versions.securityVersion}"
    const val lifecycleKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleVersion}"
    const val lifecycleLiveDataKtx =
        "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycleVersion}"
    const val lifecycleViewModelKtx =
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycleVersion}"
    const val lifecycleJava8 =
        "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycleVersion}"
    const val splashScreen = "androidx.core:core-splashscreen:${Versions.splashVersion}"
    const val pagingRuntime = "androidx.paging:paging-runtime:${Versions.pagingVersion}"
    const val workManager = "androidx.work:work-runtime-ktx:${Versions.workManagerVersion}"
    const val hiltWorkManager = "androidx.hilt:hilt-work:1.0.0"
    const val exif = "androidx.exifinterface:exifinterface:${Versions.exifVersion}"
}

object TestDependencies {
    const val jUnit = "junit:junit:${Testing.junitVersion}"
    const val androidTest = "androidx.test.ext:junit:${Testing.androidTestVersion}"
    const val espresso = "androidx.test.espresso:espresso-core:${Testing.espressoVersion}"
}

object MaterialDesignDependencies {
    const val materialDesign =
        "com.google.android.material:material:${Versions.materialDesignVersion}"
}

object KaptDependencies {
    const val hiltAndroidCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hiltVersion}"
    const val hiltWorkManagerCompiler = "androidx.hilt:hilt-compiler:1.0.0"
}

object ThirdPartyDependencies {
    const val coil = "io.coil-kt:coil:${Versions.coilVersion}"

    const val okHttpBom = "com.squareup.okhttp3:okhttp-bom:${Versions.okHttpVersion}"
    const val okHttp = "com.squareup.okhttp3:okhttp"
    const val okHttpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofitVersion}"
    const val retrofitJsonConverter =
        "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:${Versions.kotlinSerializationConverterVersion}"

    const val timber = "com.jakewharton.timber:timber:${Versions.timberVersion}"
    const val ossLicense =
        "com.google.android.gms:play-services-oss-licenses:${Versions.ossVersion}"
    const val progressView = "com.github.skydoves:progressview:${Versions.progressViewVersion}"
    const val balloon = "com.github.skydoves:balloon:${Versions.balloonVersion}"
    const val lottie = "com.airbnb.android:lottie:${Versions.lottieVersion}"

    const val flipper = "com.facebook.flipper:flipper:${Versions.flipperVersion}"
    const val flipperNetwork =
        "com.facebook.flipper:flipper-network-plugin:${Versions.flipperVersion}"
    const val flipperLeakCanary =
        "com.facebook.flipper:flipper-leakcanary2-plugin:${Versions.flipperVersion}"
    const val leakCanary =
        "com.squareup.leakcanary:leakcanary-android:${Versions.leakCanaryVersion}"
    const val soloader = "com.facebook.soloader:soloader:${Versions.soloaderVersion}"
    const val circleImageView = "de.hdodenhof:circleimageview:${Versions.circleImageViewVersion}"
    const val kakao = "com.kakao.sdk:v2-user:${Versions.kakao}"
}

object FirebaseDependencies {
    const val bom = "com.google.firebase:firebase-bom:30.1.0"
    const val messaging = "com.google.firebase:firebase-messaging-ktx"
    const val crashlytics = "com.google.firebase:firebase-crashlytics-ktx"
    const val analytics = "com.google.firebase:firebase-analytics-ktx"
    const val remoteConfig = "com.google.firebase:firebase-config-ktx"
}
