package com.karsatech.storyapp.ui.story.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.karsatech.storyapp.R
import com.karsatech.storyapp.data.remote.response.DetailStory
import com.karsatech.storyapp.data.remote.retrofit.ApiConfig
import com.karsatech.storyapp.data.remote.retrofit.ApiService
import com.karsatech.storyapp.databinding.ActivityMainBinding
import com.karsatech.storyapp.ui.ViewModelFactory
import com.karsatech.storyapp.ui.adapter.StoryAdapter
import com.karsatech.storyapp.ui.story.add.AddStoryActivity
import com.karsatech.storyapp.ui.welcome.WelcomeActivity
import com.karsatech.storyapp.utils.UserPreference

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var service: ApiService

    private val viewModelFactory: ViewModelProvider.Factory by lazy {
        ViewModelFactory(UserPreference.getInstance(application.dataStore))
    }

    private val mainViewModel: MainViewModel by viewModels { viewModelFactory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        service = ApiConfig.getApiClient(this)!!.create(ApiService::class.java)

        setOnClick()
        settingViewModel()
        setupRecyclerView()
        subscribeMainViewModel()

    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager
    }

    private fun setGithubData(story: List<DetailStory>) {
        val adapter = StoryAdapter()
        adapter.submitList(story)
        binding.rvStory.adapter = adapter

    }

    private fun subscribeMainViewModel() {
        mainViewModel.stories.observe(this) { stories ->
            setGithubData(stories)
        }

        mainViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun showLoading(loading: Boolean) {
        binding.rvStory.isVisible = !loading
        binding.progressBar.isVisible = loading
    }

    private fun settingViewModel() {
        mainViewModel.apply {
            setService(service)
            getAllStories()
        }
    }

    private fun setOnClick() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    mainViewModel.logout()
                    intentWelcome()
                    true
                }
                R.id.language -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }
                else -> false
            }
        }

        binding.fab.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun intentWelcome() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}