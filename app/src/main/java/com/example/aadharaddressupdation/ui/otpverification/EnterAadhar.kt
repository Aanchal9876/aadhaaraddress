package com.example.aadharaddressupdation.ui.otpverification

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Xml
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
import com.example.aadharaddressupdation.R
import com.example.aadharaddressupdation.databinding.FragmentEnterAadharBinding
import com.example.aadharaddressupdation.models.OtpRes
import com.google.firebase.auth.FirebaseAuth
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import org.xmlpull.v1.XmlSerializer
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import javax.xml.bind.JAXBElement


class EnterAadhar : Fragment() {
    private var _binding: FragmentEnterAadharBinding?=null
    private val binding get()=_binding
    private lateinit var firebaseAuth: FirebaseAuth


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

//        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
//        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
//        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        super.onViewCreated(view, savedInstanceState)
        val enteredNumber: EditText? = binding?.aadharNumber
        val progress: ProgressBar? = binding?.progressBar
        val contButton: Button? = binding?.continueButton
        firebaseAuth = FirebaseAuth.getInstance()

        binding?.continueButton?.setOnClickListener {
            if (enteredNumber != null) {
                if (enteredNumber.text.toString().trim().isNotEmpty()) {
                    if (enteredNumber.text.toString().trim().length == 12) {
                        progress?.visibility = View.VISIBLE
                        contButton?.visibility = View.INVISIBLE
                        val uid = enteredNumber.text.toString()
                        val txnId: String = UUID.randomUUID().toString()
                        val otpApiService=OtpAPIService()
                        otpApiService.readProperties();
                        val  ts= DateTimeFormatter.ISO_INSTANT.format(Instant.now())
                        val xmlSerializer: XmlSerializer? = Xml.newSerializer()
                        val xmlString:String=xmlSerializer!!.document {
                            element("Otp"){
                                attribute("uid",uid)
                                attribute("ac", R.string.AUTH_REQUEST_AUA.toString())
                                attribute("sa",R.string.AUTH_REQUEST_ASA.toString())
                                attribute("ver","2.5")
                                attribute("txn",txnId)
                                attribute("ts",ts)
                                attribute("lk",R.string.AUTH_REQUEST_AUA_LK.toString())
                                attribute("type","A")
                            }
                        }
                        val temp=XmlSigner();
                        temp.setupBouncyCastle();
                        val response=otpApiService.getParsedResponseFromOtpServer(xmlString,uid)
                        val xml_data: InputStream = ByteArrayInputStream(response.toByteArray())
                        val factory=XmlPullParserFactory.newInstance()
                        val parser=factory.newPullParser()
                        var result:String="n"
                        parser.setInput(xml_data,null);
                        var event=parser.eventType
                        while(event!=XmlPullParser.END_DOCUMENT)
                        {
                            val tagName=parser.name
                            when(event)
                            {
                                XmlPullParser.END_TAG->{
                                    if(tagName=="OtpRes")
                                    {
                                        result=parser.getAttributeName(0)
                                    }
                                }
                            }
                            event=parser.next()
                        }
                        if(result=="y")
                        {
                            binding?.tempid?.text="done"
                            val bundle= bundleOf("txnId" to txnId,"uid" to uid)
                            //val action=EnterAadharDirections.actionEnterOTPToEnterOTP2(amount,verificationId)
                            findNavController().navigate(R.id.enterOTP2,bundle)
                        }
                       else
                        {
                         Toast.makeText(activity, "Some error occurred, please try again after some time.", Toast.LENGTH_SHORT).show()
                     }



//                        val otpRes: OtpRes = otpApiService.getOtpRes(uid, txnId)
//                        val ans=otpRes.ret.value()
//                        if(ans=="y")
//                        {
//                            binding?.tempid?.text="done"
////                            val bundle= bundleOf("txnId" to txnId,"uid" to uid)
////                            //val action=EnterAadharDirections.actionEnterOTPToEnterOTP2(amount,verificationId)
////                            findNavController().navigate(R.id.enterOTP2,bundle)
//                        }
//                        else
//                        {
//                            Toast.makeText(activity, "Some error occurred, please try again after some time.", Toast.LENGTH_SHORT).show()
//                        }
                    }
                    else
                    {
                        Toast.makeText(activity, "Please Enter Correct Number", Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {
                    Toast.makeText(activity, "Enter Aadhar Number", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}