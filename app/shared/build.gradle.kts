
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.androidMultiplatformLibrary)
	alias(libs.plugins.composeMultiplatform)
	alias(libs.plugins.composeCompiler)
}

kotlin {
	listOf(
		iosArm64(),
		iosSimulatorArm64()
	).forEach { iosTarget ->
		iosTarget.binaries.framework {
			baseName = "Shared"
			isStatic = true
		}
	}

	androidLibrary {
		namespace = "dev.apercorn.koin.app.shared"
		compileSdk = libs.versions.android.compileSdk.get().toInt()
		minSdk = libs.versions.android.minSdk.get().toInt()

		compilerOptions {
			jvmTarget = JvmTarget.JVM_11
		}
		androidResources {
			enable = true
		}
		withHostTest {
			isIncludeAndroidResources = true
		}
	}

	sourceSets {
		androidMain.dependencies {
			implementation(libs.compose.uiToolingPreview)
		}
		commonMain.dependencies {
			api(projects.core)
			implementation(libs.compose.runtime)
			implementation(libs.compose.foundation)
			implementation(libs.compose.material3)
			implementation(libs.compose.ui)
			implementation(libs.compose.components.resources)
			implementation(libs.compose.uiToolingPreview)
			implementation(libs.androidx.lifecycle.viewmodelCompose)
			implementation(libs.androidx.lifecycle.runtimeCompose)

			// Direct deps (core uses implementation scope)
			implementation(libs.kotlinx.datetime)
			implementation(libs.uuid)

			// Koin
			implementation(libs.koin.core)
			implementation(libs.koin.compose)

			// Voyager
			implementation(libs.voyager.navigator)
			implementation(libs.voyager.screenmodel)
			implementation(libs.voyager.koin)
			implementation(libs.voyager.tab.navigator)
			implementation(libs.voyager.transitions)

			// Vico
			implementation(libs.vico.compose.m3)
			implementation(libs.vico.core)

			// Tabler Icons
			implementation(libs.tabler.icons.kmp)
		}
		commonTest.dependencies {
			implementation(libs.kotlin.test)
		}
	}
}

dependencies {
	androidRuntimeClasspath(libs.compose.uiTooling)
}