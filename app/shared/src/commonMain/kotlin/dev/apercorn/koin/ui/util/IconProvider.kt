package dev.apercorn.koin.ui.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.*


object IconProvider {
	val iconPalette: Map<String, ImageVector> = mapOf(
		// Finance
		"building-bank" to TablerIcons.Outlined.BuildingBank,
		"cash" to TablerIcons.Outlined.Cash,
		"cash-banknote" to TablerIcons.Outlined.CashBanknote,
		"credit-card" to TablerIcons.Outlined.CreditCard,
		"wallet" to TablerIcons.Outlined.Wallet,
		"coin" to TablerIcons.Outlined.Coin,
		"currency-dollar" to TablerIcons.Outlined.CurrencyDollar,
		"receipt" to TablerIcons.Outlined.Receipt,
		"report-money" to TablerIcons.Outlined.ReportMoney,
		"chart-bar" to TablerIcons.Outlined.ChartBar,
		"trending-up" to TablerIcons.Outlined.TrendingUp,
		"arrow-up-circle" to TablerIcons.Outlined.ArrowUpCircle,

		// Food & Dining
		"utensils" to TablerIcons.Outlined.ToolsKitchen,
		"pizza" to TablerIcons.Outlined.Pizza,
		"coffee" to TablerIcons.Outlined.Mug,
		"ice-cream" to TablerIcons.Outlined.IceCream,
		"glass-full" to TablerIcons.Outlined.GlassFull,

		// Transport
		"car" to TablerIcons.Outlined.Car,
		"bus" to TablerIcons.Outlined.Bus,
		"plane" to TablerIcons.Outlined.Plane,
		"bike" to TablerIcons.Outlined.Bike,
		"ship" to TablerIcons.Outlined.Ship,

		// Shopping
		"shopping-bag" to TablerIcons.Outlined.Basket,
		"shopping-cart" to TablerIcons.Outlined.ShoppingCart,
		"tag" to TablerIcons.Outlined.Tag,
		"gift" to TablerIcons.Outlined.Gift,

		// Home & Housing
		"home" to TablerIcons.Outlined.Home,
		"home-2" to TablerIcons.Outlined.Home2,
		"building" to TablerIcons.Outlined.Building,
		"bed" to TablerIcons.Outlined.Bed,

		// Bills & Utilities
		"file-invoice" to TablerIcons.Outlined.FileInvoice,
		"bolt" to TablerIcons.Outlined.Bolt,
		"droplet" to TablerIcons.Outlined.Droplet,
		"wifi" to TablerIcons.Outlined.Wifi,
		"phone" to TablerIcons.Outlined.Phone,

		// Entertainment
		"movie" to TablerIcons.Outlined.Movie,
		"music" to TablerIcons.Outlined.Music,
		"device-gamepad" to TablerIcons.Outlined.DeviceGamepad,
		"ball-football" to TablerIcons.Outlined.BallFootball,
		"device-tv" to TablerIcons.Outlined.DeviceTv,

		// Health
		"heart" to TablerIcons.Outlined.Heart,
		"ambulance" to TablerIcons.Outlined.Ambulance,
		"stethoscope" to TablerIcons.Outlined.Stethoscope,
		"pill" to TablerIcons.Outlined.Pill,

		// Education
		"book" to TablerIcons.Outlined.Book,
		"school" to TablerIcons.Outlined.School,
		"pencil" to TablerIcons.Outlined.Pencil,
		"bookmarks" to TablerIcons.Outlined.Bookmarks,

		// Income & Work
		"briefcase" to TablerIcons.Outlined.Briefcase,

		// General / Misc
		"star" to TablerIcons.Outlined.Star,
		"settings" to TablerIcons.Outlined.Settings,
		"user" to TablerIcons.Outlined.User,
		"world" to TablerIcons.Outlined.World,
		"bell" to TablerIcons.Outlined.Bell,
		"flag" to TablerIcons.Outlined.Flag,
		"dots" to TablerIcons.Outlined.Dots,
		"archive" to TablerIcons.Outlined.Archive,
		"palette" to TablerIcons.Outlined.Palette,
		"photo" to TablerIcons.Outlined.Photo,
	)

	val defaultIcon: ImageVector = TablerIcons.Outlined.QuestionMark

	fun resolve(iconName: String): ImageVector {
		return iconPalette[iconName]
			?: defaultIcon
	}

	fun parseColor(hex: String, fallback: Color = Color(0xFF26547B)): Color {
		val cleanHex = hex.removePrefix("#")
		return try {
			when (cleanHex.length) {
				6 -> Color(cleanHex.toLong(16) or 0xFF000000L)
				8 -> Color(cleanHex.toLong(16))
				else -> fallback
			}
		} catch (_: Exception) {
			fallback
		}
	}
}
