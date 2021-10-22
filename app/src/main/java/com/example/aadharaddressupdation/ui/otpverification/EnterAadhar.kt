package com.example.aadharaddressupdation.ui.otpverification

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.provider.ContactsContract
import android.text.format.Time
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aadharaddressupdation.R
import com.example.aadharaddressupdation.dao.UserDao
import com.example.aadharaddressupdation.databinding.FragmentDashboardBinding
import com.example.aadharaddressupdation.databinding.FragmentEnterAadharBinding
import com.example.aadharaddressupdation.models.User
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.ktx.Firebase
import org.json.JSONArray
import java.util.concurrent.TimeUnit

class EnterAadhar : Fragment() {
    private var _binding: FragmentEnterAadharBinding?=null
    private val binding get()=_binding
    private lateinit var mCallBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEnterAadharBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onStart() {
        super.onStart()
        val currentUser=firebaseAuth.currentUser
        if(currentUser!=null)
            updateUI(firebaseAuth)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val enteredNumber:EditText?=binding?.aadharNumber
        val progress:ProgressBar?=binding?.progressBar
        val contButton: Button?=binding?.continueButton
        firebaseAuth= FirebaseAuth.getInstance()

        binding?.continueButton?.setOnClickListener {

            if (enteredNumber != null) {
                if(enteredNumber.text.toString().trim().isNotEmpty()) {
                    if(enteredNumber.text.toString().trim().length==10)
                    {

                        progress?.visibility = View.VISIBLE
                        contButton?.visibility=View.INVISIBLE
                        val amount="+91"+enteredNumber.text.toString()

                        mCallBacks=object :PhoneAuthProvider.OnVerificationStateChangedCallbacks()
                        {
                            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                                signInWithPhoneAuthCredential(phoneAuthCredential)
                                updateUI(firebaseAuth)
                                Toast.makeText(activity,"Logged in as $amount",Toast.LENGTH_LONG).show()
                            }

                            override fun onVerificationFailed(e: FirebaseException) {
                                progress?.visibility = View.GONE
                                contButton?.visibility=View.VISIBLE
                                Toast.makeText(activity,"${e.message}",Toast.LENGTH_LONG).show()
                            }

                            override fun onCodeSent(
                                verificationId: String,
                                token: PhoneAuthProvider.ForceResendingToken
                            ) {
                                val bundle= bundleOf("amount" to amount,"id" to verificationId,"token" to token)
                                //val action=EnterAadharDirections.actionEnterOTPToEnterOTP2(amount,verificationId)
                                findNavController().navigate(R.id.enterOTP2,bundle)
                            }
                        }
                        startPhoneNumberVerification(amount)

                    }
                    else
                    {
                        Toast.makeText(activity,"Please Enter Correct Number",Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {
                    Toast.makeText(activity,"Enter Aadhar Number",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun startPhoneNumberVerification(phone:String)
    {
        val options=PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setCallbacks(mCallBacks)
            .setActivity(activity as Activity)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendVerificationCode(phone:String, token:PhoneAuthProvider.ForceResendingToken)
    {
        val options=PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setCallbacks(mCallBacks)
            .setForceResendingToken(token)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyPhoneNumberWithCode(verificationId:String,code:String)
    {
        val credential=PhoneAuthProvider.getCredential(verificationId,code)
        signInWithPhoneAuthCredential(credential)
    }
    private fun updateUI(firebaseAuth: FirebaseAuth)
    {
        val phone= firebaseAuth.currentUser?.phoneNumber.toString()
        val user=User(firebaseAuth.currentUser!!.uid,phone)
        val usersDao=UserDao()
        usersDao.addUser(user)
        Toast.makeText(activity,"Logged in as $phone",Toast.LENGTH_LONG).show()
        val action= EnterAadharDirections.actionEnterOTPToDashboardFragment2()
        findNavController().navigate(action)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                val phone= firebaseAuth.currentUser?.phoneNumber
                Toast.makeText(activity,"Logged in as $phone",Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {e->
                binding?.progressBar?.visibility = View.GONE
                binding?.continueButton?.visibility=View.VISIBLE
                Toast.makeText(activity,"${e.message}",Toast.LENGTH_LONG).show()
            }
    }

}