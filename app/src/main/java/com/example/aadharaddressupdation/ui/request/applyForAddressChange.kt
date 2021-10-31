package com.example.aadharaddressupdation.ui.request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.aadharaddressupdation.dao.UserDao
import com.example.aadharaddressupdation.databinding.FragmentApplyForAddressChangeBinding
import com.example.aadharaddressupdation.models.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class applyForAddressChange : Fragment() {

    private var _binding: FragmentApplyForAddressChangeBinding?=null
    private val binding get()=_binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentApplyForAddressChangeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val enteredNumber=binding?.landlordAadhaarNumber
        val userUid= arguments?.getString("uid").toString()
        binding?.sendRequestButton?.setOnClickListener {
            if (enteredNumber != null) {
                if (enteredNumber.text.toString().trim().isNotEmpty()) {
                    if (enteredNumber.text.toString().trim().length == 12) {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Send Request?")
                            .setMessage("We will send a request to the aadhar number: ${enteredNumber.text}, "+
                                    "please ensure it is the correct Aadhar number or your request will not be processed.")
                            .setNegativeButton("No, change") { _, _ ->
                            }
                            .setPositiveButton("Yes") { _, _ ->
                                val dao=UserDao()
                                if(dao.checkReq(userUid)) {
                                    val dao = UserDao()
                                    dao.overwrite(
                                        User(
                                            userUid,
                                            "",
                                            enteredNumber.text.toString(),
                                            loggedIn = true,
                                            reqAccepted = false,
                                        )
                                    )
                                    dao.overwrite(
                                        User(
                                            enteredNumber.text.toString(),
                                            userUid,
                                            "",
                                            loggedIn = false,
                                            reqAccepted = false,
                                        )
                                    )
                                    binding?.sendRequestButton?.visibility = View.INVISIBLE
                                    Toast.makeText(
                                        activity,
                                        "Request sent successfully",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                else
                                {
                                    Toast.makeText(activity,"You can't make more than one requests at one time",Toast.LENGTH_SHORT).show()
                                }
                            }
                            .show()
                    }
                    else
                        Toast.makeText(activity, "Enter full Aadhar Number", Toast.LENGTH_SHORT).show()
                }
                else
                    Toast.makeText(activity, "Please Enter Correct Number", Toast.LENGTH_SHORT).show()
            }
            else
                Toast.makeText(activity,"Please enter Aadhar Number First", Toast.LENGTH_SHORT).show()
        }

        }

}