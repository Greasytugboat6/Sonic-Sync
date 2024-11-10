package com.example.myapplication

import android.content.Context
import android.webkit.JavascriptInterface

class UserIDInterface(c : Context) {
    var mContext = c as MainActivity

    @JavascriptInterface
    fun getUserID(code:String){
        mContext.user = code
        UserHolder.user = code
    }

    @JavascriptInterface
    fun logOut(){
        UserHolder.user = null
        mContext.user = null
    }
}