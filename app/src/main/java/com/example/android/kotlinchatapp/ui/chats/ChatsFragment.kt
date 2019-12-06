package com.example.android.kotlinchatapp.ui.chats


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.kotlinchatapp.ui.model.Chat
import com.example.android.kotlinchatapp.ui.model.User
import com.example.android.kotlinchatapp.R
import com.example.android.kotlinchatapp.ui.notification.Token
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

    private var userAdapter: UserAdapter? = null
    private var mUsers: MutableList<User>? = null

    internal var firebaseUser: FirebaseUser? = null
    lateinit var reference: DatabaseReference

    private var userslist: ArrayList<String>? =null
    lateinit var test :List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v=  inflater.inflate(R.layout.fragment_chats, container, false)

        v.recycle_View?.layoutManager=LinearLayoutManager(context)!!
        mUsers = ArrayList()
        userslist = ArrayList()
        test= emptyList()
        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userslist!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat!!.sender == firebaseUser!!.uid)
                        userslist!!.add(chat.reciever!!)
                    if (chat.reciever == firebaseUser!!.uid)
                        userslist!!.add(chat.sender!!)


                }
                test=userslist?.distinct()!!
                userslist?.clear()
                for (index:Int in 0..(test.size-1)){
                    userslist?.add(test.get(index))
                }


                readChat(v)
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
        //updateToken(FirebaseInstanceId.getInstance().getToken()!!)
        return v
    }
    fun updateToken(newToken:String){
        val reference=FirebaseDatabase.getInstance().getReference("Tokens")
        val token= Token(newToken!!)
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

    private fun readChat(v:View) {
        reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUsers?.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)

                    for (id in userslist!!) {
                        if (user?.id.equals(id)) {
                            if (mUsers?.size != 0) {
//                                for (user1 in mUsers!!)
//                                    if (!user?.id.equals(user1.id))
                                        mUsers?.add(user!!)


                            } else {
                                mUsers!!.add(user!!)
                            }
                        }
                    }
                }
                val chatUsers:List<User> = emptyList()
//                for (index:Int in 0..(mUsers?.size!!-1) ){
//                    for (current:Int in (index)..(mUsers?.size!!-2)){
//                        if (mUsers?.get(index)?.id.equals(mUsers?.get(current)?.id)){
//                            mUsers?.remove(mUsers?.get(current)!!)
//                        }
//                    }
//                }

                userAdapter = UserAdapter(context, mUsers!!,true)
                v.recycle_View?.setAdapter(userAdapter)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

}
