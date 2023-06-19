package com.karsatech.storyapp.ui.story.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.karsatech.storyapp.data.remote.response.DetailStory
import com.karsatech.storyapp.databinding.ActivityDetailStoryBinding
import com.karsatech.storyapp.utils.withDateFormat

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        setupData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupData() {
        val data = intent.getParcelableExtra<DetailStory>("STORY") as DetailStory
        Glide.with(applicationContext)
            .load(data.photoUrl)
            .fitCenter()
            .into(binding.profileImageView)
        binding.nameTextView.text = data.name
        binding.descTextView.text = data.description
        binding.dateTextView.text = data.createdAt.withDateFormat()

        supportActionBar?.title = data.name
    }
}