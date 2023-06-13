package com.karsatech.storyapp.ui.welcome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.karsatech.storyapp.R
import com.karsatech.storyapp.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        supportActionBar?.hide()
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textWelcomeAnimate()
    }

    private fun textWelcomeAnimate() {
        val textView = binding.tvWelcome
        val textToType = resources.getString(R.string.welcome)
        val delayMillis = 100L
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