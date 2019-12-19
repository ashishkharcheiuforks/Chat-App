package com.example.android.kotlinchatapp.ui.fragments


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.kotlinchatapp.ui.model.User

import com.example.android.kotlinchatapp.R
import com.example.android.myapplication.Adapter.UserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_chats.view.recycle_View
import kotlinx.android.synthetic.main.fragment_users.*
import kotlinx.android.synthetic.main.fragment_users.view.*
import java.util.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class UsersFragment : Fragment() {
    private var recyclerView: RecyclerView? = null

     var userAdapter: UserAdapter?=null
    lateinit var mUsers: ArrayList<User>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v= inflater.inflate(R.layout.fragment_users, container, false)
        //val recycle_View:RecyclerView = v.findViewById(R.id.recycle_View)

        return v
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)
        mUsers = ArrayList()
        v.scrollView.setOnScrollChangeListener(object :View.OnScrollChangeListener{
            override fun onScrollChange(p0: View?, p1: Int, p2: Int, p3: Int, p4: Int) {
            }

        })
        var manger=LinearLayoutManager(context)
        v.recycle_View.layoutManager = manger


        v.search_users.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchUsers(charSequence.toString().toLowerCase())
            }

        })

        readUsers(v)
    }



    private fun searchUsers(s: String) {

        val fUser=FirebaseAuth.getInstance().currentUser
        val query=FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
            .startAt(s)
            .endAt(s+"\uf8ff")
        query.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUsers?.clear()
                for (snapshot :DataSnapshot in dataSnapshot.children){
                    val user =snapshot.getValue(User::class.java)
                    if (!user?.id.equals(fUser?.uid))
                    {
                        mUsers?.add(user!!)
                    }
                }
                userAdapter= UserAdapter(context!!,mUsers!!,false)
                recycle_View.adapter=userAdapter!!
            }

        })

    }

    private fun readUsers(v:View) {

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(context!!,p0.message,Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (v.search_users.text.toString()==""){
                mUsers?.clear()
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)!!

                    assert(firebaseUser != null)
                    if (user.id != firebaseUser!!.uid) {
                        mUsers.add(user)
                    }
                }

                    if (mUsers.size!=0){
                        userAdapter = UserAdapter(context, mUsers!!,false)!!
                        v.recycle_View.adapter = userAdapter!!
                    }


            }
            }

        })
    }




}
