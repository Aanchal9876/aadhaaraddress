package com.example.aadharaddressupdation.models

data class User(val uid:String="",
                var inReq:String="",
                var outReq:String="",
                var loggedIn:Boolean=false,
                var reqAccepted:Boolean=false,
                var name:String="",
                var dist:String="",
                var house:String="",
                var lm:String="",
                var loc:String="",
                var pc:String="",
                var state:String="",
                var street:String="",
                var country:String="",
                var vtc:String="",
                var subdist:String="",
                var co:String=""
)



