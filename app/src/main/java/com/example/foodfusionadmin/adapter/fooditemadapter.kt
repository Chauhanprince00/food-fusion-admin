package com.example.foodfusionadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodfusionadmin.databinding.ItempriceBinding

class fooditemadapter(private val foodNames:List<String>,private val foodQuentity:List<Int>,private val foodPrice:List<String>):RecyclerView.Adapter<fooditemadapter.fooditemViewHolder>() {
   inner class fooditemViewHolder(private val binding: ItempriceBinding):RecyclerView.ViewHolder(binding.root) {
       fun bind(position: Int) {
        binding.itemname.text = foodNames[position]
           binding.itemQuentity.text = foodQuentity[position].toString()
           binding.itemprice.text = "₹${foodPrice[position]}"
           val quentity = foodQuentity[position].toInt()
               val price = foodPrice[position].toInt()
           val totalprice = quentity * price
           binding.totalprice.text = "₹$totalprice"
       }

   }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): fooditemViewHolder {
        val binding = ItempriceBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return fooditemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return foodNames.size
    }

    override fun onBindViewHolder(holder: fooditemViewHolder, position: Int) {
        holder.bind(position)
    }
}