package com.karsatech.storyapp.ui.story.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.karsatech.storyapp.data.remote.StoryRepository
import com.karsatech.storyapp.data.remote.response.DetailStory
import com.karsatech.storyapp.data.remote.response.LoginResult
import com.karsatech.storyapp.utils.UserPreference
import kotlinx.coroutines.launch

class MainViewModel(private val pref: UserPreference, storyRepository: StoryRepository) :
    ViewModel() {

    val stories: LiveData<PagingData<DetailStory>> =
        storyRepository.getStories().cachedIn(viewModelScope)

    fun getUser(): LiveData<LoginResult> {
        return pref.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.clearData()
        }
    }

}