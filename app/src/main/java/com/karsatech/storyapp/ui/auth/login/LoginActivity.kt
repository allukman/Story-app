package com.karsatech.storyapp.ui.auth.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.karsatech.storyapp.R
import com.karsatech.storyapp.data.remote.retrofit.ApiConfig
import com.karsatech.storyapp.data.remote.retrofit.ApiService
import com.karsatech.storyapp.databinding.ActivityLoginBinding
import com.karsatech.storyapp.ui.auth.register.RegisterActivity
import com.karsatech.storyapp.ui.auth.register.RegisterViewModel
import com.karsatech.storyapp.ui.story.MainActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var service: ApiService

    private val loginViewModel by viewModels<LoginViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        service = ApiConfig.getApiClient(this)!!.create(ApiService::class.java)

        setOnClick()
        settingViewModel()
        subscribeLoginViewModel()

    }

    private fun showLoading(loading: Boolean) {
        binding.btnLogin.visibility = if (loading) View.GONE else View.VISIBLE
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.INVISIBLE
    }

    private fun subscribeLoginViewModel() {
        loginViewModel.login.observe(this) { data ->
            if (!data.error) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            Log.d(TAG, "Sampe kesini : $data")
            Toast.makeText(this, data.message, Toast.LENGTH_SHORT).show()
        }

        loginViewModel.isLoading.observe(this) { loading ->
            showLoading(loading)
        }

        loginViewModel.mError.observe(this) { error ->
            if (error.isNotEmpty()) {
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validation() {
        val emailRegex = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}".toRegex()

        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        val emailString = getString(R.string.email)
        val passwordString = getString(R.string.password)

        when {
            email.isEmpty() -> {
                binding.etLayoutEmail.error = getString(R.string.error_empty_value, emailString)
                return
            }

            !email.matches(emailRegex) -> {
                binding.etLayoutEmail.error = getString(R.string.error_email_format)
                return
            }

            password.isEmpty() -> {
                binding.etLayoutPassword.error =
                    getString(R.string.error_empty_value, passwordString)
                return
            }

            password.length < 8 -> {
                binding.etLayoutPassword.error = getString(R.string.error_length, passwordString, 8)
                return
            }

            else -> {
                binding.etLayoutEmail.error = null
                binding.etLayoutPassword.error = null

                loginViewModel.loginUser(email, password)
            }
        }
    }

    private fun settingViewModel() {
        loginViewModel.apply {
            setService(service)
        }
    }

    private fun setOnClick() {
        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnLogin.setOnClickListener {
            validation()
        }
    }

    companion object {
        private var TAG = LoginActivity::class.java.simpleName
    }
}