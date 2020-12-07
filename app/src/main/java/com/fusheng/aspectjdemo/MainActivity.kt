package com.fusheng.aspectjdemo

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.tv_test1).setOnClickListener { test1() }
        findViewById<View>(R.id.tv_test2).setOnClickListener(object : View.OnClickListener {
            @EnableDoubleClick
            override fun onClick(v: View?) {
                test2()
            }
        })
    }

    private fun test1() {
        Log.d("testtest", "111--------------------------------------")
    }

    private fun test2() {
        Log.d("testtest", "222--------------------------------------")
    }
}