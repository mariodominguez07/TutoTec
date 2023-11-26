package mx.tecnm.cdhidalgo.tutotec

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import mx.tecnm.cdhidalgo.tutotec.dataClass.Tutor

class HomeTutor : AppCompatActivity() {

    val tutor = intent.getParcelableExtra<Tutor>("tutor")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_tutor)
    }
}