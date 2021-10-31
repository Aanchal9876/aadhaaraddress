package com.example.aadharaddressupdation.ui.request

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64.DEFAULT
import android.util.Base64.decode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.aadharaddressupdation.R
import com.example.aadharaddressupdation.dao.UserDao
import com.example.aadharaddressupdation.databinding.FragmentShowAadharDesignedBinding
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ShowAadharDesigned : Fragment() {

    private var _binding:FragmentShowAadharDesignedBinding?=null
    private val binding get()=_binding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowAadharDesignedBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val eKyc=arguments?.getString("eKyc").toString()
        val inputStream: InputStream = eKyc.byteInputStream()
        val factory=XmlPullParserFactory.newInstance()
        val parser=factory.newPullParser()
        parser.setInput(inputStream,null)
        var event=parser.eventType
        /**
         * country=\"India\" dist=\"Bengaluru\" house=\"#307, 3rdMain Road\"
        lm=\"Opposite to balaji \" loc=\"shiva nagar\" pc=\"560043\" state=\"Karnataka\"
        street=\"Manjunatha nagar\" vtc=\"Horamavu\"/
         *
         */
        var country:String="India"; var dist:String=""; var house:String=""; var lm:String=""; var loc:String=""
        var pc:String=""; var state:String=""; var street:String=""; var vtc:String=""; var nameOfUser:String=""; var phone:String=""
        var photo:String=""; var subdist=""; var co=""
        val uid=arguments?.getString("uid").toString()
        while(event!=XmlPullParser.END_DOCUMENT)
        {
            var tagName=parser.name
            var text:String=""
            when(event){
                XmlPullParser.START_TAG->{
                    if(tagName=="Pht")
                    {
                        photo=parser.nextText()
                    }
                    else if(tagName=="Poi")
                    {
                        nameOfUser=parser.getAttributeValue("","name")
                        phone=parser.getAttributeValue("","phone")
                    }
                    else if(tagName=="Poa")
                    {
                        if(parser.getAttributeValue("","country")!=null)
                            country=parser.getAttributeValue("","country")
                        if(parser.getAttributeValue("","dist")!=null)
                            dist=parser.getAttributeValue("","dist")
                        if(parser.getAttributeValue("","house")!=null)
                            house=parser.getAttributeValue("","house")
                        if(parser.getAttributeValue("","lm")!=null)
                            lm=parser.getAttributeValue("","lm")
                        if(parser.getAttributeValue("","loc")!=null)
                            loc=parser.getAttributeValue("","loc")
                        if(parser.getAttributeValue("","pc")!=null)
                            pc=parser.getAttributeValue("","pc")
                        if(parser.getAttributeValue("","state")!=null)
                            state=parser.getAttributeValue("","state")
                        if(parser.getAttributeValue("","street")!=null)
                            street=parser.getAttributeValue("","street")
                        if(parser.getAttributeValue("","vtc")!=null)
                            vtc=parser.getAttributeValue("","vtc")
                        if(parser.getAttributeValue("","subdist")!=null)
                            subdist=parser.getAttributeValue("","subdist")
                        if(parser.getAttributeValue("","co")!=null)
                            co=parser.getAttributeValue("","co")
                    }
                }
                XmlPullParser.END_TAG-> {
                    if(tagName=="KycRes")
                    {
//                        val result=parser.getAttributeValue(1)
//                        if(result=="Y" || result=="y")
//                        {
//                            val bundle=bundleOf("uid" to uid)
//                        }
//                        else {
//                            Toast.makeText(
//                                activity,
//                                "Please try again after some time.",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
                    }
                    else if(tagName=="Poa")
                    {
//                        country=parser.getAttributeValue(1)
//                        dist=parser.getAttributeValue(2)
//                        house=parser.getAttributeValue(3)
//                        lm=parser.getAttributeValue(4)
//                        loc=parser.getAttributeValue(5)
//                        pc=parser.getAttributeValue(6)
//                        state=parser.getAttributeValue(7)
                        //street=parser.getAttributeValue(8)
                    }
                }
            }
            event=parser.next()
        }


        val byteArrayOutputStream = ByteArrayOutputStream()
        var imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
        imageBytes = decode(photo, DEFAULT)
        val decodedImage =
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        binding?.userPhoto?.setImageBitmap(decodedImage)
        binding?.displayName?.text=nameOfUser
        var textAddress:String=""
        if(house!=""){ textAddress+=house}
        if(street!=""){ textAddress+= ", $street"}
        if(lm!=""){textAddress+= ", $lm"}
        if(loc!=""){textAddress+= ", $loc"}
        if(dist!=""){textAddress+= ", $dist" }
        if(state!=""){textAddress+= ", $state" }
        if(pc!=""){textAddress+=", $pc"}

        binding?.address?.text=textAddress

        binding?.submitButton?.setOnClickListener {
            val dao=UserDao()
            val reqAce=dao.setReqAccepted(uid,nameOfUser,dist,house,lm,loc,pc,state,street,country,vtc,subdist,co)
            Toast.makeText(activity,"Successfully sent the address",Toast.LENGTH_SHORT).show()
            val bundle= bundleOf("uid" to uid)
            findNavController().navigate(R.id.dashboardFragment,bundle)
        }
    }
}