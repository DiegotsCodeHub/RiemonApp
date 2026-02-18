package com.example.riemonapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class RegistroUsersActivity : AppCompatActivity() {

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

    private fun isValidPassword(password: String): String? {
        if (password.length < 6) {
            return "Debe tener al menos 6 caracteres"
        }

        var tieneMayuscula = false
        var tieneEspecial = false

        for (char in password) {

            if (char.isUpperCase()) {
                tieneMayuscula = true
            }

            if (!char.isLetterOrDigit()) {
                tieneEspecial = true
            }
        }

        if (!tieneMayuscula) {
            return "Debe contener al menos una letra mayúscula"
        }

        if (!tieneEspecial) {
            return "Debe contener al menos un carácter especial"
        }

        return null // contraseña válida
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

            val passwordError = isValidPassword(pass)

            if (email.isEmpty() || pass.isEmpty() || passConfirm.isEmpty()) {
                Toast.makeText(this, "Favor de rellenar los campos requeridos",
                    Toast.LENGTH_LONG).show()
            }
            else if (pass != passConfirm)
            {
                Toast.makeText(this, "Las contraseñas no coinciden",
                    Toast.LENGTH_LONG).show()
            }
            else if(passwordError != null){
                passregistro.error = passwordError
                Toast.makeText(this, passwordError,
                    Toast.LENGTH_LONG).show()
            }
            else
            {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_LONG).show()
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