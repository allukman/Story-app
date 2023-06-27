package com.karsatech.storyapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.karsatech.storyapp.di.Injection
import com.karsatech.storyapp.ui.auth.login.LoginViewModel
import com.karsatech.storyapp.ui.auth.register.RegisterViewModel
import com.karsatech.storyapp.ui.story.add.AddStoryViewModel
import com.karsatech.storyapp.ui.story.main.MainViewModel
import com.karsatech.storyapp.utils.UserPreference

class ViewModelFactory(private val pref: UserPreference, private val context: Context) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref, Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref, Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(pref, Injection.provideRepository(context)) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}