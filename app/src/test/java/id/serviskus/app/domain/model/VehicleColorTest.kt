package id.serviskus.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class VehicleColorTest {

    @Test
    fun `semua kunci warna DESIGN dapat dipetakan ulang`() {
        val expectedPairs = listOf(
            "biru-oli" to VehicleColor.BIRU_OLI,
            "teal" to VehicleColor.TEAL,
            "ungu" to VehicleColor.UNGU,
            "plum" to VehicleColor.PLUM,
            "coklat" to VehicleColor.COKLAT,
            "baja" to VehicleColor.BAJA,
            "biru-terang" to VehicleColor.BIRU_TERANG,
            "arang" to VehicleColor.ARANG,
        )

        assertEquals(expectedPairs.size, VehicleColor.entries.size)
        expectedPairs.forEach { (key, expectedColor) ->
            assertEquals(key, expectedColor.key)
            assertEquals(expectedColor, VehicleColor.fromKey(key))
        }
    }

    @Test
    fun `kunci tidak dikenal atau null jatuh ke biru-oli`() {
        assertEquals(VehicleColor.BIRU_OLI, VehicleColor.fromKey(null))
        assertEquals(VehicleColor.BIRU_OLI, VehicleColor.fromKey(""))
        assertEquals(VehicleColor.BIRU_OLI, VehicleColor.fromKey("merah"))
        assertEquals(VehicleColor.BIRU_OLI, VehicleColor.fromKey("BIRU-OLI"))
    }
}
