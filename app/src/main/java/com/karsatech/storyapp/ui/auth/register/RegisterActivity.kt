package com.karsatech.storyapp.ui.auth.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.karsatech.storyapp.data.remote.retrofit.ApiConfig
import com.karsatech.storyapp.data.remote.retrofit.ApiService
import com.karsatech.storyapp.databinding.ActivityRegisterBinding
import com.karsatech.storyapp.ui.auth.login.LoginActivity
import com.karsatech.storyapp.utils.AppUtils
import com.karsatech.storyapp.utils.Validator
import com.karsatech.storyapp.utils.Views.onCLick
import com.karsatech.storyapp.utils.Views.onTextChanged

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var service: ApiService
    private lateinit var registerValidator: Validator.Register

    private val registerViewModel by viewModels<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        service = ApiConfig.getApiClient(this)!!.create(ApiService::class.java)
        registerValidator = Validator.Register()

        playAnimation()
        setupViews()
        setOnClick()
        settingViewModel()
        subscribeRegisterViewModel()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val name = ObjectAnimator.ofFloat(binding.etLayoutName, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.etLayoutEmail, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.etLayoutPassword, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(name, email, password, register)
            start()
        }
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

    private fun setupViews() {

        binding.apply {

            nameEditText.onTextChanged {
                AppUtils.InitTextWatcher {
                    registerValidator.name = it.isNotEmpty() && it.length >= 6
                    btnRegister.isEnabled = registerValidator.filled()
                }
            }

            emailEditText.onTextChanged {
                AppUtils.InitTextWatcher {
                    registerValidator.email = it.isNotEmpty() && AppUtils.isEmailValid(it)
                    btnRegister.isEnabled = registerValidator.filled()
                }
            }

            passwordEditText.onTextChanged {
                AppUtils.InitTextWatcher {
                    registerValidator.password = it.isNotEmpty() && it.length >= 6
                    btnRegister.isEnabled = registerValidator.filled()
                }
            }

            btnRegister.onCLick {
                val name = nameEditText.text.toString()
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()

                registerViewModel.registerUser(name, email, password)
            }
        }
    }

    companion object {
        private var TAG = RegisterActivity::class.java.simpleName
    }

}