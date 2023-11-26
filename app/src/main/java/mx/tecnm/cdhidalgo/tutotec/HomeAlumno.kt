package mx.tecnm.cdhidalgo.tutotec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mx.tecnm.cdhidalgo.tutotec.dataClass.Alumno

class HomeAlumno : AppCompatActivity() {

    private lateinit var menu : ImageButton

    private lateinit var txtActividad:TextView
    private lateinit var btnDetalleActividad: Button
    private lateinit var btnSolicitud_Canalizacion:ImageButton
    private lateinit var btnSolicitud_Asesoria:ImageButton
    private lateinit var btnSolicitud_Platica:ImageButton

    private lateinit var auth: FirebaseAuth

    val alumno = intent.getParcelableExtra<Alumno>("alumno")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_alumno)

        val baseDeDatos = Firebase.firestore

        auth = FirebaseAuth.getInstance()

        menu = findViewById(R.id.btnmenu_alumno_home)

        txtActividad = findViewById(R.id.txt_actividad)
        btnDetalleActividad = findViewById(R.id.btn_detalles_actividad)
        btnSolicitud_Canalizacion = findViewById(R.id.btnSolicitud_canalizacion)
        btnSolicitud_Asesoria = findViewById(R.id.btnSolicitud_asesoria)
        btnSolicitud_Platica = findViewById(R.id.btnSolicitud_platica)

        btnDetalleActividad.setOnClickListener {  }

        btnSolicitud_Canalizacion.setOnClickListener {  }

        btnSolicitud_Asesoria.setOnClickListener {  }

        btnSolicitud_Platica.setOnClickListener {  }

        menu.setOnClickListener {view->
            val popupMenu = PopupMenu(this, view)

            popupMenu.menuInflater.inflate(R.menu.menu_alumno, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item -> onMenuItemClick(item) }

            popupMenu.show()
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
                return true
            }
            R.id.asistencias_menu -> {
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