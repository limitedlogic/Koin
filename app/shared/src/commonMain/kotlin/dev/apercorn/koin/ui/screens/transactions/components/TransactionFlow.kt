package dev.apercorn.koin.ui.screens.transactions.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.ArrowRight

/**
 * Lightweight UI representation of an account or category for the party picker cards.
 */
data class PartyUiModel(
	val id: String,
	val label: String,
	val icon: ImageVector,
	val roleTag: String // account, expense, income
)

/**
 * Horizontal row showing FROM → TO with a directional arrow in between.
 */
@Composable
fun TransactionFlow(
	fromParty: PartyUiModel,
	toParty: PartyUiModel,
	onFromClick: () -> Unit,
	onToClick: () -> Unit,
	modifier: Modifier = Modifier
) {
	Row(
		modifier = modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		FlowButton(
			party = fromParty,
			onClick = onFromClick,
			modifier = Modifier.weight(1f)
		)

		// directional arrow
		Icon(
			imageVector = TablerIcons.Outlined.ArrowRight,
			contentDescription = "Direction",
			tint = MaterialTheme.colorScheme.onSurfaceVariant,
			modifier = Modifier.size(36.dp).padding(horizontal = 8.dp)
		)

		FlowButton(
			party = toParty,
			onClick = onToClick,
			modifier = Modifier.weight(1f)
		)
	}
}

/**
 * Single pill/card representing the FROM or TO side of a transaction — account or category.
 */
@Composable
private fun FlowButton(
	party: PartyUiModel,
	onClick: () -> Unit,
	modifier: Modifier = Modifier
) {
	Card(
		onClick = onClick,
		shape = RoundedCornerShape(16.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
		modifier = modifier
	) {
		Column(
			modifier = Modifier
				.padding(horizontal = 16.dp, vertical = 12.dp),
		) {
			Text(
				text = party.roleTag,
				style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
			Spacer(modifier = Modifier.height(8.dp))
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				Icon(
					imageVector = party.icon,
					contentDescription = party.label,
					tint = MaterialTheme.colorScheme.onSecondaryContainer,
					modifier = Modifier.size(20.dp)
				)
				Text(
					text = party.label,
					style = MaterialTheme.typography.labelLarge,
					fontWeight = FontWeight.Medium,
					color = MaterialTheme.colorScheme.onSurface
				)
			}
		}
	}
}