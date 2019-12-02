package com.example.android.kotlinchatapp.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.android.kotlinchatapp.Model.User
import com.example.android.kotlinchatapp.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    lateinit var firebaseUser:FirebaseUser
    lateinit var reference: DatabaseReference
     lateinit var user:User

    private val TAG = "Service"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        firebaseUser=FirebaseAuth.getInstance().currentUser!!
//        Toast.makeText(this,firebaseUser?.displayName,Toast.LENGTH_LONG).show()
//        user_name.text=firebaseUser?.displayName

        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        Log.e("idd",firebaseUser.uid)
        reference.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user=dataSnapshot.getValue(User::class.java)!!
                user_name.text=user?.userName
                if(user?.imageURL=="default")
                    profile_image.setImageResource(R.mipmap.ic_launcher_round)
                else {
                    Glide.with(this@MainActivity).load(user!!.imageURL).into(profile_image)
                }
            }

        })
        profile_image.setOnClickListener { launchGallery() }

        val intent = intent
        val message = intent.getStringExtra("message")
        if(!message.isNullOrEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("Notification")
                .setMessage(message)
                .setPositiveButton("Ok", { dialog, which -> }).show()
        }

    }





    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==PICK_IMAGE_REQUEST&&resultCode==Activity.RESULT_OK){
            if (data==null||data.data==null)
                return
            filePath=data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                profile_image.setImageBitmap(bitmap)

                uploadImage()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage() {
        if (filePath!=null){
            val ref =storageReference?.child("  uploads/"+UUID.randomUUID().toString())
            val uploadTask=ref?.putFile(filePath!!)

            val urlTask=uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> {task->
                if (!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl

            })?.addOnCompleteListener{
                if (it.isSuccessful){
                    val downloadUri=it.result
                    updateProfile(downloadUri.toString())

                }
                else{
                    Toast.makeText(this,"failed to upload",Toast.LENGTH_LONG).show()
                }
            }?.addOnFailureListener {
                Toast.makeText(this,it.message+"ddd",Toast.LENGTH_LONG).show()
            }
        }
        else{
            Toast.makeText(this,"file path = null",Toast.LENGTH_LONG).show()
        }
    }

    private fun updateProfile(path: String) {
        user?.imageURL=path.toString()
        Log.e("userim",user.imageURL.toString())
        val hashMap=HashMap<String,String>()
        hashMap?.put("id",user.id)
        hashMap?.put("userName",user.userName)

        hashMap?.put("imageURL",path)
        //val hash=user.to
        reference.child("imageURL").setValue(path )
            .addOnCompleteListener(OnCompleteListener {
                if (it.isSuccessful)
                    Toast.makeText(this,"saved",Toast.LENGTH_LONG).show()
            }).addOnFailureListener {
                Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
            }
    }

//    private fun addUploadRecordToDb(s: String) {
//
//    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))

                return true
            }
        }
        return false

    }



private fun initView() {
    //This method will use for fetching Token
    Thread(Runnable {
        try {
            Log.i(TAG, FirebaseInstanceId.getInstance().getToken(getString(R.string.SENDER_ID), "FCM"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }).start()
}


}
