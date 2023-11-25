package mx.tecnm.cdhidalgo.tutotec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mx.tecnm.cdhidalgo.tutotec.dataClass.Tutor
lateinit var tutor: Tutor
class LoginTutor : AppCompatActivity() {

    private lateinit var btnRegresar: ImageButton
    private lateinit var btnIngresar: Button
    private lateinit var btnRecuperar: MaterialButton
    private lateinit var btnRegistrar: MaterialButton

    private lateinit var correo: TextInputLayout
    private lateinit var pass: TextInputLayout

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_tutor)

        auth = Firebase.auth

        val baseDeDatos = Firebase.firestore

        btnRegresar = findViewById(R.id.btnRegresar_Tutor)
        btnRegistrar = findViewById(R.id.btnRegistrar_Tutor)
        btnRecuperar = findViewById(R.id.btnOlvidar_Tutor)
        btnIngresar = findViewById(R.id.btnIngresar_Tutor)
        correo = findViewById(R.id.email_tutor)
        pass = findViewById(R.id.password_tutor)

        btnRegresar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnRegistrar.setOnClickListener {
            val intent = Intent(this, RegistroTutor::class.java)
            startActivity(intent)
            finish()
        }

        btnRecuperar.setOnClickListener {

        }

        btnIngresar.setOnClickListener {
            val email = correo.editText?.text
            val psw = pass.editText?.text
            if(email.toString().isNotEmpty()&&psw.toString().isNotEmpty()){
                auth.signInWithEmailAndPassword(email.toString(),psw.toString()).addOnCompleteListener{
                    if (it.isSuccessful){
                        baseDeDatos.collection("tutores").whereEqualTo("correo",email.toString()).get().addOnSuccessListener {documentos->
                            for(documento in documentos){
                                tutor = Tutor(
                                    "${documento.data.get("correo")}",
                                    "${documento.data.get("nombre")}",
                                    "${documento.data.get("apellido_pa")}",
                                    "${documento.data.get("apellido_ma")}",
                                    "${documento.data.get("academia")}",
                                    "${documento.data.get("grupo")}",
                                    "${documento.data.get("foto")}"
                                )
                            }
                            val intent = Intent(this, InicioTutor::class.java)
                            intent.putExtra("tutor",tutor)
                            startActivity(intent)
                        }
                    }else{
                        notificacion()
                    }
                }
            }
        }
    }

    private fun notificacion() {
        val notiDialogo = AlertDialog.Builder(this)
        notiDialogo.setTitle("Error")
        notiDialogo.setMessage("Se ha producido un error en la AUTENTICACION del usuario!!")
        notiDialogo.setPositiveButton("Aceptar",null)
        notiDialogo.show()
    }
}