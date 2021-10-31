package com.example.aadharaddressupdation.ui.request

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.aadharaddressupdation.R
import com.example.aadharaddressupdation.databinding.FragmentLandLordBinding
import com.example.aadharaddressupdation.databinding.FragmentLandLordEKyc1Binding
import com.example.aadharaddressupdation.ui.otpverification.EnterOTP
import org.json.JSONObject
import java.util.*

class LandLordEKyc1 : Fragment() {

    private var _binding: FragmentLandLordEKyc1Binding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLandLordEKyc1Binding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uid = arguments?.getString("uid").toString()
        val tran = UUID.randomUUID().toString()
        binding?.currentAadhar?.text=uid
        binding?.continueButton?.setOnClickListener {
            sendOTP(uid,tran)
        }
        val inputNumber1: EditText?= binding?.box1
        val inputNumber2: EditText?= binding?.box2
        val inputNumber3: EditText?= binding?.box3
        val inputNumber4: EditText?= binding?.box4
        val inputNumber5: EditText?= binding?.box5
        val inputNumber6: EditText?= binding?.box6
        binding?.getXml?.setOnClickListener {
            if (inputNumber1 != null && inputNumber2 != null &&inputNumber3 != null &&inputNumber4 != null &&inputNumber5 != null && inputNumber6 != null) {
                if(inputNumber1.text.toString().trim().isNotEmpty() && inputNumber2.text.toString().trim().isNotEmpty() && inputNumber3.text.toString().trim().isNotEmpty() && inputNumber4.text.toString().trim().isNotEmpty() && inputNumber5.text.toString().trim().isNotEmpty() && inputNumber6.text.toString().trim().isNotEmpty()) {

                    val otpByUser = binding?.box1?.text.toString() +
                            binding?.box2?.text.toString() +
                            binding?.box3?.text.toString() +
                            binding?.box4?.text.toString() +
                            binding?.box5?.text.toString() +
                            binding?.box6?.text.toString()
                    binding?.progressBar?.visibility=View.VISIBLE
                    binding?.getXml?.visibility =View.INVISIBLE
                    // checkOTP(uid!!,txnId,otpByUser)
                    getKyc(uid,tran,otpByUser)
                }
                else
                    Toast.makeText(activity,"Please Enter ALL numbers",Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun sendOTP(uid: String,tran:String) {
        binding?.continueButton?.visibility=View.INVISIBLE
        binding?.progressBar?.visibility=View.VISIBLE
        val jsonObject = JSONObject()
        jsonObject.put("uid", uid)
        jsonObject.put("txnId", tran)
        val url = "https://stage1.uidai.gov.in/onlineekyc/getOtp"
        val que = Volley.newRequestQueue(context)
        val req = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                val status = response["status"].toString()
                if (status == "Y" || status == "y") {
                    binding?.otpBox?.visibility=View.VISIBLE
                    binding?.progressBar?.visibility=View.INVISIBLE
                    binding?.getXml?.visibility=View.VISIBLE
                    binding?.changeText?.text="Please enter the OTP you received"
//
//                    val bundle = bundleOf("uid" to uid, "txnId" to tran)
//                    //val action=EnterAadharDirections.actionEnterOTPToEnterOTP2(amount,verificationId)
//                    findNavController().navigate(R.id.enterOTP2, bundle)
                } else {
                    binding?.progressBar?.visibility = View.INVISIBLE
                    binding?.continueButton?.visibility = View.VISIBLE
                    val prob = response["errCode"].toString()
                    if (prob == "998")
                        Toast.makeText(activity, "Incorrect Aadhar number", Toast.LENGTH_SHORT)
                            .show()
                    else
                        Toast.makeText(
                            activity,
                            "Unknown error occurred, please try again after some time",
                            Toast.LENGTH_SHORT
                        ).show()
                }
            }, {
                binding?.progressBar?.visibility = View.INVISIBLE
                binding?.continueButton?.visibility = View.VISIBLE
                Toast.makeText(
                    activity,
                    "Some error occurred on our end, please try again after some time.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        que.add(req)
        EnterOTP().numberOTPMove()
    }


    private fun getKyc(uid: String, txnId: String, otp: String)
    {
        val jsonObject = JSONObject()
        jsonObject.put("uid", uid)
        jsonObject.put("txnId", txnId)
        jsonObject.put("otp", otp)
        val url = "https://stage1.uidai.gov.in/onlineekyc/getEkyc"
        val que = Volley.newRequestQueue(context)
        val req = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                val status = response["status"].toString()
                if (status == "Y" || status == "y") {
                        val eKyc=response["eKycString"]
                        binding?.forEkyc?.text= eKyc as CharSequence?
                        val bundle= bundleOf("uid" to uid,"eKyc" to eKyc)
                        findNavController().navigate(R.id.showAadharDesigned,bundle)

                } else {
                    binding?.progressBar?.visibility = View.INVISIBLE
                    binding?.getXml?.visibility = View.VISIBLE
                    val prob = response["errCode"].toString()
                    if (prob == "998")
                        Toast.makeText(activity, "Incorrect Aadhar number", Toast.LENGTH_SHORT)
                            .show()
                    else
                        Toast.makeText(
                            activity,
                            "Unknown error occurred, please try again after some time.",
                            Toast.LENGTH_SHORT
                        ).show()
                }
            }, {
                binding?.progressBar?.visibility = View.INVISIBLE
                binding?.continueButton?.visibility = View.VISIBLE
                Toast.makeText(
                    activity,
                    "Some error occurred on our end: ${it}, please try again after some time.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        que.add(req)
    }
}