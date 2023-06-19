package com.karsatech.storyapp.ui.auth.login

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
import com.karsatech.storyapp.R
import com.karsatech.storyapp.data.remote.retrofit.ApiConfig
import com.karsatech.storyapp.data.remote.retrofit.ApiService
import com.karsatech.storyapp.databinding.ActivityLoginBinding
import com.karsatech.storyapp.ui.ViewModelFactory
import com.karsatech.storyapp.ui.auth.register.RegisterActivity
import com.karsatech.storyapp.ui.story.main.MainActivity
import com.karsatech.storyapp.utils.UserPreference

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var service: ApiService

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