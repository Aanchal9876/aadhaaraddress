package com.example.aadharaddressupdation.ui.request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.aadharaddressupdation.R
import com.example.aadharaddressupdation.databinding.FragmentCheckStatusBinding
import com.example.aadharaddressupdation.models.User
import com.google.firebase.firestore.FirebaseFirestore

class CheckStatus : Fragment() {

    private var _binding:FragmentCheckStatusBinding?=null
    val binding get()= _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentCheckStatusBinding.inflate(inflater,container,false)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val uid=arguments?.getString("uid").toString()
        super.onViewCreated(view, savedInstanceState)
        val dataBase= FirebaseFirestore.getInstance()
        val userCollection=dataBase.collection("users").document(uid)
        var user: User
        var landLordUid=""
        userCollection.get()
            .addOnSuccessListener {
                document->
                if(document!=null)
                {
                    user=document.toObject(User::class.java)!!
                    if(user.outReq=="")
                    {
                        binding?.statusText?.text="You don't have any requests made."
                        binding?.progressBar?.visibility=View.INVISIBLE
                    }
                    else
                    {
                        if(user.reqAccepted)
                        {
                            binding?.statusText?.text="Your request has been accepted by:"
                            binding?.showId?.text=user.outReq
                            landLordUid=user.outReq
                            binding?.sendRequestButton?.visibility=View.VISIBLE
                        }
                        else
                        {
                            binding?.statusText?.text="Your request has not been yet accepted by:"
                            binding?.showId?.text=user.outReq
                            binding?.progressBar?.visibility=View.INVISIBLE
                        }
                    }
                }
            }

        binding?.sendRequestButton?.setOnClickListener{
            val bundle= bundleOf("uid" to uid,"landLordUid" to landLordUid)
            findNavController().navigate(R.id.checkStatusSc2,bundle)
        }

    }
}