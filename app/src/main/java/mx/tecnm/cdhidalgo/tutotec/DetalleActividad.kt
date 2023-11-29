package mx.tecnm.cdhidalgo.tutotec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mx.tecnm.cdhidalgo.tutotec.dataClass.Actividades
import mx.tecnm.cdhidalgo.tutotec.dataClass.Alumno

class DetalleActividad : AppCompatActivity() {
    private lateinit var menu : ImageButton
    private lateinit var btnRegresar: ImageButton

    private lateinit var txtActividad : TextView
    private lateinit var txtDescipcion : TextView
    private lateinit var txtLugar : TextView
    private lateinit var txtFechaHora : TextView

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_actividad)
        val baseDeDatos = Firebase.firestore

        auth = FirebaseAuth.getInstance()
        menu = findViewById(R.id.btnmenu_alumno_detact)
        btnRegresar = findViewById(R.id.btn_regresar_detact)

        txtActividad = findViewById(R.id.txtActividiad_detalle)
        txtDescipcion = findViewById(R.id.txtdescripcion_detalle)
        txtLugar = findViewById(R.id.txtlugar_detalle)
        txtFechaHora = findViewById(R.id.txtfecha_detalle)

        val alumno = intent.getParcelableExtra<Alumno>("alumno")
        val actividad = intent.getParcelableExtra<Actividades>("actividad")

        txtActividad.text = "Actividad: ${actividad?.titulo}"
        txtDescipcion.text = "Descrición: ${actividad?.descripcion}"
        txtLugar.text = "Lugar: ${actividad?.lugar}"
        txtFechaHora.text = "Fecha: ${actividad?.fecha} Hora: ${actividad?.hora}"

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
                val intent = Intent(this,AsistenciasAlumno::class.java)
                intent.putExtra("alumno",alumno)
                startActivity(intent)
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