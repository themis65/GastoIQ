package com.example.gastoiq.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gastoiq.databinding.ActivityAnalyticsBinding

class AnalyticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalyticsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnReset.setOnClickListener {
            Toast.makeText(this, "Reiniciar datos (demo)", Toast.LENGTH_SHORT).show()
        }
    }
}