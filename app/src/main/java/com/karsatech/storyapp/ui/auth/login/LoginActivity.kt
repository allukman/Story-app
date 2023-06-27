package com.karsatech.storyapp.ui.auth.login

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
import com.karsatech.storyapp.databinding.ActivityLoginBinding
import com.karsatech.storyapp.ui.ViewModelFactory
import com.karsatech.storyapp.ui.auth.register.RegisterActivity
import com.karsatech.storyapp.ui.story.main.MainActivity
import com.karsatech.storyapp.utils.AppUtils.InitTextWatcher
import com.karsatech.storyapp.utils.AppUtils.isEmailValid
import com.karsatech.storyapp.utils.UserPreference
import com.karsatech.storyapp.utils.Validator
import com.karsatech.storyapp.utils.Views.onCLick
import com.karsatech.storyapp.utils.Views.onTextChanged

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginValidator: Validator.Login

    private val viewModelFactory: ViewModelProvider.Factory by lazy {
        ViewModelFactory(UserPreference.getInstance(application.dataStore), this)
    }

    private val loginViewModel: LoginViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginValidator = Validator.Login()

        playAnimation()
        setupViews()
        setOnClick()

    }

    private fun showLoading(loading: Boolean) {
        binding.btnLogin.isVisible = !loading
        binding.progressBar.isVisible = loading
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val email = ObjectAnimator.ofFloat(binding.etLayoutEmail, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.etLayoutPassword, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(email, password, login)
            start()
        }
    }

    private fun setupViews() {

        binding.apply {
            emailEditText.onTextChanged {
                InitTextWatcher {
                    loginValidator.email = it.isNotEmpty() && isEmailValid(it)
                    btnLogin.isEnabled = loginValidator.filled()
                }
            }

            passwordEditText.onTextChanged {
                InitTextWatcher {
                    loginValidator.password = it.isNotEmpty() && it.length >= 8
                    btnLogin.isEnabled = loginValidator.filled()
                }
            }

            btnLogin.onCLick {
                login(emailEditText.text.toString(), passwordEditText.text.toString())
            }
        }
    }

    private fun login(email: String, password: String) {
        loginViewModel.login(email, password).observe(this) { result ->
            if (result != null) {
                when(result) {
                    is Result.Success -> {
                        showLoading(false)
                        Toast.makeText(this, result.data.message, Toast.LENGTH_LONG).show()

                        result.data.loginResult?.let {
                            loginViewModel.saveUser(it)
                        }

                        loginViewModel.loginUser()

                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
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

    private fun setOnClick() {
        binding.tvRegister.onCLick {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    companion object {
        private var TAG = LoginActivity::class.java.simpleName
    }
}