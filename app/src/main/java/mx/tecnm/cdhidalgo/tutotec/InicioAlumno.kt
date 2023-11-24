package mx.tecnm.cdhidalgo.tutotec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import mx.tecnm.cdhidalgo.tutotec.dataClass.Alumno

class InicioAlumno : AppCompatActivity() {

    private lateinit var editar:ImageButton
    private lateinit var carnet:ImageButton
    private lateinit var actividades:ImageButton
    private lateinit var solicitudes:ImageButton
    private lateinit var cerrarSesion:ImageButton

    private lateinit var foto:CircleImageView
    private lateinit var nombreC:TextView
    private lateinit var noControl:TextView
    private lateinit var grupo:TextView
    private lateinit var carrera:TextView
    private lateinit var tutor: TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_alumno)

        auth = FirebaseAuth.getInstance()

        editar = findViewById(R.id.btneditar)
        carnet = findViewById(R.id.btncarnet_menu)
        actividades = findViewById(R.id.btnactividades_menu)
        solicitudes = findViewById(R.id.btnsolicitudes_menu)
        cerrarSesion = findViewById(R.id.btncerrarsesion_menu)

        foto = findViewById(R.id.foto_alumno)
        nombreC = findViewById(R.id.nombreCompleto_Alumno)
        noControl = findViewById(R.id.noControl_carnet)
        grupo = findViewById(R.id.grupo_carnet)
        carrera = findViewById(R.id.carrera_carnet)
        tutor = findViewById(R.id.tutor_carnet)

        val alumno = intent.getParcelableExtra<Alumno>("alumno")

        if (alumno != null){
            Glide.with(this).load(alumno.foto).circleCrop().into(foto)
            nombreC.text = "${alumno.nombre} ${alumno.apellido_pa} ${alumno.apellido_ma}"
            noControl.text = alumno.nocontrol
            carrera.text = alumno.carrera
            grupo.text = alumno.grupo
        }

        editar.setOnClickListener {  }

        carnet.setOnClickListener {  }

        actividades.setOnClickListener {  }

        solicitudes.setOnClickListener {  }

        cerrarSesion.setOnClickListener {
            auth.signOut()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}