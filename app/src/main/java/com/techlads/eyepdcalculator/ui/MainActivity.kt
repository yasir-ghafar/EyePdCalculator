package com.techlads.eyepdcalculator.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import com.techlads.eyepdcalculator.R
import com.techlads.eyepdcalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            false

        supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
    }
}
