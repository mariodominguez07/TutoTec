package mx.tecnm.cdhidalgo.tutotec.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mx.tecnm.cdhidalgo.tutotec.R
import mx.tecnm.cdhidalgo.tutotec.dataClass.Alumno
import mx.tecnm.cdhidalgo.tutotec.dataClass.Solicitudes

class AdaptadorSolicitudesAlumno (private val listaSolicitudes: List<Solicitudes>, private val clickListener: SolicitudClickListener)
    : RecyclerView.Adapter<AdaptadorSolicitudesAlumno.SolicitudViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdaptadorSolicitudesAlumno.SolicitudViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_soli_alumno, parent, false)
        return SolicitudViewHolder(vista)
    }

    override fun onBindViewHolder(
        holder: AdaptadorSolicitudesAlumno.SolicitudViewHolder,
        position: Int
    ) {
        val solicitud = listaSolicitudes[position]
        holder.bind(solicitud)
    }

    override fun getItemCount(): Int {
        return listaSolicitudes.size
    }

    interface SolicitudClickListener {
        fun onConfirmarClick(solicitud: Solicitudes)
    }
    inner class SolicitudViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        private val tema: TextView = itemView.findViewById(R.id.tema_alumno_soli)
        private val estatus: TextView = itemView.findViewById(R.id.estatus_alumno_soli)
        private val btnConfirmar: ImageButton = itemView.findViewById(R.id.confirmar_soli_alumno)

        fun bind(solicitud: Solicitudes) {
            tema.text = solicitud.tema
            estatus.text = solicitud.estatus

            btnConfirmar.setOnClickListener {
                clickListener.onConfirmarClick(solicitud)
                btnConfirmar.isEnabled = false
            }
        }
    }
}