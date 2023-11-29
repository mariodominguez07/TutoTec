package mx.tecnm.cdhidalgo.tutotec.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mx.tecnm.cdhidalgo.tutotec.R
import mx.tecnm.cdhidalgo.tutotec.dataClass.Asistencias
import mx.tecnm.cdhidalgo.tutotec.dataClass.Solicitudes

class AdaptadorAsistenciaAlumno(private val listaAsistencias: List<Asistencias>)
    : RecyclerView.Adapter<AdaptadorAsistenciaAlumno.AsistenciasViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdaptadorAsistenciaAlumno.AsistenciasViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_asistencias_alumno, parent, false)
        return AsistenciasViewHolder(vista)
    }

    override fun onBindViewHolder(
        holder: AdaptadorAsistenciaAlumno.AsistenciasViewHolder,
        position: Int
    ) {
        val asistencia = listaAsistencias[position]
        holder.bind(asistencia)
    }

    override fun getItemCount(): Int {
        return listaAsistencias.size
    }

    inner class AsistenciasViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        private val actividad: TextView = itemView.findViewById(R.id.actividad_alumno_asis)
        private val fecha: TextView = itemView.findViewById(R.id.fecha_alumno_asis)
        private val asistio: TextView = itemView.findViewById(R.id.asistencia_alumno_asis)

        fun bind(asistencia: Asistencias) {
            actividad.text = asistencia.actividad
            fecha.text = asistencia.fecha
            asistio.text = asistencia.asistio
        }
    }
}