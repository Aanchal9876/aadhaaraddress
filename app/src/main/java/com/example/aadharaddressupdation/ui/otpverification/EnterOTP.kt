package com.example.aadharaddressupdation.ui.otpverification

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.aadharaddressupdation.dao.UserDao
import com.example.aadharaddressupdation.databinding.FragmentEnterOTPBinding
import com.example.aadharaddressupdation.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class EnterOTP : Fragment() {
    private lateinit var mCallBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var firebaseAuth: FirebaseAuth
    private var _binding: FragmentEnterOTPBinding?=null
    private val binding get()=_binding
    // val args:EnterOTPArgs by navArgs()

    override fun onStart() {
        super.onStart()
        val currentUser=firebaseAuth.currentUser
        if(currentUser!=null)
            updateUI(firebaseAuth)
    }

     override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEnterOTPBinding.inflate(inflater, container, false)
         return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inputNumber1:EditText?= binding?.box1
        val inputNumber2:EditText?= binding?.box2
        val inputNumber3:EditText?= binding?.box3
        val inputNumber4:EditText?= binding?.box4
        val inputNumber5:EditText?= binding?.box5
        val inputNumber6:EditText?= binding?.box6
        firebaseAuth= FirebaseAuth.getInstance()
        val progress: ProgressBar?=binding?.progressBar
        val amount=arguments?.getString("amount")
        val _otpCorrect=arguments?.getString("id")

        val token=arguments?.get("token")
        val otpCorrect=_otpCorrect.toString()
        binding?.showAadhar?.text = String.format("%s",amount)
        val button:Button?=binding?.continueButton

        button?.setOnClickListener {
            if (inputNumber1 != null && inputNumber2 != null &&inputNumber3 != null &&inputNumber4 != null &&inputNumber5 != null && inputNumber6 != null) {
                if(inputNumber1.text.toString().trim().isNotEmpty() && inputNumber2.text.toString().trim().isNotEmpty() && inputNumber3.text.toString().trim().isNotEmpty() && inputNumber4.text.toString().trim().isNotEmpty() && inputNumber5.text.toString().trim().isNotEmpty() && inputNumber6.text.toString().trim().isNotEmpty()) {

                    val otpByUser = binding?.box1?.text.toString() +
                            binding?.box2?.text.toString() +
                            binding?.box3?.text.toString() +
                            binding?.box4?.text.toString() +
                            binding?.box5?.text.toString() +
                            binding?.box6?.text.toString()

                    progress?.visibility=View.VISIBLE
                    button.visibility =View.INVISIBLE

                    verifyPhoneNumberWithCode(otpCorrect,otpByUser)
                }
                else
                    Toast.makeText(activity,"Please Enter ALL numbers",Toast.LENGTH_SHORT).show()
            }
        }
        numberOTPMove()

        binding?.resendOtp?.setOnClickListener{
            binding?.progressBar?.visibility=View.GONE
            binding?.continueButton?.visibility =View.VISIBLE
            resendVerificationCode(amount.toString(),
                token as PhoneAuthProvider.ForceResendingToken
            )
        }
    }

    private fun numberOTPMove() {
        val inputNumber1:EditText?= binding?.box1
        val inputNumber2:EditText?= binding?.box2
        val inputNumber3:EditText?= binding?.box3
        val inputNumber4:EditText?= binding?.box4
        val inputNumber5:EditText?= binding?.box5
        val inputNumber6:EditText?= binding?.box6

        inputNumber1?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().isNotEmpty())
                    inputNumber2?.requestFocus()
            }
        })

        inputNumber2?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().trim().isNotEmpty())
                    inputNumber3?.requestFocus()
            }
        })

        inputNumber3?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().trim().isNotEmpty())
                    inputNumber4?.requestFocus()
            }
        })

        inputNumber4?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().trim().isNotEmpty())
                    inputNumber5?.requestFocus()
            }
        })

        inputNumber5?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().trim().isNotEmpty())
                    inputNumber6?.requestFocus()
            }
        })

    }

    private fun startPhoneNumberVerification(phone:String)
    {
        val options=PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setCallbacks(mCallBacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendVerificationCode(phone:String, token:PhoneAuthProvider.ForceResendingToken)
    {

        val options=PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setCallbacks(mCallBacks)
            .setActivity(activity as Activity)
            .setForceResendingToken(token)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyPhoneNumberWithCode(verificationId:String,code:String)
    {
        val credential=PhoneAuthProvider.getCredential(verificationId,code)
        signInWithPhoneAuthCredential(credential)

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
               updateUI(firebaseAuth)
            }
            .addOnFailureListener {e->
                binding?.progressBar?.visibility=View.GONE
                binding?.continueButton?.visibility =View.VISIBLE
                Toast.makeText(activity,"${e.message}",Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUI(firebaseAuth: FirebaseAuth)
    {
        val phone= firebaseAuth.currentUser?.phoneNumber.toString()
        val user=User(firebaseAuth.currentUser!!.uid,phone)
        val usersDao=UserDao()
        usersDao.addUser(user)
        Toast.makeText(activity,"Logged in as $phone",Toast.LENGTH_LONG).show()
        val action=EnterOTPDirections.actionEnterOTP2ToDashboardFragment()
        findNavController().navigate(action)
    }
}