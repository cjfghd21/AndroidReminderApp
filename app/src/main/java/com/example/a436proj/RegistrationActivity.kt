package com.example.a436proj

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.a436proj.databinding.ActivityRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    private var validator = Validators()
    private lateinit var auth: FirebaseAuth
    private val database = Firebase.database


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.title = "Register"

        auth = requireNotNull(FirebaseAuth.getInstance())

        binding.btnSignInSubmit.setOnClickListener {registerNewUser()}
    }

    private fun registerNewUser(){
        val email: String = binding.emailText.text.toString()
        val password: String = binding.passwordText.text.toString()

        if (!validator.validEmail(email)) {  //invalid email
            Toast.makeText(
                this,
                getString(R.string.invalid_email),
                Toast.LENGTH_LONG
            ).show()

            return
        }

        if (!validator.validPassword(password)) {  //invalid password
            Toast.makeText(
                this,
                getString(R.string.invalid_password),
                Toast.LENGTH_LONG
            ).show()

            return
        }
        binding.progressBar.visibility = View.VISIBLE  //if valid email and pass, show progress bar

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.progressBar.visibility = View.GONE  // progress bar gone
                if (task.isSuccessful) {  //registration success
                    Toast.makeText(
                        this,
                        getString(R.string.register_success_string),
                        Toast.LENGTH_LONG
                    ).show()
                    val dbRef = database.getReference("User")
                    val use = User(email,password)  //data format to pass
                    dbRef.child(auth.uid!!).setValue(use)
                    startActivity(Intent(this, LoginActivity::class.java)) //registration success sending to login page
                    finishAffinity()
                } else { // registration failed
                    Toast.makeText(
                        this,
                        getString(R.string.register_failed_string),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }


    }
    data class User(
        var email: String? = null,
        var password: String? = null,
    )
}