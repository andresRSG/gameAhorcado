package com.example.ahorcadogame.models

data class UserApp (
    var name:String? = null,
    var idHome:Int? = null,
    //Config
    var sound:Boolean? = null,
    var vibrate:Boolean? = null
)

//    :SerializedName("UserApp"), Parcelable {
//    constructor(parcel: Parcel) : this(
//        parcel.readString(),
//        parcel.readValue(Int::class.java.classLoader) as? Int,
//        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
//        parcel.readValue(Boolean::class.java.classLoader) as? Boolean
//    ) {
//    }
//
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeString(name)
//        parcel.writeValue(idHome)
//        parcel.writeValue(sound)
//        parcel.writeValue(vibrate)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<UserApp> {
//        override fun createFromParcel(parcel: Parcel): UserApp {
//            return UserApp(parcel)
//        }
//
//        override fun newArray(size: Int): Array<UserApp?> {
//            return arrayOfNulls(size)
//        }
//    }
//}
