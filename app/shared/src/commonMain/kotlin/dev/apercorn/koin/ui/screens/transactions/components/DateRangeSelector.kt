package dev.apercorn.koin.ui.screens.transactions.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.ArrowRight
import dev.seyfarth.tablericons.outlined.Calendar
import dev.seyfarth.tablericons.outlined.CalendarMonth
import dev.seyfarth.tablericons.outlined.CalendarStats
import dev.seyfarth.tablericons.outlined.CalendarWeek
import dev.seyfarth.tablericons.outlined.Check
import dev.seyfarth.tablericons.outlined.ChevronLeft
import dev.seyfarth.tablericons.outlined.ChevronRight
import dev.seyfarth.tablericons.outlined.Infinity
import dev.apercorn.koin.ui.components.modal.ModalBottomSheet
import dev.apercorn.koin.ui.screens.transactions.DateRangeMode
import dev.apercorn.koin.ui.theme.ibmPlexMonoFontFamily

@Composable
fun DateRangeSelector(
	startMonthLabel: String,
	startDateLabel: String,
	endMonthLabel: String,
	endDateLabel: String,
	canGoBack: Boolean,
	canGoForward: Boolean,
	onPrev: () -> Unit,
	onNext: () -> Unit,
	onStartClick: () -> Unit,
	onEndClick: () -> Unit,
	onEditClick: () -> Unit,
	onResetClick: () -> Unit,
	modifier: Modifier = Modifier
) {
	Column(modifier = modifier.fillMaxWidth()) {
		// Control row: Edit | Reset
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(start = 4.dp, end = 4.dp, bottom = 4.dp),
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Text(
				text = "Edit",
				modifier = Modifier
					.clickable { onEditClick() }
					.padding(horizontal = 12.dp, vertical = 2.dp),
				style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
				color = MaterialTheme.colorScheme.onPrimaryContainer
			)
			Text(
				text = "Reset",
				modifier = Modifier
					.clickable { onResetClick() }
					.padding(horizontal = 12.dp, vertical = 2.dp),
				style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
		}

		// Date range card
		Card(
			shape = RoundedCornerShape(16.dp),
			colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
			modifier = Modifier.fillMaxWidth()
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 4.dp, vertical = 8.dp),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				// Left chevron
				IconButton(onClick = onPrev, enabled = canGoBack) {
					Icon(
						imageVector = TablerIcons.Outlined.ChevronLeft,
						contentDescription = "Previous",
						tint = if (canGoBack) MaterialTheme.colorScheme.onSurface
						else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
					)
				}

				// Start date block
				DateBlock(
					monthLabel = startMonthLabel,
					dateLabel = startDateLabel,
					onClick = onStartClick,
					modifier = Modifier.weight(1f)
				)

				// Arrow between dates
				Icon(
					imageVector = TablerIcons.Outlined.ArrowRight,
					contentDescription = "to",
					tint = MaterialTheme.colorScheme.onSurfaceVariant,
					modifier = Modifier.size(36.dp).padding(horizontal = 6.dp)
				)

				// End date block
				DateBlock(
					monthLabel = endMonthLabel,
					dateLabel = endDateLabel,
					onClick = onEndClick,
					modifier = Modifier.weight(1f)
				)

				// Right chevron
				IconButton(onClick = onNext, enabled = canGoForward) {
					Icon(
						imageVector = TablerIcons.Outlined.ChevronRight,
						contentDescription = "Next",
						tint = if (canGoForward) MaterialTheme.colorScheme.onSurface
						else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
					)
				}
			}
		}
	}
}

@Composable
private fun DateBlock(
	monthLabel: String,
	dateLabel: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier
) {
	Column(
		modifier = modifier.clickable { onClick() }.padding(vertical = 4.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = monthLabel,
			style = MaterialTheme.typography.labelMedium.copy(
				fontFamily = ibmPlexMonoFontFamily(),
				fontWeight = FontWeight.Bold
			),
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
		Spacer(modifier = Modifier.height(2.dp))
		Text(
			text = dateLabel,
			style = MaterialTheme.typography.displaySmall.copy(
				fontFamily = ibmPlexMonoFontFamily(),
				fontWeight = FontWeight.Bold,
				fontSize = 28.sp
			),
			color = MaterialTheme.colorScheme.onSurface
		)
	}
}

// ── Mode picker sheet ──

@Composable
fun DateRangeModeSheet(
	selectedMode: DateRangeMode,
	onModeSelected: (DateRangeMode) -> Unit,
	onDismiss: () -> Unit
) {
	ModalBottomSheet(onDismiss = onDismiss) {
		Text(
			text = "Range",
			style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
			color = MaterialTheme.colorScheme.onSurface,
			modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
		)
		Spacer(modifier = Modifier.height(8.dp))

		ModeOptionRow(
			label = "Week",
			icon = TablerIcons.Outlined.CalendarWeek,
			isSelected = selectedMode == DateRangeMode.WEEK,
			onClick = { onModeSelected(DateRangeMode.WEEK) }
		)
		ModeOptionRow(
			label = "Month",
			icon = TablerIcons.Outlined.CalendarMonth,
			isSelected = selectedMode == DateRangeMode.MONTH,
			onClick = { onModeSelected(DateRangeMode.MONTH) }
		)
		ModeOptionRow(
			label = "Quarter",
			icon = TablerIcons.Outlined.CalendarStats,
			isSelected = selectedMode == DateRangeMode.QUARTER,
			onClick = { onModeSelected(DateRangeMode.QUARTER) }
		)
		ModeOptionRow(
			label = "Year",
			icon = TablerIcons.Outlined.Calendar,
			isSelected = selectedMode == DateRangeMode.YEAR,
			onClick = { onModeSelected(DateRangeMode.YEAR) }
		)
		ModeOptionRow(
			label = "All Time",
			icon = TablerIcons.Outlined.Infinity,
			isSelected = selectedMode == DateRangeMode.ALL_TIME,
			onClick = { onModeSelected(DateRangeMode.ALL_TIME) }
		)
		ModeOptionRow(
			label = "Custom",
			icon = TablerIcons.Outlined.Calendar,
			isSelected = selectedMode == DateRangeMode.CUSTOM,
			onClick = { onModeSelected(DateRangeMode.CUSTOM) }
		)
	}
}

@Composable
private fun ModeOptionRow(
	label: String,
	icon: ImageVector,
	isSelected: Boolean,
	onClick: () -> Unit
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.height(52.dp)
			.clickable { onClick() }
			.padding(horizontal = 16.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		Icon(
			imageVector = icon,
			contentDescription = null,
			tint = if (isSelected) MaterialTheme.colorScheme.primary
			else MaterialTheme.colorScheme.onSurfaceVariant,
			modifier = Modifier.size(22.dp)
		)
		Spacer(modifier = Modifier.width(16.dp))
		Text(
			text = label,
			modifier = Modifier.weight(1f),
			style = MaterialTheme.typography.bodyLarge,
			color = MaterialTheme.colorScheme.onSurface
		)
		if (isSelected) {
			Icon(
				imageVector = TablerIcons.Outlined.Check,
				contentDescription = "Selected",
				tint = MaterialTheme.colorScheme.primary,
				modifier = Modifier.size(22.dp)
			)
		}
	}
}