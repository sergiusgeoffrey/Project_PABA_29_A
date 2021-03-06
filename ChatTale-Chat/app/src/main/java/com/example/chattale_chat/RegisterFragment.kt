package com.example.chattale_chat

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
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

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailInput = view.findViewById<EditText>(R.id.input_email_register)
        val passwordInput = view.findViewById<EditText>(R.id.input_password_register)
        val confirmPassword = view.findViewById<EditText>(R.id.input_confirm_password_register)
        val nameInput = view.findViewById<EditText>(R.id.input_name_register)
        val registerButton = view.findViewById<Button>(R.id.button_register)
        val loginButton = view.findViewById<TextView>(R.id.login_text)

        loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        registerButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val confirm_password = confirmPassword.text.toString()
            val display_name = nameInput.text.toString()

            if(isValidEmail(email))
            {
                if(password != "" && password == confirm_password)
                {
                    if(display_name != "")
                    {
                        MainActivity.auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("register_success", "createUserWithEmail:success")
                                    val user = MainActivity.auth.currentUser?.uid
                                    // set username of collection "Accounts" with the Account object just created
                                    if (user != null) {
                                        MainActivity.CurrentAccount = Account(user,true,display_name)
                                        Log.d("account", MainActivity.CurrentAccount.toString())
                                        MainActivity.DB.collection("Accounts").document(user).set(MainActivity.CurrentAccount).addOnSuccessListener {
                                            // then if success go to chat list
                                            findNavController().navigate(R.id.action_registerFragment_to_chatListFragment)
                                        }
                                    }
                                    else
                                    {
                                        Log.d("register_failed", "createUserWithEmail:failure", task.exception)
                                        Toast.makeText(this.context, "Registration failed.",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.d("register_failed", "createUserWithEmail:failure", task.exception)
                                    Toast.makeText(this.context, "Registration failed.",
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                    else
                    {
                        //display name empty
                        Toast.makeText(this.context, "Display Name cannot be empty",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {
                    //incorrect password
                    Toast.makeText(this.context, "Password Incorrect.",
                        Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                //wrong email format
                Toast.makeText(this.context, "Enter a valid email.",
                    Toast.LENGTH_SHORT).show()
            }

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegisterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}