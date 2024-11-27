package data

import com.google.firebase.database.PropertyName

data class ExciseWarmup(
    @get:PropertyName("ID") @set:PropertyName("ID") var ID: Int = 0,

    @get:PropertyName("TenBaiTap") @set:PropertyName("TenBaiTap") var TenBaiTap: String = "",

    @get:PropertyName("BoPhan") @set:PropertyName("BoPhan") var BoPhan: String = "",

    @get:PropertyName("ThoiGian") @set:PropertyName("ThoiGian") var ThoiGian: Int = 0,

    @get:PropertyName("Video") @set:PropertyName("Video") var Video: String = ""
)
