plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.recipe_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.recipe_app"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    //ViewBinding
    buildFeatures {
        viewBinding=true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //firebase
//    dependencies {
//        // Import the BoM for the Firebase platform
//        implementation(libs.google.firebase.auth)
//        implementation(libs.firebase.analytics)
//        implementation(libs.firebase.auth)
//
//    }
    dependencies {
        implementation(libs.firebase.database)
        implementation("com.github.Dhaval2404:ImagePicker:2.1")

        implementation("com.github.bumptech.glide:glide:4.15.1")


        implementation("de.hdodenhof:circleimageview:3.1.0")
        implementation(libs.google.firebase.auth)
        implementation("com.google.firebase:firebase-database:21.0.0")
        implementation(libs.firebase.firestore)
    }

}