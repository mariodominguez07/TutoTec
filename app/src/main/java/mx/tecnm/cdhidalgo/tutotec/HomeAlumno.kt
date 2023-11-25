package mx.tecnm.cdhidalgo.tutotec

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import mx.tecnm.cdhidalgo.tutotec.dataClass.Alumno

class HomeAlumno : AppCompatActivity() {
    val alumno = intent.getParcelableExtra<Alumno>("alumno")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_alumno)
    }
}