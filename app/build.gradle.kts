plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlinx.kover")
}

android {
    namespace = "fr.delcey.dailynino"
    compileSdk = 34

    defaultConfig {
        applicationId = "fr.delcey.dailynino"
        minSdk = 24
        targetSdk = 34
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

hilt {
    enableAggregatingTask = true
}

dependencies {
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation("com.google.dagger:hilt-android:2.48.1")
    ksp("com.google.dagger:hilt-compiler:2.48.1")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.10")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    implementation("com.github.bumptech.glide:glide:4.16.0")

    val mediaVersion = "1.2.0"
    implementation("androidx.media3:media3-exoplayer:$mediaVersion")
    implementation("androidx.media3:media3-ui:$mediaVersion")

    testImplementation("androidx.arch.core:core-testing:2.1.0") {
        // Removes the Mockito dependency bundled with arch core (wtf android ??)
        exclude("org.mockito", "mockito-core")
    }
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("org.slf4j:slf4j-nop:2.0.9") // To avoid the stupid "SLF4J: No SLF4J providers were found." message from MockK
    testImplementation("app.cash.turbine:turbine:1.0.0")
}

koverReport {
    filters {
        excludes {
            annotatedBy(
                "dagger.Module",
                "dagger.internal.DaggerGenerated",
                "androidx.room.Database",
            )
            packages(
                "hilt_aggregated_deps", // Hilt: GeneratedInjectors (NOT annotated by DaggerGenerated)
                "fr.delcey.dailynino.databinding", // ViewBinding
            )
            classes(
                // Hilt
                // Delete when this is fixed: https://github.com/Kotlin/kotlinx-kover/issues/331
                "*_*Factory\$*",

                // Gradle Generated
                "fr.delcey.dailynino.BuildConfig",

                // Delete when this is fixed: https://github.com/Kotlin/kotlinx-kover/issues/331
                "*AppDatabase\$*",


                // Utils
                "fr.delcey.dailynino.ui.utils*",

                // Remove below when Kover can handle Android integration tests!
                "*MainApplication",
                "*MainApplication\$*",
                "*Fragment",
                "*Fragment\$*",
                "*Activity",
                "*Activity\$*",
                "*Adapter",
                "*Adapter\$*",
            )
        }
    }
}