package dev.apercorn.koin.ui.components.modal

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.*
import dev.apercorn.koin.ui.components.CircularIcon

@Composable
fun FloatingModal(
	visible: Boolean = true,
	title: String,
	onDismiss: () -> Unit,
	showCloseButton: Boolean = true,
	maxHeight: Dp? = null,
	content: @Composable ColumnScope.() -> Unit
) {
	if (!visible) return

	Dialog(
		onDismissRequest = onDismiss,
		properties = DialogProperties(
			usePlatformDefaultWidth = false,
			dismissOnBackPress = true,
			dismissOnClickOutside = true
		).withEdgeToEdge()
	) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.clickable(
					interactionSource = remember { MutableInteractionSource() },
					indication = null,
					onClick = onDismiss
				),
			contentAlignment = Alignment.Center
		) {
			BoxWithConstraints(
				modifier = Modifier
					.widthIn(max = 360.dp)
					.padding(horizontal = 24.dp)
					.clickable(
						interactionSource = remember { MutableInteractionSource() },
						indication = null,
						onClick = {} // Intercept clicks to prevent dismissing
					)
			) {
				val effectiveMaxHeight = if (maxHeight != null) {
					maxHeight
				} else {
					(this@BoxWithConstraints.maxHeight - 48.dp).coerceAtLeast(0.dp)
				}

				Column(
					modifier = Modifier
						.fillMaxWidth()
						.heightIn(max = effectiveMaxHeight)
						.clip(RoundedCornerShape(28.dp))
						.background(MaterialTheme.colorScheme.surface)
						.padding(24.dp)
						.verticalScroll(rememberScrollState()),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					// Header with title and close button
					Row(
						modifier = Modifier.fillMaxWidth(),
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.SpaceBetween
					) {
						Text(
							text = title,
							style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
							color = MaterialTheme.colorScheme.onSurface
						)

						if (showCloseButton) {
							CircularIcon(
								onClick = onDismiss,
								imageVector = TablerIcons.Outlined.X,
								iconSize = 20.dp,
								circleSize = 36.dp,
								backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
								tint = MaterialTheme.colorScheme.onSurface
							)
						}
					}

					Spacer(modifier = Modifier.height(20.dp))

					// Variable content
					content()
				}
			}
		}
	}
}
