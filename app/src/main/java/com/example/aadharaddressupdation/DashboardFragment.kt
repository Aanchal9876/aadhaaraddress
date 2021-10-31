package com.example.aadharaddressupdation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.ACTION_DOWN
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.aadharaddressupdation.databinding.FragmentDashboardBinding
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController


class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding?=null
    private val binding get()=_binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button even
                    Toast.makeText(context,"Tap the sign-out button to go back",Toast.LENGTH_SHORT).show()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userUid=arguments?.getString("uid")
        binding?.textBox?.text= "Currently logged in as: $userUid"
        val bundle= bundleOf("uid" to userUid)
        binding?.buttonApply?.setOnClickListener{
            //val action=EnterAadharDirections.actionEnterOTPToEnterOTP2(amount,verificationId)
            findNavController().navigate(R.id.applyForAddressChange,bundle)
        }

        binding?.buttonCheckRequests?.setOnClickListener {
            findNavController().navigate(R.id.landLordFragment,bundle)
        }

        binding?.buttonCheckStatus?.setOnClickListener{
            findNavController().navigate(R.id.checkStatus,bundle)
        }

        binding?.signOut?.setOnClickListener {
            findNavController().navigate(R.id.enterAadhar)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
