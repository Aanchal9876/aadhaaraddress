package com.example.aadharaddressupdation.ui.request

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.aadharaddressupdation.dao.UserDao
import com.example.aadharaddressupdation.databinding.FragmentCheckStatusSc2Binding
import com.example.aadharaddressupdation.models.User
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.pow


class CheckStatusSc2 : Fragment() {
    private var _binding:FragmentCheckStatusSc2Binding?=null
    private val binding get()=_binding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currLatitude:Double=0.0
    private var currLongitude:Double=0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=FragmentCheckStatusSc2Binding.inflate(inflater,container,false)
        return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val dataBase= FirebaseFirestore.getInstance()
        val uid=arguments?.getString("uid").toString()
        val landLordUid=arguments?.getString("landLordUid").toString()
        val landLord=dataBase.collection("users").document(landLordUid)
        val user=dataBase.collection("users").document(uid)
        var fullAddress=""
        landLord.get()
            .addOnSuccessListener {
                document->
                if(document!=null)
                {
                    val ll= document.toObject(User::class.java)!!
                    if(ll.house!="")
                    {
                        binding?.houseBox?.visibility=View.VISIBLE
                        binding?.house?.setText(ll.house)
                        fullAddress+=ll.house
                    }
                    if(ll.street!="")
                    {
                        binding?.street?.setText(ll.street)
                        binding?.streetBox?.visibility=View.VISIBLE
                        fullAddress+=", "+ll.street
                    }
                    if(ll.lm!="")
                    {
                        binding?.lm?.setText(ll.lm)
                        binding?.landMarkBox?.visibility=View.VISIBLE
                        fullAddress+=", "+ll.lm
                    }
                    if(ll.loc!="")
                    {
                        binding?.loc?.text=ll.loc
                        binding?.locBox?.visibility=View.VISIBLE
                        fullAddress+=", "+ll.loc
                    }
                    if(ll.vtc!="")
                    {
                        binding?.vtc?.text=ll.vtc
                        binding?.vtcBox?.visibility=View.VISIBLE
                        fullAddress+=", "+ll.vtc
                    }

                    if(ll.dist!="")
                    {
                        binding?.dist?.text=ll.dist
                        binding?.distBox?.visibility=View.VISIBLE
                        fullAddress+=", "+ll.dist
                    }
                    if(ll.state!="")
                    {
                        binding?.state?.text=ll.state
                        binding?.stateBox?.visibility=View.VISIBLE
                        fullAddress+=", "+ll.state
                    }
                    if(ll.country!="")
                    {
                        binding?.country?.text=ll.country
                        binding?.countryBox?.visibility=View.VISIBLE
                        fullAddress+=", "+ll.country
                    }
                    if(ll.pc!="")
                    {
                        binding?.pc?.text=ll.pc
                        binding?.pcBox?.visibility=View.VISIBLE
                        fullAddress+=", "+ll.pc
                    }
                    if(ll.name!="")
                    {
                        val temp:String=ll.name
                        binding?.setLandLordName?.text="The address shared to you by $temp is:"
                    }
                }
            }

        user.get()
            .addOnSuccessListener { document->
                if(document!=null)
                {
                    val uSer=document.toObject(User::class.java)!!
                    if(uSer.co!="")
                    {
                        binding?.co?.text = uSer.co
                    }
                }
            }

