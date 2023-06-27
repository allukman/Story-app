package com.karsatech.storyapp.ui.auth.register

import androidx.lifecycle.ViewModel
import com.karsatech.storyapp.data.remote.StoryRepository
import com.karsatech.storyapp.utils.UserPreference

class RegisterViewModel(private val pref: UserPreference, private val storyRepository: StoryRepository) : ViewModel() {

    fun register(name: String, email: String, password: String) = storyRepository.register(name, email, password)

}