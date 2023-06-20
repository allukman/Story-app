package com.karsatech.storyapp.ui.story.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.karsatech.storyapp.data.remote.response.DetailStory
import com.karsatech.storyapp.databinding.ActivityDetailImageBinding
import com.karsatech.storyapp.utils.Views.onCLick

class DetailImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityDetailImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupData()
        binding.btnBack.onCLick {
            onBackPressed()
        }
    }

    private fun setupData() {
        val data = intent.getParcelableExtra<DetailStory>("STORY") as DetailStory
        Glide.with(applicationContext)
            .load(data.photoUrl)
            .fitCenter()
            .into(binding.ivDetailImage)
    }


}