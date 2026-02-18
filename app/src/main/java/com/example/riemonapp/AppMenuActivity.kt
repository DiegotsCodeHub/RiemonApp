package com.example.riemonapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore

private lateinit var database: DatabaseReference

class AppMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_app_menu)

        val btnEncender = findViewById<Button>(R.id.btnEncender)
        val btnApagar = findViewById<Button>(R.id.btnApagar)
        val btnProgramar = findViewById<Button>(R.id.btnProgramar)
        val btnLogOut = findViewById<Button>(R.id.btn_LogOut)

        btnEncender.setOnClickListener {}

        btnApagar.setOnClickListener {}

        btnProgramar.setOnClickListener {}

        btnLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            finish()
            startActivity(intent)
        }

        database = FirebaseDatabase.getInstance()
            .reference.child("jardin_principal")

        escucharDatos()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun escucharDatos() {

        val Bomba = findViewById<TextView>(R.id.txtBomba)
        val Estado = findViewById<TextView>(R.id.txtEstado)
        val aguanivel = findViewById<TextView>(R.id.txtNivelAgua)
        val humedadnivel = findViewById<TextView>(R.id.txtNivelHumedad)


        database.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val sistema = snapshot.getValue(SistemaRiegoValores::class.java)

                sistema?.let {

                    // Bomba
                    Bomba.text = if (sistema.bomba_activa)
                        "Activa"
                    else
                        "Inactiva"

                    // Estado sistema
                    Estado.text = if (sistema.estado_sistema == "REGANDO")
                        "Regando"
                    else
                        "Apagado"

                    // Sensores
                    humedadnivel.text = "${sistema.humedad_suelo}%"
                    aguanivel.text = "${sistema.nivel_agua}%"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Estado.text = "Error: ${error.message}"
            }
        })

    }
}