package com.example.chattale_chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var CurrentAccount : Account
        lateinit var ChatroomList : MutableList<Chatroom>
        lateinit var CurrentChatroom: Chatroom
        lateinit var DB : FirebaseFirestore
        var GlobalRefreshMS : Long = 500
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DB = FirebaseFirestore.getInstance()
    }
}