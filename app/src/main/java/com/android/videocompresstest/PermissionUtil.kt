package com.android.videocompresstest

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class PermissionUtil(
    private var context: Context?,
    private var activity: FragmentActivity?,
) {
    private var permissionResult: PermissionResult? = null

    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )

    fun checkStoragePermission(
        context: Context?,
        activity: FragmentActivity?,
        result: (Boolean) -> Unit
    ) {
        this.context = context
        this.activity = activity

        if (checkSelfPermission(permissions[0]) && checkSelfPermission(permissions[1]))
            result.invoke(true)
        else {
            requestPermission(arrayOf(permissions[0], permissions[1])) {
                if (it) {
                    result.invoke(true)
                } else {

                }
            }
        }
    }

    fun checkCameraPermission(
        context: Context?,
        activity: FragmentActivity?,
        result: (Boolean) -> Unit
    ) {
        this.context = context
        this.activity = activity

        if (checkSelfPermission(permissions[2]))
            result.invoke(true)
        else {
            requestPermission(arrayOf(permissions[2])) {
                if (it) {
                    result.invoke(true)
                } else {

                }
            }
        }
    }

    fun checkAudioPermission(
        context: Context?,
        activity: FragmentActivity?,
        result: (Boolean) -> Unit
    ) {
        this.context = context
        this.activity = activity

        if (checkSelfPermission(permissions[3]))
            result.invoke(true)
        else {
            requestPermission(arrayOf(permissions[3])) {
                if (it) {
                    result.invoke(true)
                } else {

                }
            }
        }
    }

    private fun checkSelfPermission(
        permission: String,
    ): Boolean {
        return context?.let {
            ContextCompat.checkSelfPermission(
                it,
                permission
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(
        permissions: Array<String>,
        result: (Boolean) -> Unit
    ) {
        this.permissionResult = result
        multiplePermission?.launch(permissions)
    }

    private val multiplePermission =
        activity?.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
            if (perms.values.none { !it }) {
                permissionResult?.invoke(true)
            } else {
                permissionResult?.invoke(false)
            }
        }
}

typealias  PermissionResult = (Boolean) -> Unit