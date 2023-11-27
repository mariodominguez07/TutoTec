package mx.tecnm.cdhidalgo.tutotec.dataClass

import java.sql.Date

data class Asistencias(
    val actividad: String,
    val fechayHora: Date,
    val grupo: String,
    val nomAlumno: String,
    val nocontrol: String,
    val asistio: String
)
