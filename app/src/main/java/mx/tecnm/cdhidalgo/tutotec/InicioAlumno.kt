package mx.tecnm.cdhidalgo.tutotec

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import mx.tecnm.cdhidalgo.tutotec.dataClass.Alumno
import mx.tecnm.cdhidalgo.tutotec.dataClass.Tutor

class InicioAlumno : AppCompatActivity() {

    private lateinit var editar:ImageButton
    private lateinit var menu : ImageButton

    private lateinit var foto:CircleImageView
    private lateinit var nombreC:TextView
    private lateinit var noControl:TextView
    private lateinit var grupo:TextView
    private lateinit var carrera:TextView
    private lateinit var tutor: TextView

    private lateinit var auth: FirebaseAuth

    var nomT = ""

    val EDITAR_PERFIL_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_alumno)

        auth = FirebaseAuth.getInstance()

        val baseDeDatos = Firebase.firestore

        editar = findViewById(R.id.btneditar)
        menu = findViewById(R.id.btnmenu_alumno)


        foto = findViewById(R.id.foto_alumno)
        nombreC = findViewById(R.id.nombreCompleto_Alumno)
        noControl = findViewById(R.id.noControl_carnet)
        grupo = findViewById(R.id.grupo_carnet)
        carrera = findViewById(R.id.carrera_carnet)
        tutor = findViewById(R.id.tutor_carnet)

        val alumno = intent.getParcelableExtra<Alumno>("alumno")


        if (alumno != null){
            baseDeDatos.collection("tutores")
                .whereEqualTo("grupo",alumno.grupo)
                .get()
                .addOnSuccessListener {result->
                    for (document in result){
                        val nomtutor = document.toObject(Tutor::class.java)
                        nomT = "${nomtutor.nombre} ${nomtutor.apellido_pa} ${nomtutor.apellido_ma}"

                        tutor.text = nomT

                    }

                }
                .addOnFailureListener{
                    nomT = "No se encontro tutor"

                    tutor.text = nomT

                }
            Glide.with(this).load(alumno.foto).circleCrop().into(foto)
            nombreC.text = "${alumno.nombre} ${alumno.apellido_pa} ${alumno.apellido_ma}"
            noControl.text = alumno.nocontrol
            carrera.text = alumno.carrera
            grupo.text = alumno.grupo
        }

        menu.setOnClickListener {view->
            val popupMenu = PopupMenu(this, view)

            popupMenu.menuInflater.inflate(R.menu.menu_alumno, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item -> onMenuItemClick(item) }

            popupMenu.show()
        }


        editar.setOnClickListener {
            val intent = Intent(this,EditarPerfil::class.java)
            intent.putExtra("alumno", alumno)
            startActivityForResult(intent, EDITAR_PERFIL_REQUEST_CODE)
        }
    }

    private fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.carnet_menu -> {
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDITAR_PERFIL_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val alumnoActualizado = data.getParcelableExtra<Alumno>("alumno_actualizado")
                actualizarVistasConAlumnoActualizado(alumnoActualizado)
            }
        }
    }

    private fun actualizarVistasConAlumnoActualizado(alumno: Alumno?) {
        if (alumno != null) {
            val baseDeDatos = Firebase.firestore
            baseDeDatos.collection("tutores")
                .whereEqualTo("grupo", alumno.grupo)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val nomtutor = document.toObject(Tutor::class.java)
                        nomT = "${nomtutor.nombre} ${nomtutor.apellido_pa} ${nomtutor.apellido_ma}"

                        tutor.text = nomT
                    }
                }
                .addOnFailureListener {
                    nomT = "No se encontro tutor"
                    tutor.text = nomT
                }

            Glide.with(this).load(alumno.foto).circleCrop().into(foto)
            nombreC.text = "${alumno.nombre} ${alumno.apellido_pa} ${alumno.apellido_ma}"
            noControl.text = alumno.nocontrol
            carrera.text = alumno.carrera
            grupo.text = alumno.grupo
        }
    }

}