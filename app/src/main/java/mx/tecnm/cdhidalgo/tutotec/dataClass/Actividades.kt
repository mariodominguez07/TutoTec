package mx.tecnm.cdhidalgo.tutotec.dataClass

import java.sql.Date

data class Actividades(
    val titulo: String,
    val descripcion: String,
    val fechayHora:Date,
    val grupo: String,
    val tutor: String,
    val evidencia: String
)
