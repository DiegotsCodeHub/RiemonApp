package com.example.riemonapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.Toast
import android.content.Intent
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType{
    BASIC
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val botonRegistro = findViewById<Button>(R.id.btn_Registro)
        val botonAcceso = findViewById<Button>(R.id.btn_AccesoApp)
        val email = findViewById<TextView>(R.id.email_logIn)
        val pass = findViewById<TextView>(R.id.pass_logIn)

        botonRegistro.setOnClickListener {
            val intent = Intent(this, RegistroUsersActivity::class.java)
            startActivity(intent)
        }

        botonAcceso.setOnClickListener {
            val emailtolog = email.text.toString()
            val passtolog = pass.text.toString()

            if (emailtolog.isEmpty() || passtolog.isEmpty()) {
                Toast.makeText(this, "Favor de rellenar los campos requeridos", Toast.LENGTH_SHORT).show()
            } else {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(emailtolog, passtolog)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Inicio de Sesion exitoso", Toast.LENGTH_SHORT).show()
                            showOptionsMenu()
                        } else {
                            showAlert()
                        }
                    }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error al iniciar sesion")
        builder.setPositiveButton("Aceptar", null)
        val Dialog: AlertDialog = builder.create()
        Dialog.show()
    }

    private fun showOptionsMenu() {
        val OptionsIntent = Intent(this, AppMenuActivity::class.java)
        startActivity(OptionsIntent)
    }
}