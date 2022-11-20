package com.example.a436proj

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.a436proj.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {
    private lateinit var oneTapClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private val REQ_ONE_TAP = 2
    private val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

       firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
           task-> binding.progressBar.visibility = View.GONE
           if(task.isSuccessful){
               Toast.makeText(
                   this,
                   "Login Success",
                   Toast.LENGTH_LONG
               ).show()

               //
               val dbRef = database.getReference("User")
               dbRef.child(firebaseAuth.uid!!).child("email").get() //getting email from database <- need to change to data once we know which data are going to be stored.
               //update viewModel values with data retrieved.
               startActivity(Intent(this, GroupActivity::class.java))
               finishAffinity()
           }else{
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
        startActivityForResult(signInIntent, REQ_ONE_TAP)
    }

    override fun onActivityResult(requestCode: Int , result: Int, data: Intent?){
        super.onActivityResult(requestCode, result, data)
        when (requestCode) {
            REQ_ONE_TAP -> {
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