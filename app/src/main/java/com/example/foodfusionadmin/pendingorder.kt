package com.example.foodfusionadmin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodfusionadmin.adapter.pendingorderadapter
import com.example.foodfusionadmin.databinding.ActivityPendingorderBinding
import com.example.foodfusionadmin.model.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class pendingorder : AppCompatActivity(), pendingorderadapter.OnItemClicked {
    private lateinit var binding: ActivityPendingorderBinding
    private val listofname: MutableList<String> = mutableListOf()
    private val listofprice: MutableList<String> = mutableListOf()
    private val listofimagefirstfood: MutableList<String> = mutableListOf()
    private val listofOrderitem: ArrayList<OrderDetails> = arrayListOf()
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseorderdetails: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPendingorderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //inilization
        database = FirebaseDatabase.getInstance()
        databaseorderdetails = database.reference.child("OrderDetails")
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        getordersdetaile()


        binding.backbutton.setOnClickListener {
            finish()
        }

    }

    private fun getordersdetaile() {
        //retrive order details from firebase database
        databaseorderdetails.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ordershapshort in snapshot.children) {
                    val ordderdetails = ordershapshort.getValue(OrderDetails::class.java)
                    ordderdetails?.let {
                        listofOrderitem.add(it)
                    }
                }
                addDatatoListForRecyclerview()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun addDatatoListForRecyclerview() {
        for (orderitem in listofOrderitem) {
            //add data to respective list for populating the recyclerview
            orderitem.userName?.let { listofname.add(it) }
            orderitem.totalPrice?.let { listofprice.add(it) }
            orderitem.foodImages?.filterNot { it.isEmpty() }?.forEach {
                listofimagefirstfood.add(it)
            }
        }
        setAdapter()
    }

    private fun setAdapter() {
        binding.pndingorderrecuclerview.layoutManager = LinearLayoutManager(this)
        val adapter = pendingorderadapter(this, listofname, listofprice, listofimagefirstfood, this)
        binding.pndingorderrecuclerview.adapter = adapter
    }

    override fun onItemClickListner(position: Int) {
        val intent = Intent(this, OrderDetailsActivity::class.java)
        val userOrderdetails = listofOrderitem[position]
        intent.putExtra("UserOrderdetails", userOrderdetails)
        startActivity(intent)
    }

    override fun onItemAcceptClickListner(position: Int) {
        // handle item Accept and update database
        val childItemPushKey = listofOrderitem[position].itemPushKey
        val clickItemOrderReference = childItemPushKey?.let {
            database.reference.child("OrderDetails").child(it)
        }
        clickItemOrderReference?.child("orderAccepted")?.setValue(true)
        updateOrderAcceptStatus(position)
    }


    override fun onItemDispatchClickListner(position: Int) {
        //mark as dispatch true
        updateDispatchStatus(position)
        // handle item dispatch and update database
        val dispatchItemPushKey = listofOrderitem[position].itemPushKey
        val dispatchItemOrderReference = database.reference.child("CompletedOrder").child(dispatchItemPushKey!!)
        dispatchItemOrderReference.setValue(listofOrderitem[position])
            .addOnSuccessListener {
                val clickItemOrderReference = dispatchItemPushKey?.let {
                    database.reference.child("CompletedOrder").child(it)
                }
                clickItemOrderReference?.child("orderAccepted")?.setValue(true)
                clickItemOrderReference?.child("dispatched")?.setValue(true)?.addOnCompleteListener {
                    deleteThisItemFromOrderDetails(dispatchItemPushKey)
                }
            }

    }

    private fun deleteThisItemFromOrderDetails(dispatchItemPushKey: String) {
        val orderDetailsItemRef = database.reference.child("OrderDetails").child(dispatchItemPushKey)
        orderDetailsItemRef.removeValue().addOnSuccessListener {
            Toast.makeText(this, "Order Is Dispatched", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Order is not Dispatch", Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateDispatchStatus(position: Int) {
        // update order acceptance in user's BuyHistory and OrderDetails
        val userIDofClickedItem = listofOrderitem[position].userUid
        val pushkeyOfClickedItem = listofOrderitem[position].itemPushKey
        val name = listofOrderitem[position].userName.toString()
        val foodname = listofOrderitem[position].foodNames.toString()
        val BuyHistryRef =
            database.reference.child("user").child(userIDofClickedItem!!).child("BuyHistory")
                .child(pushkeyOfClickedItem!!)
        BuyHistryRef.child("dispatched").setValue(true)
        databaseorderdetails.child(pushkeyOfClickedItem).child("dispatched").setValue(true)
        addDispatchnotification(userIDofClickedItem,name,foodname)
    }

    private fun addDispatchnotification(userIDofClickedItem: String, name: String, foodname: String) {
        val message = "Dear $name,\n" +
                "\uD83D\uDE80 Your order for $foodname has been dispatched! \uD83D\uDCE6✨ Our delivery partner is on the way to bring your delicious meal. \uD83C\uDF54\uD83E\uDD64\n" +
                "\n" +
                "\uD83D\uDCCD Track your order for live updates & get ready to enjoy! \uD83D\uDE0B\uD83C\uDF89"
        val database = FirebaseDatabase.getInstance().reference
        val databaseRef = database.child("user").child(userIDofClickedItem!!).child("notification")
        //date
        val currentTime = System.currentTimeMillis()
        val dateFormate = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
        val formatedDate = dateFormate.format(Date(currentTime))
        //formate time
        val timeFormate = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formatedTime = timeFormate.format(Date(currentTime))

        //unique key for notification
        val notificationUniquekey = databaseRef.push().key?: return

        //notification Object
        val notification = mapOf(
            "Heading" to "Order Dispatched :\uD83D\uDEB4\u200D♂\uFE0F ",
            "message" to message,
            "timestamp" to System.currentTimeMillis(),
            "isRead" to false,
            "time" to formatedTime,
            "date" to formatedDate
        )
        //save notification to firebase
        databaseRef.child(notificationUniquekey).setValue(notification)
    }


    private fun updateOrderAcceptStatus(position: Int) {
        // update order acceptance in user's BuyHistory and OrderDetails
        val userIDofClickedItem = listofOrderitem[position].userUid
        val pushkeyOfClickedItem = listofOrderitem[position].itemPushKey
        val name = listofOrderitem[position].userName.toString()
        val foodname = listofOrderitem[position].foodNames.toString()
        val BuyHistryRef =
            database.reference.child("user").child(userIDofClickedItem!!).child("BuyHistory")
                .child(pushkeyOfClickedItem!!)
        BuyHistryRef.child("orderAccepted").setValue(true)
        databaseorderdetails.child(pushkeyOfClickedItem).child("orderAccepted").setValue(true)
        addNotificationIntoUserNode(userIDofClickedItem,name,foodname)
    }

    private fun addNotificationIntoUserNode(
        userIDofClickedItem: String,
        name: String,
        foodname: String
    ) {
        val Message = "Dear $name,\n" +
                "\uD83D\uDC4B Your $foodname is now being freshly prepared! \uD83D\uDC68\u200D\uD83C\uDF73✨ Our chefs are crafting it with care to serve you the best.\n" +
                "\n" +
                "⏳ Hang tight! Your delicious meal will be ready soon. \uD83D\uDE80\uD83C\uDF55\uD83D\uDE0B"
        val database = FirebaseDatabase.getInstance().reference
        val databaseRef = database.child("user").child(userIDofClickedItem!!).child("notification")
        //date
        val currentTime = System.currentTimeMillis()
        val dateFormate = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
        val formatedDate = dateFormate.format(Date(currentTime))
        //formate time
        val timeFormate = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formatedTime = timeFormate.format(Date(currentTime))

        //unique key for notification
        val notificationUniquekey = databaseRef.push().key?: return

        //notification Object
        val notification = mapOf(
            "Heading" to "Order Accepted 😊",
            "message" to Message,
            "timestamp" to System.currentTimeMillis(),
            "isRead" to false,
            "time" to formatedTime,
            "date" to formatedDate
        )
        //save notification to firebase
        databaseRef.child(notificationUniquekey).setValue(notification)
    }
}