package com.example.aadharaddressupdation.dao

import com.example.aadharaddressupdation.models.AddressChanged
import com.example.aadharaddressupdation.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class UserDao {

    private val dataBase=FirebaseFirestore.getInstance()
    private val userCollection=dataBase.collection("users")

    fun addUser(user: User):Boolean
    {
        val docRef= userCollection.document(user.uid)
        var check=true
        docRef.get()
            .addOnSuccessListener {
                document->
                if(document==null)
                {
                    user.let {
                        //Let it work on background
                        GlobalScope.launch(Dispatchers.IO) {
                            userCollection.document(user.uid).set(it)
                        }
                    }
                }
                else{
                    val userForCheck = document.toObject(User::class.java)!!
                    if(userForCheck.loggedIn)
                    {
                        check=false
                        return@addOnSuccessListener
                    }
                }
            }
        return check
    }

    fun overwrite(user:User)
    {
        user.let{
            //Let it work on background
            GlobalScope.launch(Dispatchers.IO) {
                userCollection.document(user.uid).set(it)
            }
        }
    }

    fun checkReq(uid:String): Boolean
    {
        var user:User
        var check=true
        val docRef=userCollection.document(uid)
        docRef.get()
            .addOnSuccessListener {
                document->
                user= document.toObject(User::class.java)!!
                if(user.inReq!="")
                    check=false
            }
        return check
    }

    fun setReqAccepted(uid:String,
                       name:String,
                       dist:String,
                       house:String,
                       lm:String,
                       loc:String,
                       pc:String,
                       state:String,
                       street:String,
                       country:String,
                       vtc:String,
                       subdist:String,
                       co:String
    ):String
    {
        val docRef= userCollection.document(uid)
        val toRemove=""
        docRef.get()
            .addOnSuccessListener {
                    document->
                    val userForCheck = document.toObject(User::class.java)!!
                    setReqTrue(userForCheck.inReq.toString())
                    userForCheck.inReq=""
                    userForCheck.dist=dist
                    userForCheck.house=house
                    userForCheck.lm=lm
                    userForCheck.loc=loc
                    userForCheck.pc=pc
                    userForCheck.state=state
                    userForCheck.street=street
                    userForCheck.country=country
                    userForCheck.name=name
                    userForCheck.vtc=vtc
                    userForCheck.subdist=subdist
                    userForCheck.co=co
                    userCollection.document(userForCheck.uid).set(userForCheck)
            }
        return toRemove
    }

    private fun setReqTrue(uid:String)
    {
        val docRef=userCollection.document(uid)
        docRef.get()
            .addOnSuccessListener { document->
                if(document!=null) {
                    val userForCheck = document.toObject(User::class.java)
                    if (userForCheck != null) {
                        userForCheck.reqAccepted = true
                        userCollection.document(userForCheck.uid).set(userForCheck)
                    }
                }
            }
    }

    fun setAddress(uid:String,landLordUid:String,house:String,street:String,lm:String)
    {
        val docRef=userCollection.document(landLordUid)
        val addCollection=dataBase.collection("addressChanged")
        val user=User(uid,"","")
        docRef.get()
            .addOnSuccessListener {
                document->
                if(document!=null){
                    val userForCheck=document.toObject(User::class.java)
                    if(userForCheck!=null)
                    {
                        user.dist=userForCheck.dist
                        user.house=house
                        user.lm=lm
                        user.outReq=""
                        user.loc=userForCheck.loc
                        user.pc=userForCheck.pc
                        user.state=userForCheck.state
                        user.street=street
                        user.country=userForCheck.country
                        user.vtc=userForCheck.vtc
                        user.subdist=userForCheck.subdist
                        userForCheck.inReq=""
                        userCollection.document(uid).set(user)
                        val toSet=AddressChanged(uid,landLordUid)
                        addCollection.document(uid).set(toSet)
                    }
                }
            }
    }

}
