package id.serviskus.app.core.format

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class ServisKuFormatterTest {

    @Test
    fun `formatRupiah memformat nilai batas dan jutaan dengan pemisah titik`() {
        assertEquals("Rp 0", ServisKuFormatter.formatRupiah(0L))
        assertEquals("Rp 999", ServisKuFormatter.formatRupiah(999L))
        assertEquals("Rp 1.000", ServisKuFormatter.formatRupiah(1_000L))
        assertEquals("Rp 1.250.000", ServisKuFormatter.formatRupiah(1_250_000L))
    }

    @Test
    fun `formatKilometers memformat angka kilometer sesuai pola ServisKu`() {
        assertEquals("0 km", ServisKuFormatter.formatKilometers(0))
        assertEquals("999 km", ServisKuFormatter.formatKilometers(999))
        assertEquals("1.000 km", ServisKuFormatter.formatKilometers(1_000))
        assertEquals("12.450 km", ServisKuFormatter.formatKilometers(12_450))
        assertEquals("1.250.000 km", ServisKuFormatter.formatKilometers(1_250_000))
    }

    @Test
    fun `formatDate menghasilkan format tanggal Indonesia ringkas`() {
        assertEquals("14 Nov 2026", ServisKuFormatter.formatDate(LocalDate.of(2026, 11, 14)))
        assertEquals("3 Mei 2026", ServisKuFormatter.formatDate(LocalDate.of(2026, 5, 3)))
    }
}
