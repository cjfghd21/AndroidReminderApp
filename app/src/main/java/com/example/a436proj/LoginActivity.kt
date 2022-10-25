package com.example.a436proj

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.example.a436proj.databinding.ActivityLoginBinding
import com.example.a436proj.databinding.ActivityMainBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignInSubmit.setOnClickListener {
            val userName = binding.usernameText.text
            val password = binding.passwordText.text
            var authenticated = true

            //Begin code to handle sign in

            if(authenticated) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}