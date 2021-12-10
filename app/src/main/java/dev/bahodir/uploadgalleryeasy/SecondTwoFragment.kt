package dev.bahodir.uploadgalleryeasy

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dev.bahodir.uploadgalleryeasy.adapter.RVAdapter
import dev.bahodir.uploadgalleryeasy.adapter.RVListAdapter
import dev.bahodir.uploadgalleryeasy.databinding.BottomSheetDialogBinding
import dev.bahodir.uploadgalleryeasy.databinding.FragmentSecondTwoBinding
import dev.bahodir.uploadgalleryeasy.db.DBHelper
import dev.bahodir.uploadgalleryeasy.roomdb.RoomDB
import dev.bahodir.uploadgalleryeasy.user.UserForRoom
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SecondTwoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SecondTwoFragment : BottomSheetDialogFragment() {
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
    private var _binding: FragmentSecondTwoBinding? = null
    private val binding get() = _binding!!

    private lateinit var rvAdapter: RVAdapter
    private lateinit var rvListAdapter: RVListAdapter
    private lateinit var roomDB: RoomDB

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSecondTwoBinding.inflate(inflater, container, false)
        roomDB = RoomDB.getInstance(requireContext())

        //val list = roomDB.roomDao().getList()

        rvListAdapter = RVListAdapter(object : RVListAdapter.OnItemTouchClickListener {
            override fun more(user: UserForRoom, position: Int, view: View) {
                val dialogBinding = BottomSheetDialogBinding.inflate(layoutInflater)
                val dialog = BottomSheetDialog(requireContext(),R.style.SheetDialog)

                //dialog.setCancelable(false)
                dialog.setContentView(dialogBinding.root)

                dialogBinding.profilePhoto.setImageURI(Uri.fromFile(File(user.photo)))
                dialogBinding.name.text = user.name
                dialogBinding.address.text = user.address

                var count1 = 0
                dialogBinding.call.setOnClickListener {
                    Dexter.withActivity(requireActivity())
                        .withPermission(android.Manifest.permission.CALL_PHONE)
                        .withListener(object : PermissionListener {
                            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                                Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_LONG).show()
                                startCall(user)
                            }

                            override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                                AlertDialog.Builder(requireContext())
                                    .setTitle("Permission")
                                    .setMessage("The task will not be performed if permission is not enabled")
                                    .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                                            dialogInterface, i ->
                                        dialogInterface.dismiss()
                                        token?.cancelPermissionRequest()
                                    })
                                    .setPositiveButton("OK", DialogInterface.OnClickListener {
                                            dialogInterface, i ->
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
                dialogBinding.sms.setOnClickListener {
                    dialog.dismiss()
                    val bundle = Bundle()
                    bundle.putSerializable("user", user)
                    findNavController().navigate(R.id.action_secondTwoFragment_to_secondTwoActionFragment, bundle)
                }

                dialog.show()
            }

        })
/*
        rvAdapter = RVAdapter(list, object : RVAdapter.OnItemTouchClickListener {
            override fun more(user: UserForRoom, position: Int, view: View) {

                val dialogBinding = BottomSheetDialogBinding.inflate(layoutInflater)
                val dialog = BottomSheetDialog(requireContext(),R.style.SheetDialog)

                //dialog.setCancelable(false)
                dialog.setContentView(dialogBinding.root)

                dialogBinding.profilePhoto.setImageURI(Uri.fromFile(File(user.photo)))
                dialogBinding.name.text = user.name
                dialogBinding.address.text = user.address

                var count1 = 0
                dialogBinding.call.setOnClickListener {
                    Dexter.withActivity(requireActivity())
                        .withPermission(android.Manifest.permission.CALL_PHONE)
                        .withListener(object : PermissionListener {
                            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                                Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_LONG).show()
                                startCall(user)
                            }

                            override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                                AlertDialog.Builder(requireContext())
                                    .setTitle("Permission")
                                    .setMessage("The task will not be performed if permission is not enabled")
                                    .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                                            dialogInterface, i ->
                                        dialogInterface.dismiss()
                                        token?.cancelPermissionRequest()
                                    })
                                    .setPositiveButton("OK", DialogInterface.OnClickListener {
                                            dialogInterface, i ->
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
                dialogBinding.sms.setOnClickListener {
                    dialog.dismiss()
                    val bundle = Bundle()
                    bundle.putSerializable("user", user)
                    findNavController().navigate(R.id.action_secondTwoFragment_to_secondTwoActionFragment, bundle)
                }

                dialog.show()
            }

        })*/
        binding.recycler.adapter = rvListAdapter

        roomDB
            .roomDao()
            .getList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                rvListAdapter.submitList(it)
            }, {

            }) {

            }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun startCall(user: UserForRoom? = null) {
        Toast.makeText(requireContext(), "start call", Toast.LENGTH_SHORT).show()
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel: ${user?.number}")
        startActivity(callIntent)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SecondTwoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SecondTwoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}