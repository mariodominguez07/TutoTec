package mx.tecnm.cdhidalgo.tutotec.dataClass

import android.os.Parcel
import android.os.Parcelable

data class Alumno(
    val correo: String? = null,
    val nocontrol:String? = null,
    val nombre:String? = null,
    val apellido_pa:String? = null,
    val apellido_ma:String? = null,
    val carrera:String? = null,
    val grupo:String? = null,
    val foto:String? = null
) :Parcelable {
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

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(correo)
        dest.writeString(nocontrol)
        dest.writeString(nombre)
        dest.writeString(apellido_pa)
        dest.writeString(apellido_ma)
        dest.writeString(carrera)
        dest.writeString(grupo)
        dest.writeString(foto)
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