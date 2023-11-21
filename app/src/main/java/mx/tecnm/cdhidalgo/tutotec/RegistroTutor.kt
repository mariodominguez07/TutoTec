package mx.tecnm.cdhidalgo.tutotec

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
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



        btnRegresar.setOnClickListener {
            val intent = Intent(this,LoginTutor::class.java)
            startActivity(intent)
            finish()
        }
    }
}