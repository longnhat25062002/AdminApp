package data

import com.google.firebase.database.PropertyName

data class Category(
    @get:PropertyName("ID") @set:PropertyName("ID") var ID: Int = 0,
    @get:PropertyName("Ten") @set:PropertyName("Ten") var Ten: String = ""
)
