package com.example.chattale_chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.tasks.await
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NewChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewChatFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_new_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val chatroomDisplayNameInput = view.findViewById<EditText>(R.id.new_chat_displayname_input)
        val newMemberInput = view.findViewById<EditText>(R.id.new_chat_username_input)
        val addMemberButton = view.findViewById<ImageButton>(R.id.add_member_button)
        val newChatBackButton = view.findViewById<ImageButton>(R.id.new_chat_back_button)
        val createChatroomButton = view.findViewById<Button>(R.id.new_chat_create_button)
        val newChatRecyclerView = view.findViewById<RecyclerView>(R.id.new_chat_member_container)

        // uses set to prevent dups
        val memberSet = mutableSetOf<String>()

        // add self and set adapter
        memberSet.add(MainActivity.CurrentAccount.username!!)
        newChatRecyclerView.layoutManager = LinearLayoutManager(view.context)
        newChatRecyclerView.adapter = NewMemberAdapter(memberSet)

        // back button
        newChatBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // add member from text field
        addMemberButton.setOnClickListener {
            // WARN! no checks!
            var newMemberString = newMemberInput.text.toString()
            newMemberString = newMemberString.lowercase()
            newMemberInput.setText("")

            // adds to set and refresh recycler view
            memberSet.add(newMemberString)
            newChatRecyclerView.layoutManager = LinearLayoutManager(view.context)
            newChatRecyclerView.adapter = NewMemberAdapter(memberSet)
        }

        createChatroomButton.setOnClickListener {
            // creates chatroom

            // generate id
            val uuid = UUID.randomUUID()
            val randomUUIDString = uuid.toString()

            // get current time
            val unixTime = System.currentTimeMillis()

            // get chatroom displayname
            val chatroomDisplayName = chatroomDisplayNameInput.text.toString()

            // gat chatroom members
            val chatroomMembers: List<String> = memberSet.toList()

            // create null last message
            val chatroomLastMessage =
                Message(null, randomUUIDString, MainActivity.CurrentAccount.username, unixTime, null, false)

            // creates new chatroom
            val newChatroom = Chatroom(
                randomUUIDString,
                chatroomDisplayName,
                chatroomMembers,
                chatroomLastMessage
            )

            MainActivity.CurrentChatroom = newChatroom

            // set to firebase
            MainActivity.DB.collection("Chatrooms").document(randomUUIDString).set(newChatroom).addOnSuccessListener {
                findNavController().navigate(R.id.action_newChatFragment_to_chattingFragment)
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
         * @return A new instance of fragment NewChatFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}