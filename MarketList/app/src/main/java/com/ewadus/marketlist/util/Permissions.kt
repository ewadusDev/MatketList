package com.ewadus.marketlist.util

import android.content.Context
import androidx.fragment.app.Fragment
import com.ewadus.marketlist.util.Constants.PERMISSION_READ_EXTERNAL_STORAGE
import com.vmadalin.easypermissions.EasyPermissions

object Permissions {

    fun requestReadExternalStoragePermission(fragment: Fragment) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            EasyPermissions.requestPermissions(
                fragment, "This app need to access photos and media on this device",
                PERMISSION_READ_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }


    fun hasReadExternalStoragePermission(context: Context): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            return EasyPermissions.hasPermissions(
                context, android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
        return true
    }

}