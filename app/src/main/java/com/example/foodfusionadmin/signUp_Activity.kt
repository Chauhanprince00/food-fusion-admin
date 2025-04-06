package com.example.foodfusionadmin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodfusionadmin.databinding.ActivitySignUpBinding
import com.example.foodfusionadmin.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class signUp_Activity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var email:String
    private lateinit var password:String
    private lateinit var username:String
    private lateinit var phone:String
    private lateinit var address:String
    private lateinit var database:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initilization Firebase Auth
        auth = FirebaseAuth.getInstance()
        //initilization Firebase database
        database = Firebase.database.reference
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.Createbutton.setOnClickListener {

            // get text from edittext
            username = binding.nameofowner.text.toString().trim()
            address = binding.nameofrestaurent.text.toString().trim()
            phone = binding.phone.text.toString().trim()
            email = binding.editTextTextEmailAddress.text.toString().trim()
            password = binding.editTextTextPassword.text.toString().trim()

            if (username.isBlank()||address.isBlank()||phone.isBlank()||email.isBlank()||password.isBlank()){
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
            }else{
                createAccount(email,password)
            }
        }


    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                saveuserdata()
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
            else{
                Toast.makeText(this, "Account Creation Failed", Toast.LENGTH_SHORT).show()
                Log.d("Account", "createAccount: faliure",task.exception)
            }
        }

    }
    // save data into database
    private fun saveuserdata() {
        username = binding.nameofowner.text.toString().trim()
        address = binding.nameofrestaurent.text.toString().trim()
        phone = binding.phone.text.toString().trim()
        email = binding.editTextTextEmailAddress.text.toString().trim()
        password = binding.editTextTextPassword.text.toString().trim()

        val user = UserModel(username,email,password,phone,address)

        val Userid = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("user").child(Userid).setValue(user)
    }
}