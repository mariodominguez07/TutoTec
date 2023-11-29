package mx.tecnm.cdhidalgo.tutotec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mx.tecnm.cdhidalgo.tutotec.dataClass.Actividades
import mx.tecnm.cdhidalgo.tutotec.dataClass.Alumno

class HomeAlumno : AppCompatActivity() {

    private lateinit var menu : ImageButton

    private lateinit var spActividades: Spinner
    private lateinit var btnDetalleActividad: Button
    private lateinit var btnSolicitud_Canalizacion:ImageButton
    private lateinit var btnSolicitud_Asesoria:ImageButton
    private lateinit var btnSolicitud_Platica:ImageButton

    private lateinit var auth: FirebaseAuth

    var _actividad : Actividades = Actividades()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_alumno)

        val baseDeDatos = Firebase.firestore

        auth = FirebaseAuth.getInstance()

        menu = findViewById(R.id.btnmenu_alumno_home)

        spActividades = findViewById(R.id.sp_actividades_alumno)
        btnDetalleActividad = findViewById(R.id.btn_detalles_actividad)
        btnSolicitud_Canalizacion = findViewById(R.id.btnSolicitud_canalizacion)
        btnSolicitud_Asesoria = findViewById(R.id.btnSolicitud_asesoria)
        btnSolicitud_Platica = findViewById(R.id.btnSolicitud_platica)

        val alumno = intent.getParcelableExtra<Alumno>("alumno")

        val listaActividades = mutableListOf<Actividades>()
        baseDeDatos.collection("actividades").whereEqualTo("grupo", alumno!!.grupo)
            .get().addOnSuccessListener { result ->

                for (documento in result){
                    val actividad = documento.toObject(Actividades::class.java)

                    listaActividades.add(actividad)
                }
                val nombresActividades = listaActividades.map { it.titulo }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, nombresActividades)
                spActividades.adapter = adapter

                spActividades.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val actividadSeleccionada = listaActividades[position]
                        _actividad = actividadSeleccionada
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                }

            }
            .addOnFailureListener{
                Toast.makeText(this,"No se encontraron Actividades", Toast.LENGTH_SHORT).show()
            }


        btnDetalleActividad.setOnClickListener {
            val intent = Intent(this,DetalleActividad::class.java)
            intent.putExtra("alumno", alumno)
            intent.putExtra("actividad",_actividad)
            startActivity(intent)
        }

        btnSolicitud_Canalizacion.setOnClickListener {
            val tipoSoli = "Canalización"
            val intent = Intent(this,AgregarSolicitudes::class.java)
            intent.putExtra("alumno", alumno)
            intent.putExtra("tipoSoli",tipoSoli)
            startActivity(intent)
        }

        btnSolicitud_Asesoria.setOnClickListener {
            val tipoSoli = "Asesoria"
            val areaSel = "Ciencias Básicas"
            val intent = Intent(this,AgregarSolicitudes::class.java)
            intent.putExtra("alumno", alumno)
            intent.putExtra("tipoSoli",tipoSoli)
            intent.putExtra("areaSel",areaSel)
            startActivity(intent)
        }

        btnSolicitud_Platica.setOnClickListener {
            val tipoSoli = "Plática"
            val areaSel = "Tutorias"
            val intent = Intent(this,AgregarSolicitudes::class.java)
            intent.putExtra("alumno", alumno)
            intent.putExtra("tipoSoli",tipoSoli)
            intent.putExtra("areaSel",areaSel)
            startActivity(intent)
        }

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