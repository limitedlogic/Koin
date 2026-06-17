package dev.apercorn.koin.ui.components.form

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.*

@Composable
fun DropdownField(
	label: String,
	selectedValue: String,
	modifier: Modifier = Modifier,
	leadingIcon: @Composable (() -> Unit)? = null,
	onClick: () -> Unit
) {
	Column(
		modifier = modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(10.dp)
	) {
		Text(
			text = label,
			style = MaterialTheme.typography.labelMedium,
			color = MaterialTheme.colorScheme.onPrimaryContainer
		)

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.height(56.dp)
				.clip(RoundedCornerShape(12.dp))
				.clickable(onClick = onClick)
				.background(
					color = MaterialTheme.colorScheme.secondaryContainer,
					shape = RoundedCornerShape(12.dp)
				)
				.border(
					BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
					RoundedCornerShape(12.dp)
				)
				.padding(horizontal = 16.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(12.dp)
		) {
			leadingIcon?.invoke()

			Text(
				text = selectedValue,
				modifier = Modifier.weight(1f),
				style = MaterialTheme.typography.labelMedium,
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
			)

			Icon(
				imageVector = TablerIcons.Outlined.ChevronDown,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
			)
		}
	}
}
