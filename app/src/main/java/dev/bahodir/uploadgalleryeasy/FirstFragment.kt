package dev.bahodir.uploadgalleryeasy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import dev.bahodir.uploadgalleryeasy.databinding.FragmentFirstBinding
import dev.bahodir.uploadgalleryeasy.db.DBHelper
import dev.bahodir.uploadgalleryeasy.roomdb.RoomDB
import dev.bahodir.uploadgalleryeasy.user.User
import dev.bahodir.uploadgalleryeasy.user.UserForRoom
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FirstFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FirstFragment : Fragment() {
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

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private lateinit var roomDB: RoomDB
    lateinit var list: MutableList<UserForRoom>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        roomDB = RoomDB.getInstance(requireContext())

        //val list = roomDB.roomDao().getList().toList().map { it.flatten() }
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


        binding.apply {
            signUp.setOnClickListener {
                findNavController().navigate(R.id.action_firstFragment_to_secondOneFragment)
            }

            signIn.setOnClickListener {
                val numbers = phoneNumber.text.toString()
                val passwords = password.text.toString()

                if (numbers.isEmpty() || passwords.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Fill in all of the fields",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    var bool = false
                    for (i in list.indices) {
                        if (numbers == list[i].number && passwords == list[i].password) {
                            bool = true
                        }
                    }
                    if (bool) {
                        findNavController().navigate(R.id.action_firstFragment_to_secondTwoFragment)
                        phoneNumber.setText("")
                        password.setText("")
                    } else {
                        Toast.makeText(requireContext(), "Phone number or password entered incorrectly", Toast.LENGTH_SHORT).show()
                    }
                }
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
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FirstFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FirstFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}