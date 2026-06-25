package dev.apercorn.koin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun koinTypography(): Typography {
	val colorScheme = MaterialTheme.colorScheme

	// Display: Used to display money
	// Label: 


	return Typography(
		displayLarge = TextStyle(
			fontFamily = ibmPlexMonoFontFamily(),
			fontWeight = FontWeight.Bold,
			letterSpacing = 0.5.sp,
			fontSize = 32.sp,
			lineHeight = 40.sp,
			color = colorScheme.onBackground
		),
		displayMedium = TextStyle(
			fontFamily = ibmPlexMonoFontFamily(),
			fontWeight = FontWeight.Bold,
			letterSpacing = 0.5.sp,
			fontSize = 20.sp,
			lineHeight = 28.sp,
			color = colorScheme.onBackground
		),
		displaySmall = TextStyle(
			fontFamily = ibmPlexMonoFontFamily(),
			fontWeight = FontWeight.Medium,
			letterSpacing = 0.5.sp,
			fontSize = 16.sp,
			lineHeight = 20.sp,
			color = colorScheme.onBackground
		),
		// App TopBar
		headlineMedium = TextStyle(
			fontFamily = interFontFamily(),
			fontWeight = FontWeight.Black,
			letterSpacing = 0.5.sp,
			fontSize = 24.sp,
			lineHeight = 32.sp,
			color = colorScheme.onBackground
		),
		labelLarge = TextStyle(
			fontFamily = interFontFamily(),
			letterSpacing = 0.5.sp,
			fontWeight = FontWeight.Bold,
			fontSize = 14.sp,
			lineHeight = 20.sp,
		),
		labelMedium = TextStyle(
			fontFamily = interFontFamily(),
			letterSpacing = 0.5.sp,
			fontWeight = FontWeight.Bold,
			fontSize = 14.sp,
			lineHeight = 20.sp,
		),
		labelSmall = TextStyle(
			fontFamily = interFontFamily(),
			fontWeight = FontWeight.Bold,
			letterSpacing = 0.5.sp,
			fontSize = 12.sp,
			lineHeight = 16.sp,
		),
		bodyMedium = TextStyle(
			fontFamily = ibmPlexSansFontFamily(),
			fontWeight = FontWeight.SemiBold,
			letterSpacing = 0.5.sp,
			fontSize = 14.sp,
			lineHeight = 22.sp,
		),
		bodySmall = TextStyle(
			fontFamily = interFontFamily(),
			fontWeight = FontWeight.Normal,
			letterSpacing = 0.5.sp,
			fontSize = 13.sp,
			lineHeight = 16.sp,
		),
		titleSmall = TextStyle(
			fontFamily = interFontFamily(),
			letterSpacing = 0.5.sp,
			fontWeight = FontWeight.Bold,
			fontSize = 16.sp,
			lineHeight = 20.sp,
		),
	)
}
