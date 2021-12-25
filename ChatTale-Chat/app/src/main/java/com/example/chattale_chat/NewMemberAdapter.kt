package com.example.chattale_chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NewMemberAdapter(private val memberSet: MutableSet<String>):RecyclerView.Adapter<NewMemberAdapter.ListViewHolder>(){
    inner class ListViewHolder(itemview : View) : RecyclerView.ViewHolder(itemview){
        val removeButton = itemview.findViewById<ImageButton>(R.id.new_member_comp_remove)
        val usernameText = itemview.findViewById<TextView>(R.id.new_member_comp_username)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.component_new_member, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val currentUsername = memberSet.toList()[position]
        if (currentUsername == MainActivity.CurrentAccount.username){
            holder.removeButton.visibility = View.GONE
        }else{
            holder.removeButton.setOnClickListener {
                memberSet.remove(currentUsername)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position,memberSet.size)
            }
        }
        MainActivity.fetchDisplayName(currentUsername, object : MainActivity.fetchDisplayNameCallback {
            override fun onFetchDone(displayName: String) {
                holder.usernameText.text = displayName
                println(displayName)
            }

            override fun onFetchDone(displayNames: MutableList<String>) {
                // none
            }
        })


    }

    override fun getItemCount(): Int {
        return memberSet.size
    }

}