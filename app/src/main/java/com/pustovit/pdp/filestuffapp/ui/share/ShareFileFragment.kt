package com.pustovit.pdp.filestuffapp.ui.share

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.pustovit.pdp.filestuffapp.R
import com.pustovit.pdp.filestuffapp.databinding.FragmentShareFileBinding
import java.io.File

class ShareFragment : Fragment() {

    private val fileProviderAuthorities: String by lazy { getString(R.string.file_provider_authorities) }

    private var binding: FragmentShareFileBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = with(FragmentShareFileBinding.inflate(inflater, container, false)) {
        binding = this
        initViews(this)
        root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun initViews(binding: FragmentShareFileBinding) = with(binding) {
        shareBinaryContentChooserButton.setOnClickListener {
            val uriForShare = getUriForShare()
            shareToChooser(uriForShare)
        }

        shareBinaryContentWhatsappButton.setOnClickListener {
            val uriForShare = getUriForShare()
            shareToWhatsApp(uriForShare)
        }
    }

    private fun getUriForShare(): Uri {
        val savedFilePath = requireActivity().cacheDir.toString() + "/" + "wWow123.jpg"
        val file = File(savedFilePath)
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.cat)

        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }

        val uriForShare: Uri =
            FileProvider.getUriForFile(
                requireContext(),
                fileProviderAuthorities,
                File(savedFilePath)
            )
        return uriForShare
    }

    private fun shareToChooser(uriForShare: Uri) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, "This is one image I'm sharing.")
        shareIntent.putExtra(Intent.EXTRA_STREAM, uriForShare)
        shareIntent.type = "image/*"
        val chooserIntent = Intent.createChooser(shareIntent, "Share...")

        val resInfoList: List<ResolveInfo> = requireContext().packageManager.queryIntentActivities(
            chooserIntent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            requireContext().grantUriPermission(
                packageName,
                uriForShare,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        startActivity(Intent.createChooser(shareIntent, "Share..."))
    }

    private fun shareToWhatsApp(uriForShare: Uri) {
        try {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_TEXT, "This is one image I'm sharing.")
            shareIntent.putExtra(Intent.EXTRA_STREAM, uriForShare)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            shareIntent.type = "image/*"
            shareIntent.setPackage("com.whatsapp")

            startActivity(shareIntent)

        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")
                )
            )
        }
    }

}