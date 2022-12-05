package com.example.a436proj

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.a436proj.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await


class LoginActivity : AppCompatActivity() {
    private lateinit var oneTapClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private val reqOneTap = 2
    private lateinit var pref : SharedPreferences
    private val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = getSharedPreferences("Credentials", Context.MODE_PRIVATE) //shared ref
        with(pref.edit()){
            putString("email", "")
            putString("password", "")
            apply()
        }



        supportActionBar!!.title = "Login"

        firebaseAuth = requireNotNull(FirebaseAuth.getInstance())

        val signInRequest = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("951802864601-aiqbs92cqaq3pljd2eei6apj1cpkmc6m.apps.googleusercontent.com")
            .requestEmail()
            .build()

        oneTapClient = GoogleSignIn.getClient(this,signInRequest)



        binding.btnSignInSubmit.setOnClickListener {loginUserAccount()}

        binding.btnGoogleSign.setOnClickListener {loginGoogle()}
    }

    private fun loginUserAccount(){
        val email: String = binding.usernameText.text.toString()
        val password: String = binding.passwordText.text.toString()

        if(TextUtils.isEmpty(email)){
            Toast.makeText(
                this,
                getString(R.string.login_toast),
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(
                this,
                getString(R.string.password_toast),
                Toast.LENGTH_LONG
            ).show()
            return
        }
       binding.progressBar.visibility = View.VISIBLE

       firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{ task->
           binding.progressBar.visibility = View.GONE
           if(!(firebaseAuth.currentUser!!.isEmailVerified)){
               Toast.makeText(
                   this,
                   "Please verify email before login",
                   Toast.LENGTH_LONG
               ).show()
           }

           else if(task.isSuccessful && firebaseAuth.currentUser != null) {
                   Toast.makeText(
                       this,
                       "Login Success",
                       Toast.LENGTH_LONG
                   ).show()


                   //
                   var dbRef = database.getReference("User")
                   dbRef.child(firebaseAuth.uid!!).child("email")
                       .get() //getting email from database <- need to change to data once we know which data are going to be stored.
                   //update viewModel values with data retrieved.

                   //write to shared pref current credential
                   with(pref.edit()) {
                       putString("email", email)
                       putString("password", password)
                       apply()
                   }

                   startActivity(Intent(this, GroupActivity::class.java))
                   finishAffinity()

           }
           else{
               Toast.makeText(
                   this,
                   "Login failed! Please try again later",
                   Toast.LENGTH_LONG
               ).show()

           }
       }
    }

    private fun loginGoogle(){
        val signInIntent = oneTapClient.signInIntent
        startActivityForResult(signInIntent, reqOneTap)
    }

    override fun onActivityResult(requestCode: Int , result: Int, data: Intent?){
        super.onActivityResult(requestCode, result, data)
        when (requestCode) {
            reqOneTap -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    val idToken = account.idToken
                    when {
                        idToken != null -> {
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            firebaseAuth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(
                                            this,
                                            "Login Success",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        var dbRef = database.getReference("User")
                                        val use = RegistrationActivity.User(firebaseAuth.uid, "password")
                                        dbRef.child(firebaseAuth.uid!!).setValue(use)
                                        val email = dbRef.child(firebaseAuth.uid!!).child("email").get().toString()
                                        val password = dbRef.child(firebaseAuth.uid!!).child("password").get().toString()

                                        dbRef = database.getReference("contacts")
                                        dbRef.child(firebaseAuth.uid!!).get().addOnCompleteListener{task->
                                            if(task.isSuccessful){
                                                val result  =task.result.value
                                                Log.i("firebase", "Got value $result")
                                                if (result == null){ // value not found initialze
                                                    val data = RegistrationActivity.Group(null)
                                                    dbRef.child(firebaseAuth.uid!!).setValue(data) // initialize contacts with data.
                                                }
                                            }else{
                                                Log.e("firebase", "Error getting data")
                                            }
                                        }

                                        //write to shared pref current credential
                                        with(pref.edit()){
                                            putString("email", email)
                                            putString("password", password)
                                            apply()
                                        }

                                        startActivity(Intent(this, GroupActivity::class.java))
                                        finishAffinity()

                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Login failed! Please try again later",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        }
                        else -> {
                            // Shouldn't happen.
                            Toast.makeText(
                                this,
                                "Login failed! Please try again later",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } catch (e: ApiException) {
                    Toast.makeText(
                        this,
                        "Login failed! Please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }


}