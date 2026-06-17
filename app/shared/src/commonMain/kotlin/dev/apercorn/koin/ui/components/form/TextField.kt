package dev.apercorn.koin.ui.components.form

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.*

@Composable
fun TextField(
	label: String,
	value: String,
	onValueChange: (String) -> Unit,
	placeholder: String,
	maxLength: Int = 25,
	actionButtonText: String? = null,
	onActionButtonClick: (() -> Unit)? = null,
	modifier: Modifier = Modifier
) {
	Column(modifier = modifier.fillMaxWidth()) {
		Text(
			text = label,
			style = MaterialTheme.typography.labelMedium,
			color = MaterialTheme.colorScheme.onPrimaryContainer
		)

		Spacer(modifier = Modifier.height(10.dp))

		Box(
			modifier = Modifier
				.fillMaxWidth()
				.background(
					color = MaterialTheme.colorScheme.secondaryContainer,
					shape = RoundedCornerShape(12.dp)
				)
				.border(
					BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
					RoundedCornerShape(12.dp)
				)
				.padding(horizontal = 16.dp, vertical = 12.dp)
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically
			) {
				BasicTextField(
					value = value,
					onValueChange = { newValue ->
						if (newValue.length <= maxLength) {
							onValueChange(newValue)
						}
					},
					modifier = Modifier.weight(1f),
					singleLine = true,
					textStyle = MaterialTheme.typography.bodyLarge.copy(
						color = MaterialTheme.colorScheme.onSurface
					),
					cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
					decorationBox = { innerTextField ->
						Box {
							if (value.isEmpty()) {
								Text(
									text = placeholder,
									style = MaterialTheme.typography.bodyLarge,
									color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
								)
							}
							innerTextField()
						}
					}
				)

				Text(
					text = "${value.length}/$maxLength",
					style = MaterialTheme.typography.labelSmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
					modifier = Modifier.padding(start = 8.dp)
				)
			}
		}

		if (actionButtonText != null && onActionButtonClick != null) {
			Spacer(modifier = Modifier.height(8.dp))
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.Start
			) {
				TextButton(
					onClick = onActionButtonClick,
					contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
					colors = ButtonDefaults.textButtonColors(
						containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
						contentColor = MaterialTheme.colorScheme.onSurface
					),
					shape = RoundedCornerShape(16.dp)
				) {
					Icon(
						imageVector = TablerIcons.Outlined.Plus,
						contentDescription = null,
						modifier = Modifier.size(16.dp)
					)
					Spacer(modifier = Modifier.width(4.dp))
					Text(
						text = actionButtonText,
						style = MaterialTheme.typography.bodyMedium,
						fontWeight = FontWeight.Medium
					)
				}
			}
		}
	}
}
