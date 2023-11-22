package mx.tecnm.cdhidalgo.tutotec

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.hdodenhof.circleimageview.CircleImageView

class InicioAlumno : AppCompatActivity() {

    private lateinit var foto:CircleImageView
    private lateinit var nombreC:TextView
    private lateinit var noControl:TextView
    private lateinit var grupo:TextView
    private lateinit var carrera:TextView
    private lateinit var tutor: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_alumno)





    }
}