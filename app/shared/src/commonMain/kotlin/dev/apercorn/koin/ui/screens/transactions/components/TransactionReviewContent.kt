package dev.apercorn.koin.ui.screens.transactions.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.*
import dev.apercorn.koin.core.domain.model.Account
import dev.apercorn.koin.core.domain.model.Category
import dev.apercorn.koin.core.domain.model.TransactionType
import dev.apercorn.koin.core.util.CurrencyFormatter
import dev.apercorn.koin.ui.theme.KoinTheme
import dev.apercorn.koin.ui.theme.ibmPlexMonoFontFamily
import dev.apercorn.koin.ui.util.IconProvider
import kotlinx.datetime.*

@Composable
fun TransactionReviewContent(
	date: LocalDate,
	account: Account?,
	category: Category?,
	transactionType: TransactionType,
	amountCents: Long,
	currencyCode: String,
	title: String,
	onTitleChange: (String) -> Unit,
	onBack: () -> Unit,
	onSave: () -> Unit,
	modifier: Modifier = Modifier
) {

	Box(
		modifier = modifier
			.fillMaxWidth()
			.wrapContentHeight()
			.imePadding()
	) {
		Column(
			modifier = Modifier.fillMaxWidth(),
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			// Row: back button + title input
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(12.dp)
			) {
				// Back button — squircle with filled surface
				Surface(
					onClick = onBack,
					shape = RoundedCornerShape(12.dp),
					color = MaterialTheme.colorScheme.surface,
					modifier = Modifier.size(44.dp)
				) {
					Box(contentAlignment = Alignment.Center) {
						Icon(
							imageVector = TablerIcons.Outlined.ChevronLeft,
							contentDescription = "Back",
							tint = MaterialTheme.colorScheme.onSurface,
							modifier = Modifier.size(24.dp)
						)
					}
				}

				// Title input
				BasicTextField(
					value = title,
					onValueChange = onTitleChange,
					modifier = Modifier
						.weight(1f)
						.height(44.dp)
						.padding(start = 4.dp),
					textStyle = MaterialTheme.typography.bodyLarge.copy(
						fontFamily = ibmPlexMonoFontFamily(),
						fontWeight = FontWeight.Medium,
						color = MaterialTheme.colorScheme.onSurface
					),
					cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
					singleLine = true,
					decorationBox = { innerTextField ->
						Surface(
							shape = RoundedCornerShape(12.dp),
							color = Color.Transparent,
							border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
							modifier = Modifier.fillMaxSize()
						) {
							Box(
								modifier = Modifier
									.fillMaxSize()
									.padding(horizontal = 16.dp),
								contentAlignment = Alignment.CenterStart
							) {
								if (title.isEmpty()) {
									Text(
										"A transaction",
										style = MaterialTheme.typography.bodyLarge.copy(
											fontFamily = ibmPlexMonoFontFamily(),
											fontWeight = FontWeight.Medium,
											color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
										)
									)
								}
								innerTextField()
							}
						}
					}
				)
			}

			// Computed description
			val dateLabel = remember(date) {
				val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
				when (date) {
					today -> "Today"
					today.minus(1, DateTimeUnit.DAY) -> "Yesterday"
					today.plus(1, DateTimeUnit.DAY) -> "Tomorrow"
					else -> "${date.dayOfMonth} ${
						date.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
					}"
				}
			}

			val accountIcon = account?.let { IconProvider.resolve(it.iconName) }
			val categoryIcon = category?.let { IconProvider.resolve(it.iconName) }
			val amountFormatted = CurrencyFormatter.format(amountCents, currencyCode)

			val inlineContent = mapOf(
				"accountIcon" to InlineTextContent(
					Placeholder(
						width = 18.sp,
						height = 18.sp,
						placeholderVerticalAlign = PlaceholderVerticalAlign.Center
					)
				) {
					accountIcon?.let { icon ->
						Icon(
							imageVector = icon,
							contentDescription = null,
							tint = MaterialTheme.colorScheme.primary,
							modifier = Modifier.fillMaxSize()
						)
					}
				},
				"categoryIcon" to InlineTextContent(
					Placeholder(
						width = 18.sp,
						height = 18.sp,
						placeholderVerticalAlign = PlaceholderVerticalAlign.Center
					)
				) {
					categoryIcon?.let { icon ->
						Icon(
							imageVector = icon,
							contentDescription = null,
							tint = MaterialTheme.colorScheme.primary,
							modifier = Modifier.fillMaxSize()
						)
					}
				}
			)

			val annotatedString = buildAnnotatedString {
				val normalSpan = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)
				val boldSpan = SpanStyle(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
				val priceSpan = SpanStyle(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)

				withStyle(style = normalSpan) { append("A transaction on ") }
				withStyle(style = boldSpan) { append(dateLabel) }
				withStyle(style = normalSpan) { append(" from ") }

				if (accountIcon != null) {
					appendInlineContent("accountIcon", "[account]")
					append(" ")
				}
				withStyle(style = boldSpan) { append(account?.name ?: "—") }

				withStyle(style = normalSpan) { append(" to ") }

				if (categoryIcon != null) {
					appendInlineContent("categoryIcon", "[category]")
					append(" ")
				}
				withStyle(style = boldSpan) { append(category?.name ?: "—") }

				withStyle(style = normalSpan) { append(" for ") }
				withStyle(style = priceSpan) { append(amountFormatted) }
				withStyle(style = normalSpan) { append(".") }
			}

			Box(
				modifier = Modifier
					.fillMaxWidth()
					.clip(RoundedCornerShape(12.dp))
					.background(MaterialTheme.colorScheme.surfaceVariant)
					.padding(12.dp)
			) {
				Text(
					text = annotatedString,
					style = MaterialTheme.typography.bodyMedium,
					inlineContent = inlineContent
				)
			}

			// Option buttons
			Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
				ReviewOptionButton(
					label = "Add tag",
					icon = TablerIcons.Outlined.Tag
				)
				ReviewOptionButton(
					label = "Add receipt",
					icon = TablerIcons.Outlined.Receipt
				)
				ReviewOptionButton(
					label = "Make recurring transaction",
					icon = TablerIcons.Outlined.CreativeCommonsSa
				)
				ReviewOptionButton(
					label = "Add reminder",
					icon = TablerIcons.Outlined.Bell
				)
			}
		}

		// Floating dollar sign FAB anchored to bottom-right
		FloatingActionButton(
			onClick = onSave,
			shape = RoundedCornerShape(16.dp),
			containerColor = MaterialTheme.colorScheme.secondary,
			contentColor = MaterialTheme.colorScheme.onSurface,
			modifier = Modifier
				.align(Alignment.BottomEnd)
				.padding(top = 16.dp)
		) {
			Icon(
				imageVector = TablerIcons.Outlined.CurrencyDollar,
				contentDescription = "Adjust amount",
				modifier = Modifier.size(28.dp)
			)
		}
	}
}

@Composable
private fun ReviewOptionButton(
	label: String,
	icon: ImageVector
) {
	Surface(
		onClick = { /* TODO */ },
		shape = RoundedCornerShape(12.dp),
		color = MaterialTheme.colorScheme.surface,
		border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
		modifier = Modifier.wrapContentWidth()
	) {
		Row(
			modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(12.dp)
		) {
			Icon(
				imageVector = icon,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onSurface,
				modifier = Modifier.size(20.dp)
			)
			Text(
				text = label,
				style = MaterialTheme.typography.bodyLarge,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
	}
}