package dev.apercorn.koin.ui.screens.transactions.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.*
import dev.apercorn.koin.ui.components.CircularIcon

/**
 * Scrolls through dates one day at a time. Tapping the label opens a full calendar picker.
 */
@Composable
fun DatePicker(
	selectedDateLabel: String,
	canGoBack: Boolean,
	canGoForward: Boolean,
	onPrevDay: () -> Unit,
	onNextDay: () -> Unit,
	onPickerOpen: () -> Unit,
	modifier: Modifier = Modifier
) {
	Row(
		modifier = modifier
			.fillMaxWidth()
			.height(56.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.Center
	) {
		// left chevron
		CircularIcon(
			imageVector = TablerIcons.Outlined.ChevronLeft,
			onClick = { if (canGoBack) onPrevDay() },
			iconSize = 22.dp,
			circleSize = 36.dp,
			backgroundColor = MaterialTheme.colorScheme.surface,
			tint = if (canGoBack) MaterialTheme.colorScheme.onSurface
			else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
		)

		Spacer(modifier = Modifier.width(16.dp))

		// date label — tappable for calendar
		Row(
			modifier = Modifier
				.weight(1f)
				.clickable { onPickerOpen() },
			horizontalArrangement = Arrangement.Center,
			verticalAlignment = Alignment.CenterVertically
		) {
			Icon(
				imageVector = TablerIcons.Outlined.Calendar,
				contentDescription = "Pick date",
				tint = MaterialTheme.colorScheme.onSurface,
				modifier = Modifier.size(20.dp)
			)
			Spacer(modifier = Modifier.width(8.dp))
			Text(
				text = selectedDateLabel,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Medium,
				color = MaterialTheme.colorScheme.onSurface
			)
		}

		Spacer(modifier = Modifier.width(16.dp))

		// right chevron
		CircularIcon(
			imageVector = TablerIcons.Outlined.ChevronRight,
			onClick = { if (canGoForward) onNextDay() },
			iconSize = 22.dp,
			circleSize = 36.dp,
			backgroundColor = MaterialTheme.colorScheme.surface,
			tint = if (canGoForward) MaterialTheme.colorScheme.onSurface
			else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
		)
	}
}