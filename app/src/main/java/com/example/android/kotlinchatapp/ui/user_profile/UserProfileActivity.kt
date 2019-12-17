package com.example.android.kotlinchatapp.ui.user_profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.android.kotlinchatapp.R
import com.example.android.kotlinchatapp.databinding.ActivityUserProfileBinding
import com.example.android.kotlinchatapp.ui.model.Chat
import com.example.android.kotlinchatapp.ui.profile_photo.ProfilePhotoActivity
import com.example.android.kotlinchatapp.ui.user_profile.adapter.ImagesAdapter
import com.example.android.kotlinchatapp.utils.BindingUtils

class UserProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityUserProfileBinding
    lateinit var userProfileViewModel: UserProfileViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile)
        binding.lifecycleOwner = this
        userProfileViewModel = ViewModelProviders.of(this).get(UserProfileViewModel::class.java)
        binding.vm = userProfileViewModel
        binding.backArrow.setOnClickListener { onBackPressed() }
        userProfileViewModel.userId = intent.getStringExtra(userId)
        userProfileViewModel.getUserData()
        userProfileViewModel.getUserImages()
        val images = ArrayList<Chat>()
        val adapter = ImagesAdapter(images)
        binding.imagesRv.adapter = adapter
        userProfileViewModel.images.observe(this, Observer {
            if (!it.isEmpty()) {
                binding.media.visibility = VISIBLE
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
}
