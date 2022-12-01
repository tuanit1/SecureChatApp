package com.example.securechatapp.ui.home.chatscreen

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securechatapp.data.api.APICallback
import com.example.securechatapp.data.model.ChatMessage
import com.example.securechatapp.data.model.ChatRoom
import com.example.securechatapp.data.model.Message
import com.example.securechatapp.data.model.ResponseObject
import com.example.securechatapp.data.repository.MessageRepository
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

class ChatScreenViewModel(
    private val roomRepository: RoomRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {

    var mChatRoom: MutableLiveData<ChatRoom> = MutableLiveData()
    var mMessages: MutableLiveData<List<ChatMessage>> = MutableLiveData()
    var isAddToTop: Boolean = false
    private var mPage = 0
    private var mStep = 10

    fun loadRoom(roomID: String, callback: APICallback) {
        viewModelScope.launch(Dispatchers.IO) {
            viewModelScope.launch(Dispatchers.Main){
                callback.onStart()
            }
            roomRepository.getRoomByID(Constant.mUID, roomID)
                .enqueue(object : Callback<ResponseObject<ChatRoom>> {
                    override fun onResponse(
                        call: Call<ResponseObject<ChatRoom>>,
                        response: Response<ResponseObject<ChatRoom>>
                    ) {

                        if (response.isSuccessful) {
                            if (response.body()?.success == true) {
                                response.body()?.data?.let {
                                    mChatRoom.postValue(it)
                                    viewModelScope.launch(Dispatchers.Main){
                                        callback.onSuccess()
                                    }
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseObject<ChatRoom>>, t: Throwable) {
                        Log.d("tuan", "load room of chat screen fail")
                        viewModelScope.launch(Dispatchers.Main){
                            callback.onError(t)
                        }
                    }

                })
        }

    }

    fun loadMessage(roomID: String, callback: APICallback? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            viewModelScope.launch(Dispatchers.Main){
                callback?.onStart()
            }
            messageRepository.getMessagesByRoomID(roomID, mPage, mStep)
                .enqueue(object : Callback<ResponseObject<List<ChatMessage>>> {
                    override fun onResponse(
                        call: Call<ResponseObject<List<ChatMessage>>>,
                        response: Response<ResponseObject<List<ChatMessage>>>
                    ) {
                        if (response.isSuccessful && response.body()?.success == true) {

                            response.body()?.data?.let { list ->
                                if (list.isNotEmpty()) {

                                    checkFileMessageDownload(list)

                                    if (mMessages.value != null) {
                                        isAddToTop = true
                                        mMessages.value = mMessages.value?.toMutableList()?.apply {
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

                            viewModelScope.launch(Dispatchers.Main){
                                callback?.onSuccess()
                            }
                        } else {
                            viewModelScope.launch(Dispatchers.Main){
                                callback?.onError()
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<ResponseObject<List<ChatMessage>>>,
                        t: Throwable
                    ) {
                        callback?.onError(t)
                    }

                })
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

        viewModelScope.launch(Dispatchers.IO) {
            messageRepository.createMessage(Constant.mUID, roomID, body)
                .enqueue(object : Callback<ResponseObject<Message>> {
                    override fun onResponse(
                        call: Call<ResponseObject<Message>>,
                        response: Response<ResponseObject<Message>>
                    ) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            response.body()?.data?.let { message ->
                                Log.e("tuan", "send message successful $message")
                            }
                        }

                        onFinish()
                    }

                    override fun onFailure(call: Call<ResponseObject<Message>>, t: Throwable) {
                        Log.e("tuan", t.toString())
                        onFinish()
                    }

                })
        }

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

        viewModelScope.launch(Dispatchers.IO) {
            messageRepository.createMessage(Constant.mUID, roomID, body)
                .enqueue(object : Callback<ResponseObject<Message>> {
                    override fun onResponse(
                        call: Call<ResponseObject<Message>>,
                        response: Response<ResponseObject<Message>>
                    ) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            response.body()?.data?.let { message ->
                                Log.e("tuan", "send message successful $message")
                            }
                        }

                        onFinish()
                    }

                    override fun onFailure(call: Call<ResponseObject<Message>>, t: Throwable) {
                        Log.e("tuan", t.toString())
                        onFinish()
                    }

                })
        }
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

    private fun getFirebaseFileUrl(name: String, onEnd: (String?) -> Unit){
        val ref = FirebaseStorage.getInstance().reference.child("file/$name")
        viewModelScope.launch(Dispatchers.IO) {
            ref.downloadUrl.addOnCompleteListener {
                try {
                    onEnd(it.result.toString())
                }catch (e: Exception){
                    onEnd(null)
                }
            }.addOnFailureListener{
                onEnd(null)
            }
        }
    }

    fun handleDownloadClick(
        name: String,
        onStart: () -> Unit,
        onEnd: () -> Unit
    ){



        val root = File(Constant.DOWNLOAD_PATH)
        if (!root.exists()) {
            root.mkdirs()
        }

        getFirebaseFileUrl(name){ fileUrl ->
            fileUrl?.let {
                downloadFile(
                    fileName = name,
                    downloadPath = Constant.DOWNLOAD_PATH,
                    downloadUrl = fileUrl,
                    onStart,
                    onEnd
                )
            }
        }
    }

    private fun downloadFile(
        fileName: String,
        downloadPath: String,
        downloadUrl: String,
        onStart: () -> Unit,
        onEnd: () -> Unit
    ){

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
                    val progress = (total*100)/lengthOfFile
                    Log.e("tuan", progress.toString())
                    output.write(data, 0, count)
                }

                output.flush()
                output.close()
                input.close()

                withContext(Dispatchers.Main) {
                    onEnd()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onEnd()
                }

                Log.e("tuan", e.message.toString())
            }
        }
    }

    private fun checkFileMessageDownload(list: List<ChatMessage>): List<ChatMessage> {

        list.forEachIndexed { index, item ->
            if(item.message.type == Message.FILE){
                File("${Constant.DOWNLOAD_PATH}/${item.message.message.decodeBase64()}").run {
                    list[index].message.isDownloaded = exists()
                }
            }
        }

        return list
    }

}