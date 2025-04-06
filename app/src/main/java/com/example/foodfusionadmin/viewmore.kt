package com.example.foodfusionadmin

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodfusionadmin.adapter.fooditemadapter
import com.example.foodfusionadmin.databinding.ActivityViewmoreBinding

class viewmore : AppCompatActivity() {
    private lateinit var binding: ActivityViewmoreBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityViewmoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val name = intent.getStringExtra("name")
        val totalprice = intent.getStringExtra("totalprice")
        val datetime = intent.getLongExtra("datetime",0L)
        Log.d("TAG", "onCreate: $datetime")
        val phone = intent.getStringExtra("phone")
        val address = intent.getStringExtra("address")

        val foodNames = intent.getStringArrayListExtra("foodname") ?: arrayListOf()
        val foodQuantities = intent.getIntegerArrayListExtra("foodquentity") ?: arrayListOf()
        val foodPrices = intent.getStringArrayListExtra("foofoodprice") ?: arrayListOf()

        //set value
        binding.totalamount.text = "₹$totalprice"
        binding.ordernumber.text = datetime.toString()
        binding.paymentstatus.text = "Paid using razorpay:(₹$totalprice)"
        binding.phoneNo.text = phone
        binding.deliveryaddress.text = address

        binding.recyclerView2.layoutManager = LinearLayoutManager(this)
        val adapter = fooditemadapter(foodNames,foodQuantities,foodPrices)
        binding.recyclerView2.adapter = adapter
    }
}