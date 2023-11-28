package mx.tecnm.cdhidalgo.tutotec

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mx.tecnm.cdhidalgo.tutotec.dataClass.Tutor
import java.util.Calendar

class AgregarActividad : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private lateinit var menu : ImageButton
    private lateinit var btnRegresar: ImageButton
    private lateinit var btnAgregarActividad: Button

    private lateinit var titulo: TextInputLayout
    private lateinit var descripcion: TextInputLayout
    private lateinit var lugar: TextInputLayout

    private lateinit var auth: FirebaseAuth

    val _evidencia = "Aun no se necesita evidencia"
    var _fecha = ""
    var _hora = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_actividad)

        auth = FirebaseAuth.getInstance()
        val baseDeDatos = Firebase.firestore

        menu = findViewById(R.id.btnmenu_tutor_actividad)
        btnAgregarActividad = findViewById(R.id.btn_agregar_actividad_tutor)
        btnRegresar = findViewById(R.id.btn_regresar_actividad)

        titulo = findViewById(R.id.titulo_actividad)
        descripcion = findViewById(R.id.descripcion_actividad)
        lugar = findViewById(R.id.ubicacion_actividad)
        val tutor = intent.getParcelableExtra<Tutor>("tutor")

        btnAgregarActividad.setOnClickListener {
            val confirmarDialogo = AlertDialog.Builder(it.context)
            confirmarDialogo.setTitle("Confirmar Actividad")
            confirmarDialogo.setMessage(
                """
                   Titulo: ${titulo.editText?.text}
                   Descripción: ${descripcion.editText?.text}
                   Ubicación: ${lugar.editText?.text}
                   Fecha: ${_fecha}
                   Hora: ${_hora}
                """.trimIndent()
            )
            confirmarDialogo.setPositiveButton("Confirmar"){ confirmarDialogo,which->
                if (titulo.toString().isNotEmpty() && descripcion.toString().isNotEmpty() &&
                    lugar.toString().isNotEmpty() && _fecha.isNotEmpty() &&
                    _hora.isNotEmpty()){
                    val actividad = hashMapOf(
                        "titulo" to titulo.editText?.text.toString(),
                        "descripcion" to descripcion.editText?.text.toString(),
                        "lugar" to lugar.editText?.text.toString(),
                        "fecha" to _fecha,
                        "hora" to _hora,
                        "grupo" to "${tutor?.grupo}",
                        "tutor" to "${tutor?.nombre} ${tutor?.apellido_pa} ${tutor?.apellido_ma}",
                        "evidencia" to _evidencia
                    )
                    baseDeDatos.collection("actividades").add(actividad)
                        .addOnSuccessListener {
                            Toast.makeText(this,"Actividad Agregada correctamente", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this,HomeTutor::class.java)
                            intent.putExtra("tutor", tutor)
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this,"La Actividad no se agrego", Toast.LENGTH_SHORT).show()
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

            popupMenu.menuInflater.inflate(R.menu.menu_tutor, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item -> onMenuItemClick(item) }

            popupMenu.show()
        }
        btnRegresar.setOnClickListener {
            val intent = Intent(this,ActividadesTutor::class.java)
            intent.putExtra("tutor", tutor)
            startActivity(intent)
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
    fun mostrarDatePickerDialog(view: android.view.View) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    fun mostrarTimePickerDialog(view: android.view.View) {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            this,
            this,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }


    override fun onDateSet(view: DatePicker?, año: Int, mes: Int, dia: Int) {
        val fechaEditText: TextInputEditText = findViewById(R.id.fecha_actividad_editText)
        fechaEditText.setText("$año-${mes + 1}-$dia")
        _fecha = fechaEditText.text.toString()
    }

    override fun onTimeSet(view: TimePicker?, hora: Int, minuto: Int) {
        val horaEditText: TextInputEditText = findViewById(R.id.hora_actividad_editText)
        horaEditText.setText("$hora:$minuto")
        _hora = horaEditText.text.toString()
    }


}