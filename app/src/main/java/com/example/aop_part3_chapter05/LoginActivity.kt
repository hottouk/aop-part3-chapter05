package com.example.aop_part3_chapter05

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //파이어베이스 인증
        auth = Firebase.auth

        val emailEditText = findViewById<EditText>(R.id.email_edittext)
        val passwordEditText = findViewById<EditText>(R.id.password_edittext)
        clkLoginBtn()
        clkSIgnUpBtn()
        checkEmailAndPassword()
    }

    private fun clkLoginBtn() {
        val loginBtn = findViewById<Button>(R.id.login_btn)
        loginBtn.setOnClickListener {
            val email = getInputEmail()
            val password = getInputPassword()
            //인증 키 사용방법
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        finish()    //성공적으로 저장 되어있다면 LoginActivity를 종료한다.
                    } else {
                        Toast.makeText(this, "로그인에 실패했습니다, 이메일 또는 비밀번호를 확인해주세요", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    private fun clkSIgnUpBtn() {
        val singUpBtn = findViewById<Button>(R.id.sing_up_btn)
        singUpBtn.setOnClickListener {
            val email = getInputEmail()
            val password = getInputPassword()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "회원가입에 성공, 로그인해라", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "이미 가입한 이메일, 회원가입 실패", Toast.LENGTH_SHORT).show()
                    }

                }
        }
    }

    private fun getInputEmail(): String {
        return findViewById<EditText>(R.id.email_edittext).text.toString()
    }

    private fun getInputPassword(): String {
        return findViewById<EditText>(R.id.password_edittext).text.toString()
    }

    private fun checkEmailAndPassword() {
        val emailEditText = findViewById<EditText>(R.id.email_edittext)
        val passwordEditText = findViewById<EditText>(R.id.password_edittext)
    }

}