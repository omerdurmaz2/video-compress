package com.android.videocompresstest

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.videocompresstest.videocompressor.VideoCompress
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var permissionUtil: PermissionUtil
    private lateinit var destFile: File
    lateinit var mediaController: MediaController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionUtil = PermissionUtil(this, this)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btnSelect).setOnClickListener {
            selectFile()
        }
        destFile = File.createTempFile("compressed", ".mp4")
        mediaController = MediaController(this)
        findViewById<VideoView>(R.id.vvCompVideo).apply {
            setMediaController(mediaController)
            setOnPreparedListener {
                it.start()
            }
            setOnErrorListener { _, _, _ ->
                false
            }
        }

    }

    private fun selectFile() {
        permissionUtil.checkStoragePermission(this, this) {
            getGalleryVideo.launch("video/*")
        }
    }

    private val getGalleryVideo =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                val file = getFileFromUri(uri)
                findViewById<VideoView>(R.id.vvCompVideo).setVideoURI(uri)
                compressVideo(file.path, destFile)
            }
        }

    private fun getFileFromUri(uri: Uri): File {
        val tempFile = File.createTempFile("Intro", ".mp4")
        tempFile.outputStream().use {
            contentResolver.openInputStream(uri)?.copyTo(it)
        }
        return tempFile
    }

    private fun compressVideo(srcFile: String, destFile: File) {
        VideoCompress.compressVideoLow(
            srcFile,
            destFile.path,
            object : VideoCompress.CompressListener {
                override fun onStart() {
                }

                override fun onSuccess() {
                    val file_size: Int =
                        java.lang.String.valueOf(destFile.length() / 1024).toInt()
                    findViewById<TextView>(R.id.tvProgress).text =
                        "Compressed file size: $file_size kb"
                    findViewById<VideoView>(R.id.vvCompVideo).setVideoURI(Uri.fromFile(destFile))
                }

                override fun onFail() {
                }

                override fun onProgress(percent: Float) {
                    findViewById<TextView>(R.id.tvProgress).text = "Progress $percent"
                }

            })
    }

    override fun onDestroy() {
        super.onDestroy()
        val files = cacheDir.listFiles()
        for (f in files) {
            f.delete()
        }
    }
}