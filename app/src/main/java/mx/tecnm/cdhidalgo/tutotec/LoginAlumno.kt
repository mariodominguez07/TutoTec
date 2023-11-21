package mx.tecnm.cdhidalgo.tutotec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.google.android.material.button.MaterialButton

class LoginAlumno : AppCompatActivity() {

    private lateinit var btnRegresar:ImageButton
    private lateinit var btnRegistrar:MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_alumno)

        btnRegresar = findViewById(R.id.btnRegresar_Alumno)
        btnRegistrar = findViewById(R.id.btnRegistrar)

        btnRegresar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnRegistrar.setOnClickListener {

        }
    }
}