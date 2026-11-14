package id.serviskus.app

import androidx.compose.ui.unit.dp
import id.serviskus.app.core.designsystem.Spacing
import id.serviskus.app.core.designsystem.VehicleAccent
import org.junit.Assert.assertEquals
import org.junit.Test

private const val EXPECTED_ACCENT_COUNT = 8

class DesignSystemTokensTest {

    @Test
    fun `skala spasi mengikuti kelipatan 4dp sesuai DESIGN md`() {
        val expected = listOf(4.dp, 8.dp, 12.dp, 16.dp, 24.dp, 32.dp, 48.dp)
        val actual = listOf(
            Spacing.xs,
            Spacing.sm,
            Spacing.md,
            Spacing.base,
            Spacing.lg,
            Spacing.xl,
            Spacing.xxl,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `aksen motor berjumlah delapan dengan default biru oli`() {
        assertEquals(EXPECTED_ACCENT_COUNT, VehicleAccent.entries.size)
        assertEquals(VehicleAccent.BIRU_OLI, VehicleAccent.DEFAULT)
        assertEquals(EXPECTED_ACCENT_COUNT, VehicleAccent.entries.map { it.key }.distinct().size)
    }
}
