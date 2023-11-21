package mx.tecnm.cdhidalgo.tutotec

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class RegistroTutor : AppCompatActivity() {

    private lateinit var btnElegirImagen: Button
    private lateinit var btnRegresar: ImageButton
    private lateinit var btnRegistrame:Button

    private lateinit var nombre: TextInputLayout
    private lateinit var apePaterno: TextInputLayout
    private lateinit var apeMaterno: TextInputLayout
    private lateinit var academia: TextInputLayout
    private lateinit var grupo: TextInputLayout
    private lateinit var correo: TextInputLayout
    private lateinit var pass: TextInputLayout
    private lateinit var foto: TextView

    private val storageReference = FirebaseStorage.getInstance().reference
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_tutor)

        val baseDeDatos = Firebase.firestore

        btnElegirImagen = findViewById(R.id.btnElegirImagen_tutor)
        btnRegresar = findViewById(R.id.btnRegresar_registro_tutor)
        btnRegistrame = findViewById(R.id.btnRegistrarme_Tutor)

        nombre = findViewById(R.id.nombre_tutor)
        apePaterno = findViewById(R.id.ape_paterno_tutor)
        apeMaterno = findViewById(R.id.ape_materno_tutor)
        academia = findViewById(R.id.academia)
        grupo = findViewById(R.id.grupo_tutor)
        correo = findViewById(R.id.email_registro_tutor)
        pass = findViewById(R.id.password_registro_tutor)
        foto = findViewById(R.id.txtImagen_tutor)

        btnElegirImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        btnRegistrame.setOnClickListener {
            val email = correo.editText?.text
            val psw = pass.editText?.text

            // Asegúrate de que se haya seleccionado una imagen
            if (selectedImageUri != null) {
                // Sube la imagen a Firebase Storage
                val storageRef = storageReference.child("tutores/${selectedImageUri?.lastPathSegment}")
                storageRef.putFile(selectedImageUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        // La imagen se ha subido con éxito, obtén la URL de descarga
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            val downloadUrl = uri.toString()
                            // Guarda la información del alumno en Firestore
                            val confirmarDialogo = AlertDialog.Builder(it.context)
                            confirmarDialogo.setTitle("Confirmar Tutor")
                            confirmarDialogo.setMessage(
                                """
                                Nombre: ${nombre.editText?.text} ${apePaterno.editText?.text} ${apeMaterno.editText?.text}
                                Academia: ${academia.editText?.text}
                                Grupo: ${grupo.editText?.text}
                                Correo: ${correo.editText?.text}
                                Contraseña ${pass.editText?.text}
                                """.trimIndent()
                            )
                            confirmarDialogo.setPositiveButton("Confirmar") { confirmarDialogo, which ->
                                if (email.toString().isNotEmpty() && psw.toString().isNotEmpty()) {
                                    val tutor = hashMapOf(
                                        "correo" to email.toString(),
                                        "nombre" to nombre.editText?.text.toString(),
                                        "apellido_pa" to apePaterno.editText?.text.toString(),
                                        "apellido_ma" to apeMaterno.editText?.text.toString(),
                                        "academia" to academia.editText?.text.toString(),
                                        "grupo" to grupo.editText?.text.toString(),
                                        "foto" to downloadUrl
                                    )
                                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                                        email.toString(), psw.toString()
                                    ).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            val intent =
                                                Intent(this, LoginTutor::class.java).apply {
                                                    baseDeDatos.collection("tutores")
                                                        .document(email.toString()).set(tutor)
                                                }
                                            startActivity(intent)
                                        } else {
                                            notificacion()
                                        }
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
        }

        btnRegresar.setOnClickListener {
            val intent = Intent(this,LoginTutor::class.java)
            startActivity(intent)
            finish()
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