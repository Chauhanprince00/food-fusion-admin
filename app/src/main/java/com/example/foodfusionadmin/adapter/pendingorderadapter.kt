package com.example.foodfusionadmin.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodfusionadmin.databinding.PendingordersitemBinding

class pendingorderadapter(private val context: Context, private val customerName:MutableList<String>, private val quentity: MutableList<String>, private val foodimage: MutableList<String>,private val itemClicked:OnItemClicked):RecyclerView.Adapter<pendingorderadapter.pendingorderViewHolder>() {
    interface OnItemClicked{
        fun onItemClickListner(position: Int)
        fun onItemAcceptClickListner(position: Int)
        fun onItemDispatchClickListner(position: Int)
    }
    inner class pendingorderViewHolder(private val binding: PendingordersitemBinding):RecyclerView.ViewHolder(binding.root) {
        private var isAccepted = false
        fun bind(position: Int) {
            binding.apply {
                pendingordercustomername.text = customerName[position]
                pendingorderquentity.text = "₹ ${quentity[position]}"
                val uriString = foodimage[position]
                val uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(pendingorderfoodimage)

                orderacceptbutton.apply {
                    if (!isAccepted)
                    setOnClickListener {
                        text = "Accept"
                    }else{
                        text = "Dispatch"
                    }
                    setOnClickListener {
                        if (!isAccepted){
                            text = "Dispatch"
                            isAccepted = true
                            showtoast("Order is accepted")
                            itemClicked.onItemAcceptClickListner(position)

                        }else{
                            customerName.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                            showtoast("Order is dispatched")
                            itemClicked.onItemDispatchClickListner(position)

                        }
                    }
                }
                itemView.setOnClickListener {
                    itemClicked.onItemClickListner(position)
                }
            }

        }
        private fun showtoast(message: String){
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): pendingorderViewHolder {
        val binding = PendingordersitemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return pendingorderViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return customerName.size
    }

    override fun onBindViewHolder(holder: pendingorderViewHolder, position: Int) {
        holder.bind(position)
    }
}