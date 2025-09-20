import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  id("com.google.devtools.ksp")
  id("jacoco")
}

android {
  namespace = "com.openclassrooms.vitesse"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.openclassrooms.vitesse"
    minSdk = 30
    targetSdk = 35
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
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlinOptions {
    jvmTarget = "11"
  }
  buildFeatures {
    compose = true
  }
}

jacoco {
  toolVersion = "0.8.12"
}

tasks.withType<Test> {
  extensions.configure<JacocoTaskExtension> {
    isIncludeNoLocationClasses = true
    excludes = listOf("jdk.internal.*")
  }
}

tasks.register<JacocoReport>("jacocoDebugReport") {
  dependsOn("testDebugUnitTest")

  val fileFilter = listOf(
    "**/R.class",
    "**/R$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Test*.*",
    "android/**/*.*"
  )

  val kotlinClasses = layout.buildDirectory.dir("tmp/kotlin-classes/debug")
  val javaClasses = layout.buildDirectory.dir("intermediates/javac/debug/classes")

  classDirectories.setFrom(
    kotlinClasses.map { dir ->
      fileTree(dir) {
        setExcludes(fileFilter)
      }
    },
    javaClasses.map { dir ->
      fileTree(dir) {
        setExcludes(fileFilter)
      }
    }
  )

  sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
  executionData.setFrom(layout.buildDirectory.file("jacoco/testDebugUnitTest.exec"))

  reports {
    xml.required.set(true)
    html.required.set(true)
  }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
  implementation ("androidx.compose.material:material-icons-extended:1.4.3")
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.ui.tooling)
  implementation(libs.androidx.material3)
  // Compose Navigation
  implementation ("androidx.navigation:navigation-compose:2.6.0")
  // Coil for image loading
  implementation("io.coil-kt:coil-compose:2.6.0")
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)

  //Room
  dependencies {
    val room_version = "2.7.2"

    implementation("androidx.room:room-runtime:$room_version")

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    ksp("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

    // optional - Test helpers
    testImplementation("androidx.room:room-testing:$room_version")

  }
}
