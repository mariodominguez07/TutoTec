package mx.tecnm.cdhidalgo.tutotec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import mx.tecnm.cdhidalgo.tutotec.dataClass.Alumno
import mx.tecnm.cdhidalgo.tutotec.dataClass.Tutor

class InicioTutor : AppCompatActivity() {

    private lateinit var editar: ImageButton
    private lateinit var carnet: ImageButton
    private lateinit var actividades: ImageButton
    private lateinit var solicitudes: ImageButton
    private lateinit var cerrarSesion: ImageButton

    private lateinit var foto: CircleImageView
    private lateinit var nombreC: TextView
    private lateinit var correo: TextView
    private lateinit var grupo: TextView
    private lateinit var academia: TextView

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_tutor)

        auth = FirebaseAuth.getInstance()

        editar = findViewById(R.id.btneditar_tutor)
        /*carnet = findViewById(R.id.btncarnet_menu_tutor)
        actividades = findViewById(R.id.btnactividades_menu_tutor)
        solicitudes = findViewById(R.id.btnsolicitudes_menu_tutor)
        cerrarSesion = findViewById(R.id.btncerrarsesion_menu_tutor)*/

        foto = findViewById(R.id.foto_tutor)
        nombreC = findViewById(R.id.nombreCompleto_tutor)
        correo = findViewById(R.id.correo_tutor)
        grupo = findViewById(R.id.grupo_carnet_tutor)
        academia = findViewById(R.id.academia_tutor)

        val tutor = intent.getParcelableExtra<Tutor>("tutor")
        if (tutor != null){
            Glide.with(this).load(tutor.foto).circleCrop().into(foto)
            nombreC.text = "${tutor.nombre} ${tutor.apellido_pa} ${tutor.apellido_ma}"
            correo.text = tutor.correo
            academia.text = tutor.academia
            grupo.text = tutor.grupo
        }
       /* editar.setOnClickListener {  }

        carnet.setOnClickListener {  }

        actividades.setOnClickListener {  }

        solicitudes.setOnClickListener {  }

        cerrarSesion.setOnClickListener {
            auth.signOut()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }*/
    }
}