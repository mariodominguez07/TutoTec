package mx.tecnm.cdhidalgo.tutotec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mx.tecnm.cdhidalgo.tutotec.adaptadores.AdaptadorAlumnosActividad
import mx.tecnm.cdhidalgo.tutotec.adaptadores.AdaptadorSolicitudesAlumno
import mx.tecnm.cdhidalgo.tutotec.adaptadores.AdaptadorSolicitudesTutor
import mx.tecnm.cdhidalgo.tutotec.dataClass.Alumno
import mx.tecnm.cdhidalgo.tutotec.dataClass.Solicitudes
import mx.tecnm.cdhidalgo.tutotec.dataClass.Tutor

class SolicitudesTutor : AppCompatActivity() {
    private lateinit var menu : ImageButton
    private lateinit var btnRegresar: ImageButton

    private lateinit var rvSolicitudes: RecyclerView
    private lateinit var adaptadorSolicitudes: AdaptadorSolicitudesAlumno

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitudes_tutor)
        auth = FirebaseAuth.getInstance()
        val baseDeDatos = Firebase.firestore

        menu = findViewById(R.id.btnmenu_tutor_soli)
        btnRegresar = findViewById(R.id.btn_regresar_soli_tutor)

        rvSolicitudes = findViewById(R.id.rvSolicitudes_tutor)
        val tutor = intent.getParcelableExtra<Tutor>("tutor")

        val listaSolicitudes = mutableListOf<Solicitudes>()
        baseDeDatos.collection("solicitudes").whereEqualTo("grupo", tutor?.grupo)
            .get().addOnSuccessListener { result ->
                for (documento in result){
                    val solicitud = documento.toObject(Solicitudes::class.java)

                    listaSolicitudes.add(solicitud)
                }

            }
            .addOnFailureListener{
                Toast.makeText(this,"No se encontraron Solicitudes", Toast.LENGTH_SHORT).show()
            }
        rvSolicitudes.layoutManager = LinearLayoutManager(this)
        val adaptadorSolicitudes = AdaptadorSolicitudesTutor(listaSolicitudes) { solicitud, action ->
            when (action) {
                "confirmar asistencia" -> mostrarDialogoAsistencia(solicitud)
                "confirmar falta" -> mostrarDialogoFalta(solicitud)
            }
        }

        rvSolicitudes.adapter = adaptadorSolicitudes

        menu.setOnClickListener {view->
            val popupMenu = PopupMenu(this, view)

            popupMenu.menuInflater.inflate(R.menu.menu_tutor, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item -> onMenuItemClick(item) }

            popupMenu.show()
        }
        btnRegresar.setOnClickListener {
            val intent = Intent(this,HomeTutor::class.java)
            intent.putExtra("tutor", tutor)
            startActivity(intent)
        }
    }

    private fun mostrarDialogoAsistencia(solicitud: Solicitudes) {
        val baseDeDatos = Firebase.firestore
        val _estatus = "Solicitud Finalizada"
        val confirmarDialogo = AlertDialog.Builder(this)
        confirmarDialogo.setTitle("Confirmar Asistencia")
        confirmarDialogo.setMessage(
            """
                  ¿Confirmar Asistencia?
                A la actividad ${solicitud.tema}
                """.trimIndent()
        )
        confirmarDialogo.setPositiveButton("Confirmar"){ confirmarDialogo,which->
            val actualizar = hashMapOf<String, Any>(
                "estatus" to _estatus
            )
            baseDeDatos.collection("solicitudes")
                .whereEqualTo("tema", solicitud.tema)
                .whereEqualTo("nocontrol", alumno.nocontrol)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val documentReference = baseDeDatos.collection("solicitudes").document(document.id)
                        documentReference.update(actualizar)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Asistencia Confirmada", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "La Asistencia no se confirmó", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al obtener el documento", Toast.LENGTH_SHORT).show()
                }
        }

        confirmarDialogo.setNegativeButton("Cancelar") { confirmarDialogo, _ ->
            confirmarDialogo.cancel()
        }

        confirmarDialogo.show()
    }

    private fun mostrarDialogoFalta(solicitud: Solicitudes) {
        val baseDeDatos = Firebase.firestore
        val _estatus = "Solicitud Rechazada"
        val confirmarDialogo = AlertDialog.Builder(this)
        confirmarDialogo.setTitle("Confirmar Falta")
        confirmarDialogo.setMessage(
            """
                   ¿Confirmar Falta?
                A la actividad ${solicitud.tema}
                """.trimIndent()
        )
        confirmarDialogo.setPositiveButton("Confirmar"){ confirmarDialogo,which->
            val actualizar = hashMapOf<String, Any>(
                "estatus" to _estatus
            )
            baseDeDatos.collection("solicitudes")
                .whereEqualTo("tema", solicitud.tema)
                .whereEqualTo("nocontrol", alumno.nocontrol)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val documentReference = baseDeDatos.collection("solicitudes").document(document.id)
                        documentReference.update(actualizar)
                            .addOnSuccessListener {
                        Toast.makeText(this,"Falta Confirmada", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"La Falta no se confirmo", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
        confirmarDialogo.setNegativeButton("Cancelar"){confirmarDialogo,which->
            confirmarDialogo.cancel()
        }
        confirmarDialogo.show()
    }
    private fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.carnet_menu_tutor -> {
                val intent = Intent(this,InicioTutor::class.java)
                intent.putExtra("tutor",tutor)
                startActivity(intent)
                return true
            }
            R.id.inicio_menu_tutor -> {
                val intent = Intent(this,HomeTutor::class.java)
                intent.putExtra("tutor",tutor)
                startActivity(intent)
                return true
            }
            R.id.actividades_menu_tutor -> {
                val intent = Intent(this,ActividadesTutor::class.java)
                intent.putExtra("tutor",tutor)
                startActivity(intent)
                return true
            }
            R.id.solicitudes_menu_tutor -> {
                return true
            }
            R.id.cerrarsesion_menu_tutor -> {
                auth.signOut()
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }

            else -> return false
        }
    }
}