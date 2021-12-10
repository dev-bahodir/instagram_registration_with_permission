package dev.bahodir.uploadgalleryeasy

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dev.bahodir.uploadgalleryeasy.databinding.DialogLayoutBinding
import dev.bahodir.uploadgalleryeasy.databinding.FragmentSecondOneBinding
import dev.bahodir.uploadgalleryeasy.db.DBHelper
import dev.bahodir.uploadgalleryeasy.roomdb.RoomDB
import dev.bahodir.uploadgalleryeasy.user.User
import dev.bahodir.uploadgalleryeasy.user.UserForRoom
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.jar.Manifest
import kotlin.jvm.Throws

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SecondOneFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SecondOneFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private var _binding: FragmentSecondOneBinding? = null
    private val binding get() = _binding!!
    private lateinit var image: String
    private lateinit var roomDB: RoomDB
    private lateinit var list: MutableList<UserForRoom>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSecondOneBinding.inflate(inflater, container, false)

        roomDB = RoomDB.getInstance(requireContext())

        roomDB
            .roomDao()
            .getList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                list = it
            }, {

            }) {

            }

        binding.addPhoto.setOnClickListener {
            var dialog  = AlertDialog.Builder(requireContext())
            var dialogBinding = DialogLayoutBinding.inflate(layoutInflater)
            dialog.setView(dialogBinding.root)

            var alert = dialog.create()

            var count1 = 0
            dialogBinding.gallery.setOnClickListener {
                Dexter.withActivity(requireActivity())
                    .withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                            Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_LONG)
                                .show()
                            getImage.launch("image/*")
                            alert.dismiss()
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            permission: PermissionRequest?,
                            token: PermissionToken?
                        ) {
                            AlertDialog.Builder(requireContext())
                                .setTitle("Permission")
                                .setMessage("The task will not be performed if permission is not enabled")
                                .setNegativeButton(
                                    "Cancel",
                                    DialogInterface.OnClickListener { dialogInterface, i ->
                                        dialogInterface.dismiss()
                                        token?.cancelPermissionRequest()
                                    })
                                .setPositiveButton(
                                    "OK",
                                    DialogInterface.OnClickListener { dialogInterface, i ->
                                        dialogInterface.dismiss()
                                        token?.continuePermissionRequest()
                                    })
                                .show()
                        }

                        override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                            if (count1 > 0) {
                                var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                var uri: Uri =
                                    Uri.fromParts("package", requireContext().packageName, null)
                                intent.data = uri
                                startActivity(intent)
                            }
                            count1++
                        }
                    }
                    ).check()
            }
            var count2 = 0
            dialogBinding.camera.setOnClickListener {
                Dexter.withActivity(requireActivity())
                    .withPermission(android.Manifest.permission.CAMERA)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                            Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_LONG)
                                .show()
                            //newCamera()
                            onResult()
                            alert.dismiss()
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            permission: PermissionRequest?,
                            token: PermissionToken?
                        ) {
                            AlertDialog.Builder(requireContext())
                                .setTitle("Permission")
                                .setMessage("The task will not be performed if permission is not enabled")
                                .setNegativeButton(
                                    "Cancel",
                                    DialogInterface.OnClickListener { dialogInterface, i ->
                                        dialogInterface.dismiss()
                                        token?.cancelPermissionRequest()
                                    })
                                .setPositiveButton(
                                    "OK",
                                    DialogInterface.OnClickListener { dialogInterface, i ->
                                        dialogInterface.dismiss()
                                        token?.continuePermissionRequest()
                                    })
                                .show()
                        }

                        override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                            if (count2 > 0) {
                                var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                var uri: Uri =
                                    Uri.fromParts("package", requireContext().packageName, null)
                                intent.data = uri
                                startActivity(intent)
                            }
                            count2++
                        }
                    }
                    ).check()
            }

            alert.show()
        }



        binding.signUp.setOnClickListener {
            val name = binding.nameSurname.text.toString()
            val number = binding.phoneNumber.text.toString()
            val country = binding.spin.selectedItem.toString()
            val address = binding.address.text.toString()
            val password = binding.password.text.toString()

            if (name.isEmpty() && number.isEmpty() && address.isEmpty() && password.isEmpty()) {
                Toast.makeText(requireContext(), "Fill in all of the fields", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (image == null) {
                    Toast.makeText(
                        requireContext(),
                        "No image selected for user",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    var bool = true
                    for (i in list.indices) {
                        if (number == list[i].number) {
                            bool = false
                            Toast.makeText(
                                requireContext(),
                                "Phone number must be unique",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    if (bool) {
                        roomDB.roomDao().add(
                            UserForRoom(
                                name = name,
                                number = number,
                                country = country,
                                address = address,
                                password = password,
                                photo = image
                            )
                        )
                        findNavController().popBackStack()
                    }
                }
            }
        }

        return binding.root
    }

    private fun onResult() {
        val photoFile = try {
            createImageFile()
        }
        catch (e: Exception) {
            null
        }
        photoFile?.also{
            val uri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID, it)
            // ?: return@also
            getImageCamera.launch(uri)
        }
    }

    private fun newCamera() {
        val photoFile = try {
            createImageFile()
        }
        catch (e: Exception) {
            null
        }
        photoFile?.also{
            val uri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID, it)
               // ?: return@also
            getImageCamera.launch(uri)
        }
    }
    private fun oldCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireContext().packageManager)

        val photoFile = try {
            createImageFile()
        }
        catch (e: Exception) {
            null
        }
        photoFile?.also {
            val uri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID, it)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            startActivityForResult(intent, 101)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101) {
            if (::image.isInitialized) {
                binding.profilePhoto.setImageURI(Uri.fromFile(File(image)))
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val m = System.currentTimeMillis()
        val externalFilesDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile("picture_$m", ".jpg", externalFilesDir)
            .apply {
                image = absolutePath
            }
    }

    private val getImageCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            binding.profilePhoto.setImageURI(Uri.fromFile(File(image)))
        }
    }

    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) {
            return@registerForActivityResult
        }
        binding.profilePhoto.setImageURI(uri)

        val openInputStream = requireContext().contentResolver?.openInputStream(uri)
        val m = System.currentTimeMillis()
        val file = File(requireContext().filesDir, "$m.jpg")
        val fileOutputStream = FileOutputStream(file)
        openInputStream?.copyTo(fileOutputStream)
        openInputStream?.close()
        fileOutputStream.close()
        image = file.absolutePath
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SecondOneFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SecondOneFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}