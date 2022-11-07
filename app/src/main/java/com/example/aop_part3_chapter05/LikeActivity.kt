package com.example.aop_part3_chapter05

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LikeActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = Firebase.auth
    private lateinit var userDB: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like)

        userDB = Firebase.database.reference.child("Users") //파이어베이스 DB 최상위 항목이 reference다.
        val currentUserDB = userDB.child(getCurrentUserId())
        //DB에서 값 가져오기; 리스너를 달아야 함. 새로 배움
        currentUserDB.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //데이터가 변할때 호출; 첫 데이터 생성시
                //todo 05-05강 16:10초부터 듣기
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    //로그인한 유저 ID를 받는다.
    private fun getCurrentUserId(): String {
        if(auth.currentUser == null){
            Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }


}