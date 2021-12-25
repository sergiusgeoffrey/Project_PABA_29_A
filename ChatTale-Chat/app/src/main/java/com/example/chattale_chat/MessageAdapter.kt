package com.example.chattale_chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(private val messageList: List<Message>) :
    RecyclerView.Adapter<MessageAdapter.ListViewHolder>() {
    inner class ListViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val receiveBody = itemview.findViewById<LinearLayout>(R.id.message_comp_body_receive)
        val receiveUsername = itemview.findViewById<TextView>(R.id.message_comp_displayname_receive)
        val receiveMessage = itemview.findViewById<TextView>(R.id.message_comp_message_receive)
        val receiveTimestamp = itemview.findViewById<TextView>(R.id.message_comp_timestamp_receive)

        val sendBody = itemview.findViewById<LinearLayout>(R.id.message_comp_body_send)
        val sendUsername = itemview.findViewById<TextView>(R.id.message_comp_displayname_send)
        val sendMessage = itemview.findViewById<TextView>(R.id.message_comp_message_send)
        val sendTimestamp = itemview.findViewById<TextView>(R.id.message_comp_timestamp_send)

        val systemBody = itemview.findViewById<LinearLayout>(R.id.message_comp_body_system)
        val systemMessage = itemview.findViewById<TextView>(R.id.message_comp_message_system)
        val systemTimestamp = itemview.findViewById<TextView>(R.id.message_comp_timestamp_system)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.component_message, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val currentMessage = messageList[position]
        val calins = Calendar.getInstance()
        calins.timeInMillis = currentMessage.timestamp!!
        val myFormat = "dd/MM/yyyy hh:mm"
        val sdf = SimpleDateFormat(myFormat)
        holder.receiveBody.visibility = View.GONE
        holder.sendBody.visibility = View.GONE
        holder.systemBody.visibility = View.GONE


        if(currentMessage.isSystem == true){
            holder.systemBody.visibility = View.VISIBLE
            holder.systemMessage.text = currentMessage.message
            holder.systemTimestamp.text = sdf.format(calins.time)
        }else{
            if (currentMessage.fromUsername != MainActivity.CurrentAccount.username){
                holder.receiveBody.visibility = View.VISIBLE
                holder.receiveMessage.text = currentMessage.message
                holder.receiveTimestamp.text = sdf.format(calins.time)

                MainActivity.fetchDisplayName(currentMessage.fromUsername!!, object : MainActivity.fetchDisplayNameCallback {
                    override fun onFetchDone(displayName: String) {
                        holder.receiveUsername.text = displayName
                    }

                    override fun onFetchDone(displayNames: MutableList<String>) {
                        // none
                    }
                })


            }else{
                holder.sendBody.visibility = View.VISIBLE
                holder.sendMessage.text = currentMessage.message
                holder.sendTimestamp.text = sdf.format(calins.time)

                MainActivity.fetchDisplayName(currentMessage.fromUsername!!, object : MainActivity.fetchDisplayNameCallback {
                    override fun onFetchDone(displayName: String) {
                        holder.sendUsername.text = displayName
                    }

                    override fun onFetchDone(displayNames: MutableList<String>) {
                        // none
                    }
                })
            }

        }

    }

    override fun getItemCount(): Int {
        return messageList.size
    }

}