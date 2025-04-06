package com.example.foodfusionadmin.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodfusionadmin.databinding.OrderdetailsitemBinding

class OrderDetailsAdapter(
    private var context: Context,
    private var foodnames: ArrayList<String>,
    private var foodImage: ArrayList<String>,
    private var foodQuantity: ArrayList<Int>,
    private var foodPrice: ArrayList<String>
):RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder>() {
    inner class OrderDetailsViewHolder(private val binding: OrderdetailsitemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                foodname.text = foodnames[position]
                quantity.text = foodQuantity[position].toString()
                val uriString = foodImage[position]
                val uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(binding.pendingorderfoodimage)
                price.text = "₹ ${ foodPrice[position] }"
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailsViewHolder {
        val binding = OrderdetailsitemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OrderDetailsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return foodnames.size
    }

    override fun onBindViewHolder(holder: OrderDetailsViewHolder, position: Int) {
       holder.bind(position)
    }
}