package com.example.chattale_chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import kotlin.collections.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatListFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_chat_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val copyInviteCodeButton = view.findViewById<TextView>(R.id.copy_invite_code_button)

        copyInviteCodeButton.setOnClickListener {
            val clipboardManager = view.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", MainActivity.CurrentAccount.username)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(view.context, "Invite code copied to clipboard!", Toast.LENGTH_LONG).show()
        }

        currentView = view
        intervaler = Timer()
        intervaler.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                refreshChatrooms()
            }
        }, 0, MainActivity.GlobalRefreshMS)

        val logoutButton = view.findViewById<ImageView>(R.id.logout_image)
        logoutButton.setOnClickListener{
            MainActivity.auth.signOut()
            MainActivity.UsernameTable = mutableMapOf<String, String>()
            MainActivity.ChatroomList = mutableListOf()
            MainActivity.CurrentAccount = Account(null, null, null)
            MainActivity.CurrentChatroom = Chatroom(null, null, null, null)
            findNavController().navigate(R.id.action_chatListFragment_to_loginFragment)
        }

        val newChatButton = view.findViewById<FloatingActionButton>(R.id.new_chat_button)
        newChatButton.setOnClickListener {
            findNavController().navigate(R.id.action_chatListFragment_to_newChatFragment)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        intervaler.cancel()
        intervaler.purge()
    }

    override fun onPause() {
        super.onPause()
        intervaler.cancel()
        intervaler.purge()
    }

    override fun onResume() {
        super.onResume()
        intervaler = Timer()
        intervaler.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                refreshChatrooms()
            }
        }, 0, MainActivity.GlobalRefreshMS)
    }

    fun refreshChatrooms() {
        val chatrooms = mutableListOf<Chatroom>()
        if(MainActivity.CurrentAccount.username == null){
            intervaler.cancel()
            intervaler.purge()
            return
        }
        MainActivity.DB.collection("Chatrooms")
            .whereArrayContains("members", MainActivity.CurrentAccount.username!!).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                    val newLastMessageHashMap : HashMap<String, Any> = document.get("lastMessage") as HashMap<String, Any>
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
                    chatrooms.add(newChatroom)
                }

                chatrooms.sortByDescending { it.lastMessage?.timestamp }
                val chatlistRecyclerView = currentView.findViewById<RecyclerView>(R.id.chatlist_container)
                chatlistRecyclerView.layoutManager = LinearLayoutManager(parentFragment?.requireContext())
                try{
                    chatlistRecyclerView.adapter = ChatroomAdapter(chatrooms, findNavController())
                }catch (e : IllegalStateException){
                    intervaler.cancel()
                    intervaler.purge()
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
         * @return A new instance of fragment ChatListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        var intervaler: Timer = Timer()
        lateinit var currentView : View
        fun newInstance(param1: String, param2: String) =
            ChatListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}