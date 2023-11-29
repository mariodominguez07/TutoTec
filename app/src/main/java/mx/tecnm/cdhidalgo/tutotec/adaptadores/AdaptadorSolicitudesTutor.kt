package mx.tecnm.cdhidalgo.tutotec.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mx.tecnm.cdhidalgo.tutotec.R
import mx.tecnm.cdhidalgo.tutotec.dataClass.Alumno
import mx.tecnm.cdhidalgo.tutotec.dataClass.Solicitudes
import mx.tecnm.cdhidalgo.tutotec.tutor

class AdaptadorSolicitudesTutor (private val listaSolicitudes: List<Solicitudes>, private val clickListener: (Solicitudes, action: String) -> Unit)
    : RecyclerView.Adapter<AdaptadorSolicitudesTutor.SolicitudViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdaptadorSolicitudesTutor.SolicitudViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_soli_tutor, parent, false)
        return SolicitudViewHolder(vista)
    }

    override fun onBindViewHolder(
        holder: AdaptadorSolicitudesTutor.SolicitudViewHolder,
        position: Int
    ) {
        val solicitud = listaSolicitudes[position]
        holder.bind(solicitud)
    }

    override fun getItemCount(): Int {
        return listaSolicitudes.size
    }
    inner class SolicitudViewHolder (itemView: View):RecyclerView.ViewHolder(itemView){
        private val tema: TextView = itemView.findViewById(R.id.tema_soli_tutor)
        private val area: TextView = itemView.findViewById(R.id.area_soli_tutor)
        private val nombre: TextView = itemView.findViewById(R.id.nombre_soli_tutor)
        private val btnConfirmar: ImageButton = itemView.findViewById(R.id.confirmar_soli_tutor)
        private val btnRechazar: ImageButton = itemView.findViewById(R.id.rechazar_soli_tutor)

        fun bind(solicitud: Solicitudes) {
            val baseDeDatos = Firebase.firestore
            tema.text = solicitud.tema
            area.text = solicitud.area
            nombre.text = solicitud.nombre
            baseDeDatos.collection("solicitudes")
                .whereEqualTo("grupo", tutor.grupo)
                .get().addOnSuccessListener { documents ->
                    for (documento in documents) {
                        val estatus = documento.getString("estatus")

                        if (estatus == "Solicitud Finalizada" || estatus == "Solicitud Rechazada"){
                            btnConfirmar.isEnabled = false
                            btnRechazar.isEnabled = false
                        }else{
                            btnConfirmar.setOnClickListener {
                                clickListener(solicitud,"confirmar asistencia")
                                btnConfirmar.isEnabled = false
                                btnRechazar.isEnabled = false
                            }
                            btnRechazar.setOnClickListener {
                                clickListener(solicitud,"confirmar falta")
                                btnConfirmar.isEnabled = false
                                btnRechazar.isEnabled = false
                            }
                        }
                    }
                }
        }
    }
}