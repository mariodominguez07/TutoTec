package mx.tecnm.cdhidalgo.tutotec.dataClass

import android.os.Parcel
import android.os.Parcelable

data class Alumno(
    val correo: String? = null,
    val no_control:String? = null,
    val nombre:String? = null,
    val apellido_pa:String? = null,
    val apellido_ma:String? = null,
    val carrera:String? = null,
    val grupo:String? = null,
    val foto:String? = null
) : Parcelable {
    constructor() : this(null, null, null, null, null, null,null,null)

    constructor(parcel: Parcel) : this(
        parcel.readString(),
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
        parcel.writeString(no_control)
        parcel.writeString(nombre)
        parcel.writeString(apellido_pa)
        parcel.writeString(apellido_ma)
        parcel.writeString(carrera)
        parcel.writeString(grupo)
        parcel.writeString(foto)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Alumno> {
        override fun createFromParcel(parcel: Parcel): Alumno {
            return Alumno(parcel)
        }

        override fun newArray(size: Int): Array<Alumno?> {
            return arrayOfNulls(size)
        }
    }
}
