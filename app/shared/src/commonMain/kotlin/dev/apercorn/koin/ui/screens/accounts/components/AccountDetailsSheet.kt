package dev.apercorn.koin.ui.screens.accounts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.filled.*
import dev.seyfarth.tablericons.outlined.*
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.continuous
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import dev.apercorn.koin.core.domain.model.Account
import dev.apercorn.koin.core.util.CurrencyFormatter
import dev.apercorn.koin.core.util.CurrencyInfo
import dev.apercorn.koin.ui.components.CircularIcon
import dev.apercorn.koin.ui.components.modal.ModalBottomSheet
import dev.apercorn.koin.ui.theme.KoinTheme
import dev.apercorn.koin.ui.util.IconProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsSheet(
	onDismiss: () -> Unit,
	account: Account,
	onSetBalance: () -> Unit = {},
	onTransfer: () -> Unit = {},
	onNewTransaction: () -> Unit = {},
	onViewAll: () -> Unit = {},
	onEditAccount: () -> Unit = {},
	onTogglePrimary: () -> Unit = {}
) {
	val currencyInfo = CurrencyInfo.find(account.currency)
	val icon = IconProvider.resolve(account.iconName)
	val iconColor = IconProvider.parseColor(account.colorHex)

	ModalBottomSheet(
		onDismiss = onDismiss,
		containerColor = MaterialTheme.colorScheme.surface,
		showGrabber = false,
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 24.dp)
		) {
			// Title bar
			AccountTitleBar(
				icon = icon,
				iconColor = iconColor,
				accountName = account.name,
				isPrimary = account.isPrimary,
				onClose = onDismiss,
				onTogglePrimary = onTogglePrimary
			)

			Spacer(modifier = Modifier.height(24.dp))

			// Details row
			AccountDetails(
				balance = account.balance,
				currency = account.currency,
				currencyInfo = currencyInfo,
				type = account.type.name,
				iconColor = iconColor
			)

			// Description
			val description = account.description
			if (!description.isNullOrBlank()) {
				Spacer(modifier = Modifier.height(16.dp))
				Text(
					text = description,
					style = MaterialTheme.typography.bodyMedium.copy(
						fontStyle = FontStyle.Italic
					),
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					textAlign = TextAlign.Center,
					modifier = Modifier.fillMaxWidth()
				)
			}

			Spacer(modifier = Modifier.height(24.dp))

			// Action buttons grid
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(12.dp)
			) {
				ActionButton(
					icon = TablerIcons.Outlined.Edit,
					label = "Set balance",
					onClick = onSetBalance,
					modifier = Modifier.weight(1f)
				)
				ActionButton(
					icon = TablerIcons.Outlined.ArrowsLeftRight,
					label = "Transfer",
					onClick = onTransfer,
					modifier = Modifier.weight(1f)
				)
			}

			Spacer(modifier = Modifier.height(12.dp))

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(12.dp)
			) {
				ActionButton(
					icon = TablerIcons.Outlined.Plus,
					label = "New Transaction",
					onClick = onNewTransaction,
					modifier = Modifier.weight(1f)
				)
				ActionButton(
					icon = TablerIcons.Outlined.List,
					label = "View All",
					onClick = onViewAll,
					modifier = Modifier.weight(1f)
				)
			}

			Spacer(modifier = Modifier.height(24.dp))

			// Edit Account button
			Button(
				onClick = onEditAccount,
				modifier = Modifier
					.fillMaxWidth()
					.height(48.dp),
				colors = ButtonDefaults.filledTonalButtonColors(),
				shape = RoundedCornerShape(12.dp)
			) {
				Icon(
					imageVector = TablerIcons.Outlined.Adjustments,
					contentDescription = null,
					modifier = Modifier.size(18.dp)
				)
				Spacer(modifier = Modifier.width(8.dp))
				Text("Edit Account", style = MaterialTheme.typography.labelLarge)
			}

			Spacer(modifier = Modifier.height(32.dp))
		}
	}
}

