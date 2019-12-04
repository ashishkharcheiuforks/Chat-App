package com.example.android.kotlinchatapp.message_activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.android.kotlinchatapp.Model.Chat
import com.example.android.kotlinchatapp.Model.User
import com.example.android.kotlinchatapp.R
import com.example.android.kotlinchatapp.fragments.APIService
import com.example.android.kotlinchatapp.notification.*
import com.example.android.kotlinchatapp.message_activity.adapter.MessageAdapter
import com.example.android.kotlinchatapp.profile_photo.ProfilePhotoActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.activity_message.profile_image
import kotlinx.android.synthetic.main.activity_message.toolbar
import kotlinx.android.synthetic.main.activity_message.username
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MessageActivity : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 71
    private var storageReference: StorageReference? = null
    lateinit var progressDialog: ProgressDialog
    private var filePath: Uri? = null


    internal var firebaseUser: FirebaseUser? = null
    lateinit var reference: DatabaseReference

    lateinit var i: Intent
    lateinit var use: User


    lateinit var messageAdapter: MessageAdapter
    lateinit var mChats: MutableList<Chat>
    lateinit var currentUser: User
    lateinit var apiService: APIService
    lateinit var userId: String
    lateinit var seenListener: ValueEventListener
    var notify = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading")
        progressDialog.setCancelable(false)
        storageReference = FirebaseStorage.getInstance().reference


        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService::class.java)
        toolbar.setNavigationOnClickListener { finish() }
        i = getIntent()
        userId = i.getStringExtra("userid")


        recycle_view.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recycle_view.layoutManager = linearLayoutManager


        firebaseUser = FirebaseAuth.getInstance().currentUser

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser?.uid!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                currentUser = dataSnapshot.getValue(User::class.java)!!

            }
        })

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId)

        btn_send.setOnClickListener {
            notify = true
            val msg = text_send.text.toString()
            if (msg != "")
                sendMessage(firebaseUser!!.uid, userId, msg, "text")
            else
                Toast.makeText(
                    this@MessageActivity,
                    "you can't send empty message!",
                    Toast.LENGTH_SHORT
                ).show()
            text_send.setText("")
        }




        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                username.text = user!!.userName
                if (user.status.equals("online"))
                    userStatus.text = user.status
                else
                    userStatus.text = user.lastSeen?.let { it } ?: kotlin.run { "" }

                if (user.imageURL == "default")
                    profile_image.setImageResource(R.mipmap.ic_launcher_round)
                else
                    Glide.with(applicationContext).load(user.imageURL).into(profile_image)

                readMessage(firebaseUser!!.uid, userId, user.imageURL!!)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
        seenMessage(userId)
        send_image.setOnClickListener { launchGallery() }
    }

    fun seenMessage(id: String) {
        reference = FirebaseDatabase.getInstance().getReference("Chats")
        seenListener = reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat?.reciever.equals(firebaseUser?.uid) && chat?.sender.equals(userId)) {
                        snapshot.ref.child("isseen").setValue(true)
                    }
                }
            }

        })
    }

    fun sendMessage(sender: String, reciever: String, message: String, type: String) {
        val reference = FirebaseDatabase.getInstance().reference
        val hashMap = HashMap<String, Any>()
        hashMap["sender"] = sender
        hashMap["reciever"] = reciever
        hashMap["message"] = message
        hashMap["isseen"] = false
        hashMap["type"] = type

        reference.child("Chats").push().setValue(hashMap)
        val userReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)
//        reference.addValueEventListener(object:ValueEventListener{
//            override fun onCancelled(p0: DatabaseError) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val u=dataSnapshot.getValue(User::class.java)
//
//                if (notify) {
//                    sendNotification(reciever, currentUser?.userName, text_send.text.toString())
//                }
//                notify=false
//            }
//
//        })
    }

//    private fun sendNotification(reciever: String, userName: String?, toString: String) {
//        val tokens=FirebaseDatabase.getInstance().getReference("Tokens")
//        val query=tokens.orderByKey().equalTo(reciever)
//        query.addValueEventListener(object :ValueEventListener{
//            override fun onCancelled(p0: DatabaseError) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val token=dataSnapshot.getValue(Token::class.java)
//                val data= Data(firebaseUser?.uid!!,R.mipmap.ic_launcher,userName!!+": "+text_send.text.toString(),"New Message",userId)
//                val sender= Sender(data,token?.token!!)
//                apiService.sendNotification(sender)
//                    .enqueue(object:Callback<MyResponse>{
//                        override fun onFailure(call: Call<MyResponse>, t: Throwable) {
//                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                        }
//
//                        override fun onResponse(call: Call<MyResponse>, response: Response<MyResponse>) {
//                            if(response.code()==200){
//                                if (response.body()?.success!=1)
//                                    Toast.makeText(applicationContext,"Failed",Toast.LENGTH_LONG).show()
//
//                            }
//                        }
//
//                    })
//            }
//
//        })
//    }

    private fun readMessage(myid: String, userid: String, userImgURL: String) {
        mChats = ArrayList()
        reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mChats.clear()
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat!!.reciever == myid && chat.sender == userid || chat.reciever == userid && chat.sender == myid) {
                        mChats.add(chat)
                    }
                    messageAdapter =
                        MessageAdapter(
                            this@MessageActivity,
                            mChats,
                            userImgURL,
                            currentUser
                        )
                    messageAdapter.onClickItem=object :OnClickItem{
                        override fun onClick(path: String, image: View) {
                            navigateTOShowImageActivity(path,image)
                        }

                    }
                    recycle_view.adapter = messageAdapter
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    private fun navigateTOShowImageActivity(path: String,image:View) {
        val intent=Intent(this,ProfilePhotoActivity::class.java)
        intent.putExtra(ProfilePhotoActivity.profilePhoto,path)
        intent.putExtra(ProfilePhotoActivity.isProfileImage,false)
        intent.putExtra(ProfilePhotoActivity.transitionName,getString(R.string.message_photo))
        val option =ActivityOptionsCompat.makeSceneTransitionAnimation(this,image,getString(R.string.message_photo))
        startActivity(intent,option.toBundle())
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null)
                return
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, filePath)

                uploadImage()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage() {
        progressDialog.show()

        if (filePath != null) {
            val ref = storageReference?.child("  uploads/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)

            val urlTask =
                uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            progressDialog.hide()
                        }
                    }
                    return@Continuation ref.downloadUrl

                })?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        val downloadUri = it.result
                        progressDialog.hide()
                        sendMessage(firebaseUser!!.uid, userId, downloadUri.toString(), "image")

                    } else {
                        Toasty.error(applicationContext, "failed to upload", Toasty.LENGTH_LONG)
                            .show()
                        progressDialog.hide()
                    }
                }?.addOnFailureListener {
                    Toasty.error(applicationContext!!, it.message.toString(), Toasty.LENGTH_LONG)
                        .show()
                    progressDialog.hide()
                }
        } else {
            Toast.makeText(applicationContext!!, "file path = null", Toast.LENGTH_LONG).show()
            progressDialog.hide()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setStatus(status: String) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
        val hash: HashMap<String, String> = HashMap<String, String>()
        val date = LocalDateTime.now()
        val formater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val formatedDate = date.format(formater)
        hash.put("status", status)
        reference.child("status").setValue(status)
        reference.child("lastSeen").setValue(formatedDate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        setStatus("online")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPause() {
        super.onPause()
        reference.removeEventListener(seenListener)
        setStatus("offline")
    }
}
