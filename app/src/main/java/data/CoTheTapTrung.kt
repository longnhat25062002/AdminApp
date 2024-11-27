package data


import com.google.firebase.database.PropertyName

data class CoTheTapTrung(
    @get:PropertyName("BaiTapChinh") @set:PropertyName("BaiTapChinh")
    var BaiTapChinh: List<Int>? = null,

    @get:PropertyName("GianCo") @set:PropertyName("GianCo")
    var GianCo: List<Int>? = null,
    @get:PropertyName("KhoiDong") @set:PropertyName("KhoiDong")
    var KhoiDong: List<Int>? = null
)

