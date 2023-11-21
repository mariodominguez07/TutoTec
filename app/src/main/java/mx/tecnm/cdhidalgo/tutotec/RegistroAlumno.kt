package mx.tecnm.cdhidalgo.tutotec

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import mx.tecnm.cdhidalgo.tutotec.dataClass.Alumno

class RegistroAlumno : AppCompatActivity() {

    private lateinit var btnElegirImagen:Button
    private lateinit var btnRegresar:ImageButton
    private lateinit var btnRegistrame:Button

    private lateinit var noControl:TextInputLayout
    private lateinit var nombre:TextInputLayout
    private lateinit var apePaterno:TextInputLayout
    private lateinit var apeMaterno:TextInputLayout
    private lateinit var carrera:TextInputLayout
    private lateinit var grupo:TextInputLayout
    private lateinit var correo:TextInputLayout
    private lateinit var pass:TextInputLayout
    private lateinit var foto:TextView

    private val storageReference = FirebaseStorage.getInstance().reference
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_alumno)

        val baseDeDatos = Firebase.firestore

        btnElegirImagen = findViewById(R.id.btnElegirImagen)
        btnRegresar = findViewById(R.id.btnRegresar_registro_alumno)
        btnRegistrame = findViewById(R.id.btnRegistrarme_Alumno)

        noControl=findViewById(R.id.no_control_registro)
        nombre = findViewById(R.id.nombre)
        apePaterno = findViewById(R.id.ape_paterno)
        apeMaterno = findViewById(R.id.ape_materno)
        carrera = findViewById(R.id.carrera)
        grupo = findViewById(R.id.grupo)
        correo = findViewById(R.id.email_registro)
        pass = findViewById(R.id.password_registro)
        foto = findViewById(R.id.txtImagen)


        btnElegirImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        btnRegistrame.setOnClickListener {
            val email = correo.editText?.text
            val psw = pass.editText?.text
            val alumno = Alumno(email.toString(),
                noControl.editText?.text.toString(),
                nombre.editText?.text.toString(),
                apePaterno.editText?.text.toString(),
                apeMaterno.editText?.text.toString(),
                carrera.editText?.text.toString(),
                grupo.editText?.text.toString(),
                foto.text.toString())

        }

        btnRegresar.setOnClickListener {
            val intent = Intent(this,LoginAlumno::class.java)
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
                        val displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        // Muestra el nombre del archivo en un TextView
                        foto.text = displayName
                    }
                }
            }
        }
    }
}