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
    const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.swipeRefreshLayoutVersion}"
    const val hilt = "com.google.dagger:hilt-android:${Versions.hiltVersion}"
    const val activity = "androidx.activity:activity-ktx:${Versions.activityKtxVersion}"
    const val fragment = "androidx.fragment:fragment-ktx:${Versions.fragmentKtxVersion}"
    const val legacy = "androidx.legacy:legacy-support-v4:${Versions.legacySupportVersion}"
    const val lifecycleKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleVersion}"
    const val lifecycleLiveDataKtx =
        "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycleVersion}"
    const val lifecycleViewModelKtx =
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycleVersion}"
    const val lifecycleJava8 =
        "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycleVersion}"
    const val lifecycleService =
        "androidx.lifecycle:lifecycle-service:${Versions.lifecycleVersion}"
    const val splashScreen = "androidx.core:core-splashscreen:${Versions.splashVersion}"
    const val pagingRuntime = "androidx.paging:paging-runtime:${Versions.pagingVersion}"
    const val exif = "androidx.exifinterface:exifinterface:${Versions.exifVersion}"
    const val dataStore = "androidx.datastore:datastore-preferences:${Versions.dataStoreVersion}"
    const val dataStoreCore = "androidx.datastore:datastore-preferences-core:${Versions.dataStoreVersion}"
}

object TestDependencies {
    const val jUnit = "junit:junit:${Testing.junitVersion}"
    const val androidTest = "androidx.test.ext:junit:${Testing.androidTestVersion}"
    const val espresso = "androidx.test.espresso:espresso-core:${Testing.espressoVersion}"
}

object KaptDependencies {
    const val hiltAndroidCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hiltVersion}"
}

object GoogleDependencies {
    const val materialDesign =
        "com.google.android.material:material:${Versions.materialDesignVersion}"
    const val inAppUpdate = "com.google.android.play:app-update:${Versions.inAppUpdateVersion}"
    const val inAppUpdateKtx = "com.google.android.play:app-update-ktx:${Versions.inAppUpdateVersion}"
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
    const val progressView = "com.github.skydoves:progressview:${Versions.progressViewVersion}"
    const val lottie = "com.airbnb.android:lottie:${Versions.lottieVersion}"
    const val circleImageView = "de.hdodenhof:circleimageview:${Versions.circleImageViewVersion}"
    const val kakaoLogin = "com.kakao.sdk:v2-user:${Versions.kakaoLoginVersion}"
    const val amplitude = "com.amplitude:android-sdk:${Versions.amplitudeVersion}"
}

object FirebaseDependencies {
    const val bom = "com.google.firebase:firebase-bom:${Versions.firabaseBomVersion}"
    const val messaging = "com.google.firebase:firebase-messaging-ktx"
    const val analytics = "com.google.firebase:firebase-analytics-ktx"
    const val crashlytics = "com.google.firebase:firebase-crashlytics-ktx"
}
