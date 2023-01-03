package com.example.ahorcadogame

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ahorcadogame.databinding.ActivitySettingsBinding
import com.example.ahorcadogame.models.UserApp
import com.example.ahorcadogame.util.Constants
import com.example.ahorcadogame.util.preferences
import com.google.gson.Gson
import com.suke.widget.SwitchButton


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private  var sound:Boolean = Constants.ACTIVATE
    private  var vibrate:Boolean = Constants.ACTIVATE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userS = preferences.user
        val gson = Gson()
        val userApp: UserApp = gson.fromJson(userS, UserApp::class.java)

        sound = userApp.sound!!
        vibrate = userApp.vibrate!!

        binding.switchButtonSound.setChecked(sound)
        binding.switchButtonVibrate.setChecked(vibrate)

        playSound(sound)

        binding.switchButtonSound.setOnCheckedChangeListener(SwitchButton.OnCheckedChangeListener { view, isChecked ->
            sound = isChecked
            playSound(isChecked)
        })

        binding.switchButtonVibrate.setOnCheckedChangeListener(SwitchButton.OnCheckedChangeListener { view, isChecked ->
            vibrate = isChecked
        })

        binding.btSave.setOnClickListener {
            val userApp = UserApp(sound,vibrate)
            val gson = Gson()
            val jsonGame = gson.toJson(userApp)
            preferences.user = jsonGame
            finish()
        }

    }

    fun playSound(isChecked:Boolean){
        if(isChecked){
            Constants.playSoundIntro(this@SettingsActivity, R.raw.intro2_hp)
        }else{
            Constants.stopSoundIfExist()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        playSound(sound)
    }

    override fun onBackPressed() {
        playSound(sound)
        super.onBackPressed()
    }
}