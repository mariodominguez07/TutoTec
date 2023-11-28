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

class AdaptadorAlumnosActividad(private val listaAlumnos: List<Alumno>, private val clickListener: (Alumno, action: String) -> Unit)
    : RecyclerView.Adapter<AdaptadorAlumnosActividad.AlumnoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
    : AdaptadorAlumnosActividad.AlumnoViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_control_actividades, parent, false)
        return AlumnoViewHolder(vista)
    }

    override fun onBindViewHolder(holder: AdaptadorAlumnosActividad.AlumnoViewHolder, position: Int) {
        val alumno = listaAlumnos[position]
        holder.bind(alumno)
    }

    override fun getItemCount(): Int {
        return listaAlumnos.size
    }
    inner class AlumnoViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        private val txtNumeroControl: TextView = itemView.findViewById(R.id.nocontrol_alumno_actividades)
        private val txtNombre: TextView = itemView.findViewById(R.id.nombre_alumno_actividades)
        private val btnSiAsistio: ImageButton = itemView.findViewById(R.id.siasistio_alumno_actividades)
        private val btnNoAsistio: ImageButton = itemView.findViewById(R.id.noasistio_alumno_actividades)
        fun bind(alumno: Alumno) {
            val baseDeDatos = Firebase.firestore
            txtNumeroControl.text = alumno.nocontrol
            txtNombre.text = "${alumno.nombre} ${alumno.apellido_pa} ${alumno.apellido_ma}"
            baseDeDatos.collection("asistencias")
                .whereEqualTo("nocontrol", alumno.nocontrol)
                .get().addOnSuccessListener { documents ->
                    for (documento in documents) {
                        val asistencia = documento.getString("asistio")

                        if (asistencia == "Si" || asistencia == "No") {
                            btnSiAsistio.isEnabled = false
                            btnNoAsistio.isEnabled = false
                        }else{
                            btnSiAsistio.setOnClickListener {
                                clickListener(alumno,"confirmar asistencia")
                                btnSiAsistio.isEnabled = false
                                btnNoAsistio.isEnabled = false
                            }
                            btnNoAsistio.setOnClickListener {
                                clickListener(alumno,"confirmar falta")
                                btnSiAsistio.isEnabled = false
                                btnNoAsistio.isEnabled = false
                            }
                        }
                    }
                }
                .addOnFailureListener { }
        }
    }

}