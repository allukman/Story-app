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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.karsatech.storyapp.data.remote.retrofit.ApiConfig
import com.karsatech.storyapp.data.remote.retrofit.ApiService
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
    private lateinit var service: ApiService
    private lateinit var loginValidator: Validator.Login

    private val viewModelFactory: ViewModelProvider.Factory by lazy {
        ViewModelFactory(UserPreference.getInstance(application.dataStore))
    }

    private val loginViewModel: LoginViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        service = ApiConfig.getApiClient(this)!!.create(ApiService::class.java)
        loginValidator = Validator.Login()

        playAnimation()
        setupViews()
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
                data.loginResult?.let { loginViewModel.saveUser(it) }
                loginViewModel.login()

                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
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
                    loginValidator.password = it.isNotEmpty() && it.length >= 6
                    btnLogin.isEnabled = loginValidator.filled()
                }
            }

            btnLogin.onCLick {
                loginViewModel.loginUser(emailEditText.text.toString(), passwordEditText.text.toString())
            }
        }
    }

    private fun settingViewModel() {
        loginViewModel.apply {
            setService(service)
        }
    }

    private fun setOnClick() {
        binding.tvRegister.onCLick {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

//        binding.btnLogin.onCLick {
//            Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show()
////            validation()
//        }

    }

    companion object {
        private var TAG = LoginActivity::class.java.simpleName
    }
}