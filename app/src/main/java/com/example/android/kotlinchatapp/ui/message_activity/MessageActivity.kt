package com.example.android.kotlinchatapp.ui.message_activity

import android.annotation.SuppressLint
import retrofit2.*
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Website.URL
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.example.android.kotlinchatapp.ui.model.Chat
import com.example.android.kotlinchatapp.ui.model.User
import com.example.android.kotlinchatapp.R
import com.example.android.kotlinchatapp.ui.fragments.APIService
import com.example.android.kotlinchatapp.ui.notification.*
import com.example.android.kotlinchatapp.ui.message_activity.adapter.MessageAdapter
import com.example.android.kotlinchatapp.ui.message_activity.model.BackgroundImageModel
import com.example.android.kotlinchatapp.ui.profile_photo.ProfilePhotoActivity
import com.example.android.kotlinchatapp.ui.user_profile.UserDetailsActivity
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
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import com.android.volley.toolbox.Volley
import com.example.android.kotlinchatapp.utils.FormatDate
import com.example.android.kotlinchatapp.utils.FormatDate.getDate
import com.example.android.myapplication.Adapter.UserAdapter

class MessageActivity : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 71
    private val PICK_IMAGE_REQUEST_FOR_BACKGROUND = 70
    private var storageReference: StorageReference? = null
    lateinit var progressDialog: ProgressDialog
    private var filePath: Uri? = null

    lateinit var requestQueue: RequestQueue
    internal var firebaseUser: FirebaseUser? = null
    lateinit var reference: DatabaseReference
    var referenceBackground: DatabaseReference? = null
    var typingReference: DatabaseReference? = null
    lateinit var i: Intent
    lateinit var use: User
    var user: User? = null

    lateinit var messageAdapter: MessageAdapter
    lateinit var mChats: MutableList<Chat>
    lateinit var currentUser: User
    lateinit var apiService: APIService
    lateinit var userId: String
    lateinit var seenListener: ValueEventListener
    var notify = false
    lateinit var seenMessageReference:DatabaseReference
    @RequiresApi(Build.VERSION_CODES.O)
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
        profile_image.setOnClickListener { navigateToUserProfile() }
        data_container.setOnClickListener { navigateToUserProfile() }
        requestQueue = Volley.newRequestQueue(this)
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService::class.java)
        toolbar.setNavigationOnClickListener { finish() }
        i = getIntent()
        userId = i.getStringExtra("userid")


        recycle_view.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recycle_view.layoutManager = linearLayoutManager
        recycle_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                messageAdapter.scrollDirection =
                    if (dy > 0) MessageAdapter.ScrollDirection.DOWN else MessageAdapter.ScrollDirection.UP
            }
        })

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



        referenceBackground =
            FirebaseDatabase.getInstance().getReference("Backgrounds").child(firebaseUser!!.uid)

        val userImageReference = FirebaseDatabase.getInstance().getReference("Users")
            .child(userId).child("imageURL")
        userImageReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                toolbar_progress_bar.visibility = GONE
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userImage = dataSnapshot.getValue(String::class.java)
                Glide.with(applicationContext).load(userImage)
                    .error(R.drawable.profile_default_icon).into(profile_image)
                toolbar_progress_bar.visibility = GONE

                readMessage(firebaseUser!!.uid, userId, userImage!!)
            }

        })
        reference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user = dataSnapshot.getValue(User::class.java)
                username.text = user!!.userName
                greeting_message.text = "Say something to ${user!!.userName ?: ""}"
                if (user!!.status.equals("online"))
                    userStatus.text = user!!.status
                else
                    userStatus.text = user!!.lastSeen?.let { getDate(it) } ?: kotlin.run { "" }

//                if (user.imageURL == "default")
//                    profile_image.setImageResource(R.mipmap.ic_launcher_round)
//                else
//                Glide.with(applicationContext).load(user!!.imageURL)
//                    .error(R.drawable.profile_default_icon).into(profile_image)
//
//                readMessage(firebaseUser!!.uid, userId, user!!.imageURL!!)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
        referenceBackground!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
