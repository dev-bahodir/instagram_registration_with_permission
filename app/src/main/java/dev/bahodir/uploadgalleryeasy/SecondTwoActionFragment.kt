package dev.bahodir.uploadgalleryeasy

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.telephony.SmsManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dev.bahodir.uploadgalleryeasy.databinding.FragmentSecondTwoActionBinding
import dev.bahodir.uploadgalleryeasy.user.UserForRoom

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "user"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SecondTwoActionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SecondTwoActionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var user: UserForRoom? = null
    //private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getSerializable(ARG_PARAM1) as UserForRoom?
            //param2 = it.getString(ARG_PARAM2)
        }
    }
    private var _binding: FragmentSecondTwoActionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSecondTwoActionBinding.inflate(inflater, container, false)

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.name.text = user?.name
        binding.number.text = user?.number

        var count2 = 0
        binding.send.setOnClickListener {
            if (binding.message.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Message is empty", Toast.LENGTH_SHORT).show()
            }
            else {
                Dexter.withActivity(requireActivity())
                    .withPermission(Manifest.permission.SEND_SMS)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                            var smsManager = SmsManager.getDefault()
                            smsManager.sendTextMessage(user?.number,
                                null,
                                binding.message.text.toString(),
                                null,
                                null)
                            Toast.makeText(requireContext(), "Sms was sent successfully", Toast.LENGTH_SHORT)
                                .show()
                            findNavController().popBackStack()
                        }

                        override fun onPermissionDenied(p0: PermissionDeniedResponse?) {

                            if (count2 > 0) {
                                var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                var uri: Uri =
                                    Uri.fromParts("package", requireContext().packageName, null)
                                intent.data = uri
                                startActivity(intent)
                            }

                            count2++
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            p0: PermissionRequest?,
                            p1: PermissionToken?,
                        ) {
                            AlertDialog.Builder(requireContext())
                                .setTitle("Permission")
                                .setMessage("The task will not be performed if permission is not enabled")
                                .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                                        dialogInterface, i ->
                                    dialogInterface.dismiss()
                                    p1?.cancelPermissionRequest()
                                })
                                .setPositiveButton("OK", DialogInterface.OnClickListener {
                                        dialogInterface, i ->
                                    dialogInterface.dismiss()
                                    p1?.continuePermissionRequest()
                                })
                                .show()
                        }

                    }).check()
            }
        }

        return binding.root
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
         * @param user Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SecondTwoActionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(user: UserForRoom) =
            SecondTwoActionFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, user)
                    //putString(ARG_PARAM2, param2)
                }
            }
    }
}