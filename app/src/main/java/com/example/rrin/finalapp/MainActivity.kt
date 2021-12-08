package com.example.rrin.finalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(view: View) {
        when(view.id) {
            R.id.buttonReadText -> {
                // open read text activity
                val intent = Intent(this, ReadTextActivity::class.java)
                startActivity(intent)
            }
        }
    }
}