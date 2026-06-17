package dev.apercorn.koin.ui.screens.transactions.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.Check
import dev.apercorn.koin.ui.theme.KoinTheme
import dev.apercorn.koin.ui.theme.ibmPlexMonoFontFamily
import dev.apercorn.koin.ui.theme.interFontFamily

/**
 * Arithmetic operator used in the keypad for on-the-fly calculations.
 */
enum class Op {
	DIVIDE,
	MULTIPLY,
	SUBTRACT,
	ADD
}

/**
 * Sealed type representing every possible key press on the transaction keypad.
 * All expression state lives in the ViewModel — the keypad just emits events.
 */
sealed class NumpadKey {
	data class Digit(val value: Int) : NumpadKey()
	data object Decimal : NumpadKey()
	data class Operator(val op: Op) : NumpadKey()
	data object Backspace : NumpadKey()
	data object CurrencyToggle : NumpadKey() // cycles currency or opens picker
	data object Confirm : NumpadKey()       // ✓ — finish entry
}

/** Mapping from [Op] to its display symbol. */
private fun Op.symbol(): String = when (this) {
	Op.DIVIDE -> "÷"
	Op.MULTIPLY -> "×"
	Op.SUBTRACT -> "−"
	Op.ADD -> "+"
}

@Composable
fun Numpad(
	currencyCode: String,
	confirmEnabled: Boolean,
	onKey: (NumpadKey) -> Unit,
	onConfirm: () -> Unit,
	modifier: Modifier = Modifier
) {
	val spacing = 4.dp

	Row(
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = spacing),
		horizontalArrangement = Arrangement.spacedBy(spacing)
	) {
		// Left column: arithmetic operations
		Column(
			modifier = Modifier.weight(1f),
			verticalArrangement = Arrangement.spacedBy(spacing)
		) {
			Op.entries.forEach { op ->
				NumpadGridButton(
					text = op.symbol(),
					onClick = { onKey(NumpadKey.Operator(op)) },
					modifier = Modifier.fillMaxWidth().aspectRatio(1f)
				)
			}
		}

		// Middle column: digit grid (three columns wide)
		Column(
			modifier = Modifier.weight(3f),
			verticalArrangement = Arrangement.spacedBy(spacing)
		) {
			// Row: 7 8 9
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(spacing)
			) {
				(7..9).forEach { d ->
					NumpadGridButton(
						text = d.toString(),
						onClick = { onKey(NumpadKey.Digit(d)) },
						modifier = Modifier.weight(1f).aspectRatio(1f)
					)
				}
			}
			// Row: 4 5 6
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(spacing)
			) {
				(4..6).forEach { d ->
					NumpadGridButton(
						text = d.toString(),
						onClick = { onKey(NumpadKey.Digit(d)) },
						modifier = Modifier.weight(1f).aspectRatio(1f)
					)
				}
			}
			// Row: 1 2 3
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(spacing)
			) {
				(1..3).forEach { d ->
					NumpadGridButton(
						text = d.toString(),
						onClick = { onKey(NumpadKey.Digit(d)) },
						modifier = Modifier.weight(1f).aspectRatio(1f)
					)
				}
			}
			// Row: currency  0  .
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(spacing)
			) {
				NumpadGridButton(
					text = currencyCode,
					onClick = { onKey(NumpadKey.CurrencyToggle) },
					modifier = Modifier.weight(1f).aspectRatio(1f),
					isLabel = true
				)
				NumpadGridButton(
					text = "0",
					onClick = { onKey(NumpadKey.Digit(0)) },
					modifier = Modifier.weight(1f).aspectRatio(1f)
				)
				NumpadGridButton(
					text = ".",
					onClick = { onKey(NumpadKey.Decimal) },
					modifier = Modifier.weight(1f).aspectRatio(1f)
				)
			}
		}

		// Right column: backspace (1 box), gap (1 box), done (2 boxes)
		Column(
			modifier = Modifier.weight(1f),
			verticalArrangement = Arrangement.spacedBy(spacing)
		) {
			// Row 1: backspace
			NumpadGridButton(
				text = "⌫",
				onClick = { onKey(NumpadKey.Backspace) },
				modifier = Modifier.fillMaxWidth().aspectRatio(1f)
			)
			// Row 2: gap spacer
			Spacer(modifier = Modifier.fillMaxWidth().aspectRatio(1f))
			// Rows 3-4: done (2 boxes tall)
			NumpadGridButton(
				icon = TablerIcons.Outlined.Check,
				onClick = {
					if (confirmEnabled) onConfirm()
				},
				modifier = Modifier.fillMaxWidth().aspectRatio(0.5f),
				isAccent = true,
				enabled = confirmEnabled
			)
		}
	}
}

@Composable
private fun NumpadGridButton(
	text: String = "",
	icon: ImageVector? = null,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	isAccent: Boolean = false,
	isLabel: Boolean = false,
	enabled: Boolean = true
) {
	val containerColor = when {
		isAccent -> MaterialTheme.colorScheme.primary
		else -> KoinTheme.colors.numpadButton
	}
	val contentColor = when {
		isAccent -> MaterialTheme.colorScheme.onPrimary
		else -> MaterialTheme.colorScheme.onSurface
	}

	Surface(
		onClick = onClick,
		modifier = modifier,
		shape = RoundedCornerShape(12.dp),
		color = containerColor,
		enabled = enabled
	) {
		Box(contentAlignment = Alignment.Center) {
			if (icon != null) {
				Icon(
					imageVector = icon,
					contentDescription = text.ifEmpty { "Done" },
					tint = if (enabled) contentColor else contentColor.copy(alpha = 0.4f),
					modifier = Modifier.size(28.dp)
				)
			} else {
				Text(
					text = text,
					style = MaterialTheme.typography.titleMedium.copy(
						fontFamily = if (isLabel) interFontFamily() else ibmPlexMonoFontFamily(),
						fontWeight = FontWeight.Bold,
						fontSize = 22.sp
					),
					color = if (enabled) contentColor else contentColor.copy(alpha = 0.4f),
					textAlign = TextAlign.Center
				)
			}
		}
	}
}