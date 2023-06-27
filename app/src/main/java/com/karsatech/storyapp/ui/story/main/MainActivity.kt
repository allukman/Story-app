package com.karsatech.storyapp.ui.story.main

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.karsatech.storyapp.R
import com.karsatech.storyapp.databinding.ActivityMainBinding
import com.karsatech.storyapp.ui.ViewModelFactory
import com.karsatech.storyapp.ui.adapter.LoadingStateAdapter
import com.karsatech.storyapp.ui.adapter.StoryAdapter
import com.karsatech.storyapp.ui.map.MapsActivity
import com.karsatech.storyapp.ui.story.add.AddStoryActivity
import com.karsatech.storyapp.ui.welcome.WelcomeActivity
import com.karsatech.storyapp.utils.AppUtils
import com.karsatech.storyapp.utils.UserPreference

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: StoryAdapter
    private val viewModelFactory: ViewModelProvider.Factory by lazy {
        ViewModelFactory(UserPreference.getInstance(application.dataStore),this)
    }

    private val mainViewModel: MainViewModel by viewModels { viewModelFactory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subscribeMainViewModel()
        setOnClick()
        setupAdapter()
    }

    private fun subscribeMainViewModel() {
        mainViewModel.stories.observe(this) { stories ->
            if (stories != null) {
                adapter.submitData(lifecycle,stories)
            }
        }

        mainViewModel.getUser().observe(this) { user ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                binding.tvGreeting.text = AppUtils.generateGreeting(this)
            } else {
                binding.tvGreeting.text = getString(R.string.hello)
            }
            binding.tvName.text = user.name
        }
    }

    private fun setupAdapter() {
        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvStory.layoutManager = layoutManager

        adapter = StoryAdapter()

        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
    }

    private fun setOnClick() {
        binding.fab.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

        binding.btnSetting.setOnClickListener {
            showSortingPopMenu()
        }

        binding.btnMap.setOnClickListener {
            startActivity(Intent(this@MainActivity, MapsActivity::class.java))
        }
    }

    private fun showSortingPopMenu() {
        val view = binding.btnSetting

        PopupMenu(this, view).run {
            menuInflater.inflate(R.menu.option_menu, menu)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.logout -> {
                        mainViewModel.logout()
                        intentWelcome()
                        true
                    }

                    R.id.map -> {
                        startActivity(Intent(this@MainActivity, MapsActivity::class.java))
                        true
                    }

                    R.id.language -> {
                        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                        true
                    }

                    else -> false
                }
            }
            show()
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