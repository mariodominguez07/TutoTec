package mx.tecnm.cdhidalgo.tutotec

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import mx.tecnm.cdhidalgo.tutotec.dataClass.Alumno
import mx.tecnm.cdhidalgo.tutotec.dataClass.Tutor

class HomeTutor : AppCompatActivity() {

    private lateinit var menu : ImageButton

    private lateinit var spActividades:Spinner
    private lateinit var btnDetallesActividad:Button
    private lateinit var txtActividadFecha:TextView
    private lateinit var tablaAlumnos:TableLayout

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_tutor)

        auth = FirebaseAuth.getInstance()
        val baseDeDatos = Firebase.firestore

        menu = findViewById(R.id.btnmenu_tutor_home)
        spActividades = findViewById(R.id.sp_actividades_tutor)
        btnDetallesActividad = findViewById(R.id.btn_detalles_actividad_tutor)
        txtActividadFecha = findViewById(R.id.txtActividadFecha_tutor)

        val tutor = intent.getParcelableExtra<Tutor>("tutor")


        baseDeDatos.collection("alumnos").whereEqualTo("grupo", tutor?.grupo)
            .get().addOnSuccessListener { result ->
                val listaAlumnos = mutableListOf<Alumno>()

                for (documento in result){
                    val alumno = documento.toObject(Alumno::class.java)

                    listaAlumnos.add(alumno)
                }
                agregarFilasATabla(listaAlumnos)

            }
            .addOnFailureListener{
                Toast.makeText(this,"No se encontraron Alumnos",Toast.LENGTH_SHORT).show()
            }

        menu.setOnClickListener {view->
            val popupMenu = PopupMenu(this, view)

            popupMenu.menuInflater.inflate(R.menu.menu_tutor, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item -> onMenuItemClick(item) }

            popupMenu.show()
        }

    }

    @SuppressLint("ResourceAsColor")
    fun agregarFilasATabla(listaAlumnos : List<Alumno>){
        tablaAlumnos = findViewById(R.id.tablaAlumnos_HomeTutor)

        for (alumno in listaAlumnos){
            val fila = TableRow(this)
            val tvNumeroControl = TextView(this)
            tvNumeroControl.text = alumno.nocontrol
            tvNumeroControl.setBackgroundResource(R.drawable.border )
            tvNumeroControl.setTextColor(R.color.black)
            tvNumeroControl.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tvNumeroControl.setTypeface(null,Typeface.BOLD)
            tvNumeroControl.isAllCaps = true
            tvNumeroControl.setTextSize(11f)
            tvNumeroControl.layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,.5f)
            fila.addView(tvNumeroControl)

            val tvNombreCompleto = TextView(this)
            tvNombreCompleto.text = "${alumno.nombre} ${alumno.apellido_pa} ${alumno.apellido_ma}"
            tvNombreCompleto.setBackgroundResource(R.drawable.border )
            tvNombreCompleto.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tvNombreCompleto.setTextColor(R.color.black)
            tvNombreCompleto.setTypeface(null,Typeface.BOLD)
            tvNombreCompleto.layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,1f)
            fila.addView(tvNombreCompleto)

            val btnSiAsistio = ImageButton(this)
            btnSiAsistio.setImageResource(R.drawable.img_si_asistencia)
            btnSiAsistio.setBackgroundResource(R.drawable.border)
            btnSiAsistio.layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,.5f)
            btnSiAsistio.setOnClickListener {

            }
            fila.addView(btnSiAsistio)

            val btnNoAsistio = ImageButton(this)
            btnNoAsistio.setImageResource(R.drawable.img_no_asistencia)
            btnNoAsistio.setBackgroundResource(R.drawable.border)
            btnNoAsistio.layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,.5f)
            btnNoAsistio.setOnClickListener {

            }
            fila.addView(btnNoAsistio)

            tablaAlumnos.addView(fila)
        }
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
                return true
            }
            R.id.actividades_menu_tutor -> {
                val intent = Intent(this,ActividadesTutor::class.java)
                intent.putExtra("tutor",tutor)
                startActivity(intent)
                return true
            }
            R.id.solicitudes_menu_tutor -> {
                val intent = Intent(this,SolicitudesTutor::class.java)
                intent.putExtra("tutor",tutor)
                startActivity(intent)
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