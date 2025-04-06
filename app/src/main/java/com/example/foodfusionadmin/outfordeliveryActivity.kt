package com.example.foodfusionadmin

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodfusionadmin.adapter.deliveryadapter
import com.example.foodfusionadmin.databinding.ActivityOutfordeliveryBinding
import com.example.foodfusionadmin.model.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class outfordeliveryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOutfordeliveryBinding
    private lateinit var database: FirebaseDatabase
    private  var listOfCompleteOrder:ArrayList<OrderDetails> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOutfordeliveryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        binding.backbutton.setOnClickListener {
            finish()
        }
        //retrive and display completed Order
        retrivecompletedOrderDetails()
    }

    private fun retrivecompletedOrderDetails() {
        //initilise firebase database
        database = FirebaseDatabase.getInstance()
        val completeOrderRef = database.reference.child("CompletedOrder")
            .orderByChild("currentTime")
        completeOrderRef.addValueEventListener(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear the list before populating new data
                listOfCompleteOrder.clear()
                for (ordersnapshot in snapshot.children){
                    val completeOrder = ordersnapshot.getValue(OrderDetails::class.java)
                    completeOrder?.let {
                        listOfCompleteOrder.add(it)
                    }
                }
                //reverse the list to display order first
                listOfCompleteOrder.reverse()

                setDataintorecyclerView()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setDataintorecyclerView() {

        val adapter = deliveryadapter(listOfCompleteOrder,this)
        binding.outfordeliveryrecyclerview.layoutManager = LinearLayoutManager(this)
        binding.outfordeliveryrecyclerview.adapter = adapter

    }
}