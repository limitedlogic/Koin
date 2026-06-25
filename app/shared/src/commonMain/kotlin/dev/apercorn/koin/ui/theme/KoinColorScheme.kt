package dev.apercorn.koin.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class KoinCustomColors(
	// Layout colors
	val dragHandle: Color,
	val numpadButton: Color,
	val modalOnBackground: Color,

	// Semantic colors
	val profit: Color,
	val loss: Color,
)

val LocalKoinCustomColors = staticCompositionLocalOf {
	KoinCustomColors(
		dragHandle = Color.Unspecified,
		numpadButton = Color.Unspecified,
		modalOnBackground = Color.Unspecified,
		profit = Color.Unspecified,
		loss = Color.Unspecified,
	)
}

val DarkKoinCustomColors = KoinCustomColors(
	// Layout colors
	dragHandle = Color(0xFF313131),
	numpadButton = Color(0xFF0C0C0C),
	modalOnBackground = Color(0xFF131313),

	// Semantic colors
	profit = Color(0xFF81C784),
	loss = Color(0xFFE57373),
)

val LightKoinCustomColors = KoinCustomColors(
	// Layout colors
	dragHandle = Color(0xFF313131),
	numpadButton = Color(0xFF171717),
	modalOnBackground = Color(0xFF171717),

	// Semantic colors
	profit = Color(0xFF388E3C),
	loss = Color(0xFFD32F2F),
)

val DarkKoinColorScheme = darkColorScheme(
	background = Color(0xFF090909),
	primary = Color(0xFF26547B),
	secondary = Color(0xFF326838),
	tertiary = Color(0xFF20218D),

	surface = Color(0xFF161616),
	surfaceVariant = Color(0xFF131315),

	outline = Color(0xD51A1A1A),
	outlineVariant = Color(0xFF3A3A3A),

	primaryContainer = Color(0xFF1D1D1E),
	secondaryContainer = Color(0xFF18181A),
	tertiaryContainer = Color(0xFF0C0C0C),

	error = Color(0xFFCF6679),
	errorContainer = Color(0xFFB1384E),

	onBackground = Color(0xFFE2E2E2),
	onPrimary = Color(0xFFFFFFFF),
	onSecondary = Color(0xFFA0A0A0),
	onTertiary = Color(0xFF000000),

	onPrimaryContainer = Color(0xFFC7C7C7),
	onSecondaryContainer = Color(0xFFAAAAAA), // currently used as navbaricon non-selected
	onTertiaryContainer = Color(0xFFE2E2E2),
	onErrorContainer = Color(0xFFF9DEDC),
	onError = Color(0xFF000000),

	onSurface = Color(0xFFE2E2E2),
	onSurfaceVariant = Color(0xFF686868),

	)

// TODO: update light theme colors
val LightKoinColorScheme = lightColorScheme(
	primary = Color(0xFF26547B),
	onPrimary = Color(0xFFFFFFFF),
	primaryContainer = Color(0xFFD1E4FF),
	onPrimaryContainer = Color(0xFF001D36),

	secondary = Color(0xFF326838),
	onSecondary = Color(0xFFFFFFFF),
	secondaryContainer = Color(0xFFD2E8D4),
	onSecondaryContainer = Color(0xFF002108),

	tertiary = Color(0xFF7A5900),
	onTertiary = Color(0xFFFFFFFF),
	tertiaryContainer = Color(0xFFFFDF9E),
	onTertiaryContainer = Color(0xFF261900),

	error = Color(0xFFBA1A1A),
	onError = Color(0xFFFFFFFF),
	errorContainer = Color(0xFFFFDAD6),
	onErrorContainer = Color(0xFF410002),

	background = Color(0xFFFCFCFF),
	onBackground = Color(0xFF191C1E),

	surface = Color(0xFFFCFCFF),
	onSurface = Color(0xFF191C1E),
	surfaceVariant = Color(0xFFDEE3EB),
	onSurfaceVariant = Color(0xFF42474E),

	outline = Color(0xFF72777F),
	outlineVariant = Color(0xFFC2C7CF),
)
