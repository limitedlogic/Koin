package dev.apercorn.koin.ui.screens.transactions.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.Check
import dev.seyfarth.tablericons.outlined.CurrencyDollar
import dev.apercorn.koin.ui.theme.KoinTheme
import dev.apercorn.koin.ui.theme.ibmPlexMonoFontFamily
import dev.apercorn.koin.ui.theme.interFontFamily

private val NUMPAD_SPACING = 8.dp
private val NUMPAD_BUTTON_FONT_SIZE = 30.sp
private val NUMPAD_LABEL_FONT_SIZE = 18.sp

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
	onAdjustClick: () -> Unit,
	modifier: Modifier = Modifier
) {
	BoxWithConstraints(
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = NUMPAD_SPACING)
	) {
		val cellSize = (maxWidth - NUMPAD_SPACING * 4) / 5

		Column(verticalArrangement = Arrangement.spacedBy(NUMPAD_SPACING)) {
			// Row 1: ÷  7  8  9  ⌫
			Row(horizontalArrangement = Arrangement.spacedBy(NUMPAD_SPACING)) {
				NumpadGridButton(
					text = Op.DIVIDE.symbol(),
					onClick = { onKey(NumpadKey.Operator(Op.DIVIDE)) },
					size = cellSize
				)
				(7..9).forEach { d ->
					NumpadGridButton(
						text = d.toString(),
						onClick = { onKey(NumpadKey.Digit(d)) },
						size = cellSize
					)
				}
				NumpadGridButton(
					text = "⌫",
					isLabel = true,
					onClick = { onKey(NumpadKey.Backspace) },
					size = cellSize
				)
			}
			// Row 2: ×  4  5  6  $
			Row(horizontalArrangement = Arrangement.spacedBy(NUMPAD_SPACING)) {
				NumpadGridButton(
					text = Op.MULTIPLY.symbol(),
					onClick = { onKey(NumpadKey.Operator(Op.MULTIPLY)) },
					size = cellSize
				)
				(4..6).forEach { d ->
					NumpadGridButton(
						text = d.toString(),
						onClick = { onKey(NumpadKey.Digit(d)) },
						size = cellSize
					)
				}
				NumpadGridButton(
					icon = TablerIcons.Outlined.CurrencyDollar,
					onClick = onAdjustClick,
					size = cellSize
				)
			}
			// Rows 3-4 wrapped in a Box so the confirm button can span both rows
			Box {
				Column(verticalArrangement = Arrangement.spacedBy(NUMPAD_SPACING)) {
					// Row 3: −  1  2  3  (5th slot reserved for confirm)
					Row(horizontalArrangement = Arrangement.spacedBy(NUMPAD_SPACING)) {
						NumpadGridButton(
							text = Op.SUBTRACT.symbol(),
							onClick = { onKey(NumpadKey.Operator(Op.SUBTRACT)) },
							size = cellSize
						)
						(1..3).forEach { d ->
							NumpadGridButton(
								text = d.toString(),
								onClick = { onKey(NumpadKey.Digit(d)) },
								size = cellSize
							)
						}
						Spacer(Modifier.size(cellSize))
					}
					// Row 4: +  CUR  0  .  (5th slot reserved for confirm)
					Row(horizontalArrangement = Arrangement.spacedBy(NUMPAD_SPACING)) {
						NumpadGridButton(
							text = Op.ADD.symbol(),
							onClick = { onKey(NumpadKey.Operator(Op.ADD)) },
							size = cellSize
						)
						NumpadGridButton(
							text = currencyCode,
							onClick = { onKey(NumpadKey.CurrencyToggle) },
							size = cellSize,
							isLabel = true
						)
						NumpadGridButton(
							text = "0",
							onClick = { onKey(NumpadKey.Digit(0)) },
							size = cellSize
						)
						NumpadGridButton(
							text = ".",
							onClick = { onKey(NumpadKey.Decimal) },
							size = cellSize
						)
						Spacer(Modifier.size(cellSize))
					}
				}
				// Confirm button overlaid at bottom-right, spanning rows 3-4
				NumpadGridButton(
					icon = TablerIcons.Outlined.Check,
					onClick = { if (confirmEnabled) onConfirm() },
					size = cellSize,
					height = cellSize * 2 + NUMPAD_SPACING,
					isAccent = true,
					enabled = confirmEnabled,
					modifier = Modifier.align(Alignment.BottomEnd)
				)
			}
		}
	}
}

@Composable
private fun NumpadGridButton(
	text: String = "",
	icon: ImageVector? = null,
	onClick: () -> Unit,
	size: Dp,
	height: Dp = size,
	isAccent: Boolean = false,
	isLabel: Boolean = false,
	enabled: Boolean = true,
	modifier: Modifier = Modifier
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
		modifier = modifier.size(width = size, height = height),
		shape = RoundedCornerShape(10.dp),
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
						fontSize = if (isLabel) NUMPAD_LABEL_FONT_SIZE else NUMPAD_BUTTON_FONT_SIZE
					),
					color = if (enabled) contentColor else contentColor.copy(alpha = 0.4f),
					textAlign = TextAlign.Center
				)
			}
		}
	}
}