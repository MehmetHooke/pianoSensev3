plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.pianosense"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pianosense"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {



    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation ("com.google.android.gms:play-services-auth:21.2.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    //js analizi için kütüphaneler
    //implementation(files("libs\\TarsosDSPKit-release.aar"))




    //implementation (files("libs/TarsosDSP.jar"))
    implementation ("com.google.code.gson:gson:2.8.9")

    //implementation ("com.arthenica:mobile-ffmpeg-full:4.4.LTS")

    //implementation (libs.tarsosdsp.v24android)

    //implementation ("be.tarsos.dsp:core:2.5")
    //implementation ("be.tarsos.dsp:jvm:2.5")

    //implementation("be.tarsos.dsp:TarsosDSP:2.5")
    //implementation ("com.github.axet:TarsosDSP:2.4-1")

    implementation ("androidx.viewpager2:viewpager2:1.1.0")
    implementation ("androidx.fragment:fragment-ktx:1.8.5")
    implementation ("com.google.android.material:material:1.12.0")  // Sürümü güncelleyebilirsiniz

    implementation ("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(files("libs\\TarsosDSP-Android-2.4.jar"))


    implementation ("com.arthenica:mobile-ffmpeg-full:4.4")



    //implementation("com.github.jillesvangurp:tarsosdsp:2.4")



    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


}


