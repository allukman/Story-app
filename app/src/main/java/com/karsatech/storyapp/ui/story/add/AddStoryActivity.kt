package com.karsatech.storyapp.ui.story.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.karsatech.storyapp.R
import com.karsatech.storyapp.data.remote.retrofit.ApiConfig
import com.karsatech.storyapp.data.remote.retrofit.ApiService
import com.karsatech.storyapp.databinding.ActivityAddStoryBinding
import com.karsatech.storyapp.ui.camera.CameraActivity
import com.karsatech.storyapp.ui.story.main.MainActivity
import com.karsatech.storyapp.utils.reduceFileImage
import com.karsatech.storyapp.utils.rotateFile
import com.karsatech.storyapp.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private var getFile: File? = null
    private lateinit var service: ApiService

    private val addStoryViewModel by viewModels<AddStoryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_story)

        service = ApiConfig.getApiClient(this)!!.create(ApiService::class.java)

        if (!allPermisionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSION,
                REQUEST_CODE_PERMISSIONS
            )
        }

        setOnClick()
        settingViewModel()
        subscribeViewModel()

    }

    private fun showLoading(loading: Boolean) {
        binding.btnUpload.visibility = if (loading) View.GONE else View.VISIBLE
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.INVISIBLE
    }

    private fun subscribeViewModel() {
        addStoryViewModel.success.observe(this) { data ->
            if (!data.error) {
                intentToMainActivity()
            }
            Toast.makeText(this, data.message, Toast.LENGTH_SHORT).show()
        }

        addStoryViewModel.isLoading.observe(this) { loading ->
            showLoading(loading)
        }
    }

    private fun intentToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
    private fun setOnClick() {
        binding.btnCamera.setOnClickListener { startCameraX() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener { validation() }
    }

    private fun validation() {
        val description = binding.descEditText.text.toString()
        val stringDescription = getString(R.string.desc)

        if (getFile == null) {
            binding.tvErrorImage.visibility = View.VISIBLE
            return
        } else {
            binding.tvErrorImage.visibility = View.GONE
        }

        if (description.isEmpty()) {
            binding.etLayoutDescription.error = getString(R.string.error_empty_value, stringDescription)
            return
        } else {
            binding.etLayoutDescription.error = null
        }

        addNewStory(description)
    }

    private fun addNewStory(desc: String) {
        val file = reduceFileImage(getFile as File)
        val description = desc.toRequestBody("text/plain".toMediaType())
        val reqImgFile = file.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            reqImgFile
        )

        addStoryViewModel.addStory(photo = imageMultipart, description)
    }

    private fun settingViewModel() {
        addStoryViewModel.apply {
            setService(service)
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGalery.launch(chooser)
    }

    private fun allPermisionGranted() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.ivPreviewImage.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGalery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result?.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this)
                getFile = myFile
                binding.ivPreviewImage.setImageURI(uri)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermisionGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan izin",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10

        private val TAG = AddStoryActivity::class.java.simpleName
    }
}