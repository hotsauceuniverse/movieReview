package com.seyoung.moviereview.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.seyoung.moviereview.R

class JoinMembershipActivity : AppCompatActivity() {

    private lateinit var signName : EditText
    private lateinit var signID : EditText
    private lateinit var signPw : EditText
    private lateinit var signPw2 : EditText
    private lateinit var pwCheckBtn : Button
    private lateinit var signupBtn : Button
    private lateinit var back : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.join_membership_activity)

        signName = findViewById(R.id.sign_Name)
        signID = findViewById(R.id.sign_ID)
        signPw = findViewById(R.id.sign_Pw)
        signPw2 = findViewById(R.id.sign_Pw2)
        pwCheckBtn = findViewById(R.id.pw_Check_Btn)
        signupBtn = findViewById(R.id.signup_Btn)
        back = findViewById(R.id.back)

        back.setOnClickListener {
            finish()
        }

        // pw_Check_Btn 클릭 시, 일치 여부 Alert 으로 띄우기 (layout : check_alert)
        pwCheckBtn.setOnClickListener {
            val pw = signPw.text.toString()
            val rePw = signPw2.text.toString()

            val message = if (rePwCheck(pw, rePw)) {
                "비밀번호가 일치합니다"
            } else {
                "비밀번호가 일치하지 않습니다"
            }

            val dialog = layoutInflater.inflate(R.layout.check_alert, null)

            val text_1 = dialog.findViewById<TextView>(R.id.txt_1)
            val okBtn = dialog.findViewById<Button>(R.id.ok_btn)

            text_1.text = message

            val alertDialog = AlertDialog.Builder(this)
                .setView(dialog)
                .create()

            // 다이얼로그 기본 배경 제거
            alertDialog.window?.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )

            okBtn.setOnClickListener{
                alertDialog.dismiss()
            }

            alertDialog.show()
        }

        // 회원가입 버튼 처리
        signupBtn.setOnClickListener {
            signUp()
        }
    }

    // 닉네임 체크 : 공백 / 특수문자 제외, 영문, 숫자, 한글 허용 1 ~ 6자
    fun checkName(name: String): Boolean {
        val nameRegex = Regex("^[a-zA-Z0-9가-힣]{1,6}$")
        return nameRegex.matches(name)
    }

    // ID 체크 : 공백 / 특수문자 제외, 영문 + 숫자 포함 4 ~ 12자
    fun idCheck(id: String): Boolean {
        val idRegex = Regex("^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{4,12}$")
        return idRegex.matches(id)
    }

    // pw 체크 : 공백 / 특수문자 제외, 영문 + 숫자 포함 6 ~ 20자
    fun pwCheck(pw: String): Boolean {
        val pwRegex = Regex("^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{6,20}$")
        return pwRegex.matches(pw)
    }

    // pw 재확인 : pw 와 같은지 확인
    fun rePwCheck(pw: String, rePw: String): Boolean {
        return pw.isNotEmpty() && pw == rePw
    }

    // 회원가입 버튼 클릭 시, 닉네임 / ID / PW 검증
    fun signUp() {
        val nickName = signName.text.toString()
        val id = signID.text.toString()
        val pw = signPw.text.toString()
        val rePw = signPw2.text.toString()

        if (!checkName(nickName)) {
            signName.error = "닉네임 형식 오류"
            return
        }

        if (!idCheck(id)) {
            signID.error = "ID 형식 오류"
            return
        }

        if (!pwCheck(pw)) {
            signPw.error = "PW 형식 오류"
            return
        }

        if (!rePwCheck(pw, rePw)) {
            signPw2.error = "비밀번호 불일치"
            return
        }
    }
}