        binding?.completeButton?.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Do you want to proceed with GPS verification?")
                .setMessage("We will compare your " +
                            "location with the address you entered, they should point to the " +
                            "geographically same location")
                .setNegativeButton("NO") { _, _ ->

                }
                .setPositiveButton("YES") { _, _ ->
                    binding?.progressBar?.visibility=View.VISIBLE
                    checkWithLocation(fullAddress)
                }
                .show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("SetTextI18n")
    private fun checkWithLocation(full: String){
        var fullAddress=full.replace(",","").replace(" ","%20")
        val url="http://api.positionstack.com/v1/forward?access_key=4a3a2dff48b9e401bc8df900faf97cbd&query=$fullAddress&limit=1"
        val que= Volley.newRequestQueue(context)
        var longitude=0.0
        var latitude=0.0
        val uid=arguments?.getString("uid").toString()
        val landLordUid=arguments?.getString("landLordUid").toString()
        val jsonObjectRequest=JsonObjectRequest(Request.Method.GET,url,null,
            {
                val details=it.getJSONArray("data")
                for(i in 0 until details.length()){
                    val jsonObject=details.getJSONObject(i)
                    longitude=jsonObject.getString("longitude").toDouble()
                    latitude=jsonObject.getString("latitude").toDouble()
                    //Toast.makeText(activity,longitude.toString(),Toast.LENGTH_SHORT).show()
                    checkPermission()
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            //Toast.makeText(activity, "From outside: $currLatitude, $latitude",Toast.LENGTH_SHORT).show()
                            //Toast.makeText(activity, "From outside: $currLongitude, $longitude",Toast.LENGTH_SHORT).show()
                        },
                        1000 // value in milliseconds
                    )
                    val dist=distance(currLatitude,currLongitude,longitude.toDouble(),latitude.toDouble())
                    if(dist<=5)
                    {
                        //Toast.makeText(activity,"Distance is lesss: $dist",Toast.LENGTH_SHORT).show()
                        makeTheChanges(uid,landLordUid)
                    }
                    else {
                        //Toast.makeText(activity, "Distance is $dist", Toast.LENGTH_SHORT).show()
                        makeTheChanges(uid,landLordUid)
                    }
                }
            },
            {
                Toast.makeText(activity,"Some error occurring $it",Toast.LENGTH_SHORT).show()
                binding?.progressBar?.visibility=View.INVISIBLE
            }
        )
        que.add(jsonObjectRequest)

    }

    private fun checkPermission(){
        if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),1)
        }
        else
        {
            getLocations()
        }
    }



    @SuppressLint("MissingPermission")
    private fun getLocations() {
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if(it==null) {
                Toast.makeText(activity,"Sorry cannot get location",Toast.LENGTH_SHORT).show()
            } else
                it.apply {
                    currLatitude=it.latitude
                    currLongitude=it.longitude
//                    Toast.makeText(activity,"From inside the getLocatoin: "+it.latitude.toString(),Toast.LENGTH_SHORT).show()
//                    Toast.makeText(activity,"From inside the getLocatoin: "+it.longitude.toString(),Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==1)
        {
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(activity,"Permission Granted",Toast.LENGTH_SHORT).show()
                    getLocations()
                }
                else
                {
                    Toast.makeText(activity,"Permission Denied",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun distanceInKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        dist = dist * 1.609344
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    private fun toRadians(degree: Double): Double {
        val one_deg : Double = kotlin.math.PI/180
        return one_deg*degree
    }

    private fun distance(Lat1: Double, Long1: Double, Lat2: Double, Long2: Double): Double {
        // Convert the latitudes
        // and longitudes
        // from degree to radians.
        val lat1 = toRadians(Lat1)
        val long1 = toRadians(Long1)
        val lat2 = toRadians(Lat2)
        val long2 = toRadians(Long2)

        // Haversine Formula
        val dlong = long2 - long1
        val dlat = lat2 - lat1

        var ans = kotlin.math.sin(dlat / 2).pow(2) +
                kotlin.math.cos(lat1) * kotlin.math.cos(lat2) *
                kotlin.math.sin(dlong / 2).pow(2)

        ans = 2 * kotlin.math.asin(kotlin.math.sqrt(ans))

        // Radius of Earth in
        // Kilometers, R = 6371
        // Use R = 3956 for miles
        val R: Double = 6371.0

        // Calculate the result
        ans *= R
        return ans
    }

    private fun makeTheChanges(uid:String,landLordUid:String)
    {
        Toast.makeText(activity,"Your address has been updated successfully!!",Toast.LENGTH_LONG).show()
        val dao= UserDao()
        val uid=arguments?.getString("uid").toString()
        val house=binding?.house?.text.toString()
        val street=binding?.street?.text.toString()
        val lm=binding?.lm?.text.toString()
        dao.setAddress(uid,landLordUid,house,street,lm)
        val bundle= bundleOf("uid" to uid)
        findNavController().navigate(com.example.aadharaddressupdation.R.id.dashboardFragment,bundle)
    }
}