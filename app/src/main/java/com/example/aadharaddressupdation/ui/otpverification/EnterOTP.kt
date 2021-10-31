package com.example.aadharaddressupdation.ui.otpverification

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.aadharaddressupdation.R
import com.example.aadharaddressupdation.dao.UserDao
import com.example.aadharaddressupdation.databinding.FragmentEnterOTPBinding
import com.example.aadharaddressupdation.models.User
import org.json.JSONObject


class EnterOTP : Fragment() {

    private var _binding: FragmentEnterOTPBinding?=null
    private val binding get()=_binding
    // val args:EnterOTPArgs by navArgs()



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
        val progress: ProgressBar?=binding?.progressBar
        val uid=arguments?.getString("uid")
        val txnId=arguments?.getString("txnId").toString()
        binding?.showAadhar?.text = uid
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
                    checkOTP(uid!!,txnId,otpByUser)
                }
                else
                    Toast.makeText(activity,"Please Enter ALL numbers",Toast.LENGTH_SHORT).show()
            }
        }
        numberOTPMove()

//        binding?.resendOtp?.setOnClickListener{
//            binding?.progressBar?.visibility=View.GONE
//            binding?.continueButton?.visibility =View.VISIBLE
//            resendVerificationCode(amount.toString(),
//                token as PhoneAuthProvider.ForceResendingToken
//            )
//        }
    }


    private fun checkOTP(uid:String,txnId:String,otp:String)
    {
        val jsonObject = JSONObject()

        jsonObject.put("uid",uid)
        jsonObject.put("txnId",txnId)
        jsonObject.put("otp",otp)
        val url="https://stage1.uidai.gov.in/onlineekyc/getAuth"
        val que= Volley.newRequestQueue(context)
        val req= JsonObjectRequest(
            Request.Method.POST,url,jsonObject,
            { response->
                val status=response["status"].toString()
                if(status=="Y" || status=="y")
                {
                    val dao=UserDao()
                    val bundle= bundleOf("uid" to uid)
                    if(dao.addUser(User(uid,"","",loggedIn = true, reqAccepted = false)))
                        findNavController().navigate(R.id.dashboardFragment,bundle)
                    else
                        Toast.makeText(activity,"You're logged in through other device!",Toast.LENGTH_LONG).show()
                }
                else
                {
                    binding?.progressBar?.visibility = View.INVISIBLE
                    binding?.continueButton?.visibility = View.VISIBLE
                    if(response["errCode"].toString()=="998")
                        Toast.makeText(activity, "Invalid Aadhar Number", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(activity, "You entered the wrong OTP.", Toast.LENGTH_SHORT).show()
                }
            },{
                binding?.progressBar?.visibility = View.INVISIBLE
                binding?.continueButton?.visibility = View.VISIBLE
                Toast.makeText(activity, "Some error occurred, please try again after some time.", Toast.LENGTH_SHORT).show()
            }
        )
        que.add(req)
    }


     fun numberOTPMove() {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}