package com.example.everyonesgourmet.kotlin_class

class CheckNull(str1:String?="null",str2:String?="null",str3:String?="null",str4:String?="null") {

    private val string1 = str1
    private val string2 = str2
    private val string3 = str3
    private val string4 = str4

    fun checkifNullOrEmpty():Boolean {
        return checkIfNull()||checkIfEmpty()
    }

    fun checkIfNull():Boolean{

        if(string1!=null&&string2!=null&&string3!=null&&string4!=null)
            return false

        return true
    }

    fun checkIfEmpty():Boolean{

        if(string1!=""&&string2!=""&&string3!=""&&string4!="")
            return false

        return true
    }
}