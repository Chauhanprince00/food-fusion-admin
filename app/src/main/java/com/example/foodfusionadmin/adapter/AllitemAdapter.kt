package com.example.foodfusionadmin.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodfusionadmin.databinding.ItemItemBinding
import com.example.foodfusionadmin.model.AllMenu
import com.google.firebase.database.DatabaseReference

class AllitemAdapter(
    private val context: Context,
    private val menulist: ArrayList<AllMenu>,
    databaseReference: DatabaseReference,
    private val onDeleteClickListner: (position: Int) -> Unit
) : RecyclerView.Adapter<AllitemAdapter.AddItemViewHolder>() {
    private val itemQuentity = IntArray(menulist.size) { 1 }

    inner class AddItemViewHolder(private val binding: ItemItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {

                val quentity = itemQuentity[position]
                val menuitem = menulist[position]
                val uriString = menuitem.foodimage


                foodnametextview.text = menuitem.foodfoodname
                foodprice.text = "₹ ${menuitem.foodprice}"
                Glide.with(context).load(uriString).into(foodimageview)
                Log.d("TAG", "bind: $uriString")


                deletebutton.setOnClickListener {
                    onDeleteClickListner(position)
                }

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddItemViewHolder {
        val binding = ItemItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return menulist.size
    }

    override fun onBindViewHolder(holder: AddItemViewHolder, position: Int) {
        holder.bind(position)
    }
}