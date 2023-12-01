package mx.tecnm.cdhidalgo.tutotec

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import mx.tecnm.cdhidalgo.tutotec.dataClass.Alumno

class RegistroAlumno : AppCompatActivity() {

    private lateinit var btnElegirImagen: Button
    private lateinit var btnRegresar: ImageButton
    private lateinit var btnRegistrame: Button

    private lateinit var noControl: TextInputLayout
    private lateinit var nombre: TextInputLayout
    private lateinit var apePaterno: TextInputLayout
    private lateinit var apeMaterno: TextInputLayout
    private lateinit var carrera: Spinner
    private lateinit var grupo: TextInputLayout
    private lateinit var correo: TextInputLayout
    private lateinit var pass: TextInputLayout
    private lateinit var foto: TextView

    private val storageReference = FirebaseStorage.getInstance().reference
    private var selectedImageUri: Uri? = null

    var _carrera = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_alumno)

        val baseDeDatos = Firebase.firestore

        btnElegirImagen = findViewById(R.id.btnElegirImagen)
        btnRegresar = findViewById(R.id.btnRegresar_registro_alumno)
        btnRegistrame = findViewById(R.id.btnRegistrarme_Alumno)

        noControl = findViewById(R.id.no_control_registro)
        nombre = findViewById(R.id.nombre)
        apePaterno = findViewById(R.id.ape_paterno)
        apeMaterno = findViewById(R.id.ape_materno)
        carrera = findViewById(R.id.carrera_registro)
        grupo = findViewById(R.id.grupo)
        correo = findViewById(R.id.email_registro)
        pass = findViewById(R.id.password_registro)
        foto = findViewById(R.id.txtImagen)


        btnElegirImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        val arreglo_carreras = resources.getStringArray(R.array.carreras)
        val adaptadorCarreras = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            arreglo_carreras
        )
        carrera.adapter = adaptadorCarreras

        carrera.onItemSelectedListener = object : OnItemSelectedListener{
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

        btnRegistrame.setOnClickListener {
            val email = correo.editText?.text
            val psw = pass.editText?.text

            // Asegúrate de que se haya seleccionado una imagen
            if (selectedImageUri != null) {
                // Sube la imagen a Firebase Storage
                val storageRef =
                    storageReference.child("alumnos/${selectedImageUri?.lastPathSegment}")
                storageRef.putFile(selectedImageUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        // La imagen se ha subido con éxito, obtén la URL de descarga
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            val downloadUrl = uri.toString()
                            // Guarda la información del alumno en Firestore
                            val confirmarDialogo = AlertDialog.Builder(it.context)
                            confirmarDialogo.setTitle("Confirmar Alumno")
                            confirmarDialogo.setMessage(
                                """
                                Número de Control: ${noControl.editText?.text} 
                                Nombre: ${nombre.editText?.text} ${apePaterno.editText?.text} ${apeMaterno.editText?.text}
                                Carrera: ${_carrera}
                                Grupo: ${grupo.editText?.text}
                                Correo: ${correo.editText?.text}
                                Contraseña ${pass.editText?.text}
                                """.trimIndent()
                            )
                            confirmarDialogo.setPositiveButton("Confirmar") { confirmarDialogo, which ->
                                baseDeDatos.collection("alumnos")
                                    .whereEqualTo("nocontrol", noControl.editText?.text.toString())
                                    .get().addOnSuccessListener {
                                        val noControl = noControl.editText?.text.toString()

                                        baseDeDatos.collection("alumnos")
                                            .whereEqualTo("nocontrol", noControl)
                                            .get()
                                            .addOnSuccessListener { querySnapshot ->
                                                if (!querySnapshot.isEmpty) {
                                                    // El número de control ya está registrado
                                                    Toast.makeText(
                                                        this,
                                                        "El alumno ya ha sido registrado",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                } else {
                                                    // El número de control no está registrado, puedes proceder con el registro
                                                    if (email.toString()
                                                            .isNotEmpty() && psw.toString()
                                                            .isNotEmpty()
                                                    ) {
                                                        val alumno = hashMapOf(
                                                            "correo" to email.toString(),
                                                            "nocontrol" to noControl,
                                                            "nombre" to nombre.editText?.text.toString(),
                                                            "apellido_pa" to apePaterno.editText?.text.toString(),
                                                            "apellido_ma" to apeMaterno.editText?.text.toString(),
                                                            "carrera" to _carrera,
                                                            "grupo" to grupo.editText?.text.toString(),
                                                            "foto" to downloadUrl
                                                        )

                                                        FirebaseAuth.getInstance()
                                                            .createUserWithEmailAndPassword(
                                                                email.toString(), psw.toString()
                                                            ).addOnCompleteListener { task ->
                                                            if (task.isSuccessful) {
                                                                // Usuario creado exitosamente, ahora guarda la información en Firestore
                                                                val user = task.result?.user
                                                                baseDeDatos.collection("alumnos")
                                                                    .document(noControl).set(alumno)
                                                                    .addOnSuccessListener {
                                                                        val intent = Intent(
                                                                            this,
                                                                            LoginAlumno::class.java
                                                                        )
                                                                        startActivity(intent)
                                                                    }
                                                                    .addOnFailureListener {
                                                                        Toast.makeText(
                                                                            this,
                                                                            "Error al guardar la información",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                    }
                                                            } else {
                                                                // Manejar errores al crear el usuario
                                                                Toast.makeText(
                                                                    this,
                                                                    "Error al crear usuario",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            .addOnFailureListener {
                                                // Manejar errores en la consulta
                                                Toast.makeText(
                                                    this,
                                                    "Error al consultar la base de datos",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    }
                            }
                            confirmarDialogo.setNegativeButton("Editar") { confirmarDialogo, which ->
                                confirmarDialogo.cancel()
                            }
                            confirmarDialogo.show()
                        }
                            .addOnFailureListener { e ->
                                // Error al subir la imagen a Firebase Storage
                            }
                    }

            }

            btnRegresar.setOnClickListener {
                val intent = Intent(this, LoginAlumno::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    // Método para manejar la selección de imágenes desde la galería
    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                // Almacena la URI seleccionada
                selectedImageUri = uri

                // Obtiene el nombre del archivo de la URI
                val cursor = contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val displayName =
                            it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        // Muestra el nombre del archivo en un TextView
                        foto.text = displayName
                    }
                }
            }
        }
    }


    private fun notificacion() {
        val notiDialogo = AlertDialog.Builder(this)
        notiDialogo.setTitle("Error")
        notiDialogo.setMessage("Se ha producido un error en la AUTENTICACION!!")
        notiDialogo.setPositiveButton("Aceptar",null)
        notiDialogo.show()
    }
}
