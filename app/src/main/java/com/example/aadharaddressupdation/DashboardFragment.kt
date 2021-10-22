package com.example.aadharaddressupdation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.aadharaddressupdation.databinding.FragmentDashboardBinding
import com.google.firebase.auth.FirebaseAuth

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding?=null
    private val binding get()=_binding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        firebaseAuth= FirebaseAuth.getInstance()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkUser()

    }

    private fun checkUser() {
        val firebaseuser=firebaseAuth.currentUser
        if(firebaseuser==null)
        {
            val action=DashboardFragmentDirections.actionDashboardFragmentToEnterOTP()
        //    findNavController().navigate(action)
        }
        else
        {
            val phone=firebaseuser.phoneNumber
            binding?.textBox?.text=phone
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
