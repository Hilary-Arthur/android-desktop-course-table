plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.coursetable"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.coursetable"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "1.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    flavorDimensions += "school"
    productFlavors {
        create("swu") {
            dimension = "school"
            resValue("string", "app_name", "课程表（西大）")
            resValue("string", "school_name", "西南大学")
            buildConfigField("String", "SCHOOL_ID", "\"swu\"")
            buildConfigField("String", "SCHOOL_NAME", "\"西南大学\"")
            buildConfigField("String", "COURSE_FILE", "\"swu_course\"")
        }
        create("cqmu") {
            dimension = "school"
            resValue("string", "app_name", "课程表（重医）")
            resValue("string", "school_name", "重庆医科大学")
            buildConfigField("String", "SCHOOL_ID", "\"cqmu\"")
            buildConfigField("String", "SCHOOL_NAME", "\"重庆医科大学\"")
            buildConfigField("String", "COURSE_FILE", "\"cqmu_course\"")
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
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.5"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")

    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    implementation("com.google.code.gson:gson:2.10.1")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
