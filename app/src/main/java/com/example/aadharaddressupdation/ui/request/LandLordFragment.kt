package com.example.aadharaddressupdation.ui.request

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.aadharaddressupdation.R
import com.example.aadharaddressupdation.databinding.FragmentLandLordBinding
import com.example.aadharaddressupdation.models.User
import com.google.firebase.firestore.FirebaseFirestore


class LandLordFragment : Fragment() {

    private var _binding:FragmentLandLordBinding?=null
    private val binding get()=_binding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
          _binding = FragmentLandLordBinding.inflate(inflater, container, false)
          return binding?.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uid= arguments?.getString("uid").toString()
        val dataBase= FirebaseFirestore.getInstance()
        val userCollection=dataBase.collection("users").document(uid)
        var user: User
        userCollection.get().addOnSuccessListener { document->
            if(document!=null){
                user = document.toObject(User::class.java)!!
                if(user.inReq!="")
                {
                    binding?.showHere?.text="You have a request from : "+user.inReq
                    binding?.approveRequest?.visibility=View.VISIBLE
                    Toast.makeText(activity,"You have one pending request.",Toast.LENGTH_LONG).show()
                }
                else {
                    binding?.showHere?.text = getString(R.string.noResources)
                    binding?.approveRequest?.visibility=View.INVISIBLE
                    Toast.makeText(activity,"You currently don't have any requests.",Toast.LENGTH_SHORT).show()
                }
            }
        }
        // binding?.showHere?.text="${user.uid} ${user.inReq}"

        binding?.approveRequest?.setOnClickListener {
            val bundle= bundleOf("uid" to uid)
            findNavController().navigate(R.id.landLordEKyc1,bundle)

            // Toast.makeText(activity,"Haan ruko zara sabar karo",Toast.LENGTH_SHORT).show()
        }
    }
}