//                for (snapshot in p0.children) {
                for (s in p0.children) {
                    var imageModel = s.getValue(BackgroundImageModel::class.java)
                    imageModel?.let {
                        if (imageModel.myid.equals(firebaseUser!!.uid) && imageModel.userid.equals(
                                userId
                            )
                        ) {

                            Glide.with(applicationContext).load(imageModel.image.let { it } ?: "")
                                .into(backgroundImage)
                        }

//                        }
                    }
                }
            }

        })
        seenMessage(userId)
        send_image.setOnClickListener { launchGallery(PICK_IMAGE_REQUEST) }
        typingReference =
            FirebaseDatabase.getInstance().getReference("Users").child(userId).child("typingTo")
        typingReference!!.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val typingTo = dataSnapshot.getValue(String::class.java)
                if (typingTo.equals(firebaseUser!!.uid)){
                    if (dotLoader.visibility!= VISIBLE){
                        dotLoader.visibility= VISIBLE
                    }
                }
                else{
                    if (dotLoader.visibility!= GONE){
                        dotLoader.visibility= GONE
                    }
                }
            }

        })
        text_send.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (s!!.isEmpty()) {
                    typingReference =
                        FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
                    typingReference!!.child("typingTo").setValue("noOne")
                }
                else{
                    typingReference =
                        FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
                    typingReference!!.child("typingTo").setValue(userId)
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isEmpty()) {
                    typingReference =
                        FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
                    typingReference!!.child("typingTo").setValue("noOne")
                }
                else{
                    typingReference =
                        FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
                    typingReference!!.child("typingTo").setValue(userId)
                }
            }

        })

    }

    private fun navigateToUserProfile() {
        val i = Intent(this, UserDetailsActivity::class.java)
        i.putExtra(UserDetailsActivity.userId, userId)
        val p1: androidx.core.util.Pair<View, String> =
            androidx.core.util.Pair(profile_image, getString(R.string.profile_photo))
        val p2: androidx.core.util.Pair<View, String> =
            androidx.core.util.Pair(username, getString(R.string.userName))
        val p3: androidx.core.util.Pair<View, String> =
            androidx.core.util.Pair(userStatus, getString(R.string.userStatus))
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2, p3)
        startActivity(i, options.toBundle())
    }

    fun seenMessage(id: String) {
        seenMessageReference = FirebaseDatabase.getInstance().getReference("Chats")
        seenListener = seenMessageReference.addValueEventListener(object : ValueEventListener {
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage(sender: String, reciever: String, message: String, type: String) {
        val date = LocalDateTime.now()
        val formater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a")
        val formatedDate = date.format(formater)
        val reference = FirebaseDatabase.getInstance().reference
        val hashMap = HashMap<String, Any>()
        hashMap["sender"] = sender
        hashMap["reciever"] = reciever
        hashMap["message"] = message
        hashMap["isseen"] = false
        hashMap["type"] = type
        hashMap["date"] = formatedDate

        reference.child("Chats").push().setValue(hashMap)
        val currentDate = FormatDate.currentdateinDdMmYyyyHhMmA()
        var chatListReference =
            FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser!!.uid)
                .child(userId)
        chatListReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                chatListReference.child("date").setValue(currentDate)
                if (!dataSnapshot.exists()) {
                    chatListReference.child("id").setValue(userId)
                }

            }

        })

        var chatListReference2 =
            FirebaseDatabase.getInstance().getReference("ChatList").child(userId)
                .child(firebaseUser!!.uid)
        chatListReference2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                chatListReference2.child("date").setValue(currentDate)
                if (!dataSnapshot.exists()) {
                    chatListReference2.child("id").setValue(firebaseUser!!.uid)
                }

            }

        })

        val userReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val u = dataSnapshot.getValue(User::class.java)

                if (notify) {
                    sendNotification(reciever, currentUser.userName, message)
                }
                notify = false
            }

        })
    }

    private fun sendNotification(reciever: String, userName: String?, message: String) {
        val tokens = FirebaseDatabase.getInstance().getReference("Tokens")
        val query = tokens.orderByKey().equalTo(reciever)
        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val token = snapshot.getValue(Token::class.java)
                    val data = Data(
                        firebaseUser?.uid!!,
                        R.mipmap.ic_launcher,
                        userName!! + ": " + message,
                        "New Message",
                        userId
                    )
                    val sender = Sender(data, token?.token!!)
                    apiService.sendNotification(sender)
                        .enqueue(object : Callback<MyResponse> {
                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                            }

                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
                                if (response.code() == 200) {
                                    if (response.body()?.success != 1)
                                        Toast.makeText(
                                            applicationContext,
                                            "Failed",
                                            Toast.LENGTH_LONG
                                        ).show()

                                }
                            }

                        })
                }
            }

        })
    }


    private fun readMessage(myid: String, userid: String, userImgURL: String) {
        mChats = ArrayList()
        val messagesReference = FirebaseDatabase.getInstance().getReference("Chats")
        messagesReference.addValueEventListener(object : ValueEventListener {
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
                    messageAdapter.onClickItem = object : OnClickItem {
                        override fun onClick(path: String, image: View) {
                            navigateTOShowImageActivity(path, image)
                        }

                    }
                    greeting_container.visibility = if (mChats.isEmpty()) VISIBLE else GONE
                    progress_bar.visibility = GONE

                    recycle_view.adapter = messageAdapter
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                progress_bar.visibility = GONE

            }
        })

    }

    private fun navigateTOShowImageActivity(path: String, image: View) {
        val intent = Intent(this, ProfilePhotoActivity::class.java)
        intent.putExtra(ProfilePhotoActivity.profilePhoto, path)
        intent.putExtra(ProfilePhotoActivity.isProfileImage, false)
        intent.putExtra(ProfilePhotoActivity.transitionName, path)
        val option = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            image,
            getString(R.string.message_photo)
        )
        startActivity(intent, option.toBundle())
    }

    private fun launchGallery(FLAG: Int) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), FLAG)

    }

    @RequiresApi(Build.VERSION_CODES.O)
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
        } else if (requestCode == PICK_IMAGE_REQUEST_FOR_BACKGROUND && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null)
                return
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, filePath)

                uploadBackGroundImage()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadBackGroundImage() {
        progressDialog.show()
        if (filePath != null) {
            val ref = storageReference?.child("  uploads/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)

            val urlTask =
                uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            progressDialog.hide()
                            throw it

                        }
                    }
                    return@Continuation ref.downloadUrl

                })?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        val downloadUri = it.result
                        progressDialog.hide()
                        sendBackGroundImage(firebaseUser!!.uid, userId, downloadUri.toString())

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

    private fun sendBackGroundImage(myId: String, userId: String, image: String) {
        val hashMap = HashMap<String, Any>()
        hashMap["myid"] = myId
        hashMap["userid"] = userId
        hashMap["image"] = image


        referenceBackground!!.child(userId).setValue(hashMap)
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
                        notify = true
                        sendNotification(userId, currentUser.userName, "Image")

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
//        setStatus("online")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPause() {
        super.onPause()
        seenMessageReference.removeEventListener(seenListener)
        typingReference =
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
        typingReference!!.child("typingTo").setValue("noOne")
//        setStatus("offline")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.message_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.changeBackGround -> {
                launchGallery(PICK_IMAGE_REQUEST_FOR_BACKGROUND)

                return true
            }
            R.id.clearBackGround -> {
                clearBackground()
                return true
            }
        }
        return false

    }

    private fun clearBackground() {
        referenceBackground!!.child(userId).removeValue().addOnCompleteListener {
            backgroundImage.setImageBitmap(null)
            Toasty.success(this, "deleted", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toasty.error(this, "error occurred", Toast.LENGTH_SHORT).show()

        }

    }
}
