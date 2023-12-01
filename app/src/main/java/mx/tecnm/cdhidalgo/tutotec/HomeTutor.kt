package mx.tecnm.cdhidalgo.tutotec

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import mx.tecnm.cdhidalgo.tutotec.adaptadores.AdaptadorAlumnosActividad
import mx.tecnm.cdhidalgo.tutotec.dataClass.Actividades
import mx.tecnm.cdhidalgo.tutotec.dataClass.Alumno
import mx.tecnm.cdhidalgo.tutotec.dataClass.Tutor

class HomeTutor : AppCompatActivity() {

    private lateinit var menu : ImageButton

    private lateinit var spActividades:Spinner
    private lateinit var btnSubirEvidencia:Button
    private lateinit var txtActividadFecha:TextView
    private lateinit var rvAlumnos:RecyclerView
    private lateinit var adaptadorAlumnos : AdaptadorAlumnosActividad

    private lateinit var auth: FirebaseAuth

    var _actividad : Actividades = Actividades()

    private val storageReference = FirebaseStorage.getInstance().reference
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_tutor)

        auth = FirebaseAuth.getInstance()
        val baseDeDatos = Firebase.firestore

        menu = findViewById(R.id.btnmenu_tutor_home)
        spActividades = findViewById(R.id.sp_actividades_tutor)
        btnSubirEvidencia = findViewById(R.id.btn_subir_evidencia_tutor)
        txtActividadFecha = findViewById(R.id.txtActividadFecha_tutor)
        rvAlumnos = findViewById(R.id.rvAlumnos_actividades)

        val tutor = intent.getParcelableExtra<Tutor>("tutor")
        
        val nomT = "${tutor?.nombre} ${tutor?.apellido_pa} ${tutor?.apellido_ma}"

        val listaActividades = mutableListOf<Actividades>()
        baseDeDatos.collection("actividades").whereEqualTo("grupo", tutor!!.grupo)
            .whereEqualTo("tutor",nomT)
            .get().addOnSuccessListener { result ->

                for (documento in result){
                    val actividad = documento.toObject(Actividades::class.java)

                    listaActividades.add(actividad)
                }
                val nombresActividades = listaActividades.map { it.titulo }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, nombresActividades)
                spActividades.adapter = adapter

                spActividades.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val actividadSeleccionada = listaActividades[position]
                        _actividad = actividadSeleccionada
                        txtActividadFecha.text = "Actividad: ${_actividad.titulo} Fecha: ${_actividad.fecha} ${_actividad.hora}"

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                }

            }
            .addOnFailureListener{
                Toast.makeText(this,"No se encontraron Actividades",Toast.LENGTH_SHORT).show()
            }
        val listaAlumnos = mutableListOf<Alumno>()
        baseDeDatos.collection("alumnos").whereEqualTo("grupo", tutor?.grupo)
            .get().addOnSuccessListener { result ->
                for (documento in result){
                    val alumno = documento.toObject(Alumno::class.java)

                    listaAlumnos.add(alumno)
                }
                rvAlumnos.layoutManager = LinearLayoutManager(this)
                adaptadorAlumnos = AdaptadorAlumnosActividad(listaAlumnos,_actividad) { alumno, action ->
                    when (action) {
                        "confirmar asistencia" -> mostrarDialogoAsistencia(alumno)
                        "confirmar falta" -> mostrarDialogoFalta(alumno)
                    }
                }
                rvAlumnos.adapter = adaptadorAlumnos


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

        btnSubirEvidencia.setOnClickListener {
            if (selectedImageUri != null) {
                // Sube la imagen a Firebase Storage
                val storageRef = storageReference.child("evidencias/${selectedImageUri?.lastPathSegment}")
                storageRef.putFile(selectedImageUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        // La imagen se ha subido con éxito, obtén la URL de descarga
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            val downloadUrl = uri.toString()
                            val confirmarDialogo = AlertDialog.Builder(this)
                            confirmarDialogo.setTitle("Subir Evidencia")
                            confirmarDialogo.setMessage(
                                """
                                    ¿Desea subir la evidencia?
                                    """.trimIndent()
                            )
                            confirmarDialogo.setPositiveButton("Confirmar"){ confirmarDialogo,which->
                                val actualizar = hashMapOf<String, Any>(
                                    "evidencia" to downloadUrl
                                )
                                baseDeDatos.collection("actividades")
                                    .whereEqualTo("titulo", _actividad.titulo)
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        for (document in documents) {
                                            val documentReference = baseDeDatos.collection("actividades").document(document.id)
                                            documentReference.update(actualizar)
                                                .addOnSuccessListener {
                                                    Toast.makeText(this, "Evidencia subida", Toast.LENGTH_SHORT).show()
                                                }
                                                .addOnFailureListener {
                                                    Toast.makeText(this, "La Evidencia no se pudo subir", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Error al obtener el documento", Toast.LENGTH_SHORT).show()
                                    }
                            }

                            confirmarDialogo.setNegativeButton("Cancelar") { confirmarDialogo, _ ->
                                confirmarDialogo.cancel()
                            }

                            confirmarDialogo.show()
                        }
                    }
            }
        }

    }

    private fun mostrarDialogoAsistencia(alumno: Alumno) {
        val baseDeDatos = Firebase.firestore
        val _nombre = "${alumno.nombre} ${alumno.apellido_pa} ${alumno.apellido_ma}"
        val _siAsistio = "Si"
        val confirmarDialogo = AlertDialog.Builder(this)
        confirmarDialogo.setTitle("Confirmar Asistencia")
        confirmarDialogo.setMessage(
            """
                   Actividad: ${_actividad.titulo}
                   Fecha: ${_actividad.fecha}
                   Hora: ${_actividad.hora}
                   Grupo: ${alumno.grupo}
                   Nombre: ${_nombre}
                   No. Control: ${alumno.nocontrol}
                """.trimIndent()
        )
        confirmarDialogo.setPositiveButton("Confirmar"){ confirmarDialogo,which->
            if (_actividad.titulo.toString().isNotEmpty()  && _actividad.fecha.toString().isNotEmpty() &&
                _actividad.hora.toString().isNotEmpty() && alumno.grupo.toString().isNotEmpty()
                && _nombre.isNotEmpty() && alumno.nocontrol.toString().isNotEmpty()){
                val actividad = hashMapOf(
                    "actividad" to _actividad.titulo.toString(),
                    "fecha" to _actividad.fecha.toString(),
                    "hora" to _actividad.hora.toString(),
                    "grupo" to "${alumno.grupo}",
                    "nomalumno" to _nombre,
                    "nocontrol" to alumno.nocontrol.toString(),
                    "asistio" to _siAsistio
                )
                baseDeDatos.collection("asistencias").whereEqualTo("actividad",_actividad.titulo)
                    .whereEqualTo("nocontrol",alumno.nocontrol)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            Toast.makeText(this, "Asistencia Registrada", Toast.LENGTH_SHORT).show()
                        } else {
                            baseDeDatos.collection("asistencias").add(actividad)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Asistencia Confirmada",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        "La Asistencia no se confirmo",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
            }

        }
        confirmarDialogo.setNegativeButton("Cancelar"){confirmarDialogo,which->
            confirmarDialogo.cancel()
        }
        confirmarDialogo.show()
    }
    private fun mostrarDialogoFalta(alumno: Alumno) {
        val baseDeDatos = Firebase.firestore
        val _nombre = "${alumno.nombre} ${alumno.apellido_pa} ${alumno.apellido_ma}"
        val _noAsistio = "No"
        val confirmarDialogo = AlertDialog.Builder(this)
        confirmarDialogo.setTitle("Confirmar Falta")
        confirmarDialogo.setMessage(
            """
                   Actividad: ${_actividad.titulo}
                   Fecha: ${_actividad.fecha}
                   Hora: ${_actividad.hora}
                   Grupo: ${alumno.grupo}
                   Nombre: ${_nombre}
                   No. Control: ${alumno.nocontrol}
                """.trimIndent()
        )
        confirmarDialogo.setPositiveButton("Confirmar"){ confirmarDialogo,which->
            if (_actividad.titulo.toString().isNotEmpty()  && _actividad.fecha.toString().isNotEmpty() &&
                _actividad.hora.toString().isNotEmpty() && alumno.grupo.toString().isNotEmpty()
                && _nombre.isNotEmpty() && alumno.nocontrol.toString().isNotEmpty()){
                val actividad = hashMapOf(
                    "actividad" to _actividad.titulo.toString(),
                    "fecha" to _actividad.fecha.toString(),
                    "hora" to _actividad.hora.toString(),
                    "grupo" to "${alumno.grupo}",
                    "nomalumno" to _nombre,
                    "nocontrol" to alumno.nocontrol.toString(),
                    "asistio" to _noAsistio
                )
                baseDeDatos.collection("asistencias").whereEqualTo("actividad",_actividad.titulo)
                    .whereEqualTo("nocontrol",alumno.nocontrol)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            Toast.makeText(this, "Asistencia Registrada", Toast.LENGTH_SHORT).show()
                        } else {
                            baseDeDatos.collection("asistencias").add(actividad)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Falta Confirmada", Toast.LENGTH_SHORT)
                                        .show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        "La Falta no se confirmo",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                        }
                    }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                // Almacena la URI seleccionada
                selectedImageUri = uri
            }
        }
    }
}