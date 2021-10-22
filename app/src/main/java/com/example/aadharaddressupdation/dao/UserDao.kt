package com.example.aadharaddressupdation.dao

import com.example.aadharaddressupdation.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {

    private val dataBase=FirebaseFirestore.getInstance()
    private val userCollection=dataBase.collection("users")

    fun addUser(user: User?)
    {
        user?.let{
            //Let it work on background
            GlobalScope.launch(Dispatchers.IO) {
                userCollection.document(user.uid).set(it)
            }
        }
    }
}