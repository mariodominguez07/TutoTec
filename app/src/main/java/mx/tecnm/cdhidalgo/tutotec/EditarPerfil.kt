package mx.tecnm.cdhidalgo.tutotec

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mx.tecnm.cdhidalgo.tutotec.dataClass.Alumno
import mx.tecnm.cdhidalgo.tutotec.dataClass.Tutor

class EditarPerfil : AppCompatActivity() {
    private lateinit var btnRegresar: ImageButton
    private lateinit var btnEditarPerfil: Button
    private lateinit var btnEliminarPerfil: Button

    private lateinit var nombre: TextInputEditText
    private lateinit var apellidoPa: TextInputEditText
    private lateinit var apellidoMa: TextInputEditText
    private lateinit var grupo: TextInputEditText
    private lateinit var carrera: Spinner

    private lateinit var auth: FirebaseAuth

    var _nombre = ""
    var _apePa = ""
    var _apeMa = ""
    var _grupo = ""
    var _carrera = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)
        val baseDeDatos = Firebase.firestore

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        btnEditarPerfil = findViewById(R.id.btn_editar_perfil)
        btnRegresar = findViewById(R.id.btn_regresar_editar)
        btnEliminarPerfil= findViewById(R.id.btn_eliminar_usuario)

        nombre = findViewById(R.id.nombre_Editar)
        apellidoPa = findViewById(R.id.apePaterno_Editar)
        apellidoMa = findViewById(R.id.apeMaterno_editar)
        grupo = findViewById(R.id.grupo_editar)
        carrera = findViewById(R.id.carrera_Editar)

        val alumno = intent.getParcelableExtra<Alumno>("alumno")
        val tutor = intent.getParcelableExtra<Tutor>("tutor")

        if(alumno != null && tutor == null){
            nombre.setText(alumno.nombre)
            apellidoPa.setText(alumno.apellido_pa)
            apellidoMa.setText(alumno.apellido_ma)
            grupo.setText(alumno.grupo)
            val arreglo_carreras = resources.getStringArray(R.array.carreras)
            val adaptadorCarreras = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                arreglo_carreras
            )
            carrera.adapter = adaptadorCarreras
            val indice = arreglo_carreras.indexOf(alumno.carrera)
            if (indice != -1) {
                carrera.setSelection(indice)
                }
            carrera.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    _carrera = carrera.getItemAtPosition(position) as String
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }

        }else if (tutor != null && alumno == null){
            nombre.setText(tutor.nombre)
            apellidoPa.setText(tutor.apellido_pa)
            apellidoMa.setText(tutor.apellido_ma)
            grupo.setText(tutor.grupo)
            val arreglo_carreras = resources.getStringArray(R.array.carreras)
            val adaptadorCarreras = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                arreglo_carreras
            )
            carrera.adapter = adaptadorCarreras
            val indice = arreglo_carreras.indexOf(tutor.academia)
            if (indice != -1) {
                carrera.setSelection(indice)
            }
            carrera.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    _carrera = carrera.getItemAtPosition(position) as String
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }


        }
        btnEditarPerfil.setOnClickListener {
            _nombre = nombre.text.toString()
            _apePa = apellidoPa.text.toString()
            _apeMa = apellidoMa.text.toString()
            _grupo = grupo.text.toString()
            if (alumno != null && tutor ==null){

                val actualizar = hashMapOf<String, Any>(
                    "nombre" to _nombre,
                    "apellido_pa" to _apePa,
                    "apellido_ma" to _apeMa,
                    "grupo" to _grupo,
                    "carrera" to _carrera
                )
                baseDeDatos.collection("alumnos")
                    .whereEqualTo("nocontrol", alumno.nocontrol)
                    .get()
                    .addOnSuccessListener {documents ->
                        for (document in documents){
                            val documentReference = baseDeDatos.collection("alumnos").document(document.id)
                            documentReference.update(actualizar)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Perfil Actualizado", Toast.LENGTH_SHORT).show()
                                    val intent = Intent()
                                    intent.putExtra("alumno_actualizado", alumno)
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "El Perfil No se Actualizo", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Algo salio mal", Toast.LENGTH_SHORT).show()
                    }
            }else if(tutor != null && alumno == null){

                val actualizar = hashMapOf<String, Any>(
                    "nombre" to _nombre,
                    "apellido_pa" to _apePa,
                    "apellido_ma" to _apeMa,
                    "grupo" to _grupo,
                    "academia" to _carrera
                )
                baseDeDatos.collection("tutores")
                    .whereEqualTo("correo", tutor.correo)
                    .get()
                    .addOnSuccessListener {documents ->
                        for (document in documents){
                            val documentReference = baseDeDatos.collection("tutores").document(document.id)
                            documentReference.update(actualizar)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Perfil Actualizado", Toast.LENGTH_SHORT).show()
                                    val intent = Intent()
                                    intent.putExtra("tutor_actualizado", tutor)
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "El Perfil No se Actualizo", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Algo salio mal", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        btnEliminarPerfil.setOnClickListener {
            if (alumno != null && tutor ==null){
                val confirmarDialogo = AlertDialog.Builder(this)
                confirmarDialogo.setTitle("Eliminar Perfil")
                confirmarDialogo.setMessage(
                    """
                   ¿Estas seguro de eliminar el perfil?
                """.trimIndent()
                )
                confirmarDialogo.setPositiveButton("Confirmar"){ confirmarDialogo,which->
                    baseDeDatos.collection("alumnos")
                        .whereEqualTo("nocontrol", alumno.nocontrol)
                        .get()
                        .addOnSuccessListener {documents ->
                            for (document in documents){
                                val documentReference = baseDeDatos.collection("alumnos").document(document.id)
                                documentReference.delete()
                                    .addOnSuccessListener {
                                        user?.delete()
                                            ?.addOnCompleteListener {task ->
                                                if (task.isSuccessful){
                                                    Toast.makeText(this, "Usuario Eliminado", Toast.LENGTH_SHORT).show()
                                                    val intent = Intent(this,MainActivity::class.java)
                                                    startActivity(intent)
                                                }else{
                                                    Toast.makeText(this, "No se pudo eliminar al usuario", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                    }
                                    .addOnFailureListener {  }
                            }
                        }

                }
                confirmarDialogo.setNegativeButton("Cancelar"){confirmarDialogo,which->
                    confirmarDialogo.cancel()
                }
                confirmarDialogo.show()

            }else if(tutor != null && alumno == null){
                val confirmarDialogo = AlertDialog.Builder(this)
                confirmarDialogo.setTitle("Eliminar Perfil")
                confirmarDialogo.setMessage(
                    """
                   ¿Estas seguro de eliminar el perfil?
                """.trimIndent()
                )
                confirmarDialogo.setPositiveButton("Confirmar"){ confirmarDialogo,which->
                baseDeDatos.collection("tutores")
                    .whereEqualTo("correo", tutor.correo)
                    .get()
                    .addOnSuccessListener {documents ->
                        for (document in documents){
                            val documentReference = baseDeDatos.collection("tutores").document(document.id)
                            documentReference.delete()
                                .addOnSuccessListener {
                                    user?.delete()
                                        ?.addOnCompleteListener {task ->
                                            if (task.isSuccessful){
                                                Toast.makeText(this, "Usuario Eliminado", Toast.LENGTH_SHORT).show()
                                                val intent = Intent(this,MainActivity::class.java)
                                                startActivity(intent)
                                            }else{
                                                Toast.makeText(this, "No se pudo eliminar al usuario", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                }
                                .addOnFailureListener {  }
                        }
                    }
                }
                confirmarDialogo.setNegativeButton("Cancelar"){confirmarDialogo,which->
                    confirmarDialogo.cancel()
                }
                confirmarDialogo.show()

            }
        }

        btnRegresar.setOnClickListener {
            if (alumno != null && tutor ==null){
                val intent = Intent(this,InicioAlumno::class.java)
                intent.putExtra("alumno", alumno)
                startActivity(intent)
            }else if(tutor != null && alumno == null){
                val intent = Intent(this,InicioTutor::class.java)
                intent.putExtra("tutor", tutor)
                startActivity(intent)
            }
        }

    }
}