@Composable
private fun AccountTitleBar(
	icon: ImageVector,
	iconColor: Color,
	accountName: String,
	isPrimary: Boolean,
	onClose: () -> Unit,
	onTogglePrimary: () -> Unit
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically
	) {
		// Close button (left)
		CircularIcon(
			imageVector = TablerIcons.Outlined.X,
			onClick = onClose,
			iconSize = 26.dp,
			circleSize = 40.dp,
			backgroundColor = MaterialTheme.colorScheme.primaryContainer,
			tint = MaterialTheme.colorScheme.onSurface
		)

		// Center: icon + name
		Row(
			modifier = Modifier.weight(1f),
			horizontalArrangement = Arrangement.Center,
			verticalAlignment = Alignment.CenterVertically,
		) {
			Box(
				modifier = Modifier
					.size(40.dp)
					.background(iconColor.copy(alpha = 0.2f), shape = CircleShape),
				contentAlignment = Alignment.Center
			) {
				Icon(
					imageVector = icon,
					contentDescription = null,
					tint = iconColor,
					modifier = Modifier.size(24.dp)
				)
			}
			Spacer(modifier = Modifier.width(12.dp))
			Text(
				text = accountName,
				style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
				color = MaterialTheme.colorScheme.onSurface
			)
		}

		// Star button (right)
		CircularIcon(
			imageVector = if (isPrimary) TablerIcons.Filled.Star else TablerIcons.Outlined.Star,
			onClick = onTogglePrimary,
			iconSize = 24.dp,
			circleSize = 40.dp,
			backgroundColor = Color.Transparent,
			tint = if (isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer
		)
	}
}

@Composable
private fun AccountDetails(
	balance: Long,
	currency: String,
	currencyInfo: CurrencyInfo?,
	type: String,
	iconColor: Color
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically
	) {
		// Left: balance details
		Column(modifier = Modifier.weight(1f)) {
			// Absolute diff + percent diff
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				Icon(
					imageVector = TablerIcons.Outlined.TrendingUp,
					contentDescription = null,
					tint = KoinTheme.colors.profit,
					modifier = Modifier.size(18.dp)
				)
				Text(
					text = "+$0.00",
					style = MaterialTheme.typography.displaySmall.copy(fontSize = 14.sp),
					color = KoinTheme.colors.profit
				)
				Text(
					text = "(0.00%)",
					style = MaterialTheme.typography.displaySmall.copy(fontSize = 14.sp),
					color = KoinTheme.colors.profit
				)
			}

			Spacer(modifier = Modifier.height(4.dp))

			// Money value + currency code
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				Text(
					text = CurrencyFormatter.format(balance, currency),
					style = MaterialTheme.typography.displayMedium,
					color = MaterialTheme.colorScheme.onSurface
				)
				Text(
					text = currency,
					style = MaterialTheme.typography.labelMedium,
					color = MaterialTheme.colorScheme.onPrimaryContainer
				)
			}

			Spacer(modifier = Modifier.height(4.dp))

			// Account type • currency name
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(6.dp)
			) {
				Text(
					text = "$type Account",
					style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
					color = MaterialTheme.colorScheme.onPrimaryContainer
				)
				Box(
					modifier = Modifier
						.size(4.dp)
						.background(
							color = MaterialTheme.colorScheme.onPrimaryContainer,
							shape = CircleShape
						)
				)
				Text(
					text = currencyInfo?.name ?: currency,
					style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
					color = MaterialTheme.colorScheme.onPrimaryContainer
				)
			}
		}

		// Right: mini sparkline chart
		MiniChart(
			modifier = Modifier
				.width(80.dp)
				.height(56.dp)
		)
	}
}

@Composable
private fun MiniChart(modifier: Modifier = Modifier) {
	val modelProducer = remember { CartesianChartModelProducer() }

	LaunchedEffect(Unit) {
		modelProducer.runTransaction {
			lineSeries {
				series(listOf(2400000, 2600000, 2500000, 2800000, 2900000, 3100000))
			}
		}
	}

	CartesianChartHost(
		chart = rememberCartesianChart(
			rememberLineCartesianLayer(
				lineProvider = LineCartesianLayer.LineProvider.series(
					LineCartesianLayer.rememberLine(
						fill = LineCartesianLayer.LineFill.single(fill(MaterialTheme.colorScheme.primary)),
						stroke = LineCartesianLayer.LineStroke.continuous(thickness = 2.dp)
					)
				)
			),
			startAxis = null,
			bottomAxis = null
		),
		modelProducer = modelProducer,
		modifier = modifier
	)
}

@Composable
private fun ActionButton(
	icon: ImageVector,
	label: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier
) {
	OutlinedButton(
		onClick = onClick,
		modifier = modifier.height(56.dp),
		shape = RoundedCornerShape(16.dp)
	) {
		Icon(
			imageVector = icon,
			contentDescription = null,
			modifier = Modifier.size(20.dp)
		)
		Spacer(modifier = Modifier.width(8.dp))
		Text(label, style = MaterialTheme.typography.labelLarge)
	}
}
