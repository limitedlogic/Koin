package dev.apercorn.koin.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.Check

data class Option<T>(
	val value: T,
	val label: String,
	val icon: ImageVector,
	val color: Color? = null
)

@Composable
fun <T> OptionRadioSelect(
	options: List<Option<T>>,
	selected: T?,
	onSelected: (T) -> Unit,
	modifier: Modifier = Modifier
) {
	val defaultColor = MaterialTheme.colorScheme.primary

	Column(modifier) {
		options.forEach { opt ->
			val isSelected = selected == opt.value
			val optionColor = opt.color ?: defaultColor

			Row(
				modifier = Modifier
					.fillMaxWidth()
					.height(56.dp)
					.clickable { onSelected(opt.value) }
					.padding(horizontal = 8.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Icon(
					imageVector = opt.icon,
					contentDescription = null,
					tint = if (isSelected) optionColor else optionColor.copy(alpha = 0.7f),
					modifier = Modifier.size(24.dp)
				)
				Spacer(modifier = Modifier.width(16.dp))
				Text(
					text = opt.label,
					modifier = Modifier.weight(1f),
					style = MaterialTheme.typography.bodyLarge,
					color = MaterialTheme.colorScheme.onSurface
				)
				if (isSelected) {
					Icon(
						imageVector = TablerIcons.Outlined.Check,
						contentDescription = "Selected",
						tint = MaterialTheme.colorScheme.primary,
						modifier = Modifier.size(24.dp)
					)
				}
			}
		}
	}
}
