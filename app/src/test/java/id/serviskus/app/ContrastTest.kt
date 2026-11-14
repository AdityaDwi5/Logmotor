package id.serviskus.app

import androidx.compose.ui.graphics.Color
import id.serviskus.app.core.designsystem.DarkBg
import id.serviskus.app.core.designsystem.DarkInk
import id.serviskus.app.core.designsystem.DarkInkMuted
import id.serviskus.app.core.designsystem.DarkOk
import id.serviskus.app.core.designsystem.DarkOnPrimary
import id.serviskus.app.core.designsystem.DarkOutlineStrong
import id.serviskus.app.core.designsystem.DarkOverdue
import id.serviskus.app.core.designsystem.DarkSoon
import id.serviskus.app.core.designsystem.DarkSurface
import id.serviskus.app.core.designsystem.DarkSurfaceVariant
import id.serviskus.app.core.designsystem.LightBg
import id.serviskus.app.core.designsystem.LightInk
import id.serviskus.app.core.designsystem.LightInkMuted
import id.serviskus.app.core.designsystem.LightOk
import id.serviskus.app.core.designsystem.LightOnPrimary
import id.serviskus.app.core.designsystem.LightOutlineStrong
import id.serviskus.app.core.designsystem.LightOverdue
import id.serviskus.app.core.designsystem.LightSoon
import id.serviskus.app.core.designsystem.LightSurface
import id.serviskus.app.core.designsystem.LightSurfaceVariant
import id.serviskus.app.core.designsystem.VehicleAccent
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.pow

private const val MIN_TEXT_CONTRAST = 4.5
private const val MIN_INTERACTIVE_CONTRAST = 3.0

class ContrastTest {

    @Test
    fun `teks tema terang memenuhi kontras minimal 4 koma 5 banding 1`() {
        val foregrounds = listOf(
            "LightInk" to LightInk,
            "LightInkMuted" to LightInkMuted,
            "LightOverdue" to LightOverdue,
            "LightSoon" to LightSoon,
            "LightOk" to LightOk,
        )
        val backgrounds = listOf(
            "LightBg" to LightBg,
            "LightSurface" to LightSurface,
            "LightSurfaceVariant" to LightSurfaceVariant,
        )

        assertPairsMeetContrast(foregrounds, backgrounds, MIN_TEXT_CONTRAST)
    }

    @Test
    fun `teks tema gelap memenuhi kontras minimal 4 koma 5 banding 1`() {
        val foregrounds = listOf(
            "DarkInk" to DarkInk,
            "DarkInkMuted" to DarkInkMuted,
            "DarkOverdue" to DarkOverdue,
            "DarkSoon" to DarkSoon,
            "DarkOk" to DarkOk,
        )
        val backgrounds = listOf(
            "DarkBg" to DarkBg,
            "DarkSurface" to DarkSurface,
            "DarkSurfaceVariant" to DarkSurfaceVariant,
        )

        assertPairsMeetContrast(foregrounds, backgrounds, MIN_TEXT_CONTRAST)
    }

    @Test
    fun `teks onPrimary di atas semua aksen motor memenuhi kontras minimal 4 koma 5 banding 1`() {
        VehicleAccent.entries.forEach { accent ->
            val lightRatio = contrastRatio(LightOnPrimary, accent.light)
            assertTrue(
                "LightOnPrimary on ${accent.key} ratio=$lightRatio < $MIN_TEXT_CONTRAST",
                lightRatio >= MIN_TEXT_CONTRAST,
            )

            val darkRatio = contrastRatio(DarkOnPrimary, accent.dark)
            assertTrue(
                "DarkOnPrimary on ${accent.key} ratio=$darkRatio < $MIN_TEXT_CONTRAST",
                darkRatio >= MIN_TEXT_CONTRAST,
            )
        }
    }

    @Test
    fun `semua aksen motor memenuhi kontras minimal 3 banding 1 di atas latar dan permukaan`() {
        val lightBackgrounds = listOf(
            "LightBg" to LightBg,
            "LightSurface" to LightSurface,
            "LightSurfaceVariant" to LightSurfaceVariant,
        )
        val darkBackgrounds = listOf(
            "DarkBg" to DarkBg,
            "DarkSurface" to DarkSurface,
            "DarkSurfaceVariant" to DarkSurfaceVariant,
        )

        VehicleAccent.entries.forEach { accent ->
            assertPairsMeetContrast(
                foregrounds = listOf("accentLight:${accent.key}" to accent.light),
                backgrounds = lightBackgrounds,
                minRatio = MIN_INTERACTIVE_CONTRAST,
            )
            assertPairsMeetContrast(
                foregrounds = listOf("accentDark:${accent.key}" to accent.dark),
                backgrounds = darkBackgrounds,
                minRatio = MIN_INTERACTIVE_CONTRAST,
            )
        }
    }

    @Test
    fun `garis batas outlineStrong memenuhi kontras minimal 3 banding 1 di atas latar dan permukaan`() {
        assertPairsMeetContrast(
            foregrounds = listOf("LightOutlineStrong" to LightOutlineStrong),
            backgrounds = listOf(
                "LightBg" to LightBg,
                "LightSurface" to LightSurface,
                "LightSurfaceVariant" to LightSurfaceVariant,
            ),
            minRatio = MIN_INTERACTIVE_CONTRAST,
        )
        assertPairsMeetContrast(
            foregrounds = listOf("DarkOutlineStrong" to DarkOutlineStrong),
            backgrounds = listOf(
                "DarkBg" to DarkBg,
                "DarkSurface" to DarkSurface,
                "DarkSurfaceVariant" to DarkSurfaceVariant,
            ),
            minRatio = MIN_INTERACTIVE_CONTRAST,
        )
    }

    private fun assertPairsMeetContrast(
        foregrounds: List<Pair<String, Color>>,
        backgrounds: List<Pair<String, Color>>,
        minRatio: Double,
    ) {
        foregrounds.forEach { (fgName, fg) ->
            backgrounds.forEach { (bgName, bg) ->
                val ratio = contrastRatio(fg, bg)
                assertTrue(
                    "$fgName on $bgName ratio=$ratio < $minRatio",
                    ratio >= minRatio,
                )
            }
        }
    }

    private fun contrastRatio(foreground: Color, background: Color): Double {
        val l1 = foreground.relativeLuminance()
        val l2 = background.relativeLuminance()
        val lighter = maxOf(l1, l2)
        val darker = minOf(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    private fun Color.relativeLuminance(): Double {
        fun linearize(channel: Float): Double {
            val value = channel.toDouble()
            return if (value <= 0.04045) {
                value / 12.92
            } else {
                ((value + 0.055) / 1.055).pow(2.4)
            }
        }
        return 0.2126 * linearize(red) + 0.7152 * linearize(green) + 0.0722 * linearize(blue)
    }
}
