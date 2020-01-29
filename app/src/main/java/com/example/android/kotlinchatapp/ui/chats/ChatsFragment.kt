package com.example.android.kotlinchatapp.ui.chats


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.kotlinchatapp.ui.model.Chat
import com.example.android.kotlinchatapp.ui.model.User
import com.example.android.kotlinchatapp.R
import com.example.android.kotlinchatapp.ui.model.ChatList
import com.example.android.kotlinchatapp.ui.notification.Token
import com.example.android.kotlinchatapp.utils.FormatDate
import com.example.android.kotlinchatapp.utils.FormatDate.dateFormatter_v3
import com.example.android.kotlinchatapp.utils.SpacingItemDecoration
import com.example.android.myapplication.Adapter.UserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_chats.view.*
import java.util.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ChatsFragment : Fragment() {
    private var recycle_View: RecyclerView? = null
    private val spacingItemDecoration= SpacingItemDecoration()

    private var userAdapter: UserAdapter? = null
    private var mUsers: MutableList<User>? = null
    private var finalChatListUsers: MutableList<User>? = null
    internal var firebaseUser: FirebaseUser? = null
    lateinit var reference: DatabaseReference

    private var userslist: ArrayList<ChatList>? = null
    lateinit var test: List<String>
    lateinit var chatsViewModel: ChatsViewModel
    lateinit var v:View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         v = inflater.inflate(R.layout.fragment_chats, container, false)
        chatsViewModel = ChatsViewModel()
        v.recycle_View?.layoutManager = LinearLayoutManager(context)!!
        v.recycle_View.addItemDecoration(SpacingItemDecoration())
        mUsers = ArrayList()
        userslist = ArrayList()
        test = emptyList()
        firebaseUser = FirebaseAuth.getInstance().currentUser

        reference =
            FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser!!.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userslist!!.clear()
                for (snapShot in dataSnapshot.children) {
                    val chatList = snapShot.getValue(ChatList::class.java)
                    userslist!!.add(chatList!!)
                }
                readChatList()
            }

        })
        chatsViewModel.updateToken()
        chatsViewModel.token.observe(this, Observer {
            updateToken(it)
        })

//        reference = FirebaseDatabase.getInstance().getReference("Chats")
//        reference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                userslist!!.clear()
//                for (snapshot in dataSnapshot.children) {
//                    val chat = snapshot.getValue(Chat::class.java)
//                    if (chat!!.sender == firebaseUser!!.uid)
//                        userslist!!.add(chat.reciever!!)
//                    if (chat.reciever == firebaseUser!!.uid)
//                        userslist!!.add(chat.sender!!)
//
//
//                }
//                test=userslist?.distinct()!!
//                userslist?.clear()
//                for (index:Int in 0..(test.size-1)){
//                    userslist?.add(test.get(index))
//                }
//
//
//                readChat(v)
//            }
//            override fun onCancelled(databaseError: DatabaseError) {
//
//            }
//        })
//        chatsViewModel.updateToken()
//        chatsViewModel.token.observe(this, Observer {
//            updateToken(it)
//        })
//        //updateToken(FirebaseInstanceId.getInstance().getToken()!!)
        return v
    }

    private fun readChatList() {
        userslist?.sortWith(Comparator{o1, o2 ->
            val firstDate=o1.date?.let { dateFormatter_v3(it) }
            var secondDate = o2.date?.let { dateFormatter_v3(it) }
            secondDate?.compareTo(firstDate!!)?:0
        })

        mUsers = ArrayList()
        reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                v.progress_bar.visibility= GONE
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUsers!!.clear()
                for (snapShot in dataSnapshot.children) {
                    val user = snapShot.getValue(User::class.java)
                    for (chatList in userslist!!) {
                        if (user!!.id.equals(chatList.id))
                            mUsers!!.add(user)
                    }
                }
                prepairUsersToShow()
            }

        })
    }

    private fun prepairUsersToShow() {
        finalChatListUsers=ArrayList()
        finalChatListUsers!!.clear()
        for (chatList in userslist!!){
            for (user in mUsers!!){
                if (chatList.id.equals(user.id))
                    finalChatListUsers!!.add(user)
            }
        }
        v.progress_bar.visibility= GONE
        v.container.visibility= if (mUsers!!.isEmpty()) VISIBLE else GONE

        userAdapter= UserAdapter(context!!,if (finalChatListUsers!!.size>0) finalChatListUsers!! else ArrayList(),true)
        v.recycle_View.adapter=userAdapter
    }

    fun updateToken(newToken: String) {
        val reference = FirebaseDatabase.getInstance().getReference("Tokens")
        val token = Token(newToken!!)
        reference.child(firebaseUser?.uid!!).setValue(token)
    }

    //private fun readChat() {
//        reference = FirebaseDatabase.getInstance().getReference("Users")
//        reference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                mUsers!!.clear()
//                for (snapshot in dataSnapshot.children) {
//                    val user = snapshot.getValue(User::class.java)
//
//                    for (id in userslist!!) {
//                        if (user!!.id == id) {
//                            if (mUsers!!.size != 0) {
//                                for (user1 in mUsers!!)
//                                    if (user.id != user1.id)
//                                        mUsers!!.add(user)
//
//
//                            } else {
//                                mUsers!!.add(user)
//                            }
//                        }
//                    }
//                }
//                userAdapter = UserAdapter(context!!, mUsers!!)
//                recycle_View?.adapter = userAdapter
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Toast.makeText(context!!,databaseError.message,Toast.LENGTH_LONG).show()
//                Log.e("errorr",databaseError.message)
//            }
//        })

    //   }

//    private fun readChat(v:View) {
//        reference = FirebaseDatabase.getInstance().getReference("Users")
//        reference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                mUsers?.clear()
//                for (snapshot in dataSnapshot.children) {
//                    val user = snapshot.getValue(User::class.java)
//
//                    for (id in userslist!!) {
//                        if (user?.id.equals(id)) {
//                            if (mUsers?.size != 0) {
////                                for (user1 in mUsers!!)
////                                    if (!user?.id.equals(user1.id))
//                                        mUsers?.add(user!!)
//
//
//                            } else {
//                                mUsers!!.add(user!!)
//                            }
//                        }
//                    }
//                }
//                val chatUsers:List<User> = emptyList()
////                for (index:Int in 0..(mUsers?.size!!-1) ){
////                    for (current:Int in (index)..(mUsers?.size!!-2)){
////                        if (mUsers?.get(index)?.id.equals(mUsers?.get(current)?.id)){
////                            mUsers?.remove(mUsers?.get(current)!!)
////                        }
////                    }
////                }
//                if (mUsers!!.isNotEmpty())
//                    v.container.visibility=GONE
//                userAdapter = UserAdapter(context, mUsers!!,true)
//                v.recycle_View?.setAdapter(userAdapter)
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//
//            }
//        })
//
//    }

}
