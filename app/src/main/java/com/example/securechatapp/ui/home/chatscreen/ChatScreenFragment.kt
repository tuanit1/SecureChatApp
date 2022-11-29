package com.example.securechatapp.ui.home.chatscreen

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.securechatapp.MainActivity
import com.example.securechatapp.R
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.data.model.Room
import com.example.securechatapp.databinding.FragmentChatScreenBinding
import com.example.securechatapp.extension.*
import com.example.securechatapp.ui.home.HomeFragment
import com.example.securechatapp.ui.home.chatlist.ChatListFragment
import com.example.securechatapp.utils.AppSocket
import com.example.securechatapp.utils.InjectorUtils
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream


class ChatScreenFragment : Fragment() {

    companion object {
        const val ROOM_ID = "room_id"
        const val PICK_IMAGE_REQUEST = "PICK_IMAGE_REQUEST"
        const val TAKE_PHOTO_REQUEST = "TAKE_PHOTO_REQUEST"
        const val PICK_FILE_REQUEST = "PICK_FILE_REQUEST"

        fun newInstance(roomId: String) = ChatScreenFragment().apply {
            arguments = Bundle().apply {
                putString(ROOM_ID, roomId)
            }
        }
    }

    private var isEmptyInput = true
    private var mRoomID: String? = null
    private var binding: FragmentChatScreenBinding? = null
    private var chatListFragment: ChatListFragment? = null
    private var mViewModel: ChatScreenViewModel? = null
    private var mAdapter: ChatScreenAdapter? = null
    private var resultCode: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatScreenBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
    }

    private fun initListener() {

        (activity as MainActivity).run {
            onActivityResultListener = {
                when (resultCode) {
                    PICK_IMAGE_REQUEST -> {
                        onPickPhotoResult(it)
                    }
                    PICK_FILE_REQUEST -> {
                        onPickFileResult(it)
                    }
                    TAKE_PHOTO_REQUEST -> {
                        onTakePhotoResult()
                    }
                }
            }
        }

        binding?.run {
            edtMessage.addTextChangedListener { text ->

                ivSendMessage.visibility = View.VISIBLE
                llSendMore.visibility = View.GONE

                if (text?.isNotEmpty() == true) {
                    isEmptyInput = false
                    ivSendMessage.isSelected = true
                } else {
                    isEmptyInput = true
                    ivSendMessage.isSelected = false
                }
            }

            ivBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            ivSendMessage.setOnClickListener {
                if (!isEmptyInput) {
                    mRoomID?.let {
                        progressBarMsg.visibility = View.VISIBLE
                        mViewModel?.sendTextMessage(edtMessage.text.toString(), it) {
                            progressBarMsg.visibility = View.GONE
                        }
                        edtMessage.text.clear()
                    }
                } else {
                    llSendMore.visibility = View.VISIBLE
                    ivSendMessage.visibility = View.GONE
                }
            }

            btnTakePhoto.setOnClickListener {
                (activity as MainActivity).run {
                    checkUserPermission(Manifest.permission.CAMERA) {
                        resultCode = TAKE_PHOTO_REQUEST
                        getImageFromCamera()
                    }
                }
            }

            btnPickPhoto.setOnClickListener {
                (activity as MainActivity).run {
                    checkUserPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                        resultCode = PICK_IMAGE_REQUEST
                        pickImageFromGallery()
                    }
                }
            }

            btnPickFile.setOnClickListener {
                (activity as MainActivity).run {
                    checkUserPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                        resultCode = PICK_FILE_REQUEST
                        pickFileFromStorage()
                    }
                }
            }


            rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(
                            -1
                        )
                    ) {
                        mRoomID?.let { mViewModel?.loadMessage(it) }
                    }
                }
            })

        }

        AppSocket.getInstance().onListenMessage = {
            if (it.message.roomID == mRoomID) {
                activity?.runOnUiThread {
                    mViewModel?.addIncomingMessage(it)
                }
            }
        }

        observeRoom()
        observeMessages()
    }

    private fun observeMessages() {
        binding?.run {
            mViewModel?.mMessages?.observe(viewLifecycleOwner) { list ->
                if (list?.isNotEmpty() == true) {
                    mAdapter?.submitList(list)

                    if (mViewModel?.isAddToTop == false) {
                        binding?.rv?.smoothScrollToPosition(list.size - 1)
                    }

                }
            }
        }
    }


    private fun observeRoom() {
        binding?.run {
            mViewModel?.mChatRoom?.observe(viewLifecycleOwner) { chatRoom ->
                when (chatRoom.room?.type) {
                    Room.GROUP -> run {
                        tvRoomName.text = chatRoom?.room?.name?.decodeBase64()
                        tvType.text = "@${Room.GROUP}"

                        chatRoom?.room?.image?.decodeBase64()?.let { url ->
                            if (url.isNotEmpty()) {
                                Picasso.get()
                                    .load(url)
                                    .placeholder(R.drawable.ic_user_placholder)
                                    .into(ivThumb)
                            } else {
                                Picasso.get()
                                    .load(R.drawable.ic_user_placholder)
                                    .into(ivThumb)
                            }
                        }

                    }
                    Room.PRIVATE -> run {
                        tvRoomName.text = chatRoom?.participant?.user?.name?.decodeBase64()
                        tvType.text = "@${Room.PRIVATE}"

                        chatRoom?.participant?.user?.image?.decodeBase64()?.let { url ->
                            if (url.isNotEmpty()) {
                                Picasso.get()
                                    .load(url)
                                    .placeholder(R.drawable.ic_user_placeholder2)
                                    .into(ivThumb)
                            } else {
                                Picasso.get()
                                    .load(R.drawable.ic_user_placeholder2)
                                    .into(ivThumb)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initView() {
        mRoomID = arguments?.getString(ROOM_ID)
        val frg =
            parentFragmentManager.findFragmentByTag(HomeFragment::class.java.name) as HomeFragment
        chatListFragment = frg.mAdapter?.mFragments?.get(0) as ChatListFragment

        val factory = InjectorUtils.provideChatScreenViewModelFactory()
        mViewModel = ViewModelProvider(this, factory)[ChatScreenViewModel::class.java]

        mAdapter = ChatScreenAdapter()

        binding?.run {
            rv.adapter = mAdapter
            rv.layoutManager = LinearLayoutManager(context).apply { stackFromEnd = true }
            rv.itemAnimator = DefaultItemAnimator()
        }

        loadChatRoom()
        loadMessages()
    }

    private fun loadMessages() {
        mRoomID?.let {
            mViewModel?.loadMessage(it, callback = object : APICallback {
                override fun onStart() {
                    binding?.progressBarRV?.visibility = View.VISIBLE
                }

                override fun onSuccess(data: Any?) {
                    binding?.progressBarRV?.visibility = View.GONE
                }

                override fun onError(t: Throwable?) {
                    binding?.progressBarRV?.visibility = View.GONE
                    Toast.makeText(context, "Something wrong happened!", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    private fun loadChatRoom() {
        mRoomID?.let {
            mViewModel?.loadRoom(it, callback = object : APICallback {
                override fun onStart() {
                    binding?.titleProgressbar?.visibility = View.VISIBLE
                    binding?.clTitle?.visibility = View.GONE
                }

                override fun onSuccess(data: Any?) {
                    binding?.titleProgressbar?.visibility = View.GONE
                    binding?.clTitle?.visibility = View.VISIBLE
                }

                override fun onError(t: Throwable?) {
                    binding?.titleProgressbar?.visibility = View.GONE
                    binding?.clTitle?.visibility = View.VISIBLE
                }
            })
        }
    }

    private fun onTakePhotoResult() {
        val bitmap = MediaStore.Images.Media.getBitmap(
            context?.contentResolver, (activity as MainActivity).imageUri
        );
        val os = ByteArrayOutputStream()
        compressBitmap(bitmap).compress(Bitmap.CompressFormat.JPEG, 100, os)
        val byteArray = os.toByteArray()

        val path = "image/message/IMG_${System.currentTimeMillis()}.jpg"
        val imageRef = FirebaseStorage.getInstance().reference.child(path)

        binding?.progressBarMsg?.visibility = View.VISIBLE

        imageRef.putBytes(byteArray).addOnFailureListener {
            Log.e("tuan", it.message.toString())
            binding?.progressBarMsg?.visibility = View.GONE
        }.addOnSuccessListener {
            imageRef.downloadUrl.addOnCompleteListener {
                val downloadUrl = it.result.toString()
                mRoomID?.let { roomId ->
                    binding?.progressBarMsg?.visibility = View.VISIBLE
                    mViewModel?.sendFileMessage(downloadUrl, roomId, true) {
                        binding?.progressBarMsg?.visibility = View.GONE
                    }
                }
            }.addOnFailureListener {
                binding?.progressBarMsg?.visibility = View.GONE
            }
        }

    }

    private fun onPickPhotoResult(data: Intent?) {
        data?.data?.let { uri ->
            if (checkFileIsImage(context, uri)) {
                val path = "image/message/IMG_${System.currentTimeMillis()}.jpg"
                val imageRef = FirebaseStorage.getInstance().reference.child(path)
                val os = ByteArrayOutputStream()

                context?.let { ct ->
                    compressBitmapFromUri(ct, uri).compress(Bitmap.CompressFormat.JPEG, 100, os)
                    val byteArray = os.toByteArray()

                    binding?.progressBarMsg?.visibility = View.VISIBLE
                    imageRef.putBytes(byteArray).addOnFailureListener {
                        Log.e("tuan", it.message.toString())
                        binding?.progressBarMsg?.visibility = View.GONE
                    }.addOnSuccessListener {
                        imageRef.downloadUrl.addOnCompleteListener {
                            val downloadUrl = it.result.toString()
                            mRoomID?.let { roomId ->
                                binding?.progressBarMsg?.visibility = View.VISIBLE
                                mViewModel?.sendFileMessage(downloadUrl, roomId, true) {
                                    binding?.progressBarMsg?.visibility = View.GONE
                                }
                            }
                        }.addOnFailureListener {
                            binding?.progressBarMsg?.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun onPickFileResult(data: Intent?) {
        data?.data?.let { uri ->
            if (!checkFileIsImage(context, uri)) {
                val filename = getFileName(context, uri)
                filename?.let { fileName ->
                    val path = "file/$fileName"
                    val imageRef = FirebaseStorage.getInstance().reference.child(path)

                    binding?.progressBarMsg?.visibility = View.VISIBLE
                    imageRef.putFile(uri).addOnFailureListener {
                        Log.e("tuan", it.message.toString())
                        binding?.progressBarMsg?.visibility = View.GONE
                    }.addOnSuccessListener {
                        imageRef.downloadUrl.addOnCompleteListener {
                            val downloadUrl = it.result.toString()
                            mRoomID?.let { roomId ->
                                binding?.progressBarMsg?.visibility = View.VISIBLE
                                mViewModel?.sendFileMessage(downloadUrl, roomId, false) {
                                    binding?.progressBarMsg?.visibility = View.GONE
                                }
                            }
                        }.addOnFailureListener {
                            binding?.progressBarMsg?.visibility = View.GONE
                        }
                    }
                }
            }else{
                onPickPhotoResult(data)
            }
        }
    }

    private fun handleSendClick() {

//        binding?.run {
//            if(edtMessage.text.isNotEmpty()){
//                val text = edtMessage.text.toString()
//
//                mRoomID?.let { mRoomID ->
//                    val message = Message("aaa", text.encodeBase64(), "2022-11-05T02:12:07.937Z", "text", Constant.mUID,
//                        mRoomID
//                    )
//                    chatListFragment?.addMessage(message)
//                    parentFragmentManager.popBackStack()
//                }
//            }
//        }


    }


}