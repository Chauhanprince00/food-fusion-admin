package com.example.foodfusionadmin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.transition.Visibility
import com.example.foodfusionadmin.databinding.ActivityAdminProfileBinding
import com.example.foodfusionadmin.model.UserModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminProfile : AppCompatActivity() {
    private lateinit var binding: ActivityAdminProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var oldPassword:String
    private lateinit var oldEmail:String
    private lateinit var database: FirebaseDatabase
    private lateinit var AdminRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        AdminRef = database.reference.child("user")
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backbutton.setOnClickListener {
            finish()
        }
        binding.adminname.isEnabled = false
        binding.adminemail.isEnabled = false
        binding.adminaddress.isEnabled = false
        binding.adminphone.isEnabled = false
        binding.adminpassword.isEnabled = false
        binding.saveinformation.visibility = View.GONE


        binding.Editorifile.setOnClickListener {
            binding.adminname.isEnabled = true
            binding.adminaddress.isEnabled = true
            binding.adminphone.isEnabled = true
            binding.adminpassword.isEnabled = true
            binding.saveinformation.visibility = View.VISIBLE

            oldPassword = binding.adminpassword.text.toString().trim()
            oldEmail = binding.adminemail.text.toString().trim()

            binding.adminname.requestFocus()

        }
        binding.saveinformation.setOnClickListener {
            updateUserData()
        }
        retriveUserData()
    }

    private fun updateUserData() {
        val updateName = binding.adminname.text.toString()
        val address = binding.adminaddress.text.toString()
        val phone = binding.adminphone.text.toString()
        val password = binding.adminpassword.text.toString()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val credential = EmailAuthProvider.getCredential(currentUser.email!!, oldPassword)

            // 🔹 Step 1: Reauthenticate User
            currentUser.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    // 🔹 Step 2: Update Password
                    currentUser.updatePassword(password).addOnCompleteListener { passUpdateTask ->
                        if (passUpdateTask.isSuccessful) {
                            // 🔹 Step 3: Save Updated Data to Database
                            val userRef = AdminRef.child(currentUser.uid)
                            userRef.child("name").setValue(updateName)
                            userRef.child("phone").setValue(phone)
                            userRef.child("password").setValue(password)
                            userRef.child("address").setValue(address)
                            startActivity(Intent(this,MainActivity::class.java))

                            Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to update password: ${passUpdateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Reauthentication failed. Check old password.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun retriveUserData() {
        val currentUser = auth.currentUser?.uid
        if (currentUser != null) {
            val userRef = AdminRef.child(currentUser)

            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var ownerName = snapshot.child("name").getValue()
                        var ownerEmail = snapshot.child("email").getValue()
                        var ownerPassword = snapshot.child("password").getValue()
                        var ownerAddress = snapshot.child("address").getValue()
                        var ownerPhone = snapshot.child("phone").getValue()
                        setDataToTextView(
                            ownerName,
                            ownerEmail,
                            ownerPassword,
                            ownerAddress,
                            ownerPhone
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

    }

    private fun setDataToTextView(
        ownerName: Any?,
        ownerEmail: Any?,
        ownerPassword: Any?,
        ownerAddress: Any?,
        ownerPhone: Any?
    ) {
        binding.adminname.setText(ownerName.toString())
        binding.adminaddress.setText(ownerAddress.toString())
        binding.adminemail.setText(ownerEmail.toString())
        binding.adminphone.setText(ownerPhone.toString())
        binding.adminpassword.setText(ownerPassword.toString())
    }
}