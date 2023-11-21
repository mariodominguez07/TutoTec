package mx.tecnm.cdhidalgo.tutotec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var btnAlumno:Button
    private lateinit var btnTutor:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnAlumno = findViewById(R.id.btnAlumno)
        btnTutor = findViewById(R.id.btnTutor)

        btnAlumno.setOnClickListener {
            val intent = Intent(this,LoginAlumno::class.java)
            startActivity(intent)
            finish()
        }

        btnTutor.setOnClickListener {
            val intent = Intent(this, LoginTutor::class.java)
            startActivity(intent)
            finish()
        }
    }
}