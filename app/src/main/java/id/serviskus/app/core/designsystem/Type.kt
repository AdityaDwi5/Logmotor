package id.serviskus.app.core.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import id.serviskus.app.R

val IbmPlexSans = FontFamily(
    Font(R.font.ibm_plex_sans_regular, FontWeight.Normal),
    Font(R.font.ibm_plex_sans_medium, FontWeight.Medium),
    Font(R.font.ibm_plex_sans_semibold, FontWeight.SemiBold),
)

val IbmPlexMono = FontFamily(
    Font(R.font.ibm_plex_mono_regular, FontWeight.Normal),
    Font(R.font.ibm_plex_mono_medium, FontWeight.Medium),
)

private val OdometerHeroStyle = TextStyle(
    fontFamily = IbmPlexMono,
    fontWeight = FontWeight.Medium,
    fontSize = 40.sp,
    lineHeight = 48.sp,
)

private val NumberLargeStyle = TextStyle(
    fontFamily = IbmPlexMono,
    fontWeight = FontWeight.Medium,
    fontSize = 24.sp,
    lineHeight = 32.sp,
)

private val TitleLargeStyle = TextStyle(
    fontFamily = IbmPlexSans,
    fontWeight = FontWeight.SemiBold,
    fontSize = 22.sp,
    lineHeight = 28.sp,
)

private val TitleMediumStyle = TextStyle(
    fontFamily = IbmPlexSans,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 24.sp,
)

private val BodyStyle = TextStyle(
    fontFamily = IbmPlexSans,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
)

private val BodySmallStyle = TextStyle(
    fontFamily = IbmPlexSans,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
)

private val LabelStyle = TextStyle(
    fontFamily = IbmPlexSans,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.4.sp,
)

val ServisKuTypography = Typography(
    displayLarge = OdometerHeroStyle,
    headlineMedium = NumberLargeStyle,
    titleLarge = TitleLargeStyle,
    titleMedium = TitleMediumStyle,
    bodyLarge = BodyStyle,
    bodyMedium = BodySmallStyle,
    bodySmall = BodySmallStyle,
    labelLarge = LabelStyle,
    labelMedium = LabelStyle,
    labelSmall = LabelStyle,
)

val Typography.odometerHero: TextStyle
    get() = displayLarge

val Typography.numberLarge: TextStyle
    get() = headlineMedium

val TextStyle.tabular: TextStyle
    get() = copy(fontFeatureSettings = "tnum")
