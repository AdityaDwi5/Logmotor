package id.serviskus.app.domain.model

enum class VehicleColor(val key: String) {
    BIRU_OLI("biru-oli"),
    TEAL("teal"),
    UNGU("ungu"),
    PLUM("plum"),
    COKLAT("coklat"),
    BAJA("baja"),
    BIRU_TERANG("biru-terang"),
    ARANG("arang"),
    ;

    companion object {
        val DEFAULT: VehicleColor = BIRU_OLI

        fun fromKey(key: String?): VehicleColor =
            entries.firstOrNull { it.key == key } ?: DEFAULT
    }
}
