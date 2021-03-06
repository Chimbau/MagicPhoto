package com.example.magicphoto

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.utils.ThumbnailItem
import com.zomato.photofilters.utils.ThumbnailsManager
import java.net.URI


const val CAMERA_IMAGE = 1
const val GALLERY_IMAGE = 2

class SelectImage : Fragment() {
    private lateinit var image: ImageView
    private lateinit var imageBitmap : Bitmap
    private lateinit var filteredBitmap : Bitmap
    private lateinit var save_button: ImageView
    private lateinit var addButton : ImageView
    private lateinit var discardButton : ImageView
    private lateinit var cameraButton: ImageView
    private lateinit var galleryButton: ImageView
    private lateinit var shareButton: ImageView
    private lateinit var recylerView: RecyclerView
    private lateinit var thumbnailList: MutableList<ThumbnailItem>
    private lateinit var adapter : FilterListAdapter
    private lateinit var filters: List<Filter>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        System.loadLibrary("NativeImageProcessor");
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        image = view.findViewById(R.id.image)
        imageBitmap = BitmapFactory.decodeResource(context?.getResources(), R.drawable.default_picture);
        filteredBitmap = imageBitmap
        save_button = view.findViewById(R.id.arrow_button)
        addButton = view.findViewById(R.id.check_button)
        discardButton = view.findViewById(R.id.cross_button)
        cameraButton = view.findViewById(R.id.cameraButton)
        galleryButton = view.findViewById(R.id.galleryButton)
        recylerView = view.findViewById(R.id.recyler_list)
        shareButton = view.findViewById(R.id.share_button)

        filters  = FilterPack.getFilterPack(activity)


        cameraButton.setOnClickListener {
            val cam = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (cam.resolveActivity(requireContext().packageManager) != null) {
                startActivityForResult(cam, CAMERA_IMAGE)
            }
        }
        galleryButton.setOnClickListener {
            startActivityForResult(Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }, GALLERY_IMAGE)
        }

        addButton.setOnClickListener {
            imageBitmap = filteredBitmap
            updateImages(imageBitmap)
            Toast.makeText(requireContext(), "Filter Applied", Toast.LENGTH_SHORT).show()
        }

        save_button.setOnClickListener{

//              startActivity(Intent(requireContext(), SaveShareActivity::class.java).apply{
//                  putExtra("image", imageBitmap)
//              })

              saveImage()
        }

        discardButton.setOnClickListener {
            imageBitmap = BitmapFactory.decodeResource(context?.getResources(), R.drawable.default_picture);
            filteredBitmap = imageBitmap
            updateImages(imageBitmap)
        }

        shareButton.setOnClickListener{
            if(isStoragePermissionGranted()){
                val imageURI = Uri.parse(MediaStore.Images.Media.insertImage(activity?.contentResolver, filteredBitmap, System.currentTimeMillis().toString() + ".jpg", "description"));
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, imageURI)
                    type = "image/*"
                }
                startActivity(Intent.createChooser(shareIntent, "Share with:"))
            }
        }

        thumbnailList = mutableListOf()

        for (filter in filters) {

            val item = ThumbnailItem()
            item.image = AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.default_picture
            )!!.toBitmap()

            item.filter = filter
            item.filterName = filter.name
            ThumbnailsManager.addThumb(item)
        }


        thumbnailList.addAll(ThumbnailsManager.processThumbs(activity))

        adapter = FilterListAdapter(::filterClick)
        adapter.setData(thumbnailList, imageBitmap)
        recylerView.adapter = adapter

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_IMAGE && resultCode == RESULT_OK) {
            imageBitmap = data?.extras?.get("data") as Bitmap
            filteredBitmap = imageBitmap
            updateImages(imageBitmap)


        } else if (requestCode == GALLERY_IMAGE && resultCode == RESULT_OK) {

            imageBitmap = MediaStore.Images.Media.getBitmap(
                context?.contentResolver,
                data?.data
            )
            filteredBitmap = imageBitmap
            updateImages(imageBitmap)

        }
    }


    private fun updateImages(imageBitmap: Bitmap){
        ThumbnailsManager.clearThumbs()

        for (filter in filters) {
            val item = ThumbnailItem()
            item.image = imageBitmap
            item.filter = filter
            item.filterName = filter.name
            ThumbnailsManager.addThumb(item)
        }
        image.setImageBitmap(imageBitmap)

        thumbnailList.clear()
        thumbnailList.addAll(ThumbnailsManager.processThumbs(activity))
        adapter.setData(thumbnailList, imageBitmap)
    }

    private fun filterClick(imageBitmap: Bitmap){
        image.setImageBitmap(imageBitmap)
        filteredBitmap = imageBitmap
    }
    private fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(this.context!!,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                === PermissionChecker.PERMISSION_GRANTED
            ) {

                true
            } else {
                Toast.makeText(this.context, "Couldn't save file, permission not granted", Toast.LENGTH_LONG).show()
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this.context, "Permission: " + permissions[0] + "was " + grantResults[0], Toast.LENGTH_LONG).show()
            //resume tasks needing this permission
        }
    }

    private fun saveImage() {


          MediaStore.Images.Media.insertImage(activity?.contentResolver, filteredBitmap, System.currentTimeMillis().toString() + ".jpg", "description");
          Toast.makeText(requireContext(), "Successfuly Saved", Toast.LENGTH_SHORT).show()



//        val dir = File(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "SaveImage")
//
//        if (!dir.exists()){
//            dir.mkdir();
//        }
//
//        val file = File(dir, System.currentTimeMillis().toString() + ".jpg")
//        var  outputStream : OutputStream? = null
//        try {
//            outputStream = FileOutputStream(file)
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        }
//
//        image.drawToBitmap().compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
//
//        Toast.makeText(requireContext(), "Successfuly Saved", Toast.LENGTH_SHORT).show()
//
//        try {
//            outputStream?.flush()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        try {
//            outputStream?.close()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//        MediaScannerConnection.scanFile(context, arrayOf(file.toString()), null,
//            OnScanCompletedListener { path, uri ->
//            })
    }


}