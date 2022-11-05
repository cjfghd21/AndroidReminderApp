package com.example.a436proj

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.a436proj.databinding.ActivityRegistrationBinding
import com.google.firebase.auth.FirebaseAuth

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    private var validator = Validators()
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = requireNotNull(FirebaseAuth.getInstance())

        binding.btnSignInSubmit.setOnClickListener {registerNewUser()}
    }

    private fun registerNewUser(){
        val email: String = binding.emailText.text.toString()
        val password: String = binding.passwordText.text.toString()

        if (!validator.validEmail(email)) {
            Toast.makeText(
                this,
                getString(R.string.invalid_email),
                Toast.LENGTH_LONG
            ).show()

            return
        }

        if (!validator.validPassword(password)) {
            Toast.makeText(
                this,
                getString(R.string.invalid_password),
                Toast.LENGTH_LONG
            ).show()

            return
        }
        binding.progressBar.visibility = View.VISIBLE

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        getString(R.string.register_success_string),
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.register_failed_string),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }


    }
}