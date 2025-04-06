package com.example.foodfusionadmin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodfusionadmin.adapter.OrderDetailsAdapter
import com.example.foodfusionadmin.databinding.ActivityOrderDetailsBinding
import com.example.foodfusionadmin.model.OrderDetails

class OrderDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderDetailsBinding
    private var UserName:String? = null
    private var Address:String? = null
    private var PhoneNumber:String? = null
    private var totalPrice:String? = null
    private  var foodnames:ArrayList<String> = arrayListOf()
    private  var foodimage:ArrayList<String> = arrayListOf()
    private  var foodQuantity:ArrayList<Int> = arrayListOf()
    private  var foodPrices:ArrayList<String> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.backbutton.setOnClickListener {
            finish()
        }
        getdataFromIntent()

    }

    private fun getdataFromIntent() {
        val recivedOrderDetails = intent.getSerializableExtra("UserOrderdetails") as OrderDetails
        recivedOrderDetails?.let { OrderDetails ->
                UserName = recivedOrderDetails.userName
                foodnames = recivedOrderDetails.foodNames as ArrayList<String>
                foodimage = recivedOrderDetails.foodImages as ArrayList<String>
                foodQuantity = recivedOrderDetails.foodQuentities as ArrayList<Int>

                Address = recivedOrderDetails.address
                PhoneNumber = recivedOrderDetails.phoneNumber
                foodPrices = recivedOrderDetails.foodPrices as ArrayList<String>
                totalPrice = recivedOrderDetails.totalPrice

                setUserDetails()
                setAdapter()
        }

    }

    private fun setAdapter() {
        binding.orderdetailsrecyclerview.layoutManager = LinearLayoutManager(this)
        val adapter = OrderDetailsAdapter(this,foodnames,foodimage,foodQuantity,foodPrices)
        binding.orderdetailsrecyclerview.adapter = adapter
    }

    private fun setUserDetails() {
        binding.name.text = UserName
        binding.address.text = Address
        binding.totalpay.text = "₹ $totalPrice"
        binding.phone.text = PhoneNumber
    }
}