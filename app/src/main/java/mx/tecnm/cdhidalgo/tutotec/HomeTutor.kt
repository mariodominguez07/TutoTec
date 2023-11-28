package mx.tecnm.cdhidalgo.tutotec

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import mx.tecnm.cdhidalgo.tutotec.dataClass.Actividades
import mx.tecnm.cdhidalgo.tutotec.dataClass.Alumno
import mx.tecnm.cdhidalgo.tutotec.dataClass.Tutor

class HomeTutor : AppCompatActivity() {

    private lateinit var menu : ImageButton

    private lateinit var spActividades:Spinner
    private lateinit var btnSubirEvidencia:Button
    private lateinit var txtActividadFecha:TextView
    private lateinit var tablaAlumnos:TableLayout

    private lateinit var auth: FirebaseAuth

    var _actividad : Actividades = Actividades()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_tutor)

        auth = FirebaseAuth.getInstance()
        val baseDeDatos = Firebase.firestore

        menu = findViewById(R.id.btnmenu_tutor_home)
        spActividades = findViewById(R.id.sp_actividades_tutor)
        btnSubirEvidencia = findViewById(R.id.btn_subir_evidencia_tutor)
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

        val nomT = "${tutor?.nombre} ${tutor?.apellido_pa} ${tutor?.apellido_ma}"

        baseDeDatos.collection("actividades").whereEqualTo("grupo", mx.tecnm.cdhidalgo.tutotec.tutor.grupo)
            .whereEqualTo("tutor",nomT)
            .get().addOnSuccessListener { result ->
                val listaActividades = mutableListOf<Actividades>()
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

        val baseDeDatos = Firebase.firestore

        for (alumno in listaAlumnos){
            var _nombre = "${alumno.nombre} ${alumno.apellido_pa} ${alumno.apellido_ma}"

            val fila = TableRow(this)
            val btnSiAsistio = ImageButton(this)
            val btnNoAsistio = ImageButton(this)

            baseDeDatos.collection("asistencias")
                .whereEqualTo("nocontrol",alumno.nocontrol)
                .get().addOnSuccessListener {documents ->
                    for(documento in documents){
                        val asistencia = documento.getString("asistio")

                        if (asistencia == "Si"){
                            btnSiAsistio.isEnabled = false
                            btnNoAsistio.isEnabled = false
                        } else if(asistencia == "No"){
                            btnSiAsistio.isEnabled = false
                            btnNoAsistio.isEnabled = false
                        }
                    }
                }
                .addOnFailureListener {  }

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
            tvNombreCompleto.text = _nombre
            tvNombreCompleto.setBackgroundResource(R.drawable.border )
            tvNombreCompleto.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tvNombreCompleto.setTextColor(R.color.black)
            tvNombreCompleto.setTypeface(null,Typeface.BOLD)
            tvNombreCompleto.layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,1f)
            fila.addView(tvNombreCompleto)


            btnSiAsistio.setImageResource(R.drawable.img_si_asistencia)
            btnSiAsistio.setBackgroundResource(R.drawable.border)
            btnSiAsistio.layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,.5f)
            btnSiAsistio.setOnClickListener {
                val _siAsistio = "Si"
                val confirmarDialogo = AlertDialog.Builder(it.context)
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
                        baseDeDatos.collection("asistencias").add(actividad)
                            .addOnSuccessListener {
                                Toast.makeText(this,"Asistencia Confirmada", Toast.LENGTH_SHORT).show()
                                btnSiAsistio.isEnabled = false
                                btnNoAsistio.isEnabled = false
                            }
                            .addOnFailureListener {
                                Toast.makeText(this,"La Asistencia no se confirmo", Toast.LENGTH_SHORT).show()
                            }
                    }

                }
                confirmarDialogo.setNegativeButton("Editar"){confirmarDialogo,which->
                    confirmarDialogo.cancel()
                }
                confirmarDialogo.show()
            }
            fila.addView(btnSiAsistio)


            btnNoAsistio.setImageResource(R.drawable.img_no_asistencia)
            btnNoAsistio.setBackgroundResource(R.drawable.border)
            btnNoAsistio.layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,.5f)
            btnNoAsistio.setOnClickListener {
                val _noAsistio = "No"
                val confirmarDialogo = AlertDialog.Builder(it.context)
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
                        baseDeDatos.collection("asistencias").add(actividad)
                            .addOnSuccessListener {
                                Toast.makeText(this,"Falta Confirmada", Toast.LENGTH_SHORT).show()
                                btnSiAsistio.isEnabled = false
                                btnNoAsistio.isEnabled = false
                            }
                            .addOnFailureListener {
                                Toast.makeText(this,"La Falta no se confirmo", Toast.LENGTH_SHORT).show()
                            }
                    }

                }
                confirmarDialogo.setNegativeButton("Editar"){confirmarDialogo,which->
                    confirmarDialogo.cancel()
                }
                confirmarDialogo.show()
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