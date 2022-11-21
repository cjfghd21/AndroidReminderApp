package com.example.a436proj

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.a436proj.databinding.ActivityAccessBinding
import com.example.a436proj.databinding.ActivityLoginBinding

class AccessActivity : AppCompatActivity() {
    private lateinit var pref: SharedPreferences
    private lateinit var binding: ActivityAccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pref = getSharedPreferences("Credentials", Context.MODE_PRIVATE)
        if(pref.getString("email", "") != ""){

            //testing purpose popup. can remove for final release.
            Toast.makeText(
                this,
                "Login persisted.",
                Toast.LENGTH_LONG
            ).show()

            startActivity(Intent(this, GroupActivity::class.java))
        }

        binding = ActivityAccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.title = "Welcome"

        binding.btnSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }
}