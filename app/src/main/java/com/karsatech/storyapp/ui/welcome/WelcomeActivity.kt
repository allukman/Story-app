package com.karsatech.storyapp.ui.welcome

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.karsatech.storyapp.R
import com.karsatech.storyapp.databinding.ActivityWelcomeBinding
import com.karsatech.storyapp.ui.auth.login.LoginActivity
import com.karsatech.storyapp.ui.auth.register.RegisterActivity
import com.karsatech.storyapp.ui.story.main.MainActivity
import com.karsatech.storyapp.utils.UserPreference
import com.karsatech.storyapp.utils.Views.onCLick
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var userPreference: UserPreference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        supportActionBar?.hide()
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(application.dataStore)

        playAnimation()
        onClick()

    }

    private fun onClick() {
        binding.btnLogin.onCLick {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.tvRegister.onCLick {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivWelcome, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            val token = userPreference.getToken().first()

            if (token.isNotEmpty()) {
                val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                binding.container.visibility = View.VISIBLE
                textWelcomeAnimate()
            }
        }

    }

    private fun textWelcomeAnimate() {
        val textView = binding.tvAppName
        val textToType = resources.getString(R.string.app_name)
        val delayMillis = 200L
        val handler = Handler(Looper.getMainLooper())
        var index = 0

        handler.postDelayed(object : Runnable {
            override fun run() {
                if (index <= textToType.length) {
                    textView.text = textToType.subSequence(0, index)
                    index++
                    handler.postDelayed(this, delayMillis)
                }
            }
        }, delayMillis)
    }
}