package com.example.securechatapp

import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.securechatapp.data.api.API
import com.example.securechatapp.data.model.Playlist
import com.example.securechatapp.data.model.ResponseObject
import com.example.securechatapp.databinding.ActivityMainBinding
import com.example.securechatapp.extension.addFragment
import com.example.securechatapp.ui.auth.login.LoginFragment
import com.example.securechatapp.ui.home.HomeFragment
import com.example.securechatapp.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = Firebase.auth

        initListener()
        initView()
    }

    private fun initListener() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val count = supportFragmentManager.backStackEntryCount
                if(count > 1){
                    supportFragmentManager.popBackStack()
                }else{
                    finish()
                }
            }
        })
    }

    private fun initView() {

        auth?.run {
            if(currentUser == null){
                addFragment(
                    containerId = getContainerId(),
                    fragment = LoginFragment.newInstance(),
                    addToBackStack = true,
                    tag = getString(R.string.login)
                )
            }else{

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

    private fun getContainerId() = R.id.fragmentContainerView

    private fun getAllPlaylist() {
        API.apiService.getAllPlaylist()
            .enqueue(object : Callback<ResponseObject<MutableList<Playlist>>> {
                override fun onResponse(
                    call: Call<ResponseObject<MutableList<Playlist>>>,
                    response: Response<ResponseObject<MutableList<Playlist>>>
                ) {

                    val playlist = response.body()?.data

                    playlist?.forEach {
                        Log.e(
                            "tuan", """
                        {
                            "id" = ${it.id},
                            "name" = ${it.name},
                            "thumbnail" = ${it.thumbnail}
                        }
                    """.trimIndent()
                        )
                    }

                }

                override fun onFailure(
                    call: Call<ResponseObject<MutableList<Playlist>>>,
                    t: Throwable
                ) {
                    Log.e("tuan", "Fetch fail: ${t.message}")
                }

            })
    }

    private fun getPlaylistByID() {
        API.apiService.getPlaylistByID(2).enqueue(object : Callback<ResponseObject<Playlist>> {
            override fun onResponse(
                call: Call<ResponseObject<Playlist>>,
                response: Response<ResponseObject<Playlist>>
            ) {

                val playlist = response.body()?.data
                Log.e("tuan", playlist.toString())

            }

            override fun onFailure(
                call: Call<ResponseObject<Playlist>>,
                t: Throwable
            ) {
                Log.e("tuan", "Fetch fail: ${t.message}")
            }

        })
    }

    private fun createPlaylist() {
        val body = HashMap<String, String>().apply {
            set("name", "new name")
            set("thumb", "new thumb")
        }

        API.apiService.createPlaylist(body).enqueue(object : Callback<ResponseObject<Unit>>{
            override fun onResponse(
                call: Call<ResponseObject<Unit>>,
                response: Response<ResponseObject<Unit>>
            ) {
                val message = response.body()?.responseMessage
            }

            override fun onFailure(call: Call<ResponseObject<Unit>>, t: Throwable) {
                Log.e("tuan", "Fetch fail: ${t.message}")
            }

        })
    }

}