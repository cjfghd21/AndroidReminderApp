package com.example.a436proj

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
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

        if(validator.isGoogleAccount(email)){  //google account. please use google sigin option
            Toast.makeText(
                this,
                getString(R.string.invalid_google),
                Toast.LENGTH_LONG
            ).show()
            return
        }


        if (!validator.validPassword(password)) {  //invalid password
            val message = validator.reasonInvalid(password)

            Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
            ).show()

            return
        }
//        binding.progressBar.visibility = View.VISIBLE  //if valid email and pass, show progress bar

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
//                binding.progressBar.visibility = View.GONE  // progress bar gone
                Log.i("Our logg", "our log here@@@@@@@@@@")
                if (task.isSuccessful) {  //registration success

                    Toast.makeText(
                        this,
                        getString(R.string.register_success_string),
                        Toast.LENGTH_LONG
                    ).show()
                    var dbRef = database.getReference("User")
                    val use = User(email,password)  //data format to pass
                    dbRef.child(auth.uid!!).setValue(use)
                    dbRef = database.getReference("contacts")
                    val data = Group(null)
                    dbRef.child(auth.uid!!).setValue(data) // initialize contacts with data.
                    startActivity(Intent(this, LoginActivity::class.java)) //registration success sending to login page
                    finishAffinity()
                } else { // registration failed
                    auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(){task->
                        val existingEmail = task.result
                        Log.i("existing Email", "$existingEmail")
                        if(existingEmail != null){
                            Toast.makeText(
                                this,
                                getString(R.string.register_failed_existing),
                                Toast.LENGTH_LONG
                            ).show()
                        }else{
                            Toast.makeText(
                                this,
                                getString(R.string.register_failed_string),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }


    }
    data class User(
        var email: String? = null,
        var password: String? = null,
    )

    data class Group(
        var group: MutableLiveData<MutableList<ExpandableGroupModel>>? = null
    )
}