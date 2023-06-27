package com.karsatech.storyapp.ui.auth.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.karsatech.storyapp.data.remote.Result
import com.karsatech.storyapp.databinding.ActivityRegisterBinding
import com.karsatech.storyapp.ui.ViewModelFactory
import com.karsatech.storyapp.ui.auth.login.LoginActivity
import com.karsatech.storyapp.utils.AppUtils
import com.karsatech.storyapp.utils.UserPreference
import com.karsatech.storyapp.utils.Validator
import com.karsatech.storyapp.utils.Views.onCLick
import com.karsatech.storyapp.utils.Views.onTextChanged

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerValidator: Validator.Register

    private val viewModelFactory: ViewModelProvider.Factory by lazy {
        ViewModelFactory(UserPreference.getInstance(application.dataStore), this)
    }

    private val registerViewModel: RegisterViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerValidator = Validator.Register()

        playAnimation()
        setupViews()
        setOnClick()

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

    private fun showLoading(loading: Boolean) {
        binding.btnRegister.isVisible = !loading
        binding.progressBar.isVisible = loading
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
                    registerValidator.password = it.isNotEmpty() && it.length >= 8
                    btnRegister.isEnabled = registerValidator.filled()
                }
            }

            btnRegister.onCLick {
                val name = nameEditText.text.toString()
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()

                register(name, email, password)
            }
        }
    }

    private fun register(name: String, email: String, password: String) {
        registerViewModel.register(name, email, password).observe(this) { result ->
            if (result != null) {
                when(result) {
                    is Result.Success -> {
                        showLoading(false)
                        intentLogin()
                        Toast.makeText(this, result.data.message, Toast.LENGTH_SHORT).show()
                    }
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(this, result.error, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    companion object {
        private var TAG = RegisterActivity::class.java.simpleName
    }

}