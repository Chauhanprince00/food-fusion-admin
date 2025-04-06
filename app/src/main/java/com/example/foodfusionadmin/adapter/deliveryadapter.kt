package com.example.foodfusionadmin.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodfusionadmin.databinding.DeliveryitemBinding
import com.example.foodfusionadmin.model.OrderDetails
import com.example.foodfusionadmin.viewmore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class deliveryadapter(private val listOfCompleteOrder:ArrayList<OrderDetails>,private val context: Context):RecyclerView.Adapter<deliveryadapter.deliveryViewHolder>() {

    inner class deliveryViewHolder(private val binding: DeliveryitemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.apply {
                customername.text = listOfCompleteOrder[position].userName
                phoneno.text = listOfCompleteOrder[position].phoneNumber
                customeraddress.text = listOfCompleteOrder[position].address
                //timestamp
                val timestamp = listOfCompleteOrder[position].currentTime
                val dateformate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val timeformate = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val date = Date(timestamp)
                val formatedDate = dateformate.format(date)
                val formatedTime = timeformate.format(date)
                orderdate.text = formatedDate
                currenttime.text = formatedTime
                val deliveredStatus = listOfCompleteOrder[position].Delivered
                if (deliveredStatus == true){
                    binding.orderStatus.text = "Delivered ✅"
                    binding.orderstatusnotice.text = "\uD83D\uDCE6 The order has been successfully delivered to the customer."
                }else{
                    binding.orderStatus.text = "On the way \uD83D\uDE9A"
                    binding.orderstatusnotice.text = "\uD83D\uDCE2 Order dispatched! Waiting for the customer to receive and confirm. ⏳"
                }
                ViewMoreActivity.setOnClickListener {
                    val foodNameList = listOfCompleteOrder[position].foodNames
                    val foodquentity = listOfCompleteOrder[position].foodQuentities
                    val foodprice = listOfCompleteOrder[position].foodPrices
                    val intent = Intent(context,viewmore::class.java)
                    intent.putExtra("name",listOfCompleteOrder[position].userName)
                    intent.putExtra("totalprice",listOfCompleteOrder[position].totalPrice)
                    intent.putExtra("datetime",listOfCompleteOrder[position].currentTime)
                    intent.putExtra("phone",listOfCompleteOrder[position].phoneNumber)
                    intent.putExtra("address",listOfCompleteOrder[position].address)
                    intent.putStringArrayListExtra("foodname",ArrayList(foodNameList))
                    intent.putIntegerArrayListExtra("foodquentity",ArrayList(foodquentity))
                    intent.putStringArrayListExtra("foofoodprice",ArrayList(foodprice))
                    context.startActivity(intent)
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): deliveryViewHolder {
        val binding = DeliveryitemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return deliveryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listOfCompleteOrder.size
    }

    override fun onBindViewHolder(holder: deliveryViewHolder, position: Int) {
        holder.bind(position)

    }
}