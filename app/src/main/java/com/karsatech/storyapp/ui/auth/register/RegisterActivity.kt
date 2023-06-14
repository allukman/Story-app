package com.karsatech.storyapp.ui.auth.register

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.karsatech.storyapp.R
import com.karsatech.storyapp.data.remote.retrofit.ApiConfig
import com.karsatech.storyapp.data.remote.retrofit.ApiService
import com.karsatech.storyapp.databinding.ActivityRegisterBinding
import com.karsatech.storyapp.ui.auth.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var service: ApiService

    private val registerViewModel by viewModels<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        service = ApiConfig.getApiClient(this)!!.create(ApiService::class.java)

        setOnClick()
        settingViewModel()
        subscribeRegisterViewModel()
    }

    private fun subscribeRegisterViewModel() {
        registerViewModel.register.observe(this) { data ->
            if (!data.error) {
                intentLogin()
            }
            Toast.makeText(this, data.message, Toast.LENGTH_SHORT).show()
        }

        registerViewModel.isLoading.observe(this) { loading ->
            showLoading(loading)
        }
    }

    private fun settingViewModel() {
        registerViewModel.apply {
            setService(service)
        }
    }

    private fun setOnClick() {
        binding.tvLogin.setOnClickListener {
            intentLogin()
        }

        binding.btnRegister.setOnClickListener {
            validation()
        }
    }

    private fun intentLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showLoading (loading: Boolean) {
        binding.btnRegister.visibility = if (loading) View.GONE else View.VISIBLE
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.INVISIBLE
    }

    private fun validation() {
        val emailRegex = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}".toRegex()

        val name = binding.nameEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        val nameString = getString(R.string.name)
        val emailString = getString(R.string.email)
        val passwordString = getString(R.string.password)

        when {
            name.isEmpty() -> {
                binding.etLayoutName.error = getString(R.string.error_empty_value, nameString)
                return
            }

            name.length < 6 -> {
                binding.etLayoutName.error = getString(R.string.error_length, nameString, 6)
                return
            }

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
                binding.etLayoutName.error = null
                binding.etLayoutEmail.error = null
                binding.etLayoutPassword.error = null

                Log.d(TAG, "name: $name, email: $email, password: $password")

                registerViewModel.registerUser(name, email, password)
            }
        }
    }

    companion object {
        private var TAG = RegisterActivity::class.java.simpleName
    }

}