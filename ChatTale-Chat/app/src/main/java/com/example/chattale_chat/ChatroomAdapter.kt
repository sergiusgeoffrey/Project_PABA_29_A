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

class ChatroomAdapter(private val chatroomList: List<Chatroom>, private val navController: NavController) :
    RecyclerView.Adapter<ChatroomAdapter.ListViewHolder>() {
    inner class ListViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val leaveButton = itemview.findViewById<ImageButton>(R.id.chatroom_comp_leave)
        val chatroomDisplayName = itemview.findViewById<TextView>(R.id.chatroom_comp_displayname)
        val chatroomLastMessage = itemview.findViewById<TextView>(R.id.chatroom_comp_lastmessage)
        val chatroomTimestamp = itemview.findViewById<TextView>(R.id.chatroom_comp_timestamp)
        val chatroomDisplayPic = itemview.findViewById<ImageView>(R.id.chatroom_comp_displaypic)
        val chatroomBody = itemview.findViewById<LinearLayout>(R.id.chatroom_comp_body)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.component_chatroom, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val currentChatroom = chatroomList[position]
        if (currentChatroom.members!!.size > 2) {
            holder.chatroomDisplayPic.setImageResource(R.drawable.ic_baseline_groups_24)
        } else {
            holder.chatroomDisplayPic.setImageResource(R.drawable.ic_baseline_person_24)
        }
        if (currentChatroom.displayName == "") {

            MainActivity.fetchDisplayName(currentChatroom.members!! , object : MainActivity.fetchDisplayNameCallback {
                override fun onFetchDone(displayName: String) {
                   // none
                }

                override fun onFetchDone(displayNames: MutableList<String>) {
                    holder.chatroomDisplayName.text =
                        displayNames.joinToString(", ")
                }
            })


        } else {
            holder.chatroomDisplayName.text = currentChatroom.displayName!!
        }
        if (currentChatroom.lastMessage!!.messageID == null || currentChatroom.lastMessage!!.messageID == "null") {
            holder.chatroomLastMessage.visibility = View.GONE
            holder.chatroomTimestamp.visibility = View.GONE
        } else {
            val calins = Calendar.getInstance()
            calins.timeInMillis = currentChatroom.lastMessage!!.timestamp!!

            val myFormat = "dd/MM/yyyy hh:mm"
            val sdf = SimpleDateFormat(myFormat)

            if (currentChatroom.lastMessage!!.isSystem == true) {
                holder.chatroomLastMessage.text = currentChatroom.lastMessage!!.message!!
            } else {
                MainActivity.fetchDisplayName(currentChatroom.lastMessage!!.fromUsername!!, object : MainActivity.fetchDisplayNameCallback {
                    override fun onFetchDone(displayName: String) {
                        holder.chatroomLastMessage.text =
                            displayName+ ": " + currentChatroom.lastMessage!!.message!!
                    }

                    override fun onFetchDone(displayNames: MutableList<String>) {
                        // none
                    }
                })
            }

            holder.chatroomTimestamp.text = sdf.format(calins.time)
        }
        holder.chatroomBody.setOnClickListener {

            MainActivity.DB.collection("Chatrooms").document(currentChatroom!!.chatroomID!!).get()
                .addOnSuccessListener { document ->
                    val newLastMessageHashMap: HashMap<String, Any> =
                        document.get("lastMessage") as HashMap<String, Any>
                    val newLastMessage = Message(
                        newLastMessageHashMap["messageID"] as String?,
                        newLastMessageHashMap["chatroomID"] as String?,
                        newLastMessageHashMap["fromUsername"] as String?,
                        newLastMessageHashMap["timestamp"] as Long?,
                        newLastMessageHashMap["message"] as String?,
                        newLastMessageHashMap["isSystem"] as Boolean?
                    )

                    val newChatroom = Chatroom(
                        document.get("chatroomID") as String?,
                        document.get("displayName") as String?,
                        document.get("members") as List<String>?,
                        newLastMessage
                    )

                    MainActivity.CurrentChatroom = newChatroom
                    navController.navigate(R.id.action_chatListFragment_to_chattingFragment)
                }
        }

        holder.chatroomDisplayName.setOnClickListener {

            MainActivity.DB.collection("Chatrooms").document(currentChatroom!!.chatroomID!!).get()
                .addOnSuccessListener { document ->
                    val newLastMessageHashMap: HashMap<String, Any> =
                        document.get("lastMessage") as HashMap<String, Any>
                    val newLastMessage = Message(
                        newLastMessageHashMap["messageID"] as String?,
                        newLastMessageHashMap["chatroomID"] as String?,
                        newLastMessageHashMap["fromUsername"] as String?,
                        newLastMessageHashMap["timestamp"] as Long?,
                        newLastMessageHashMap["message"] as String?,
                        newLastMessageHashMap["isSystem"] as Boolean?
                    )

                    val newChatroom = Chatroom(
                        document.get("chatroomID") as String?,
                        document.get("displayName") as String?,
                        document.get("members") as List<String>?,
                        newLastMessage
                    )

                    MainActivity.CurrentChatroom = newChatroom
                    navController.navigate(R.id.action_chatListFragment_to_chattingFragment)
                }
        }

        holder.leaveButton.setOnClickListener {

            MainActivity.DB.collection("Chatrooms").document(currentChatroom!!.chatroomID.toString()).get()
                .addOnSuccessListener { document ->
                    if (document!= null){
                        val newLastMessageHashMap: HashMap<String, Any> =
                            document.get("lastMessage") as HashMap<String, Any>
                        val newLastMessage = Message(
                            newLastMessageHashMap["messageID"] as String?,
                            newLastMessageHashMap["chatroomID"] as String?,
                            newLastMessageHashMap["fromUsername"] as String?,
                            newLastMessageHashMap["timestamp"] as Long?,
                            newLastMessageHashMap["message"] as String?,
                            newLastMessageHashMap["isSystem"] as Boolean?
                        )

                        val newChatroom = Chatroom(
                            document.get("chatroomID") as String?,
                            document.get("displayName") as String?,
                            document.get("members") as List<String>?,
                            newLastMessage
                        )
                        val newmember = newChatroom.members!!.toMutableList()
                        newmember.remove(MainActivity.CurrentAccount.username)
                        newChatroom.members = newmember.toList()



                        val uuid = UUID.randomUUID()
                        val randomUUIDString = uuid.toString()

                        val unixTime = System.currentTimeMillis()

                        MainActivity.fetchDisplayName(MainActivity.CurrentAccount.username!!, object : MainActivity.fetchDisplayNameCallback {
                            override fun onFetchDone(displayName: String) {
                                val exitMessage = Message(
                                    randomUUIDString,
                                    currentChatroom!!.chatroomID!!,
                                    MainActivity.CurrentAccount.username,
                                    unixTime,
                                    displayName + " has left the chat",
                                    true
                                )

                                newChatroom.lastMessage = exitMessage

                                if (newmember.size > 0) {
                                    MainActivity.DB.collection("Chatrooms")
                                        .document(currentChatroom!!.chatroomID!!).set(newChatroom)
                                } else {
                                    MainActivity.DB.collection("Chatrooms")
                                        .document(currentChatroom!!.chatroomID!!).delete()
                                }

                                MainActivity.DB.collection("Messages").document(exitMessage!!.messageID!!).set(exitMessage)
                            }

                            override fun onFetchDone(displayNames: MutableList<String>) {
                                // none
                            }
                        })
                    }

                }

        }

    }

    override fun getItemCount(): Int {
        return chatroomList.size
    }

}