package com.karsatech.storyapp.ui.welcome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.karsatech.storyapp.R
import com.karsatech.storyapp.databinding.ActivityWelcomeBinding
import com.karsatech.storyapp.ui.auth.LoginActivity
import com.karsatech.storyapp.ui.auth.RegisterActivity

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        supportActionBar?.hide()
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

//        textWelcomeAnimate()
    }

//    private fun textWelcomeAnimate() {
//        val textView = binding.tvWelcome
//        val textToType = resources.getString(R.string.welcome)
//        val delayMillis = 200L
//        val handler = Handler(Looper.getMainLooper())
//        var index = 0
//
//        handler.postDelayed(object : Runnable {
//            override fun run() {
//                if (index <= textToType.length) {
//                    textView.text = textToType.subSequence(0, index)
//                    index++
//                    handler.postDelayed(this, delayMillis)
//                }
//            }
//        }, delayMillis)
//    }
}