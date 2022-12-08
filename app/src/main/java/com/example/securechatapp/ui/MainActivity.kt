package com.example.securechatapp.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.securechatapp.R
import com.example.securechatapp.data.api.API
import com.example.securechatapp.data.model.ChatMessage
import com.example.securechatapp.data.model.Message
import com.example.securechatapp.data.model.Participant
import com.example.securechatapp.databinding.ActivityMainBinding
import com.example.securechatapp.extension.addFragment
import com.example.securechatapp.ui.auth.login.LoginFragment
import com.example.securechatapp.ui.home.HomeFragment
import com.example.securechatapp.utils.AppSocket
import com.example.securechatapp.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var auth: FirebaseAuth? = null
    private var onPermissionGranted: () -> Unit = {}
    var chatScreenHandleMessage: (ChatMessage) -> Unit = {}
    var chatListHandleMessage: (Message) -> Unit = {}
    var chatSettingHandleParticipant: (Participant) -> Unit = {}
    var onActivityResultListener : (data: Intent?) -> Unit = {}
    var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = Firebase.auth

        initView()
        initListener()

    }

    override fun onResume() {
        super.onResume()

        AppSocket.getInstance().run {
            mSocketMessage.connect()
            mSocketParticipant.connect()
        }
    }

    private fun initListener() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val count = supportFragmentManager.backStackEntryCount
                if (count > 1) {
                    supportFragmentManager.popBackStack()
                } else {
                    finish()
                }
            }
        })

        AppSocket.getInstance().run {
            onListenMessage = {
                chatScreenHandleMessage(it)
                chatListHandleMessage(it.message)
            }

            onListenParticipant = {
                chatSettingHandleParticipant(it)
            }
        }
    }

    private fun initView() {

        auth?.run {
            if (currentUser == null) {
                addFragment(
                    containerId = getContainerId(),
                    fragment = LoginFragment.newInstance(),
                    addToBackStack = true,
                    tag = getString(R.string.login)
                )
            } else {

                Constant.mUID = currentUser?.uid ?: ""

                addFragment(
                    containerId = getContainerId(),
                    fragment = HomeFragment.newInstance(),
                    addToBackStack = true,
                    tag = HomeFragment::class.java.name
                )
            }
        }
    }

    fun checkUserPermission(permission: String, onGranted: () -> Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(this, permission) -> {
                onGranted()
            }
            else -> {
                onPermissionGranted = { onGranted() }
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            onPermissionGranted()
        }
    }

    private var resultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data.let {
                    onActivityResultListener(it)
                }
            }
        }

    @SuppressLint("IntentReset")
    fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    fun pickFileFromStorage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        resultLauncher.launch(intent)
    }

    fun getImageFromCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
        imageUri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        resultLauncher.launch(intent)
    }

    fun handleLogout(){

        Firebase.auth.signOut()
        Constant.mUID = ""

        lifecycleScope.launch {
            try{
                API.apiService.logoutToken()
            }catch (e: Exception) {
                Log.e("tuan", e.message.toString())
            }
        }

        with(supportFragmentManager){
            for(i in 1 .. backStackEntryCount){
                popBackStack()
            }
        }

        addFragment(
            containerId = getContainerId(),
            fragment = LoginFragment.newInstance(),
            addToBackStack = true,
            tag = getString(R.string.login)
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        AppSocket.getInstance().run {
            mSocketMessage.disconnect()
            mSocketParticipant.disconnect()
        }
    }

    private fun getContainerId() = R.id.fragmentContainerView

}