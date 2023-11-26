package mx.tecnm.cdhidalgo.tutotec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import mx.tecnm.cdhidalgo.tutotec.dataClass.Alumno
import mx.tecnm.cdhidalgo.tutotec.dataClass.Tutor

class InicioTutor : AppCompatActivity() {

    private lateinit var editar: ImageButton
    private lateinit var menu : ImageButton

    private lateinit var foto: CircleImageView
    private lateinit var nombreC: TextView
    private lateinit var correo: TextView
    private lateinit var grupo: TextView
    private lateinit var academia: TextView

    private lateinit var auth: FirebaseAuth
    val tutor = intent.getParcelableExtra<Tutor>("tutor")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_tutor)

        auth = FirebaseAuth.getInstance()

        editar = findViewById(R.id.btneditar_tutor)
        menu = findViewById(R.id.btnmenu_tutor)

        foto = findViewById(R.id.foto_tutor)
        nombreC = findViewById(R.id.nombreCompleto_tutor)
        correo = findViewById(R.id.correo_tutor)
        grupo = findViewById(R.id.grupo_carnet_tutor)
        academia = findViewById(R.id.academia_tutor)

        if (tutor != null){
            Glide.with(this).load(tutor.foto).circleCrop().into(foto)
            nombreC.text = "${tutor.nombre} ${tutor.apellido_pa} ${tutor.apellido_ma}"
            correo.text = tutor.correo
            academia.text = tutor.academia
            grupo.text = tutor.grupo
        }

        menu.setOnClickListener {view->
            val popupMenu = PopupMenu(this, view)

            popupMenu.menuInflater.inflate(R.menu.menu_tutor, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item -> onMenuItemClick(item) }

            popupMenu.show()
        }

    }

    private fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.carnet_menu_tutor -> {
                return true
            }
            R.id.inicio_menu_tutor -> {
                val intent = Intent(this,HomeTutor::class.java)
                intent.putExtra("tutor",tutor)
                startActivity(intent)
                return true
            }
            R.id.actividades_menu_tutor -> {
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