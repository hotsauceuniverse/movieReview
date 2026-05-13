package com.seyoung.moviereview.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.seyoung.moviereview.R

class  LoginFragment : Fragment(){

    private lateinit var editID : EditText
    private lateinit var editPassword : EditText
    private lateinit var loginBtn : Button
    private lateinit var signUpBtn : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 로그인 페이지 구현 예정
        editID = view.findViewById(R.id.edit_ID)
        editPassword = view.findViewById(R.id.edit_Password)
        loginBtn = view.findViewById(R.id.login_Btn)
        signUpBtn = view.findViewById(R.id.sign_Up_Btn)

        // 회원가입 이동
        signUpBtn.setOnClickListener {
            val intent = Intent(context, JoinMembershipActivity::class.java)
            startActivity(intent)
        }
    }
}
