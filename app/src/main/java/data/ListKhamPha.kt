package data

import com.google.firebase.database.PropertyName

data class ListKhamPha(
    @get:PropertyName("ID") @set:PropertyName("ID")
    var ID: Int = 0,

    @get:PropertyName("IDKhamPha") @set:PropertyName("IDKhamPha")
    var IDKhamPha: Int = 0,

    @get:PropertyName("Ten") @set:PropertyName("Ten")
    var Ten: String = "",

    @get:PropertyName("ListBaiTapKhamPha") @set:PropertyName("ListBaiTapKhamPha")
    var ListBaiTapKhamPha: List<Int>? = null
)
