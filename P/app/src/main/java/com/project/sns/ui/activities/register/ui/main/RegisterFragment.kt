package com.project.sns.ui.activities.register.ui.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.project.sns.databinding.FragmentRegisterInfoBinding
import com.project.sns.ui.activities.MainActivity


class RegisterFragment : Fragment() {

    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private var registerBinding : FragmentRegisterInfoBinding ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        registerBinding = FragmentRegisterInfoBinding.inflate(layoutInflater)

        return registerBinding!!.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        registerBinding!!.registerText.setTextColor(Color.BLUE)

        registerBinding!!.registerText.setOnClickListener {

        }

        registerBinding!!.loginButton.setOnClickListener {
            doLogin()
        }
    }
    private fun doLogin() {
        firebaseAuth.signInWithEmailAndPassword(registerBinding!!.idTextInput.text.toString(), registerBinding!!.passwordTextInput.text.toString())
                .addOnCompleteListener(requireActivity(), OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(requireContext(), "로그인 오류", Toast.LENGTH_SHORT).show()
                    }
                })
    }
}