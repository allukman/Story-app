package com.karsatech.storyapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.karsatech.storyapp.data.remote.response.DetailStory
import com.karsatech.storyapp.databinding.ItemStoryBinding
import com.karsatech.storyapp.ui.story.detail.DetailStoryActivity
import com.karsatech.storyapp.utils.loadImage
import com.karsatech.storyapp.utils.withCurrentDateFormat

class StoryAdapter : ListAdapter<DetailStory, StoryAdapter.RecyclerViewHolder>(DIFF_CALLBACK){

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DetailStory>() {
            override fun areItemsTheSame(
                oldItem: DetailStory,
                newItem: DetailStory
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: DetailStory,
                newItem: DetailStory
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class RecyclerViewHolder(private val bind: ItemStoryBinding) :
        RecyclerView.ViewHolder(bind.root) {
        fun bind(data: DetailStory) {
            bind.nameTextView.text = data.name
            bind.descTextView.text = data.description
            bind.dateTextView.text = data.createdAt.withCurrentDateFormat()
            bind.profileImageView.loadImage(data.photoUrl, itemView.context, bind.progressBar)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                intent.putExtra("STORY", data)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}