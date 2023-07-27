package com.example.securechatapp.ui.home.chatscreen

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securechatapp.data.api.API
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.data.model.ChatMessage
import com.example.securechatapp.data.model.ChatRoom
import com.example.securechatapp.data.model.Message
import com.example.securechatapp.data.model.Participant
import com.example.securechatapp.data.model.api.ResponseObject
import com.example.securechatapp.data.repository.MessageRepository
import com.example.securechatapp.data.repository.ParticipantRepository
import com.example.securechatapp.data.repository.RoomRepository
import com.example.securechatapp.extension.*
import com.example.securechatapp.utils.Constant
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.net.URL
import java.net.URLConnection

@Suppress("DEPRECATION")
class ChatScreenViewModel(
    private val roomRepository: RoomRepository,
    private val messageRepository: MessageRepository,
    private val participantRepository: ParticipantRepository
) : ViewModel() {

    var mChatRoom: MutableLiveData<ChatRoom> = MutableLiveData()
    var mMessages: MutableLiveData<List<ChatMessage>> = MutableLiveData()
    var isTokenExpired: MutableLiveData<Boolean> = MutableLiveData(false)
    var mParticipant: MutableLiveData<Participant> = MutableLiveData()
    var isFirstLoad: Boolean = true
    var isAddToTop: Boolean = false
    var isDownload: Boolean = false
    private var mPage = 0
    private var mStep = 10

    fun fetchScreenData(
        roomID: String,
        roomCallback: APICallback,
        messageCallback: APICallback,
    ) {

        roomCallback.onStart()
        messageCallback.onStart()

        viewModelScope.launch(Dispatchers.IO) {
            val roomResponse = roomRepository.getRoomByID(Constant.mUID, roomID)
            val messageResponse = messageRepository.getMessagesByRoomID(roomID, mPage, mStep)
            val partyResponse = participantRepository.getParticipantByRoomIDSuspend(roomID)

            API.checkTokenExpiredThreeRequest(
                roomResponse,
                messageResponse,
                partyResponse,
                onTokenInUse = {
                    //handle room
                    if (roomResponse.isSuccessful) {
                        if (roomResponse.body()?.success == true) {
                            roomResponse.body()?.data?.let {
                                mChatRoom.postValue(it)
                                roomCallback.onSuccess()
                            }
                        }
                    }

                    //handle participant
                    if (partyResponse.body()?.success == true) {
                        partyResponse.body()?.data?.let { list ->
                            if (list.isNotEmpty()) {
                                mParticipant.value = list.find { it.user.uid == Constant.mUID }
                            }
                        }
                    }

                    //handle message
                    if (messageResponse.body()?.success == true) {

                        messageResponse.body()?.data?.let { list ->
                            if (list.isNotEmpty()) {
                                checkFileMessageDownload(list)

                                if (mMessages.value != null) {
                                    isAddToTop = true
                                    mMessages.value =
                                        mMessages.value?.toMutableList()?.apply {
                                            addAll(0, list)
                                        }
                                } else {
                                    isAddToTop = false

                                    mMessages.value = list
                                }

                                mPage++
                                Log.e("tuan", "${list.size} more messages added")
                            }
                        }

                        messageCallback.onSuccess()
                    } else {
                        messageCallback.onError()
                    }

                    isFirstLoad = false

                },
                onTokenUpdated = {
                    fetchScreenData(roomID, roomCallback, messageCallback)
                },
                onRefreshTokenExpired = {
                    isTokenExpired.value = true
                },
                onError = {
                    roomCallback.onError()
                    messageCallback.onError()
                }
            )

        }

    }

    fun loadMessage(roomID: String, callback: APICallback? = null) {

        viewModelScope.launch(Dispatchers.IO) {
            launch(Dispatchers.Main) {
                callback?.onStart()
            }
            val response = messageRepository.getMessagesByRoomID(roomID, mPage, mStep)

            API.checkTokenExpired(
                response,
                onTokenInUse = {
                    if (response.body()?.success == true) {

                        response.body()?.data?.let { list ->
                            launch(Dispatchers.Main) {
                                if (list.isNotEmpty()) {
                                    checkFileMessageDownload(list)

                                    if (mMessages.value != null) {
                                        isAddToTop = true
                                        mMessages.value =
                                            mMessages.value?.toMutableList()?.apply {
                                                addAll(0, list)
                                            }
                                    } else {
                                        isAddToTop = false

                                        mMessages.value = list
                                    }

                                    mPage++
                                    Log.e("tuan", "${list.size} more messages added")
                                }
                            }
                        }

                        launch(Dispatchers.Main) {
                            callback?.onSuccess()
                        }
                    } else {
                        launch(Dispatchers.Main) {
                            callback?.onError()
                        }
                    }
                },
                onTokenUpdated = {
                    loadMessage(roomID, callback)
                },
                onRefreshTokenExpired = {
                    isTokenExpired.value = true
                },
                onError = {
                    launch(Dispatchers.Main) {
                        callback?.onError()
                    }
                }
            )

        }
    }

    fun addIncomingMessage(chatMessage: ChatMessage) {
        isAddToTop = false
        mMessages.value = mMessages.value?.toMutableList()?.apply {
            add(chatMessage)
        } ?: mutableListOf(chatMessage)
    }

    fun sendTextMessage(text: String, roomID: String, onFinish: () -> Unit) {


        val body = HashMap<String, String>().apply {
            set("message", text.encodeBase64())
            set("type", Message.TEXT)
        }

        messageRepository.createMessage(Constant.mUID, roomID, body)
            .enqueue(object : Callback<ResponseObject<Message>> {
                override fun onResponse(
                    call: Call<ResponseObject<Message>>,
                    response: Response<ResponseObject<Message>>
                ) {

                    viewModelScope.launch(Dispatchers.IO) {
                        API.checkTokenExpired(
                            response,
                            onTokenInUse = {
                                if (response.isSuccessful && response.body()?.success == true) {
                                    response.body()?.data?.let { message ->
                                        Log.e("tuan", "send message successful $message")
                                    }
                                }

                                onFinish()
                            },
                            onTokenUpdated = {
                                sendTextMessage(text, roomID, onFinish)
                            },
                            onRefreshTokenExpired = {
                                isTokenExpired.value = true
                            },
                            onError = { onFinish() }
                        )
                    }
                }

                override fun onFailure(call: Call<ResponseObject<Message>>, t: Throwable) {
                    Log.e("tuan", t.toString())
                    onFinish()
                }

            })

    }

    private fun sendFileMessage(
        url: String,
        roomID: String,
        isImage: Boolean,
        onFinish: () -> Unit
    ) {
        val body = HashMap<String, String>().apply {
            set("message", url.encodeBase64())
            set("type", if (isImage) Message.IMAGE else Message.FILE)
        }

        messageRepository.createMessage(Constant.mUID, roomID, body)
            .enqueue(object : Callback<ResponseObject<Message>> {
                override fun onResponse(
                    call: Call<ResponseObject<Message>>,
                    response: Response<ResponseObject<Message>>
                ) {

                    viewModelScope.launch(Dispatchers.IO) {
                        API.checkTokenExpired(
                            response,
                            onTokenInUse = {
                                if (response.isSuccessful && response.body()?.success == true) {
                                    response.body()?.data?.let { message ->
                                        Log.e("tuan", "send message successful $message")
                                    }
                                }

                                onFinish()
                            },
                            onTokenUpdated = {
                                sendFileMessage(url, roomID, isImage, onFinish)
                            },
                            onRefreshTokenExpired = {
                                isTokenExpired.value = true
                            },
                            onError = onFinish
                        )
                    }
                }

                override fun onFailure(call: Call<ResponseObject<Message>>, t: Throwable) {
                    Log.e("tuan", t.toString())
                    onFinish()
                }

            })
    }

    fun onTakePhotoResult(
        context: Context?,
        uri: Uri?,
        onStart: () -> Unit,
        onEnd: () -> Unit
    ) {
        val bitmap = MediaStore.Images.Media.getBitmap(
            context?.contentResolver, uri
        )
        val os = ByteArrayOutputStream()
        compressBitmap(bitmap).compress(Bitmap.CompressFormat.JPEG, 100, os)
        val byteArray = os.toByteArray()

        val path = "image/message/IMG_${System.currentTimeMillis()}.jpg"
        val imageRef = FirebaseStorage.getInstance().reference.child(path)

        onStart()

        imageRef.putBytes(byteArray).addOnFailureListener {
            onEnd()
        }.addOnSuccessListener {
            imageRef.downloadUrl.addOnCompleteListener {
                val downloadUrl = it.result.toString()
                mChatRoom.value?.room?.id?.let { roomId ->
                    sendFileMessage(downloadUrl, roomId, true) {
                        onEnd()
                    }
                }
            }.addOnFailureListener {
                onEnd()
            }
        }

    }

    fun onPickPhotoResult(
        data: Intent?,
        context: Context?,
        onStart: () -> Unit,
        onEnd: () -> Unit
    ) {
        data?.data?.let { uri ->
            if (checkFileIsImage(context, uri)) {
                val path = "image/message/IMG_${System.currentTimeMillis()}.jpg"
                val imageRef = FirebaseStorage.getInstance().reference.child(path)
                val os = ByteArrayOutputStream()

                context?.let { ct ->
                    compressBitmapFromUri(ct, uri).compress(Bitmap.CompressFormat.JPEG, 100, os)
                    val byteArray = os.toByteArray()

                    onStart()
                    imageRef.putBytes(byteArray).addOnFailureListener {
                        onEnd()
                    }.addOnSuccessListener {
                        imageRef.downloadUrl.addOnCompleteListener {
                            val downloadUrl = it.result.toString()
                            mChatRoom.value?.room?.id?.let { roomId ->
                                sendFileMessage(downloadUrl, roomId, true) {
                                    onEnd()
                                }
                            }
                        }.addOnFailureListener {
                            onEnd()
                        }
                    }
                }
            }
        }
    }

    fun onPickFileResult(
        data: Intent?,
        context: Context?,
        onStart: () -> Unit,
        onEnd: () -> Unit
    ) {
        data?.data?.let { uri ->
            if (!checkFileIsImage(context, uri)) {
                val filename = getFileName(context, uri)
                filename?.let { fileName ->

                    val fName = fileName.split(".").first()
                    val extension = fileName.split(".").last()
                    val newName = "${fName}_${System.currentTimeMillis()}.$extension"

                    val path = "file/$newName"
                    val imageRef = FirebaseStorage.getInstance().reference.child(path)

                    onStart()
                    imageRef.putFile(uri).addOnFailureListener {
                        onEnd()
                    }.addOnSuccessListener {
                        mChatRoom.value?.room?.id?.let { roomId ->
                            sendFileMessage(newName, roomId, false) {
                                onEnd()
                            }
                        }
                    }
                }
            } else {
                onPickPhotoResult(data, context, onStart, onEnd)
            }
        }
    }

    private fun getFirebaseFileUrl(name: String, onEnd: (String?) -> Unit) {
        val ref = FirebaseStorage.getInstance().reference.child("file/$name")
        viewModelScope.launch(Dispatchers.IO) {
            ref.downloadUrl.addOnCompleteListener {
                try {
                    onEnd(it.result.toString())
                } catch (e: Exception) {
                    onEnd(null)
                }
            }.addOnFailureListener {
                onEnd(null)
            }
        }
    }

    fun handleDownloadClick(
        name: String,
        onStart: () -> Unit,
        onEnd: (Boolean) -> Unit,
        onProgress: (Int) -> Unit
    ) {
        val root = File(Constant.DOWNLOAD_PATH)
        if (!root.exists()) {
            root.mkdirs()
        }

        getFirebaseFileUrl(name) { fileUrl ->
            fileUrl?.let {
                downloadFile(
                    fileName = name,
                    downloadPath = Constant.DOWNLOAD_PATH,
                    downloadUrl = fileUrl,
                    onStart,
                    onEnd,
                    onProgress
                )
            }
        }
    }

    private fun downloadFile(
        fileName: String,
        downloadPath: String,
        downloadUrl: String,
        onStart: () -> Unit,
        onEnd: (Boolean) -> Unit,
        onProgress: (Int) -> Unit
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                onStart()
            }
            var count: Int
            try {
                val url = URL(downloadUrl)
                val connection: URLConnection = url.openConnection()
                connection.connect()

                val lengthOfFile: Int = connection.contentLength

                val input: InputStream = BufferedInputStream(
                    url.openStream(),
                    8192
                )
                val output: OutputStream = FileOutputStream(
                    "$downloadPath/$fileName"
                )
                val data = ByteArray(1024)
                var total: Long = 0
                while ((input.read(data).also { count = it }) != -1) {
                    total += count.toLong()
                    val progress = (total * 100) / lengthOfFile
                    onProgress(progress.toInt())
                    output.write(data, 0, count)
                }

                output.flush()
                output.close()
                input.close()

                withContext(Dispatchers.Main) {
                    onEnd(true)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onEnd(false)
                }

                Log.e("tuan", e.message.toString())
            }
        }
    }

    private fun checkFileMessageDownload(list: List<ChatMessage>): List<ChatMessage> {
        return list.toMutableList().apply {
            forEachIndexed { index, chatMessage ->
                if (chatMessage.message.type == Message.FILE) {
                    File("${Constant.DOWNLOAD_PATH}/${chatMessage.message.message.decodeBase64()}").run {
                        this@apply[index] = get(index).copy().apply {
                            message = message.copy().apply {
                                this.isDownloaded = exists()
                            }
                        }
                    }
                }
            }
        }
    }

    fun checkDownloadFolderChange() {
        mMessages.value?.let { list ->
            val newList = checkFileMessageDownload(list)
            mMessages.postValue(newList.toMutableList())
        }
    }

    fun setMessageDownloadState(messageID: String, isDownloaded: Boolean) {
        isDownload = true
        mMessages.value = mMessages.value?.toMutableList()?.apply {
            forEachIndexed { index, chatMessage ->
                if (chatMessage.message.id == messageID) {
                    this[index] = get(index).copy().apply {
                        message = message.copy().apply {
                            this.isDownloaded = isDownloaded
                        }
                    }
                }
            }
        }
    }

    fun setParticipantUpdateFromSocket(party: Participant) {
        mChatRoom.value?.room?.id.let { roomID ->
            if (party.user.uid == Constant.mUID && party.roomID == roomID) {
                mParticipant.postValue(party)
            }
        }

    }

}