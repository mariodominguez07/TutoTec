package mx.tecnm.cdhidalgo.tutotec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mx.tecnm.cdhidalgo.tutotec.dataClass.Alumno

class AgregarSolicitudes : AppCompatActivity() {
    private lateinit var menu : ImageButton
    private lateinit var btnRegresar: ImageButton
    private lateinit var btnAgregarSolicitud: Button

    private lateinit var tipo: TextInputEditText
    private lateinit var tema: TextInputLayout
    private lateinit var area: Spinner

    private lateinit var auth: FirebaseAuth

    var _tipo = ""
    var _area = ""
    var _estatus = "Enviado al tutor"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_solicitudes)
        val baseDeDatos = Firebase.firestore

        auth = FirebaseAuth.getInstance()
        menu = findViewById(R.id.btnmenu_alumno_agsoli)
        btnAgregarSolicitud = findViewById(R.id.btn_agregar_soli_alumno)
        btnRegresar = findViewById(R.id.btn_regresar_agsoli)

        tipo = findViewById(R.id.tipo_solicitud)
        tema = findViewById(R.id.tema_solicitud)
        area = findViewById(R.id.area_soli)

        val alumno = intent.getParcelableExtra<Alumno>("alumno")


        val tipoSoli = intent.hasExtra("tipoSoli")
        if (tipoSoli.toString().isNotEmpty()){
            _tipo = intent.getStringExtra("tipoSoli").toString()
        }

        val areaSel = intent.getStringExtra("areaSel")
        if (areaSel.toString().isNotEmpty()){
            _area = intent.getStringExtra("areaSel").toString()
        }

        if (_tipo == "Canalización"){
            tipo.setText(_tipo)
            tipo.isEnabled = false
        }else if(_tipo == "Asesoria"){
            tipo.setText(_tipo)
            tipo.isEnabled = false
        }else if (_tipo == "Plática"){
            tipo.setText(_tipo)
            tipo.isEnabled = false
        }

        val arregloAreas = resources.getStringArray(R.array.areas)
        val adaptadorAreas = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item,
            arregloAreas)

        area.adapter = adaptadorAreas
        if (_area == "Ciencias Básicas") {
            val indice = arregloAreas.indexOf(_area)
            if (indice != -1) {
                area.setSelection(indice)
                area.isEnabled = false
            }
        }else if(_area == "Tutorias"){
            val indice = arregloAreas.indexOf(_area)
            if (indice != -1) {
                area.setSelection(indice)
                area.isEnabled = false
            }
        }
        area.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                _area = area.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        btnAgregarSolicitud.setOnClickListener {
            val confirmarDialogo = AlertDialog.Builder(it.context)
            confirmarDialogo.setTitle("Confirmar Solicitud")
            confirmarDialogo.setMessage(
                """
                   Tipo: ${tipo.text}
                   Tema: ${tema.editText?.text}
                   Area: ${_area}
                   No. Control: ${alumno?.nocontrol}
                   Nombre: ${alumno?.nombre} ${alumno?.apellido_pa} ${alumno?.apellido_ma}
                   Grupo: ${alumno?.grupo}
                """.trimIndent()
            )
            confirmarDialogo.setPositiveButton("Confirmar"){ confirmarDialogo,which->
                if (tipo.toString().isNotEmpty() && tema.toString().isNotEmpty() &&
                    _area.isNotEmpty() ){
                    val solicitud = hashMapOf(
                        "tipo" to tipo.text.toString(),
                        "tema" to tema.editText?.text.toString(),
                        "area" to _area,
                        "nocontrol" to "${alumno?.nocontrol}",
                        "nombre" to "${alumno?.nombre} ${alumno?.apellido_pa} ${alumno?.apellido_ma}",
                        "grupo" to "${alumno?.grupo}",
                        "estatus" to _estatus
                    )
                    baseDeDatos.collection("solicitudes").add(solicitud)
                        .addOnSuccessListener {
                            Toast.makeText(this,"Solicitud Agregada correctamente", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this,HomeAlumno::class.java)
                            intent.putExtra("alumno", alumno)
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this,"La Solicitud no se agrego", Toast.LENGTH_SHORT).show()
                        }
                }

            }
            confirmarDialogo.setNegativeButton("Editar"){confirmarDialogo,which->
                confirmarDialogo.cancel()
            }
            confirmarDialogo.show()
        }

        menu.setOnClickListener {view->
            val popupMenu = PopupMenu(this, view)

            popupMenu.menuInflater.inflate(R.menu.menu_alumno, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item -> onMenuItemClick(item) }

            popupMenu.show()
        }

        btnRegresar.setOnClickListener {
            val intent = Intent(this,HomeAlumno::class.java)
            intent.putExtra("alumno", alumno)
            startActivity(intent)
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
}