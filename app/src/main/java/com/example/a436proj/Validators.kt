package com.example.a436proj

import android.util.Log

class Validators {
    fun validEmail(email: String?) : Boolean {
        if (email.isNullOrEmpty()) {
            return false
        }

        // General Email Regex (RFC 5322 Official Standard)
        val emailRegex = Regex("(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'" +
                "*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x" +
                "5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z" +
                "0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4" +
                "][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z" +
                "0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|" +
                "\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])")
        return emailRegex.matches(email)
    }

    fun validPassword(password: String?) : Boolean {
        if (password.isNullOrEmpty()) {
            return false
        }

        // Min 8 char, 1 upper letter, 1 lower letter one numeric and 1 special @$!%*?&
        val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
        return passwordRegex.matches(password)
    }



    fun isGoogleAccount(email: String?) : Boolean{
        if(email.isNullOrEmpty()){
            return false
        }
        val googleEmail = Regex("^^[\\w.+\\-]+@gmail\\.com\$")
        return googleEmail.matches(email)
    }

    fun reasonInvalid(password: String?) : String{
        var message = ""
        val lower = Regex("(?=.*[a-z])")
        val upper = Regex("(?=.*[A-Z])")
        val number =Regex("(?=.*\\d)")
        val special = Regex("(?=.*[@!%*?&])")

        if (password.isNullOrEmpty()) {
            return "password cannot be empty"
        }
        if(password.length < 8){
            return "password must be 8+ long"
        }

        if(!lower.containsMatchIn(password)){
            return message +"Password doesn't contain: lower,"
        }

        if(!upper.containsMatchIn(password)){
            if(message == ""){
                message += "Password doesn't contain: Upper"
            }else{
                message += ", Upper"
            }
        }

        if(!number.containsMatchIn(password)){
            if(message == ""){
                message += "Password doesn't contain: number"
            }else{
                message += ", number"
            }
        }
        if(!special.containsMatchIn(password)){
            if(message == ""){
                message +="Password doesn't contain: @!%*?&"
            }else{
                message +=", @!%*?&"
            }
        }
        return message
    }

}
