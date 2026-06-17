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
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.ArrowRight

/**
 * Lightweight UI representation of an account or category for the party picker cards.
 */
data class PartyUiModel(
	val id: String,
	val label: String,          // display name
	val icon: ImageVector,      // resolved from account/category type
	val roleTag: String         // "ACCOUNT" | "EXPENSE" | "INCOME" | "TRANSFER"
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
	onArrowClick: () -> Unit,
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
			modifier = Modifier.size(24.dp).padding(horizontal = 4.dp)
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
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
		modifier = modifier
	) {
		Column(
			modifier = Modifier
				.padding(horizontal = 16.dp, vertical = 12.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				text = party.roleTag,
				style = MaterialTheme.typography.labelSmall,
				fontWeight = FontWeight.SemiBold,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Spacer(modifier = Modifier.height(4.dp))
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				Icon(
					imageVector = party.icon,
					contentDescription = party.label,
					tint = MaterialTheme.colorScheme.primary,
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