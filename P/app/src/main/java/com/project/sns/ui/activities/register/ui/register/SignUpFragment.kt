package com.project.sns.ui.activities.register.ui.register

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.project.sns.R
import com.project.sns.data.board.User
import com.project.sns.databinding.FragmentSignUpBinding
import com.project.sns.ui.activities.MainActivity


class SignUpFragment : Fragment() {

    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    var signUpFragment : FragmentSignUpBinding ?= null
    private lateinit var database: DatabaseReference
    private var sharedPreferences: SharedPreferences ?= null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        signUpFragment = FragmentSignUpBinding.inflate(layoutInflater)
        return signUpFragment!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLayout();
    }
    private fun initLayout(){
        sharedPreferences = requireContext().getSharedPreferences("Account", MODE_PRIVATE)
        database = Firebase.database.reference
        signUpFragment!!.loginText.setTextColor(Color.BLUE)
        signUpFragment!!.loginText.paintFlags = Paint.UNDERLINE_TEXT_FLAG;

        signUpFragment!!.registerButton.setOnClickListener {
            if(signUpFragment!!.idTextInput.text!!.isNotEmpty()
                    && signUpFragment!!.passwordTextInput.text!!.isNotEmpty()) {
                doRegister()
            }
        }

        signUpFragment!!.loginText.setOnClickListener {
            view?.findNavController()!!.navigate(R.id.action_signUpFragment_to_registerFragment)
        }
    }
    private fun doRegister(){
        firebaseAuth.createUserWithEmailAndPassword(signUpFragment!!.idTextInput.text.toString(), signUpFragment!!.passwordTextInput.text.toString())
                .addOnCompleteListener(requireActivity(), OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        val key: String? = database.push().key
                        database.child("user").child(key!!).setValue(User(signUpFragment!!.nameTextInput.text.toString(), signUpFragment!!.idTextInput.text.toString(), key))
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        val editor = sharedPreferences?.edit()
                        editor?.putString("userName", signUpFragment!!.nameTextInput.text.toString())
                        editor?.putString("Email", signUpFragment!!.idTextInput.text.toString())
                        editor?.putString("PassWord", signUpFragment!!.passwordTextInput.text.toString())
                        editor?.commit()

                        requireActivity().finish()
                    } else {
                        Toast.makeText(requireContext(), "등록 에러", Toast.LENGTH_SHORT).show()
                        return@OnCompleteListener
                    }
                })
    }
}