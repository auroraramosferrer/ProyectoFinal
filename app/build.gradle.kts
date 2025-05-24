plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

}

android {
    namespace = "com.example.proyectofinal"
    compileSdk = 30

    defaultConfig {
        applicationId = "com.example.proyectofinal"
        minSdk = 26
        //noinspection ExpiredTargetSdkVersion
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        viewBinding = true
    }
}

dependencies {

    implementation ("com.google.zxing:core:3.5.2")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")


    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

    // Jetpack Compose (versión beta compatible con API 29)
    implementation("androidx.compose.ui:ui:1.0.0-beta09") // Composables
    implementation("androidx.compose.runtime:runtime:1.0.0-beta09") // Para manejar estados
    implementation("androidx.compose.material:material:1.0.0-beta09") // Material Design
    implementation("androidx.compose.foundation:foundation:1.0.0-beta09") // Para contenedores y layouts

    // Postgres JDBC
    implementation("org.postgresql:postgresql:42.2.19")

    // Corrutinas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.3")

    // Retrofit para hacer peticiones HTTP
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // AndroidX Core
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("androidx.core:core-ktx:1.6.0")

    // Material Design
    implementation("com.google.android.material:material:1.4.0")

    // Layout
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")

    // Lifecycle (ViewModel + LiveData)
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.0")

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")

    // Activity & Fragment
    implementation("androidx.activity:activity-ktx:1.2.4")
    implementation("androidx.activity:activity:1.2.4")
    implementation("androidx.fragment:fragment-ktx:1.2.4")

    // Legacy Support (opcional)
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // Annotation
    implementation("androidx.annotation:annotation:1.2.0")

    // Room (si lo usas)
    implementation("androidx.room:room-runtime:2.2.6")
    implementation(libs.volley)
   // implementation(libs.androidx.media3.common.ktx)
    implementation(libs.androidx.tools.core)
   // implementation(libs.androidx.media3.common.ktx)

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
