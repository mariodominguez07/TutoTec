package mx.tecnm.cdhidalgo.tutotec.dataClass

import java.sql.Date

data class Asistencias(
    val actividad: String,
    val fecha: String,
    val hora: String,
    val grupo: String,
    val nomalumno: String,
    val nocontrol: String,
    val asistio: String
){
    // Constructor sin argumentos
    constructor() : this("", "", "", "", "", "", "")
}
