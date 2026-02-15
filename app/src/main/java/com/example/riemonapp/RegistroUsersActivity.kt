package com.example.riemonapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistroUsersActivity : AppCompatActivity() {

    private val dbusers = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_users)

        setup()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setup() {
        val botonregistro = findViewById<Button>(R.id.btn_Registrarse)
        val emailregistro = findViewById<TextView>(R.id.emailregistro)
        val passregistro = findViewById<TextView>(R.id.passregistro)
        val passconfirmregistro = findViewById<TextView>(R.id.passconfirmregistro)

        botonregistro.setOnClickListener {
            val email = emailregistro.text.toString()
            val pass = passregistro.text.toString()
            val passConfirm = passconfirmregistro.text.toString()

            if (email.isEmpty() || pass.isEmpty() || passConfirm.isEmpty()) {
                Toast.makeText(this, "Favor de rellenar los campos requeridos", Toast.LENGTH_SHORT).show()
            }
            else if (pass != passConfirm)
            {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }
            else
            {
                dbusers.collection("users").document(email).set(
                    hashMapOf("password" to pass)
                )

                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                            showHome(it.result?.user?.email?:"", ProviderType.BASIC)
                        } else {
                            showAlert()
                        }
                    }
            }
        }
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error al registrar el usuario")
        builder.setPositiveButton("Aceptar", null)
        val Dialog: AlertDialog=builder.create()
        Dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType){
        val HomeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(HomeIntent)
    }

}