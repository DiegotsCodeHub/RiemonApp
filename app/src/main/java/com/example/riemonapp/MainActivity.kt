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
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.SignInButton
import com.google.firebase.firestore.FirebaseFirestore

enum class ProviderType{
    BASIC
}
private lateinit var auth: FirebaseAuth
private lateinit var googleSignInClient: GoogleSignInClient

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val botonRegistro = findViewById<Button>(R.id.btn_Registro)
        val botonAcceso = findViewById<Button>(R.id.btn_AccesoApp)
        val btnGoogle = findViewById<SignInButton>(R.id.btnGoogle)
        val email = findViewById<TextView>(R.id.email_logIn)
        val pass = findViewById<TextView>(R.id.pass_logIn)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("78406427559-7njc34r11eqe1rcnbebol1096sq2dio3.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btnGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, 100)

        }

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
                            email.text = ""
                            pass.text = ""
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val user = auth.currentUser

                    Toast.makeText(this, "Bienvenido ${user?.displayName}", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this, AppMenuActivity::class.java))
                    finish()

                } else {
                    Toast.makeText(this, "Error en login", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error al iniciar sesion, prueba a revisar tus credenciales")
        builder.setPositiveButton("Aceptar", null)
        val Dialog: AlertDialog = builder.create()
        Dialog.show()
    }

    private fun showOptionsMenu() {
        val AppIntent = Intent(this, AppMenuActivity::class.java)
        startActivity(AppIntent)
    }
}