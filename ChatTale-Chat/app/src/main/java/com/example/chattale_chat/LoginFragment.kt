package com.example.chattale_chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.FirebaseAuthCredentialsProvider
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var auth: FirebaseAuth

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
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            //reload();
        }
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // THIS IS VERY VERY WRONG TO DO, WINSON, THIS IS UR PART YE

        auth = FirebaseAuth.getInstance()
        val usernameInput = view.findViewById<EditText>(R.id.username_input)
        val passwordInput = view.findViewById<EditText>(R.id.password_input)
        val loginButton = view.findViewById<Button>(R.id.login_button)


        loginButton.setOnClickListener {
            // DISCLAIMER, THIS IS TEMPORARY
            // on login click, create new account and push to server
            //val usernameString = usernameInput.text.toString()
            // WARN! no username checking + username length check + etc!
            //MainActivity.CurrentAccount = Account(usernameString,true,usernameString)
            // set username of collection "Accounts" with the Account object just created
            //MainActivity.DB.collection("Accounts").document(usernameString).set(MainActivity.CurrentAccount).addOnSuccessListener {
            // then if success go to chat list
            //findNavController().navigate(R.id.action_loginFragment_to_chatListFragment)
            //}
            // WARN! no on failed listener!

            val email = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            //validate email
            if (isValidEmail(email)) {
                //check email and password
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {task ->
                        if (task.isSuccessful) {
                            // Sign in success
                            Log.d("login_success", "signInWithEmail:success")
                            val user = auth.currentUser
                            Log.d("user", user.toString())
                            findNavController().navigate(R.id.action_loginFragment_to_chatListFragment)
                        } else {
                            // Sign in failed
                            Log.d("login_failed", "signInWithEmail:failure", task.exception)
                            Toast.makeText(this.context, "Wrong password/email.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            else
            {
                Toast.makeText(this.context, "Invalid Email.",
                    Toast.LENGTH_SHORT).show()
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
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}