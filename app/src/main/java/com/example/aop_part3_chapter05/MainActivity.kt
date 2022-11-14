package com.example.aop_part3_chapter05

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            //로그인 되지 않았을 때
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        } else {
            //로그인 되었을 떄, Like엑티비티가 열린 경우 MainActivity는 종료된다.
            val intent = Intent(this, LikeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}