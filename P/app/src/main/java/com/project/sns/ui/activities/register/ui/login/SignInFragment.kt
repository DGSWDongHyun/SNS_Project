package com.project.sns.ui.activities.register.ui.login

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.project.sns.R
import com.project.sns.databinding.FragmentSignInBinding
import com.project.sns.ui.activities.MainActivity


class SignInFragment : Fragment() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var registerBinding: FragmentSignInBinding? = null
    private var sharedPreferences: SharedPreferences? = null
    private lateinit var database: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        registerBinding = FragmentSignInBinding.inflate(layoutInflater)

        return registerBinding!!.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initLayout();
    }

    private fun initLayout() {
        database = Firebase.database.reference
        sharedPreferences = requireContext().getSharedPreferences("Account", MODE_PRIVATE)
        registerBinding!!.isCheckedAuto.isChecked = sharedPreferences!!.getBoolean("isChecked", false)

        if (registerBinding!!.isCheckedAuto.isChecked) {
            val isLogined = sharedPreferences!!.getBoolean("isLogined", false)
            Log.d("TAG" , "$isLogined")
            if(isLogined) {
                requireActivity().finish()
            }
        }

        registerBinding!!.registerText.setTextColor(Color.BLUE)
        registerBinding!!.registerText.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        registerBinding!!.registerText.setOnClickListener {
            view?.findNavController()!!.navigate(R.id.action_registerFragment_to_signUpFragment)
        }

        registerBinding!!.loginButton.setOnClickListener {
            if (registerBinding!!.idTextInput.text!!.isNotEmpty() && registerBinding!!.passwordTextInput.text!!.isNotEmpty()) {
                doLogin()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun doLogin() {
        val isLogined = sharedPreferences!!.getBoolean("isLogined", false)
        Log.d("TAG" , "$isLogined")
        if (isLogined) {
                    requireActivity().finish()
                } else {
                    firebaseAuth.signInWithEmailAndPassword(registerBinding!!.idTextInput.text.toString(), registerBinding!!.passwordTextInput.text.toString())
                            .addOnCompleteListener(requireActivity(), OnCompleteListener<AuthResult?> { task ->
                                if (task.isSuccessful) {
                                    val editor = sharedPreferences!!.edit()

                                    editor?.putBoolean("isLogined", true)
                                    editor.putBoolean("isChecked", registerBinding!!.isCheckedAuto.isChecked)
                                    editor.apply()

                                    requireActivity().finish()
                                } else {
                                    Toast.makeText(requireContext(), "로그인 오류, 아이디나 비밀번호에 오류가 없는지 다시 확인하세요.", Toast.LENGTH_SHORT).show()
                                }
                            })
                }

    }
}