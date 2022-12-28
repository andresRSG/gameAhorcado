package com.example.ahorcadogame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.ahorcadogame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listeners()

    }


    override fun onResume() {
        super.onResume()
        Animatoo.animateSlideRight(this@MainActivity)

    }

    private fun listeners(){
        binding.tvPlay.setOnClickListener {
            val intent = Intent(this@MainActivity, PlayActivity::class.java)
            startActivity(intent)
            Animatoo.animateShrink(this@MainActivity)
        }
    }
}