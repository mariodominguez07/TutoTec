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
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mx.tecnm.cdhidalgo.tutotec.dataClass.Actividades
import mx.tecnm.cdhidalgo.tutotec.dataClass.Alumno
import mx.tecnm.cdhidalgo.tutotec.dataClass.Asistencias
import mx.tecnm.cdhidalgo.tutotec.dataClass.Tutor

class ActividadesTutor : AppCompatActivity() {

    private lateinit var menu : ImageButton
    private lateinit var btnAgregarActividad: Button
    private lateinit var tablaActividades: TableLayout

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividades_tutor)

        auth = FirebaseAuth.getInstance()
        val baseDeDatos = Firebase.firestore

        menu = findViewById(R.id.btnmenu_tutor_actividades)
        btnAgregarActividad = findViewById(R.id.btn_agregar_actividad_tutor)

        val tutor = intent.getParcelableExtra<Tutor>("tutor")

        baseDeDatos.collection("actividades").whereEqualTo("grupo", tutor?.grupo)
            .get().addOnSuccessListener { result ->
                val listaActividades = mutableListOf<Actividades>()

                for (documento in result){
                    val actividad = documento.toObject(Actividades::class.java)

                    listaActividades.add(actividad)
                }
                agregarFilasATabla(listaActividades)

            }
            .addOnFailureListener{
                Toast.makeText(this,"No se encontraron Actividades", Toast.LENGTH_SHORT).show()
            }

        menu.setOnClickListener {view->
            val popupMenu = PopupMenu(this, view)

            popupMenu.menuInflater.inflate(R.menu.menu_tutor, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item -> onMenuItemClick(item) }

            popupMenu.show()
        }

        btnAgregarActividad.setOnClickListener {
            val intent = Intent(this,AgregarActividad::class.java)
            intent.putExtra("tutor",tutor)
            startActivity(intent)
        }
    }
    @SuppressLint("ResourceAsColor")
    fun agregarFilasATabla(listaActividades : List<Actividades>){
        tablaActividades = findViewById(R.id.tablaActividades_Tutor)

        val baseDeDatos = Firebase.firestore



        for (actividad in listaActividades){
            val fila = TableRow(this)
            val tvActividad = TextView(this)
            tvActividad.text = actividad.titulo
            tvActividad.setBackgroundResource(R.drawable.border )
            tvActividad.setTextColor(R.color.black)
            tvActividad.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tvActividad.setTypeface(null, Typeface.BOLD)
            tvActividad.setTextSize(11f)
            tvActividad.layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,1f)
            fila.addView(tvActividad)

            val tvFecha = TextView(this)
            tvFecha.text = "${actividad.fechayHora}"
            tvFecha.setBackgroundResource(R.drawable.border )
            tvFecha.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tvFecha.setTextColor(R.color.black)
            tvFecha.setTypeface(null, Typeface.BOLD)
            tvFecha.layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,1f)
            fila.addView(tvFecha)

            val tvSiAsistio = TextView(this)
            baseDeDatos.collection("asistencias").whereEqualTo("actividad",actividad.titulo)
                .whereEqualTo("grupo",tutor.grupo).whereEqualTo("asistio","Si")
                .get().addOnSuccessListener {result->
                    val listaSiAsistencia = mutableListOf<Asistencias>()

                    for (documento in result){
                        val siAsistencia = documento.toObject(Asistencias::class.java)

                        listaSiAsistencia.add(siAsistencia)
                    }
                    val listaFinalSi = listaSiAsistencia.size
                    tvSiAsistio.text = "${listaFinalSi}"
                }
                .addOnFailureListener {
                    val listaFinalSi = 0
                    tvSiAsistio.text = "${listaFinalSi}"
                }

            tvSiAsistio.setBackgroundResource(R.drawable.border)
            tvSiAsistio.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tvSiAsistio.setTextColor(R.color.black)
            tvSiAsistio.setTypeface(null, Typeface.BOLD)
            tvSiAsistio.layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,.5f)
            fila.addView(tvSiAsistio)

            val tvNoAsistio = TextView(this)
            baseDeDatos.collection("asistencias").whereEqualTo("actividad",actividad.titulo)
                .whereEqualTo("grupo",tutor.grupo).whereEqualTo("asistio","No")
                .get().addOnSuccessListener {result->
                    val listaNoAsistencia = mutableListOf<Asistencias>()

                    for (documento in result){
                        val noAsistencia = documento.toObject(Asistencias::class.java)

                        listaNoAsistencia.add(noAsistencia)
                    }
                    val listaFinalno = listaNoAsistencia.size
                    tvNoAsistio.text = "${listaFinalno}"
                }
                .addOnFailureListener {
                    val listaFinalno = 0
                    tvNoAsistio.text = "${listaFinalno}"
                }
            tvNoAsistio.setBackgroundResource(R.drawable.border)
            tvNoAsistio.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tvNoAsistio.setTextColor(R.color.black)
            tvNoAsistio.setTypeface(null, Typeface.BOLD)
            tvNoAsistio.layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,.5f)
            fila.addView(tvNoAsistio)

            val btnEliminar = ImageButton(this)
            btnEliminar.setImageResource(R.drawable.img_eliminar)
            btnEliminar.setBackgroundResource(R.drawable.border)
            btnEliminar.layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,.5f)
            btnEliminar.setOnClickListener {
                baseDeDatos.collection("actividades")
                    .document(actividad.titulo)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this,"Actividad Eliminada correctamente", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"Ocurrio un error al Eliminar la Actividad", Toast.LENGTH_SHORT).show()
                    }
            }
            fila.addView(btnEliminar)

            tablaActividades.addView(fila)
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
                val intent = Intent(this,HomeTutor::class.java)
                intent.putExtra("tutor",tutor)
                startActivity(intent)
                return true
            }
            R.id.actividades_menu_tutor -> {
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