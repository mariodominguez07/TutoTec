package mx.tecnm.cdhidalgo.tutotec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mx.tecnm.cdhidalgo.tutotec.adaptadores.AdaptadorAsistenciaAlumno
import mx.tecnm.cdhidalgo.tutotec.adaptadores.AdaptadorSolicitudesAlumno
import mx.tecnm.cdhidalgo.tutotec.dataClass.Alumno
import mx.tecnm.cdhidalgo.tutotec.dataClass.Asistencias
import mx.tecnm.cdhidalgo.tutotec.dataClass.Solicitudes

class AsistenciasAlumno : AppCompatActivity() {
    private lateinit var menu : ImageButton
    private lateinit var btnRegresar: ImageButton

    private lateinit var rvAsistencias: RecyclerView
    private lateinit var adaptadorAsistencias: AdaptadorAsistenciaAlumno

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asistencias_alumno)
        auth = FirebaseAuth.getInstance()
        val baseDeDatos = Firebase.firestore

        menu = findViewById(R.id.btnmenu_alumno_asistencias)
        btnRegresar = findViewById(R.id.btn_regresar_asistencias)

        rvAsistencias = findViewById(R.id.rvasistencias_alumno)

        val alumno = intent.getParcelableExtra<Alumno>("alumno")

        val listaAsistencias = mutableListOf<Asistencias>()
        baseDeDatos.collection("asistencias").whereEqualTo("nocontrol", alumno?.nocontrol)
            .get().addOnSuccessListener { result ->
                for (documento in result){
                    val asistencia = documento.toObject(Asistencias::class.java)

                    listaAsistencias.add(asistencia)
                }

            }
            .addOnFailureListener{
                Toast.makeText(this,"No se encontraron Asistencias", Toast.LENGTH_SHORT).show()
            }
        rvAsistencias.layoutManager = LinearLayoutManager(this)
        adaptadorAsistencias = AdaptadorAsistenciaAlumno(listaAsistencias)
        rvAsistencias.adapter = adaptadorAsistencias

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
                return true
            }
            R.id.solicitudes_menu -> {
                val intent = Intent(this,SolicitudesAlumno::class.java)
                intent.putExtra("alumno",alumno)
                startActivity(intent)
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