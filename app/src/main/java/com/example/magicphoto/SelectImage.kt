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
import androidx.fragment.app.Fragment
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

const val CAMERA_IMAGE = 1
const val GALLERY_IMAGE = 2
/**
 * A simple [Fragment] subclass.
 * Use the [SelectImage.newInstance] factory method to
 * create an instance of this fragment.
 */
class SelectImage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var cameraButton : ImageView
    private lateinit var galleryButton : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }



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
        cameraButton = view.findViewById(R.id.cameraButton)
        galleryButton = view.findViewById(R.id.galleryButton)


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

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_IMAGE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
//            cameraButton.setImageBitmap(imageBitmap)

            val imageEditor = Intent(this.context, ImageEditor::class.java).apply{
                putExtra( "image", imageBitmap)
            }
            startActivity(imageEditor)

        }else if(requestCode == GALLERY_IMAGE && resultCode == RESULT_OK){


            val imageBitmap = MediaStore.Images.Media.getBitmap(
                context?.contentResolver,
                data?.data
            )

            val bs = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bs)


//            galleryButton.setImageBitmap(imageBitmap)

            val imageEditor = Intent(this.context, ImageEditor::class.java).apply{
                putExtra("image", bs.toByteArray())
            }
            startActivity(imageEditor)

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SelectImage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SelectImage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}