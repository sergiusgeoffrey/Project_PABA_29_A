package com.example.chattale_chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChattingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChattingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chatting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chatroomDisplayName = view.findViewById<TextView>(R.id.chatroomname)
        val chatroomBackButton = view.findViewById<ImageButton>(R.id.back_button)
        val chatroomSendButton = view.findViewById<ImageButton>(R.id.send_button)
        val chatroomMessageField = view.findViewById<EditText>(R.id.message_input)

        if (MainActivity.CurrentChatroom.displayName == "") {
            chatroomDisplayName.text =
                MainActivity.CurrentChatroom.members!!.joinToString(", ", limit = 3)
        } else {
            chatroomDisplayName.text = MainActivity.CurrentChatroom.displayName!!.take(20)
        }

        ChattingFragment.currentView = view
        ChattingFragment.intervaler = Timer()
        ChattingFragment.intervaler.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                refreshMessages()
            }
        }, 0, MainActivity.GlobalRefreshMS)

        chatroomBackButton.setOnClickListener {
            ChattingFragment.intervaler.cancel()
            ChattingFragment.intervaler.purge()
            findNavController().popBackStack()
        }

        chatroomSendButton.setOnClickListener {
            MainActivity.DB.collection("Chatrooms")
                .document(MainActivity.CurrentChatroom!!.chatroomID!!).get()
                .addOnSuccessListener { document ->
                    val newLastMessageHashMap: HashMap<String, Any> =
                        document.get("lastMessage") as HashMap<String, Any>
                    val newLastMessage = Message(
                        newLastMessageHashMap["messageID"] as String?,
                        newLastMessageHashMap["chatroomID"] as String?,
                        newLastMessageHashMap["fromUsername"] as String?,
                        newLastMessageHashMap["timestamp"] as Long?,
                        newLastMessageHashMap["message"] as String?,
                        newLastMessageHashMap["system"] as Boolean?
                    )

                    val newChatroom = Chatroom(
                        document.get("chatroomID") as String?,
                        document.get("displayName") as String?,
                        document.get("members") as List<String>?,
                        newLastMessage
                    )

                    MainActivity.CurrentChatroom = newChatroom
                    // creates message

                    // generate id
                    val uuid = UUID.randomUUID()
                    val randomUUIDString = uuid.toString()

                    // get current time
                    val unixTime = System.currentTimeMillis()

                    // get message string
                    val messageString = chatroomMessageField.text.toString()
                    chatroomMessageField.setText("")

                    val newMessage = Message(
                        randomUUIDString,
                        MainActivity.CurrentChatroom.chatroomID,
                        MainActivity.CurrentAccount.username,
                        unixTime,
                        messageString,
                        false
                    )

                    MainActivity.CurrentChatroom.lastMessage = newMessage

                    MainActivity.DB.collection("Chatrooms")

                        .document(MainActivity.CurrentChatroom!!.chatroomID!!)
                        .set(MainActivity.CurrentChatroom)

                    MainActivity.DB.collection("Messages").document(newMessage.messageID!!)
                        .set(newMessage)
                }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        ChattingFragment.intervaler.cancel()
        ChattingFragment.intervaler.purge()
    }

    override fun onPause() {
        super.onPause()
        ChattingFragment.intervaler.cancel()
        ChattingFragment.intervaler.purge()
    }

    override fun onResume() {
        super.onResume()
        ChattingFragment.intervaler = Timer()
        ChattingFragment.intervaler.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                refreshMessages()
            }
        }, 0, MainActivity.GlobalRefreshMS)
    }

    fun refreshMessages() {
        MainActivity.DB.collection("Chatrooms")
            .document(MainActivity.CurrentChatroom!!.chatroomID!!).get()
            .addOnSuccessListener { document ->
                val newLastMessageHashMap: HashMap<String, Any> =
                    document.get("lastMessage") as HashMap<String, Any>
                val newLastMessage = Message(
                    newLastMessageHashMap["messageID"] as String?,
                    newLastMessageHashMap["chatroomID"] as String?,
                    newLastMessageHashMap["fromUsername"] as String?,
                    newLastMessageHashMap["timestamp"] as Long?,
                    newLastMessageHashMap["message"] as String?,
                    newLastMessageHashMap["system"] as Boolean?
                )

                val newChatroom = Chatroom(
                    document.get("chatroomID") as String?,
                    document.get("displayName") as String?,
                    document.get("members") as List<String>?,
                    newLastMessage
                )

                MainActivity.CurrentChatroom = newChatroom

                val messages = mutableListOf<Message>()
                MainActivity.DB.collection("Messages")
                    .whereEqualTo("chatroomID", MainActivity.CurrentChatroom.chatroomID)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val newMessage = Message(
                                document.get("messageID") as String?,
                                document.get("chatroomID") as String?,
                                document.get("fromUsername") as String?,
                                document.get("timestamp") as Long?,
                                document.get("message") as String?,
                                document.get("system") as Boolean?
                            )
                            messages.add(newMessage)
                        }

                        messages.sortBy { it.timestamp }

                        val messageRecyclerView =
                            ChattingFragment.currentView.findViewById<RecyclerView>(R.id.message_container)
                        messageRecyclerView.layoutManager =
                            LinearLayoutManager(parentFragment?.requireContext())
                        messageRecyclerView.adapter = MessageAdapter(messages)
                    }
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChattingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        var intervaler: Timer = Timer()
        lateinit var currentView: View
        fun newInstance(param1: String, param2: String) =
            ChattingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}