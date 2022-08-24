package com.pustovit.pdp.filestuffapp.ui.save

import android.Manifest
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.pustovit.pdp.filestuffapp.R
import com.pustovit.pdp.filestuffapp.databinding.FragmentSaveFileBinding
import com.pustovit.pdp.filestuffapp.tools.MultiplePermissionsDelegate
import com.pustovit.pdp.filestuffapp.tools.PermissionsValueCallback
import com.pustovit.pdp.filestuffapp.tools.showAppSettingDialog
import java.io.File

class SaveFileFragment : Fragment() {

    private val fileProviderAuthorities: String by lazy { getString(R.string.file_provider_authorities) }

    private var permissionValueCallback by MultiplePermissionsDelegate(this)

    private var binding: FragmentSaveFileBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = with(FragmentSaveFileBinding.inflate(inflater, container, false)) {
        binding = this
        initViews(this)
        root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun initViews(binding: FragmentSaveFileBinding) = with(binding) {
        saveBinaryContentButton.setOnClickListener {
            onSaveBinaryContentClick()
        }
    }

    private fun onSaveBinaryContentClick() {

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.cat)

        if (Build.VERSION.SDK_INT >= 29) {
            val values = contentValues()
            val uri: Uri? = requireContext().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )

            if (uri != null) {
                requireContext().contentResolver.openOutputStream(uri).use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }

                requireContext().contentResolver.update(uri, values, null, null)
                Toast.makeText(requireContext(),"Image saved!", Toast.LENGTH_LONG).show()
            }
        } else {
            permissionValueCallback = object : PermissionsValueCallback {

                override fun requiredPermissions(): Array<String> {
                    return arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }

                override fun onReceiveValue(value: Map<String, Boolean>) {
                    if (value[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true) {
                        val dirPath =
                            Environment.getExternalStorageDirectory().toString()
                        val directory = File(dirPath)
                        if (!directory.exists()) directory.mkdirs()
                        val fileName = System.currentTimeMillis().toString()
                        val file = File(directory, fileName)

                        file.outputStream().use {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                        }

                        file.absolutePath.let { absolutePath ->
                            val values = contentValues().apply {
                                put(MediaStore.Images.Media.DATA, absolutePath)
                            }
                            requireContext().contentResolver.insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                values
                            )
                        }
                        Toast.makeText(requireContext(),"Image saved!", Toast.LENGTH_LONG).show()
                    } else{
                        showAppSettingDialog(requireContext())
                    }
                }
            }
        }
    }

    private fun contentValues(): ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        return values
    }

}