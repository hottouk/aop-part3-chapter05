package com.example.aop_part3_chapter05

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    //2.페이스북 로그인 콜백 매니져
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //1. 파이어베이스 인증
        auth = Firebase.auth
        //2. 페이스북 로그인
        callbackManager = CallbackManager.Factory.create()

        val emailEditText = findViewById<EditText>(R.id.email_edittext)
        val passwordEditText = findViewById<EditText>(R.id.password_edittext)
        //1. 이메일 로그인
        clkLoginBtn()
        clkSignUpBtn()
        checkEmailAndPassword()
        //2. 페북 로그인
        clkFaceBookLoginBtn()

    }

    //1.이메일 로그인-------------------------------------------------------------------------------
    private fun clkLoginBtn() {
        val loginBtn = findViewById<Button>(R.id.login_btn)
        loginBtn.setOnClickListener {
            val email = getInputEmail()
            val password = getInputPassword()
            //인증 키 사용방법
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        handleLogInSuccess()    //성공적으로 저장 되어있다면 LoginActivity를 종료한다.
                    } else {
                        Toast.makeText(this, "로그인에 실패했습니다, 이메일 또는 비밀번호를 확인해주세요", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    private fun clkSignUpBtn() {
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
        val singUpBtn = findViewById<Button>(R.id.sing_up_btn)
        val loginBtn = findViewById<Button>(R.id.login_btn)
        val emailEditText = findViewById<EditText>(R.id.email_edittext)
        val passwordEditText = findViewById<EditText>(R.id.password_edittext)

        //텍스트가 입력 될때마다 이 리스너로 이벤트가 내려온다.
        emailEditText.addTextChangedListener {
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            loginBtn.isEnabled = enable
            singUpBtn.isEnabled = enable
        }

        passwordEditText.addTextChangedListener {
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            loginBtn.isEnabled = enable
            singUpBtn.isEnabled = enable
        }
    }

    private fun handleLogInSuccess() {
        if (auth.currentUser == null) {//if문 널처리
            Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            return
        } else {
            //로그인시 DB에 저장된 User
            val userId = auth.currentUser?.uid.orEmpty() //로그인 성공시 userID를 받는다.
            val currentUserDB = Firebase.database.reference.child("Users").child(userId) //DB 생성
            val user = mutableMapOf<String, Any>()
            user["userId"] = userId //user 변수에 현 유저 ID를 저장한다.
            currentUserDB.updateChildren(user)  //그 변수를 DB에 업데이트
            finish()
        }
    }

    //2.페이스북 로그인------------------------------------------------------------------------------
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun clkFaceBookLoginBtn() {
        val facebookLoginBtn = findViewById<LoginButton>(R.id.facebook_login_btn)
        facebookLoginBtn.setPermissions("email", "public_profile")
        facebookLoginBtn.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                Log.d("tinder", credential.toString())
                //페북 어플에서 credential을 받는다.
                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this@LoginActivity) { task ->
                        if (task.isSuccessful) {
                            finish()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "페이스북 로그인이 실패했습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }

            override fun onCancel() {
                //로그인 취소
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(this@LoginActivity, "페북 로그인 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

}