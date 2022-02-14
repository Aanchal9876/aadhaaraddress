package com.example.aadharaddressupdation.ui.otpverification

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.aadharaddressupdation.R
import com.example.aadharaddressupdation.databinding.FragmentEnterAadharBinding
import org.json.JSONObject
import java.util.*


class EnterAadhar : Fragment() {
    private var _binding: FragmentEnterAadharBinding?=null
    private val binding get()=_binding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEnterAadharBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val enteredNumber: EditText? = binding?.aadharNumber
        val progress: ProgressBar? = binding?.progressBar
        val contButton: Button? = binding?.continueButton

        binding?.continueButton?.setOnClickListener {
            if (enteredNumber != null) {
                if (enteredNumber.text.toString().trim().isNotEmpty()) {
                    if (enteredNumber.text.toString().trim().length == 12) {
                        progress?.visibility = View.VISIBLE
                        contButton?.visibility = View.INVISIBLE
                        val uid = enteredNumber.text.toString()
                        sendOTP(uid)
                    }
                        else
                        {
                            Toast.makeText(activity, "Enter full Aadhar Number", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else
                    {
                        Toast.makeText(activity, "Please Enter Correct Number", Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {
                    Toast.makeText(activity,"Please enter Aadhar Number First",Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun sendOTP(uid:String)
    {
        val tran=UUID.randomUUID().toString()
        val jsonObject = JSONObject()
        jsonObject.put("uid",uid)
        jsonObject.put("txnId", tran)
        val url="https://stage1.uidai.gov.in/onlineekyc/getOtp"
        val que= Volley.newRequestQueue(context)
        val req=JsonObjectRequest(Request.Method.POST,url,jsonObject,
            { response->
                val status=response["status"].toString()
                if(status=="Y" || status=="y")
                {
                    val bundle= bundleOf("uid" to uid,"txnId" to tran)
                    //val action=EnterAadharDirections.actionEnterOTPToEnterOTP2(amount,verificationId)
                    findNavController().navigate(R.id.enterOTP2,bundle)
                }
                else
                {
                    binding?.progressBar?.visibility = View.INVISIBLE
                    binding?.continueButton?.visibility = View.VISIBLE
                    val dikkat=response["errCode"].toString()
                    if(dikkat=="998")
                        Toast.makeText(activity, "Incorrect Aadhar number", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(activity, "Unknown error occurred, please try again after some time", Toast.LENGTH_SHORT).show()
                }
            },{
                binding?.progressBar?.visibility = View.INVISIBLE
                binding?.continueButton?.visibility = View.VISIBLE
                Toast.makeText(activity, "Some error occurred on our end: ${it}, please try again after some time.", Toast.LENGTH_SHORT).show()
            }
        )
        que.add(req)
    }


//    private fun setImage(base64String: String)
//    {
//        val byteArrayOutputStream = ByteArrayOutputStream()
//        var imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
//        imageBytes = android.util.Base64.decode(base64String, DEFAULT)
//        val decodedImage =
//            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
//        binding?.captchaImage?.setImageBitmap(decodedImage)
//    }
//
//    private fun loadCaptcha()
//    {
//        var txnID:String=""
//        var base64String:String=""
//        var status:String=""
//        var statusCode=""
//        val jsonObject = JSONObject()
//        jsonObject.put("langCode", "en")
//        jsonObject.put("captchaLength", "3")
//        jsonObject.put("captchaType", "2")
//        val url="https://stage1.uidai.gov.in/unifiedAppAuthService/api/v2/get/captcha"
//        val que= Volley.newRequestQueue(context)
//        val req=JsonObjectRequest(Request.Method.POST,url,jsonObject,
//            { response->
//                txnID=response["captchaTxnId"].toString()
//                base64String=response["captchaBase64String"].toString()
//                status=response["status"].toString()
//                statusCode=response["statusCode"].toString()
//                Toast.makeText(activity, "$status:status, code: $statusCode", Toast.LENGTH_SHORT).show()
//                setImage(base64String)
//            },{
//                Toast.makeText(activity, "Some error occurred, please try again after some time.", Toast.LENGTH_SHORT).show()
//            }
//        )
//        que.add(req)
//    }
//
//
//    private fun verifyCaptchaAndGetOtp(txnId:String,uid:String, captchaValue:String)
//    {
//        val tran="MYAADHAAR:"+UUID.randomUUID().toString()
//        val jsonObject = JSONObject()
//        jsonObject.put("uidNumber", uid)
//        jsonObject.put("captchaTxnId", txnId)
//        jsonObject.put("captchaValue", captchaValue)
//        jsonObject.put("transactionId", tran)
//        val url="https://stage1.uidai.gov.in/unifiedAppAuthService/api/v2/generate/aadhaar/otp"
//        val que= Volley.newRequestQueue(context)
//        val req=JsonObjectRequest(Request.Method.POST,url,jsonObject,
//            { response->
//                val status=response["status"].toString()
//                val toTran=response["txnId"].toString()
//                if(status=="Success")
//                {
//                    val bundle= bundleOf("uid" to uid,"txnId" to toTran)
//                    //val action=EnterAadharDirections.actionEnterOTPToEnterOTP2(amount,verificationId)
//                    findNavController().navigate(R.id.enterOTP2,bundle)
//                }
//                else
//                {
//                    binding?.captchaValue?.text?.clear();
//                    binding?.aadharNumber?.text?.clear();
//                    Toast.makeText(activity, "I guess you're not human after all.", Toast.LENGTH_SHORT).show()
//                }
//
//            },{
//                Toast.makeText(activity, "Some error occurred, please try again after some time.", Toast.LENGTH_SHORT).show()
//            }
//        )
//        que.add(req)
//
//    }



}


