import java.io.FileInputStream
import java.nio.file.Paths
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.geomate"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.geomate"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        manifestPlaceholders["GOOGLE_MAPS_API_KEY"] = ::googleMapsApiKey.invoke() ?: ""

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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    // Jetpack Compose
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.ui:ui:1.6.0-beta02")
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.7.1")
    implementation("com.google.firebase:firebase-firestore-ktx:24.7.1")

    // Testing and tooling
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.2.2"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    // Authentication
    implementation("com.google.android.gms:play-services-auth:20.6.0")

    //Facebook
    implementation("com.facebook.android:facebook-login:latest.release")

    // Add the dependency for the Firebase Authentication library
    implementation("com.google.firebase:firebase-auth-ktx")

    // Icons
    implementation("androidx.compose.material:material-icons-extended:1.5.0")

    // Status bar color
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.1")

    // Google Maps
    implementation("com.google.maps.android:maps-compose:2.7.2")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Image loader
    implementation("com.github.skydoves:landscapist-fresco:2.2.6")

    // Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.23.1")
}

fun googleMapsApiKey(): Any? {
    val secrets = Paths.get(projectDir.path, "secrets.properties").toFile()
    val props = Properties()
    val fis = FileInputStream(secrets)
    props.load(fis)
    return props["GOOGLE_MAPS_API_KEY"]
}