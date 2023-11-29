package mx.tecnm.cdhidalgo.tutotec.dataClass

data class Solicitudes(
    val tipo :String,
    val tema: String,
    val area: String,
    val nocontrol: String,
    val nombre: String,
    val grupo: String,
    val estatus: String
){
    // Constructor sin argumentos
    constructor() : this("", "", "", "", "", "", "")
}
