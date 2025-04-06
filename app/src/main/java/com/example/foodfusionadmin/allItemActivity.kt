package com.example.foodfusionadmin

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodfusionadmin.adapter.AllitemAdapter
import com.example.foodfusionadmin.databinding.ActivityAllItemBinding
import com.example.foodfusionadmin.model.AllMenu
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class allItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAllItemBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private  var menuitems:ArrayList<AllMenu> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAllItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseReference = FirebaseDatabase.getInstance().reference
        retrivemenuitem()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backbutton.setOnClickListener {
            finish()
        }

    }

    private fun retrivemenuitem() {
database = FirebaseDatabase.getInstance()
        val foodreference:DatabaseReference = database.reference.child("menu")

        //fetch data from database
        foodreference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //Clear existing data before populating
                menuitems.clear()
                //loop for each food item
                for (foodSnapshot in snapshot.children){
                   val  menuitem = foodSnapshot.getValue(AllMenu::class.java)
                    menuitem?.let {
                        menuitems.add(it)
                    }
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError", "onCancelled: $error")
            }
        })
    }

    private fun setAdapter() {
        val adapter = AllitemAdapter(this,menuitems,databaseReference){position ->
            deleteMenuItem(position)
        }
        binding.menurecyclerview.layoutManager = LinearLayoutManager(this)
        binding.menurecyclerview.adapter = adapter
    }

    private fun deleteMenuItem(position: Int) {
        val menuItemToDelete = menuitems[position]
        val menuItemKey = menuItemToDelete.key
        val foodMenuRef = database.reference.child("menu").child(menuItemKey!!)
        foodMenuRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful){
                menuitems.removeAt(position)
                binding.menurecyclerview.adapter?.notifyItemRemoved(position)
            }else{
                Toast.makeText(this, "Failed to delete🔴", Toast.LENGTH_SHORT).show()
            }
        }
    }
}