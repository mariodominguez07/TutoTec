package mx.tecnm.cdhidalgo.tutotec

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)
        val baseDeDatos = Firebase.firestore

        auth = FirebaseAuth.getInstance()
        btnEditarPerfil = findViewById(R.id.btn_editar_perfil)
        btnRegresar = findViewById(R.id.btn_regresar_editar)
        btnEliminarPerfil= findViewById(R.id.btn_eliminar_usuario)



    }
}