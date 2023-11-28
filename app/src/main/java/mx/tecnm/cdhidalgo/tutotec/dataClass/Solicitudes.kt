package mx.tecnm.cdhidalgo.tutotec.dataClass

data class Solicitudes(
    val tipo :String,
    val tema: String,
    val departamento: String,
    val nocontrol: String,
    val nombre: String,
    val estatus: String
){
    // Constructor sin argumentos
    constructor() : this("", "", "", "", "", "")
}
