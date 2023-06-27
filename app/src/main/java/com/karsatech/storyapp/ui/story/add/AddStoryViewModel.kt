package com.karsatech.storyapp.ui.story.add

import androidx.lifecycle.ViewModel
import com.karsatech.storyapp.data.remote.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun addStory(file: MultipartBody.Part, description: RequestBody, lat: RequestBody? = null, lon: RequestBody? = null) = storyRepository.addStory(file, description, lat, lon, TAG)

    companion object {
        private const val TAG = "AddStoryViewModel"
    }
}