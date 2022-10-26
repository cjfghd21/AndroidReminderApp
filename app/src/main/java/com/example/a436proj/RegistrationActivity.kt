package com.example.a436proj

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.a436proj.databinding.ActivityLoginBinding
import com.example.a436proj.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignInSubmit.setOnClickListener {
            val email = binding.emailText.text
            val userName = binding.usernameText.text
            val password = binding.passwordText.text
            var validRegistration = true

            //Begin code to handle registration. Things like checking that
            //username is free and password is valid.

            if(validRegistration) {
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
        }
    }
}