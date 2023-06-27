package com.karsatech.storyapp.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karsatech.storyapp.data.remote.StoryRepository
import com.karsatech.storyapp.data.remote.response.LoginResult
import com.karsatech.storyapp.utils.UserPreference
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: UserPreference, private val storyRepository: StoryRepository) : ViewModel(){

    fun login(email: String, password: String) = storyRepository.login(email, password)

    fun saveUser(user: LoginResult) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }

    fun loginUser() {
        viewModelScope.launch {
            pref.login()
        }
    }
}