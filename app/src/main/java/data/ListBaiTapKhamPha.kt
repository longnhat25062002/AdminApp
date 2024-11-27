package data

import com.google.firebase.database.PropertyName

data class ListBaiTapKhamPha(
    @get:PropertyName("ID") @set:PropertyName("ID") var ID: Int = 0,
    @get:PropertyName("SoRep") @set:PropertyName("SoRep") var SoRep: String = "",
    @get:PropertyName("TenBaiTap") @set:PropertyName("TenBaiTap") var TenBaiTap: String = "",
    @get:PropertyName("ThoiGian") @set:PropertyName("ThoiGian") var ThoiGian: String = "",
    @get:PropertyName("Video") @set:PropertyName("Video") var Video: String = ""
)
