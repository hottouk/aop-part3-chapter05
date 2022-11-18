package com.example.aop_part3_chapter05

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.aop_part3_chapter05.adapter.CardItemAdapter
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
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction

class LikeActivity : AppCompatActivity(), CardStackListener {

    //파이어베이스 인증
    private val auth: FirebaseAuth = Firebase.auth

    //파이어베이스 DB
    private lateinit var usersDB: DatabaseReference
    private var cardItems = mutableListOf<CardItem>()

    //카드 스와이프 에니메이션
    val cardStackView: CardStackView by lazy { findViewById(R.id.card_stack_view) }
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val adapter by lazy { CardItemAdapter(cardItems) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like)
        //파이어베이스 DB
        usersDB = Firebase.database.reference.child("Users")
        //파이어베이스 DB 최상위 항목이 reference다.
        val currentUserDB = usersDB.child(getCurrentUserId())
        //DB에서 값 가져오기; 리스너를 달아야 함.
        currentUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {//데이터가 변경될 때 호출되는 함수; 첫 데이터 생성시;
                //현재는 UserId
                if (snapshot.child("userName").value == null) {
                    Log.d("tinder", "한번만 실행되어야 하는 코드ㅠㅠ")
                    showNameInputPopUp()
                    return
                }
                getUnselectedUsers()
            }

            override fun onCancelled(error: DatabaseError) { //변경하다 취소 시
            }

        })
        initCardStackView()
        initLogOutBtn()
        initMatchListBtn()
    }

    private fun initMatchListBtn() {
        val matchListBtn: Button = findViewById(R.id.match_list_btn)
        matchListBtn.setOnClickListener {
            val intent = Intent(this, MatchedUserActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initLogOutBtn() {
        val logOutBtn: Button = findViewById(R.id.log_out_btn)
        logOutBtn.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() //메인 엑티비티로 돌아간다.
        }
    }

    //유저 이름 저장하기
    private fun saveUserName(name: String) {
        val userId = getCurrentUserId()
        val currentUserDB = usersDB.child(userId)
        val user = mutableMapOf<String, Any>()
        user["userId"] = userId
        user["userName"] = name
        currentUserDB.updateChildren(user)
        //유저 정보 DB에서 가져오기
        Log.d("tinder", "이름 저장시 실행되는 getUnselectedUser 코드")
        getUnselectedUsers()
    }

    //이름 입력 박스
    private fun showNameInputPopUp() {
        val editText = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("이름 입력")
            .setMessage("이름을 입력해 주세요")
            .setView(editText)
            .setPositiveButton("저장") { dialog, which ->
                if (editText.text.isEmpty()) {
                    showNameInputPopUp()
                } else {
                    saveUserName(editText.text.toString())
                }
            }
            .setCancelable(false)
            .show()
    }

    //로그인한 유저 ID를 받는다.
    private fun getCurrentUserId(): String {
        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }

    //카드 스택 그리기
    private fun initCardStackView() {
        Log.d("tinder", "카드 스택 그리는 단계")
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
    }

    //만난 적 없는 유저, 즉 좋아요나 싫어요를 누르지 않았던 유저를 가져와서 RecyclerView에 넣는다.
    private fun getUnselectedUsers() { //나 이외의 내가 선택하지 않은 유저정보를 파이어 베이스 DB에서 가져오기;
        Log.d("tinder", "만난 적 없는 유저 뜨게하는 단계")
        //DB의 하위 항목에 변동이 있을 떄 호출된다. 인자 object로 childEventListener를 받는다.
        usersDB.addChildEventListener(object :
            ChildEventListener { //유저 DB에서 생기는 모든 변동사항이 이 Listner를 호출한다.
            override fun onChildAdded(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) { //스냅샷은 DB의 읽기전용 복사본
                //유저 추가 된 경우
                if (snapshot.child("userId").value != getCurrentUserId() //현재 유저 ID가 나와 같지 않고(즉 상대방)
                    && snapshot.child("emotion").child("like").hasChild(getCurrentUserId()).not()
                    //상대방의 좋아요에 내가 없고
                    && snapshot.child("emotion").child("disLike").hasChild(getCurrentUserId()).not()
                //상대방의 싫어요에 내가 없는 경우에 실행한다.
                ) {
                    Log.d("tinder", "내 아이디: ${getCurrentUserId()}")
                    val userId = snapshot.child("userId").value.toString()
                    var name = "undecided"
                    if (snapshot.child("userName").value != null) {
                        name = snapshot.child("userName").value.toString()
                    }
                    cardItems.add(CardItem(userId, name))
                    cardItems.forEach {
                        Log.d("tinder", "다른 User ID: ${it.userId}")
                        Log.d("tinder", "다른 User Name: ${it.userName}")
                    }
                    initCardStackView()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("tinder","유저 정보 수정된 경우")
                cardItems.find { it.userId == snapshot.key }?.let {
                    it.userName = snapshot.child("name").value.toString()
                }
                initCardStackView()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) { //유저 정보 제거
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) { //순서가 바뀜
            }

            override fun onCancelled(error: DatabaseError) { //유저 정보 변경하다 취소 시
            }
        })
    }

    //카드 애니메이션
    override fun onCardDragging(direction: Direction?, ratio: Float) {
    }

    override fun onCardSwiped(direction: Direction?) {
        when (direction) {
            Direction.Left -> {
                dislike()
            }
            Direction.Right -> {
                like()
            }
            else -> {}
        }
    }

    override fun onCardRewound() {
    }

    override fun onCardCanceled() {
    }

    override fun onCardAppeared(view: View?, position: Int) {
    }

    override fun onCardDisappeared(view: View?, position: Int) {
    }

    private fun like() {
        val card = cardItems[manager.topPosition - 1]
        cardItems.removeFirst() //첫째 값을 지운다.
        usersDB.child(card.userId)
            .child("emotion")
            .child("like")
            .child(getCurrentUserId())
            .setValue(true)
        matchSuccess(card.userId)
        Toast.makeText(this, "${card.userName}을 좋아요 하셨습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun matchSuccess(otherUserId: String) {
        val isOtherUserLikingMe =
            usersDB.child(getCurrentUserId()).child("emotion").child("like").child(otherUserId)
        isOtherUserLikingMe.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //데이터 가 있을 때
                if (snapshot.value == true) {
                    usersDB.child(getCurrentUserId())
                        .child("match")
                        .child(otherUserId)
                        .setValue(true)

                    usersDB.child(otherUserId)
                        .child("match")
                        .child(getCurrentUserId())
                        .setValue(true)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    private fun dislike() {
        val card = cardItems[manager.topPosition - 1]
        cardItems.removeFirst() //첫째 값을 지운다.
        usersDB.child(card.userId)
            .child("emotion")
            .child("disLike")
            .child(getCurrentUserId())
            .setValue(true)
        Toast.makeText(this, "${card.userName}을 싫어요 하셨습니다.", Toast.LENGTH_SHORT).show()
    }

}

