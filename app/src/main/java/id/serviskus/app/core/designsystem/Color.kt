package id.serviskus.app.core.designsystem

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Light palette tokens from DESIGN.md
val LightBg = Color(0xFFF5F2EB)
val LightSurface = Color(0xFFFDFCF8)
val LightSurfaceVariant = Color(0xFFEAE5DA)
val LightInk = Color(0xFF1C1B18)
val LightInkMuted = Color(0xFF5E5A50)
val LightOutline = Color(0xFFCFC8B8)
val LightOutlineStrong = Color(0xFF898273)
val LightOverdue = Color(0xFFB3261E)
val LightSoon = Color(0xFF8A5A00)
val LightOk = Color(0xFF2F6B3A)
val LightOnPrimary = Color(0xFFFFFFFF)

// Dark palette tokens from DESIGN.md
val DarkBg = Color(0xFF151412)
val DarkSurface = Color(0xFF1E1C19)
val DarkSurfaceVariant = Color(0xFF2A2723)
val DarkInk = Color(0xFFECE8DF)
val DarkInkMuted = Color(0xFFA8A294)
val DarkOutline = Color(0xFF3A3630)
val DarkOutlineStrong = Color(0xFF7D766A)
val DarkOverdue = Color(0xFFFFB4AB)
val DarkSoon = Color(0xFFE8B84A)
val DarkOk = Color(0xFF8FD19A)
val DarkOnPrimary = Color(0xFF151412)

enum class VehicleAccent(
    val key: String,
    val light: Color,
    val dark: Color,
) {
    BIRU_OLI(key = "biru-oli", light = Color(0xFF1D4E6B), dark = Color(0xFF8FC1E0)),
    TEAL(key = "teal", light = Color(0xFF14675E), dark = Color(0xFF7FCBC0)),
    UNGU(key = "ungu", light = Color(0xFF5B3F8C), dark = Color(0xFFC4B0EC)),
    PLUM(key = "plum", light = Color(0xFF8C3B62), dark = Color(0xFFE5A5C4)),
    COKLAT(key = "coklat", light = Color(0xFF7A5230), dark = Color(0xFFD9B48C)),
    BAJA(key = "baja", light = Color(0xFF4A5560), dark = Color(0xFFAEB9C4)),
    BIRU_TERANG(key = "biru-terang", light = Color(0xFF2A66B0), dark = Color(0xFF9DC1F0)),
    ARANG(key = "arang", light = Color(0xFF2B2B2B), dark = Color(0xFFD0D0D0)),
    ;

    companion object {
        val DEFAULT = BIRU_OLI

        fun fromKey(key: String?): VehicleAccent =
            entries.firstOrNull { it.key == key } ?: DEFAULT
    }
}

@Immutable
data class StatusColors(
    val overdue: Color,
    val soon: Color,
    val ok: Color,
)

val LightStatusColors = StatusColors(
    overdue = LightOverdue,
    soon = LightSoon,
    ok = LightOk,
)

val DarkStatusColors = StatusColors(
    overdue = DarkOverdue,
    soon = DarkSoon,
    ok = DarkOk,
)

val LocalStatusColors = staticCompositionLocalOf<StatusColors> {
    error("StatusColors not provided; wrap content in ServisKuTheme")
}

fun servisKuLightColorScheme(accent: VehicleAccent = VehicleAccent.DEFAULT): ColorScheme =
    lightColorScheme(
        primary = accent.light,
        onPrimary = LightOnPrimary,
        background = LightBg,
        onBackground = LightInk,
        surface = LightSurface,
        onSurface = LightInk,
        surfaceVariant = LightSurfaceVariant,
        onSurfaceVariant = LightInkMuted,
        outline = LightOutlineStrong,
        outlineVariant = LightOutline,
        error = LightOverdue,
        onError = LightOnPrimary,
    )

fun servisKuDarkColorScheme(accent: VehicleAccent = VehicleAccent.DEFAULT): ColorScheme =
    darkColorScheme(
        primary = accent.dark,
        onPrimary = DarkOnPrimary,
        background = DarkBg,
        onBackground = DarkInk,
        surface = DarkSurface,
        onSurface = DarkInk,
        surfaceVariant = DarkSurfaceVariant,
        onSurfaceVariant = DarkInkMuted,
        outline = DarkOutlineStrong,
        outlineVariant = DarkOutline,
        error = DarkOverdue,
        onError = DarkOnPrimary,
    )
