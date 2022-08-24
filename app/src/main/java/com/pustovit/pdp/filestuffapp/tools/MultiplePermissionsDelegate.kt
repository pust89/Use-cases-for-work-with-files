package com.pustovit.pdp.filestuffapp.tools

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class MultiplePermissionsDelegate(
    fragment: Fragment
) : ReadWriteProperty<Fragment, PermissionsValueCallback?> {

    private var mPermissionsValueCallback: PermissionsValueCallback? = null

    private val requestPermissions = fragment.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { map ->
        mPermissionsValueCallback?.onReceiveValue(map)
    }

    override fun getValue(
        thisRef: Fragment,
        property: KProperty<*>
    ): PermissionsValueCallback? {
        return mPermissionsValueCallback
    }

    override fun setValue(
        thisRef: Fragment,
        property: KProperty<*>,
        value: PermissionsValueCallback?
    ) {
        mPermissionsValueCallback = null
        mPermissionsValueCallback = value
        mPermissionsValueCallback?.requiredPermissions()?.let {
            if (it.isNotEmpty()) {
                requestPermissions.launch(it)
            }
        }
    }
}

interface PermissionsValueCallback {
    fun requiredPermissions(): Array<String>
    fun onReceiveValue(value: Map<String, Boolean>)
}

fun showAppSettingDialog(context: Context){
    AlertDialog.Builder(context)
        .setTitle("Permissions not granted!")
        .setMessage("Please grant permissions in app settings")
        .setPositiveButton(context.getString(android.R.string.ok)) { dialog, _ ->
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri: Uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)
            dialog.dismiss()
        }
        .create()
        .show()
}