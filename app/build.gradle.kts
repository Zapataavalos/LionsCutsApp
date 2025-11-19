plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.0.21-1.0.25"
}

android {
    namespace = "com.example.applionscuts"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.applionscuts"
        minSdk = 25
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.incremental", "true")
    }
}

dependencies {
    // Versiones centralizadas
    val room_version = "2.6.1"
    val lifecycle_version = "2.8.2"
    val compose_bom_version = "2024.05.00"
    val activity_compose_version = "1.9.0"
    val nav_compose_version = "2.7.7"
    val core_ktx_version = "1.13.1"

    // Funcionalidades básicas de Android con Kotlin
    implementation("androidx.core:core-ktx:$core_ktx_version") // Extensiones Kotlin para Android
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version") // Corrutinas + ciclo de vida
    implementation("androidx.activity:activity-compose:$activity_compose_version") // Integración Activity con Compose

    // Jetpack Compose (UI moderna)
    implementation(platform("androidx.compose:compose-bom:$compose_bom_version")) // Gestiona versiones Compose
    implementation("androidx.compose.ui:ui") // Núcleo de la UI
    implementation("androidx.compose.ui:ui-graphics") // Soporte para gráficos y dibujo
    implementation("androidx.compose.ui:ui-tooling-preview") // Vista previa en Android Studio
    implementation("androidx.compose.material3:material3") // Componentes Material Design 3
    implementation("androidx.compose.material:material") // Compatibilidad con Material Design 2
    implementation("androidx.compose.material:material-icons-extended") // Iconos Material extendidos
    debugImplementation("androidx.compose.ui:ui-tooling") // Herramientas de depuración Compose
    debugImplementation("androidx.compose.ui:ui-test-manifest") // Configuración de tests en debug

    // Ciclo de vida, ViewModel y LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version") // ViewModel en Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version") // ViewModel + corrutinas
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version") // LiveData + corrutinas
    implementation("androidx.compose.runtime:runtime-livedata") // Observa LiveData en Compose

    // Navegación entre pantallas
    implementation("androidx.navigation:navigation-compose:$nav_compose_version") // Navegación declarativa Compose

    // Base de datos local (Room)
    implementation("androidx.room:room-runtime:$room_version") // Motor de Room (SQLite)
    implementation("androidx.room:room-ktx:$room_version") // Extensiones Kotlin + corrutinas
    ksp("androidx.room:room-compiler:$room_version") // Genera DAOs y entidades (KSP)

    // Testing
    testImplementation("junit:junit:4.13.2") // Pruebas unitarias
    androidTestImplementation("androidx.test.ext:junit:1.2.1") // JUnit para tests Android
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1") // Tests de UI (interacción)
    androidTestImplementation(platform("androidx.compose:compose-bom:$compose_bom_version")) // Compatibilidad Compose en tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4") // Tests UI en Compose

    // Cámara y galería
    implementation("androidx.activity:activity-compose:1.9.0") // Necesario para manejar Intents de cámara/galería
    implementation("androidx.appcompat:appcompat:1.6.1") // Compatibilidad con versiones antiguas de Android
    implementation("androidx.core:core-ktx:1.13.1") // Funciones Kotlin para Android
    implementation("androidx.fragment:fragment-ktx:1.7.0") // Manejo moderno de Fragments

    // Procesamiento y carga de imágenes
    implementation("io.coil-kt:coil-compose:2.7.0") // Cargar y mostrar imágenes (URLs, archivos, etc.)
}
