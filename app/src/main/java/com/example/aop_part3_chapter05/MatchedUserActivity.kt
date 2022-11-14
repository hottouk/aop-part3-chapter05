package com.example.aop_part3_chapter05

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aop_part3_chapter05.adapter.MatchedUserAdpater
import com.example.aop_part3_chapter05.model.CardItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MatchedUserActivity : AppCompatActivity() {

    //파베 인증
    private val auth: FirebaseAuth = Firebase.auth

    //파베 DB
    private lateinit var usersDB: DatabaseReference

    //리사 어뎁터
    private val adapter by lazy { MatchedUserAdpater(cardItems) }

    //컨텐츠
    private var cardItems = MutableList<CardItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matched_user)

        //파베 DB 가져오기
        usersDB = Firebase.database.reference.child("Users")

        initMatchedUserRecyclerView()
        getMatchUsers()

    }

    private fun getMatchUsers() {
        val matchedDb = usersDB.child(getCurrentUserId()).child("match")
        matchedDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.key?.isNotEmpty() == true)
                    getUserByKey(snapshot.key.orEmpty())
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun getUserByKey(userId: String) {
        usersDB.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //데이터가 있을 때
                cardItems.add(CardItem(userId, snapshot.child("userName").value.toString()))
                initMatchedUserRecyclerView()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    //리사이클러뷰 실행함수
    private fun initMatchedUserRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.matched_user_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    //로그인한 유저 ID를 받는다.
    private fun getCurrentUserId(): String {
        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }
}