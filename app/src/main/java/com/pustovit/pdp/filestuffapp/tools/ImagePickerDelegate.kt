package com.pustovit.pdp.filestuffapp.tools

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ImagePickerDelegate(
    activity: AppCompatActivity,
    private val allowMultipleChoice: Boolean = false
) : ReadWriteProperty<AppCompatActivity, ValueCallback<Array<Uri>>?> {

    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null

    private var photosActivityResultCallback = PhotosActivityResultCallback(
        allowMultipleChoice = allowMultipleChoice,
        handleAttachedUri = ::handleAttachedUri
    )

    private val attachPhotosActivityResultLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        photosActivityResultCallback
    )

    override fun getValue(
        thisRef: AppCompatActivity,
        property: KProperty<*>
    ): ValueCallback<Array<Uri>>? {
        return mFilePathCallback
    }

    override fun setValue(
        thisRef: AppCompatActivity,
        property: KProperty<*>,
        value: ValueCallback<Array<Uri>>?
    ) {
        mFilePathCallback = null
        mFilePathCallback = value
        browseGalleries(allowMultipleChoice)
    }

    // Select image from galleries
    private fun browseGalleries(
        selectMultiplePictures: Boolean = false
    ) {
        val imageSelectionIntent = createImageSelectionIntent(selectMultiplePictures)
        attachPhotosActivityResultLauncher.launch(imageSelectionIntent)
    }

    private fun handleAttachedUri(arrayOfUri: Array<Uri>?) {
        mFilePathCallback?.onReceiveValue(arrayOfUri)
    }

    private class PhotosActivityResultCallback(
        var allowMultipleChoice: Boolean,
        val handleAttachedUri: (Array<Uri>?) -> Unit
    ) : ActivityResultCallback<ActivityResult> {

        override fun onActivityResult(result: ActivityResult?) {
            if (result == null) {
                handleAttachedUri(null)
                return
            }

            val attachedUri = mutableListOf<Uri>()
            if (result.resultCode == Activity.RESULT_OK) {
                // ClipData когда выбираем несколько картинок,
                // иногда срабатывает когда выбираем одну картинку,
                // в зависимости от версии Андройд
                val clipData = result.data?.clipData
                if (clipData != null && clipData.itemCount > 0) {
                    if (allowMultipleChoice) {
                        for (i in 0 until (clipData.itemCount)) {
                            val uri = clipData.getItemAt(i).uri
                            attachedUri.add(uri)
                        }
                    } else {
                        val uri = clipData.getItemAt(0).uri
                        attachedUri.add(uri)
                    }
                } else {
                    // it.data?.data - uri, когда выбираем одну картинку
                    result.data?.data?.let { uri ->
                        attachedUri.add(uri)
                    }
                }
                handleAttachedUri(attachedUri.toTypedArray())
            } else {
                handleAttachedUri(null)
            }
        }
    }

    private fun createImageSelectionIntent(allowMultipleChoice: Boolean = false): Intent {
        val chooseImageIntent = Intent(Intent.ACTION_PICK)
        chooseImageIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        chooseImageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultipleChoice)
        return Intent.createChooser(chooseImageIntent, "")
    }

}

interface ValueCallback<T> {
    fun onReceiveValue(value: T?)
}
