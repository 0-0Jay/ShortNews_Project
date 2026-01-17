plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.shortnews"
    compileSdk = 34
    buildFeatures.viewBinding = true

    defaultConfig {
        applicationId = "com.example.shortnews"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Navigation
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // View Model
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Activity & Fragment Extension
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Retrofit 2
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // 이미지 동그랗게
    implementation("de.hdodenhof:circleimageview:3.1.0")
    // 달력
    implementation("com.github.prolificinteractive:material-calendarview:2.0.1")
    implementation("com.jakewharton.threetenabp:threetenabp:1.2.4")
    // 이미지 가져와서 업로드
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.google.firebase:firebase-components:17.1.5")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    // cardview
    implementation("androidx.cardview:cardview:1.0.0")
    // AWS
    implementation("com.amazonaws:aws-android-sdk-s3:2.13.5")
    // 네이버
    implementation ("com.navercorp.nid:oauth-jdk8:5.9.0")
    // 카카오
    implementation ("com.kakao.sdk:v2-all:2.19.0")
    implementation("com.kakao.sdk:v2-share:2.20.0")
    // SSE 기반의 서버 푸시 노티로 알려진 실리콘 밸리의 LaunchDarkly가 제작한 OkHttp 기반의 EventSource 라이브러리를 사용 (SSE 알림)
    implementation("com.launchdarkly:okhttp-eventsource:4.1.0")
    testImplementation("com.squareup.okhttp3:okhttp-sse:4.12.0")
    // SSE 관련 끝

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}