package com.example.foodfusionadmin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ActivityChooserView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodfusionadmin.databinding.ActivityLoginBinding
import com.example.foodfusionadmin.model.UserModel
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class Login_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var email:String
    private lateinit var password:String
    private lateinit var database: DatabaseReference
    private  var nameOfRestorent:String ?= null
    private  var username:String ?= null
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        //initilization Firebase Auth
        auth = FirebaseAuth.getInstance()
        //initilization Firebase database
        database = Firebase.database.reference
        //initilization google Sign In
        googleSignInClient = GoogleSignIn.getClient(this,GoogleSignInOptions)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.dontHaveAccount.setOnClickListener {
            startActivity(Intent(this,signUp_Activity::class.java))
        }
        binding.Google.setOnClickListener {
            val signIntent = googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }
        binding.loginbutton.setOnClickListener {

            //get text from edittext
            email = binding.editTextTextEmailAddress.text.toString().trim()
            password = binding.editTextTextPassword.text.toString().trim()

            if (email.isBlank() || password.isBlank()){
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
            }else{
                createUserAccount(email,password)
            }
        }
    }


    private fun createUserAccount(email: String, password: String) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                Toast.makeText(this, "Admin SignIn sucessfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }else{
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }

    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful){
                val account:GoogleSignInAccount = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken,null)
                auth.signInWithCredential(credential).addOnCompleteListener { task->
                    if (task.isSuccessful){
                        //successfully signind with google
                        Toast.makeText(this, "Successfully sign-in with google", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }else{
            Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
        }
    }
    //chek if user is already loged
    override fun onStart() {
        super.onStart()
        val currentuser = auth.currentUser
        if (currentuser != null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }


}