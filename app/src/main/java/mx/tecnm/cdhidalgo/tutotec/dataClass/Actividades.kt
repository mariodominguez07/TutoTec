package mx.tecnm.cdhidalgo.tutotec.dataClass

import android.os.Parcel
import android.os.Parcelable
import java.sql.Date

data class Actividades(
    val titulo: String? = null,
    val descripcion: String?= null,
    val fechayHora:String?= null,
    val grupo: String?= null,
    val tutor: String?= null,
    val evidencia: String?= null
) : Parcelable {
    constructor() : this(null, null, null, null, null, null)
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(titulo)
        parcel.writeString(descripcion)
        parcel.writeString(fechayHora)
        parcel.writeString(grupo)
        parcel.writeString(tutor)
        parcel.writeString(evidencia)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Actividades> {
        override fun createFromParcel(parcel: Parcel): Actividades {
            return Actividades(parcel)
        }

        override fun newArray(size: Int): Array<Actividades?> {
            return arrayOfNulls(size)
        }
    }
}
