package com.project.sns.ui.activities.register.ui.login

import android.content.Intent
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
import com.project.sns.R
import com.project.sns.databinding.FragmentSignInBinding
import com.project.sns.ui.activities.MainActivity


class SignInFragment : Fragment() {

    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private var registerBinding : FragmentSignInBinding ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        registerBinding = FragmentSignInBinding.inflate(layoutInflater)

        return registerBinding!!.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initLayout();
    }
    private fun initLayout(){

        registerBinding!!.registerText.setTextColor(Color.BLUE)
        registerBinding!!.registerText.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        registerBinding!!.registerText.setOnClickListener {
            view?.findNavController()!!.navigate(R.id.action_registerFragment_to_signUpFragment)
        }

        registerBinding!!.loginButton.setOnClickListener {
            if (!registerBinding!!.idTextInput.text!!.isEmpty() && !registerBinding!!.passwordTextInput.text!!.isEmpty()) {
                doLogin()
            }
        }
    }
    private fun doLogin() {
        firebaseAuth.signInWithEmailAndPassword(registerBinding!!.idTextInput.text.toString(), registerBinding!!.passwordTextInput.text.toString())
                .addOnCompleteListener(requireActivity(), OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        requireActivity().finish()
                    } else {
                        Toast.makeText(requireContext(), "로그인 오류, 아이디나 비밀번호에 오류가 없는지 다시 확인하세요.", Toast.LENGTH_SHORT).show()
                    }
                })
    }
}