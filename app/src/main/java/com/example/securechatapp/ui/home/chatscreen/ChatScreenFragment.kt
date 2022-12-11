package com.example.securechatapp.ui.home.chatscreen

import android.Manifest
import android.os.Bundle
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
import com.example.securechatapp.R
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.data.model.Room
import com.example.securechatapp.databinding.FragmentChatScreenBinding
import com.example.securechatapp.extension.addFragment
import com.example.securechatapp.extension.decodeBase64
import com.example.securechatapp.ui.MainActivity
import com.example.securechatapp.ui.home.HomeFragment
import com.example.securechatapp.ui.home.chatlist.ChatListFragment
import com.example.securechatapp.ui.home.chatscreen.chatsetting.ChatSettingFragment
import com.example.securechatapp.utils.InjectorUtils
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso


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
    private var isAllowToSendFile: Boolean = true
    private var isAllowToSendMessage: Boolean = true

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

        fetchScreenData()
    }

    private fun initListener() {

        (activity as MainActivity).run {

            chatScreenHandleMessage = {
                if (it.message.roomID == mRoomID) {
                    activity?.runOnUiThread {
                        mViewModel?.addIncomingMessage(it)
                    }
                }
            }

            onActivityResultListener = {
                when (resultCode) {
                    PICK_IMAGE_REQUEST -> {
                        mViewModel?.onPickPhotoResult(
                            data = it,
                            context = context,
                            onStart = { binding?.progressBarMsg?.visibility = View.VISIBLE },
                            onEnd = { binding?.progressBarMsg?.visibility = View.GONE }
                        )

                    }
                    PICK_FILE_REQUEST -> {
                        mViewModel?.onPickFileResult(
                            data = it,
                            context = context,
                            onStart = { binding?.progressBarMsg?.visibility = View.VISIBLE },
                            onEnd = { binding?.progressBarMsg?.visibility = View.GONE }
                        )
                    }
                    TAKE_PHOTO_REQUEST -> {
                        mViewModel?.onTakePhotoResult(
                            uri = imageUri,
                            context = context,
                            onStart = { binding?.progressBarMsg?.visibility = View.VISIBLE },
                            onEnd = { binding?.progressBarMsg?.visibility = View.GONE }
                        )
                    }
                }
            }
        }

        binding?.run {

            ivRoomSetting.setOnClickListener {
                openChatSettingScreen()
            }

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
                    if(isAllowToSendMessage){
                        mRoomID?.let {
                            progressBarMsg.visibility = View.VISIBLE
                            mViewModel?.sendTextMessage(edtMessage.text.toString(), it) {
                                progressBarMsg.visibility = View.GONE
                            }
                            edtMessage.text.clear()
                        }
                    }else{
                        Toast.makeText(context, "You are not allow to send message!", Toast.LENGTH_SHORT).show()
                        edtMessage.text.clear()
                    }

                } else {
                    llSendMore.visibility = View.VISIBLE
                    ivSendMessage.visibility = View.GONE
                }
            }

            btnTakePhoto.setOnClickListener {
                (activity as MainActivity).run {
                    if(isAllowToSendFile){
                        checkUserPermission(Manifest.permission.CAMERA) {
                            resultCode = TAKE_PHOTO_REQUEST
                            getImageFromCamera()
                        }
                    }else{
                        Toast.makeText(context, "You are not allow to send image!", Toast.LENGTH_SHORT).show()
                    }

                }
            }

            btnPickPhoto.setOnClickListener {
                (activity as MainActivity).run {
                    if(isAllowToSendFile){
                        checkUserPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                            resultCode = PICK_IMAGE_REQUEST
                            pickImageFromGallery()
                        }
                    }else{
                        Toast.makeText(context, "You are not allow to send image!", Toast.LENGTH_SHORT).show()

                    }
                }
            }

            btnPickFile.setOnClickListener {
                (activity as MainActivity).run {
                    if(isAllowToSendFile){
                        checkUserPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                            resultCode = PICK_FILE_REQUEST
                            pickFileFromStorage()
                        }
                    }else{
                        Toast.makeText(context, "You are not allow to send file!", Toast.LENGTH_SHORT).show()
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
                        mRoomID?.let {
                            mViewModel?.loadMessage(
                                it,
                                callback = object : APICallback {
                                    override fun onStart() {
                                        progressBarFetch.visibility = View.VISIBLE
                                    }

                                    override fun onSuccess(data: Any?) {
                                        progressBarFetch.visibility = View.GONE
                                    }

                                    override fun onError(t: Throwable?) {
                                        progressBarFetch.visibility = View.GONE
                                    }

                                }
                            )
                        }
                    }
                }
            })
        }

        mAdapter?.onDownloadClickListener = { messageID, name, progressBar, ivDownload ->
            (activity as MainActivity).run {
                checkUserPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    mViewModel?.handleDownloadClick(
                        name,
                        onStart = {
                            ivDownload.visibility = View.GONE
                            progressBar.visibility = View.VISIBLE
                        },
                        onEnd = { isSuccess ->
                            if(isSuccess){
                                mViewModel?.setMessageDownloadState(messageID, true)
                            }else{
                                Snackbar.make(requireView(), "Something wrong when download $name 😥", Snackbar.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        onProgress = { progress ->
                            progressBar.progress = progress
                        }
                    )
                }
            }
        }

        listenTokenExpired()
        observeRoom()
        observeMessages()
        observeParticipant()
        listenParticipantFromSocket()
    }

    private fun listenParticipantFromSocket() {
        (activity as MainActivity).chatScreenHandleParticipant = {
            mViewModel?.setParticipantUpdateFromSocket(it)
        }
    }

    private fun listenTokenExpired() {
        mViewModel?.isTokenExpired?.observe(viewLifecycleOwner){ isExpired ->
            if(isExpired){
                Toast.makeText(context, getString(R.string.author_expired), Toast.LENGTH_SHORT).show()
                (activity as MainActivity).handleLogoutExpired()
            }
        }
    }


    private fun observeParticipant() {
        mViewModel?.mParticipant?.observe(viewLifecycleOwner){
            mAdapter?.setShowFile(it)

            isAllowToSendMessage = it.allowSendMSG
            isAllowToSendFile = it.allowSendFile
        }
    }

    private fun observeMessages() {
        binding?.run {
            mViewModel?.mMessages?.observe(viewLifecycleOwner) { list ->
                if (list?.isNotEmpty() == true) {
                    mAdapter?.submitList(list)

                    if (mViewModel?.isAddToTop == false && mViewModel?.isDownload == false) {
                        binding?.rv?.smoothScrollToPosition(list.size - 1)
                    }

                    mViewModel?.isDownload = false

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
                        tvType.text = buildString {
                            append("@")
                            append(Room.GROUP)
                        }

                        ivRoomSetting.visibility = View.VISIBLE

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
                        tvType.text = buildString {
                            append("@")
                            append(Room.PRIVATE)
                        }

                        ivRoomSetting.visibility = View.GONE

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

    private fun fetchScreenData(){
        mRoomID?.let { roomID ->
            mViewModel?.run {
                fetchScreenData(
                    roomID,
                    roomCallback = object : APICallback {
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
                    },

                    messageCallback = object : APICallback {
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

                    }
                )
            }
        }
    }

    private fun openChatSettingScreen(){
        addFragment(
            R.id.fragmentContainerView,
            ChatSettingFragment.newInstance(mRoomID),
            true,
            ChatSettingFragment::class.java.name
        )
    }

    override fun onResume() {
        super.onResume()
        mViewModel?.checkDownloadFolderChange()
    }
}