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
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mx.tecnm.cdhidalgo.tutotec.adaptadores.AdaptadorActividadesTutor
import mx.tecnm.cdhidalgo.tutotec.adaptadores.AdaptadorSolicitudesAlumno
import mx.tecnm.cdhidalgo.tutotec.dataClass.Actividades
import mx.tecnm.cdhidalgo.tutotec.dataClass.Alumno
import mx.tecnm.cdhidalgo.tutotec.dataClass.Asistencias
import mx.tecnm.cdhidalgo.tutotec.dataClass.Solicitudes
import mx.tecnm.cdhidalgo.tutotec.dataClass.Tutor

class ActividadesTutor : AppCompatActivity(), AdaptadorActividadesTutor.ActividadClickListener{

    private lateinit var menu : ImageButton
    private lateinit var btnAgregarActividad: Button
    private lateinit var rvActividades: RecyclerView

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividades_tutor)

        auth = FirebaseAuth.getInstance()
        val baseDeDatos = Firebase.firestore

        menu = findViewById(R.id.btnmenu_tutor_actividades)
        btnAgregarActividad = findViewById(R.id.btn_agregar_actividad_tutor_home)
        rvActividades = findViewById(R.id.rvActividades)
        val tutor = intent.getParcelableExtra<Tutor>("tutor")

        baseDeDatos.collection("actividades").whereEqualTo("grupo", tutor?.grupo)
            .get().addOnSuccessListener { result ->
                val listaActividades = mutableListOf<Actividades>()

                for (documento in result){
                    val actividad = documento.toObject(Actividades::class.java)

                    listaActividades.add(actividad)

                }
                rvActividades.layoutManager = LinearLayoutManager(this)
                val adaptadorActividades = AdaptadorActividadesTutor(listaActividades, this)
                rvActividades.adapter = adaptadorActividades
                adaptadorActividades.notifyDataSetChanged()

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

    override fun onEliminarClick(actividad: Actividades) {
        val baseDeDatos = Firebase.firestore
        val confirmarDialogo = AlertDialog.Builder(this)
        confirmarDialogo.setTitle("Eliminar Actividad")
        confirmarDialogo.setMessage(
            """
                   ¿Estas seguro de eliminar la Actividad ${actividad.titulo}?
                """.trimIndent()
        )
        confirmarDialogo.setPositiveButton("Confirmar") { confirmarDialogo, which ->
            baseDeDatos.collection("actividades")
                .whereEqualTo("titulo", actividad.titulo)
                .get()
                .addOnSuccessListener {documents ->
                    for (document in documents){
                        val documentReference = baseDeDatos.collection("actividades").document(document.id)
                        documentReference.delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Actividad Eliminada", Toast.LENGTH_SHORT).show()
                                baseDeDatos.collection("actividades").whereEqualTo("grupo", tutor?.grupo)
                                    .get().addOnSuccessListener { result ->
                                        val listaActividades = mutableListOf<Actividades>()

                                        for (documento in result){
                                            val actividad = documento.toObject(Actividades::class.java)

                                            listaActividades.add(actividad)
                                        }
                                        rvActividades.layoutManager = LinearLayoutManager(this)
                                        val adaptadorActividades = AdaptadorActividadesTutor(listaActividades, this)
                                        rvActividades.adapter = adaptadorActividades
                                        adaptadorActividades.notifyDataSetChanged()

                                    }
                                    .addOnFailureListener{
                                        Toast.makeText(this,"No se encontraron Actividades", Toast.LENGTH_SHORT).show()
                                    }
                            }
                    }
                }
                            .addOnFailureListener {
                                Toast.makeText(this, "No se pudo eliminar la Actividad", Toast.LENGTH_SHORT).show()
                            }
                    }
        confirmarDialogo.setNegativeButton("Cancelar"){confirmarDialogo,which->
            confirmarDialogo.cancel()
        }
        confirmarDialogo.show()
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