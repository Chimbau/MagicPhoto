package com.example.magicphoto

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.utils.ThumbnailItem
import com.zomato.photofilters.utils.ThumbnailsManager

const val CAMERA_IMAGE = 1
const val GALLERY_IMAGE = 2

/**
 * A simple [Fragment] subclass.
 * Use the [SelectImage.newInstance] factory method to
 * create an instance of this fragment.
 */
class SelectImage : Fragment() {
    private lateinit var image: ImageView
    private lateinit var cameraButton: ImageView
    private lateinit var galleryButton: ImageView
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        image = view.findViewById(R.id.image)
        adapter = FilterListAdapter(image)
        cameraButton = view.findViewById(R.id.cameraButton)
        galleryButton = view.findViewById(R.id.galleryButton)
        recylerView = view.findViewById(R.id.recyler_list)
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


        thumbnailList = mutableListOf()

        for (filter in filters) {

            val item = ThumbnailItem()
            item.image = AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.camera1
            )!!.toBitmap()

            item.filter = filter
            item.filterName = filter.name
            ThumbnailsManager.addThumb(item)
        }

        thumbnailList.addAll(ThumbnailsManager.processThumbs(activity))
        adapter.setData(thumbnailList)
        recylerView.adapter = adapter

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_IMAGE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
//            cameraButton.setImageBitmap(imageBitmap)

            val imageEditor = Intent(this.context, ImageEditor::class.java).apply {
                putExtra("image", imageBitmap)
            }
            startActivity(imageEditor)

        } else if (requestCode == GALLERY_IMAGE && resultCode == RESULT_OK) {


            val imageBitmap = MediaStore.Images.Media.getBitmap(
                context?.contentResolver,
                data?.data
            )


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
            adapter.setData(thumbnailList)


        }
    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment SelectImage.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            SelectImage().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}