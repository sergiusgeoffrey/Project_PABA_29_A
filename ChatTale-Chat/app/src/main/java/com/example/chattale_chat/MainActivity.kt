package com.example.chattale_chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


class MainActivity : AppCompatActivity() {

    interface fetchDisplayNameCallback {
        fun onFetchDone(displayName : String)
        fun onFetchDone(displayNames : MutableList<String>)
    }

    companion object{
        lateinit var CurrentAccount : Account
        lateinit var ChatroomList : MutableList<Chatroom>
        lateinit var CurrentChatroom: Chatroom
        lateinit var DB : FirebaseFirestore
        lateinit var auth: FirebaseAuth
        lateinit var UsernameTable : MutableMap<String,String>
        var GlobalRefreshMS : Long = 1000
        fun fetchDisplayName(username : String, fetchDisplayNameCallback: fetchDisplayNameCallback){
            // displayname fetcher
            if(MainActivity.UsernameTable.containsKey(username)){
                // if data is already available, return immediately
                if(MainActivity.UsernameTable[username] == null || MainActivity.UsernameTable[username] == ""){
                    fetchDisplayNameCallback.onFetchDone(username)
                }else{
                    fetchDisplayNameCallback.onFetchDone(MainActivity.UsernameTable[username]!!)
                }
            }else{
                // else, fetch from server
                MainActivity.DB.collection("Accounts").document(username).get().addOnSuccessListener {
                    document->
                    if(document["displayName"] == null || document["displayName"] == ""){
                        fetchDisplayNameCallback.onFetchDone(username)
                        MainActivity.UsernameTable[username] = username

                    }else{
                        fetchDisplayNameCallback.onFetchDone(document["displayName"].toString())
                        MainActivity.UsernameTable[username] = document["displayName"].toString()
                    }
                }
            }
        }

        fun fetchDisplayName(usernameList: List<String>, fetchDisplayNameCallback: fetchDisplayNameCallback){
            // displayname fetcher, list version

            var displayNames = mutableListOf<String>()

            for (username in usernameList){
                MainActivity.fetchDisplayName(username, object : MainActivity.fetchDisplayNameCallback{
                    override fun onFetchDone(displayName: String) {
                        displayNames.add(displayName)
                        fetchDisplayNameCallback.onFetchDone(displayNames)
                    }

                    override fun onFetchDone(displayNames: MutableList<String>) {
                        // none
                    }
                })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}