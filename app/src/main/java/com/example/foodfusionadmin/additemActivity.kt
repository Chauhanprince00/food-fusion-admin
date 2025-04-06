package com.example.foodfusionadmin

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodfusionadmin.databinding.ActivityAdditemBinding
import com.example.foodfusionadmin.model.AllMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class additemActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAdditemBinding
    //fooditem details
    private lateinit var foodName:String
    private lateinit var foodPrice:String
    private lateinit var foodDescription:String
    private lateinit var foodImageURL:String
    private lateinit var foodIngredient:String

    //firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdditemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //initilization Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.backbutton.setOnClickListener {
            finish()
        }
        binding.additembutton.setOnClickListener {
            //get data from field
            foodName = binding.enterfoodname.text.toString().trim()
            foodPrice = binding.enterfoodprice.text.toString().trim()
            foodDescription = binding.description.text.toString().trim()
            foodIngredient = binding.ingredint.text.toString().trim()
            foodImageURL = binding.enterImageURL.text.toString().trim()

            if (!(foodName.isBlank() || foodPrice.isBlank()||foodDescription.isBlank()||foodIngredient.isBlank()||foodImageURL.isBlank())){
               uploadData()
                finish()
            }else{
                Toast.makeText(this, "Fill all the details", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun uploadData() {
        // get  areference to the "menu" node in the database
        val MenuRef = database.getReference("menu")
        // generate unique key for the menu item
        val newItemKey = MenuRef.push().key


        val allitemlist = AllMenu(
            newItemKey,
            foodfoodname = foodName,
            foodprice = foodPrice,
            foodimage = foodImageURL,
            fooddescription = foodDescription,
            foodingredient = foodIngredient
        )
        newItemKey?.let { key->
            MenuRef.child(key).setValue(allitemlist).addOnSuccessListener {
                Toast.makeText(this, "data save successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "failed to save data", Toast.LENGTH_SHORT).show()
            }.addOnCanceledListener {
                Toast.makeText(this, "cancel", Toast.LENGTH_SHORT).show()
            }
        }
    }

}