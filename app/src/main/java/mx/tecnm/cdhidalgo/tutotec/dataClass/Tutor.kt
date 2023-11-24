package mx.tecnm.cdhidalgo.tutotec.dataClass

import android.os.Parcel
import android.os.Parcelable

data class Tutor(
    val correo:String? = null,
    val nombre:String? = null,
    val apellido_pa:String? = null,
    val apellido_ma:String? = null,
    val academia:String? = null,
    val grupo:String? = null,
    val foto:String? = null
    ): Parcelable {
    constructor() : this(null, null, null, null, null, null, null)
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(correo)
        parcel.writeString(nombre)
        parcel.writeString(apellido_pa)
        parcel.writeString(apellido_ma)
        parcel.writeString(academia)
        parcel.writeString(grupo)
        parcel.writeString(foto)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Tutor> {
        override fun createFromParcel(parcel: Parcel): Tutor {
            return Tutor(parcel)
        }

        override fun newArray(size: Int): Array<Tutor?> {
            return arrayOfNulls(size)
        }
    }
}
