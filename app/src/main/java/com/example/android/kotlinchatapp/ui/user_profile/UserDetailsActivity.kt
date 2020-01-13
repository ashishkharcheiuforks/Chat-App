package com.example.android.kotlinchatapp.ui.user_profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.android.kotlinchatapp.R
import com.example.android.kotlinchatapp.databinding.ActivityUserDetailsBinding
import com.example.android.kotlinchatapp.ui.model.Chat
import com.example.android.kotlinchatapp.ui.profile_photo.ProfilePhotoActivity
import com.example.android.kotlinchatapp.ui.user_profile.adapter.ImagesAdapter
import kotlinx.android.synthetic.main.activity_user_details.*

class UserDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityUserDetailsBinding
    lateinit var userProfileViewModel: UserProfileViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_details)
        binding.lifecycleOwner = this
        userProfileViewModel = ViewModelProviders.of(this).get(UserProfileViewModel::class.java)
        toolbar!!.title=""
        binding.vm = userProfileViewModel

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.back_arrow)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        userProfileViewModel.userId = intent.getStringExtra(userId)!!
        userProfileViewModel.getUserData()
        userProfileViewModel.getUserImages()
        val images = ArrayList<Chat>()
        val adapter = ImagesAdapter(images)
        binding.imagesRv.adapter = adapter
        userProfileViewModel.images.observe(this, Observer {
            if (it.isNotEmpty()) {
                binding.media.visibility = View.VISIBLE
                images.clear()
                images.addAll(it)
                adapter.notifyDataSetChanged()
            }
        })
        adapter.onItemClick = object : OnItemClick {
            override fun onClick(path: String, image: View) {
                navigateTOShowImageActivity(path, image,path)
            }

        }
        binding.userImage.setOnClickListener {
            userProfileViewModel.user.value?.let {
                navigateTOShowImageActivity(
                    it.imageURL!!,
                    binding.userImage,
                    getString(R.string.profile_photo)
                )
            }
        }
    }
    private fun navigateTOShowImageActivity(path: String, image: View,transitionName:String) {
        val intent = Intent(this, ProfilePhotoActivity::class.java)
        intent.putExtra(ProfilePhotoActivity.profilePhoto, path)
        intent.putExtra(ProfilePhotoActivity.isProfileImage, false)
        intent.putExtra(ProfilePhotoActivity.transitionName, transitionName)
        val option = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            image,
            getString(R.string.message_photo)
        )
        startActivity(intent, option.toBundle())
    }

    companion object {
        val userId = "userId"
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
