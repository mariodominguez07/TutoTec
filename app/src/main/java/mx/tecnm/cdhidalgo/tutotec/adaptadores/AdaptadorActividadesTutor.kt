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
import mx.tecnm.cdhidalgo.tutotec.dataClass.Actividades
import mx.tecnm.cdhidalgo.tutotec.dataClass.Asistencias
import mx.tecnm.cdhidalgo.tutotec.tutor

class AdaptadorActividadesTutor (private val listaActividades: List<Actividades>, private val clickListener: ActividadClickListener)
    : RecyclerView.Adapter<AdaptadorActividadesTutor.ActividadViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdaptadorActividadesTutor.ActividadViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_actividades, parent, false)
        return ActividadViewHolder(vista)
    }

    override fun onBindViewHolder(
        holder: AdaptadorActividadesTutor.ActividadViewHolder,
        position: Int
    ) {
        val actividad = listaActividades[position]
        holder.bind(actividad)
    }

    override fun getItemCount(): Int {
        return listaActividades.size
    }
    interface ActividadClickListener {
        fun onEliminarClick(actividad: Actividades)
    }
    inner class ActividadViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        private val titulo: TextView = itemView.findViewById(R.id.titulo_tutor_actividades)
        private val fecha: TextView = itemView.findViewById(R.id.fecha_tutor_actividades)
        private val alumnosAsistieron: TextView = itemView.findViewById(R.id.alumnos_asistieron)
        private val alumnosNoAsistieron: TextView = itemView.findViewById(R.id.alumnos_noasistieron)
        private val btnEliminar: ImageButton = itemView.findViewById(R.id.eliminar_actividades_tutor)

        fun bind(actividad: Actividades) {
            val baseDeDatos = Firebase.firestore
            titulo.text = actividad.titulo
            fecha.text = actividad.fecha
            baseDeDatos.collection("asistencias").whereEqualTo("actividad",actividad.titulo)
                .whereEqualTo("grupo",tutor.grupo).whereEqualTo("asistio","Si")
                .get().addOnSuccessListener {result->
                    val listaSiAsistencia = mutableListOf<Asistencias>()

                    for (documento in result){
                        val siAsistencia = documento.toObject(Asistencias::class.java)

                        listaSiAsistencia.add(siAsistencia)
                    }
                    val listaFinalSi = listaSiAsistencia.size
                    notifyDataSetChanged()
                    alumnosAsistieron.text = "${listaFinalSi}"
                }
                .addOnFailureListener {
                    val listaFinalSi = 0
                    notifyDataSetChanged()
                    alumnosAsistieron.text = "${listaFinalSi}"

                }
            baseDeDatos.collection("asistencias")
                .whereEqualTo("actividad",actividad.titulo)
                .whereEqualTo("grupo", tutor.grupo).whereEqualTo("asistio","No")
                .get().addOnSuccessListener { documents ->
                    val listaNoAsistencia = mutableListOf<Asistencias>()
                    for (documento in documents) {

                        val noAsistencia = documento.toObject(Asistencias::class.java)

                        listaNoAsistencia.add(noAsistencia)
                    }
                    val listaFinalno = listaNoAsistencia.size
                    notifyDataSetChanged()
                    alumnosNoAsistieron.text = "${listaFinalno}"

                }
                .addOnFailureListener {
                    val listaFinalno = 0
                    notifyDataSetChanged()
                    alumnosNoAsistieron.text = "${listaFinalno}"

                }

            btnEliminar.setOnClickListener {
                clickListener.onEliminarClick(actividad)
            }
        }
    }
}