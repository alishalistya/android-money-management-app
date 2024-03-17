package com.example.tubespbd.database

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.tubespbd.R
import com.example.tubespbd.database.Transaction
import android.content.Intent
import android.net.Uri

class TransactionAdapter(private val transactions: List<Transaction>, private val itemClickListener: (Transaction) -> Unit) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val categoryTextView: TextView = itemView.findViewById(R.id.categoryTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)

        init {
            locationTextView.setOnClickListener {
                val location = locationTextView.text.toString()
                val gmmIntentUri = Uri.parse("geo:0,0?q=$location")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(itemView.context.packageManager) != null) {
                    itemView.context.startActivity(mapIntent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val currentItem = transactions[position]
        holder.titleTextView.text = currentItem.title
        holder.categoryTextView.text = currentItem.category
        holder.amountTextView.text = currentItem.amount.toString()
        holder.dateTextView.text = currentItem.tanggal
        holder.locationTextView.text = currentItem.location
        holder.itemView.setOnClickListener {
            itemClickListener(currentItem)
        }
    }

    override fun getItemCount() = transactions.size
}