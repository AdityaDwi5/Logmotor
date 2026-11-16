package id.serviskus.app.core.format

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.util.Locale

object ServisKuFormatter {

    private val indonesianSymbols = DecimalFormatSymbols(Locale.forLanguageTag("id-ID")).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }

    private val shortMonths = arrayOf(
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "Mei",
        "Jun",
        "Jul",
        "Agu",
        "Sep",
        "Okt",
        "Nov",
        "Des",
    )

    fun formatNumber(value: Long): String {
        val formatter = DecimalFormat("#,##0", indonesianSymbols)
        return formatter.format(value)
    }

    fun formatNumber(value: Int): String = formatNumber(value.toLong())

    fun formatRupiah(amount: Long): String =
        if (amount < 0L) {
            "-Rp ${formatNumber(-amount)}"
        } else {
            "Rp ${formatNumber(amount)}"
        }

    fun formatKilometers(km: Int): String = "${formatNumber(km)} km"

    fun formatDate(date: LocalDate): String {
        val monthLabel = shortMonths[date.monthValue - 1]
        return "${date.dayOfMonth} $monthLabel ${date.year}"
    }
}
