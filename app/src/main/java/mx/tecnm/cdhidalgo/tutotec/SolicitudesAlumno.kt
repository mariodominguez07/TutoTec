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
import mx.tecnm.cdhidalgo.tutotec.adaptadores.AdaptadorSolicitudesAlumno
import mx.tecnm.cdhidalgo.tutotec.dataClass.Alumno
import mx.tecnm.cdhidalgo.tutotec.dataClass.Solicitudes

class SolicitudesAlumno : AppCompatActivity(), AdaptadorSolicitudesAlumno.SolicitudClickListener {
    private lateinit var menu : ImageButton
    private lateinit var btnRegresar: ImageButton

    private lateinit var rvSolicitudes: RecyclerView
    private lateinit var adaptadorSolicitudes: AdaptadorSolicitudesAlumno

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitudes_alumno)
        auth = FirebaseAuth.getInstance()
        val baseDeDatos = Firebase.firestore

        menu = findViewById(R.id.btnmenu_alumno_soli)
        btnRegresar = findViewById(R.id.btn_regresar_soli)

        rvSolicitudes = findViewById(R.id.rvsolicitudes_alumno)

        val alumno = intent.getParcelableExtra<Alumno>("alumno")

        val listaSolicitudes = mutableListOf<Solicitudes>()
        baseDeDatos.collection("solicitudes").whereEqualTo("nocontrol", alumno?.nocontrol)
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
        adaptadorSolicitudes = AdaptadorSolicitudesAlumno(listaSolicitudes, this)
        rvSolicitudes.adapter = adaptadorSolicitudes

        menu.setOnClickListener {view->
            val popupMenu = PopupMenu(this, view)

            popupMenu.menuInflater.inflate(R.menu.menu_alumno, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item -> onMenuItemClick(item) }

            popupMenu.show()
        }

        btnRegresar.setOnClickListener {
            val intent = Intent(this,HomeAlumno::class.java)
            intent.putExtra("alumno", alumno)
            startActivity(intent)
        }
    }

    override fun onConfirmarClick(solicitud: Solicitudes) {
        val baseDeDatos = Firebase.firestore
        val _estatus = "Solicitud atendida por alumno"
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

    private fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.carnet_menu -> {
                val intent = Intent(this,InicioAlumno::class.java)
                intent.putExtra("alumno",alumno)
                startActivity(intent)
                return true
            }
            R.id.inicio_menu -> {
                val intent = Intent(this,HomeAlumno::class.java)
                intent.putExtra("alumno",alumno)
                startActivity(intent)
                return true
            }
            R.id.asistencias_menu -> {
                val intent = Intent(this,AsistenciasAlumno::class.java)
                intent.putExtra("alumno",alumno)
                startActivity(intent)
                return true
            }
            R.id.solicitudes_menu -> {
                return true
            }
            R.id.cerrarsesion_menu -> {
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