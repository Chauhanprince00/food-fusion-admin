package com.example.foodfusionadmin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodfusionadmin.databinding.ActivityMainBinding
import com.example.foodfusionadmin.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var completedOrderRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.addmenu.setOnClickListener {
            startActivity(Intent(this, additemActivity::class.java))
        }

        binding.allitems.setOnClickListener {
            startActivity(Intent(this, allItemActivity::class.java))
        }
        binding.adminorderdispatch.setOnClickListener {
            startActivity(Intent(this, outfordeliveryActivity::class.java))
        }
        binding.adminprofile.setOnClickListener {
            startActivity(Intent(this, AdminProfile::class.java))
        }
        binding.createadminnewuser.setOnClickListener {
            startActivity(Intent(this, signUp_Activity::class.java))
        }
        binding.pendingorder.setOnClickListener {
            startActivity(Intent(this, pendingorder::class.java))
        }
        binding.adminlogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, Login_Activity::class.java))
            finish()
        }
        pendingOrders()
        completedOrders()
        wholetimeEarning()
    }

    private fun wholetimeEarning() {
        var listOfTotalPay = mutableListOf<Int>()
        completedOrderRef = FirebaseDatabase.getInstance().reference.child("CompletedOrder")
        completedOrderRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listOfTotalPay.clear()
                for (orderShapshot in snapshot.children) {
                    var completeOrder = orderShapshot.getValue(OrderDetails::class.java)
                    completeOrder?.totalPrice?.toIntOrNull()
                        ?.let { i ->
                            listOfTotalPay.add(i)

                        }
                }
                val earning =listOfTotalPay.sum()
                binding.wholetimeearning.text = "₹"+earning.toString()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun completedOrders() {
        val completedOrderRef = database.reference.child("CompletedOrder")
        var completedOrderItemCount = 0
        completedOrderRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                completedOrderItemCount = snapshot.childrenCount.toInt()
                binding.completedOrderCount.text = completedOrderItemCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun pendingOrders() {
        database = FirebaseDatabase.getInstance()
        val pendingOrderRef = database.reference.child("OrderDetails")
        var pendingOrderItemCount = 0
        pendingOrderRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pendingOrderItemCount = snapshot.childrenCount.toInt()
                binding.pendingOrders.text = pendingOrderItemCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}