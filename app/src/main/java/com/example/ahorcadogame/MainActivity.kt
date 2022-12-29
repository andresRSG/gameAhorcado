package com.example.ahorcadogame

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.ahorcadogame.databinding.ActivityMainBinding
import com.example.ahorcadogame.util.Constants
import com.example.ahorcadogame.util.preferences
import com.realpacific.clickshrinkeffect.applyClickShrink

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
        binding.tvPlay.applyClickShrink()
        binding.goOut.applyClickShrink()

        binding.tvPlay.setOnClickListener {
            if(preferences.game.isNullOrEmpty())
                startGame(Constants.NEWGAME)
            else showDialogSaveGame()
        }

        binding.goOut.setOnClickListener { finish() }
    }

    fun showDialogSaveGame(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_continue_game)
        val buttonNewGame = dialog.findViewById<Button>(R.id.btNewGame)
        val buttonContinueGame = dialog.findViewById<Button>(R.id.btContinueGame)

        buttonNewGame.setOnClickListener {
            dialog.dismiss()
            startGame(Constants.NEWGAME) }
        buttonContinueGame.setOnClickListener {
            dialog.dismiss()
            startGame(Constants.GAMESAVE) }

        dialog.show()
    }

    fun startGame(isNew:Boolean){
        val intent = Intent(this@MainActivity, PlayActivity::class.java)
        val parameters = Bundle()

        if(isNew) parameters.putBoolean(Constants.IDNEWGAME, Constants.NEWGAME)
        else parameters.putBoolean(Constants.IDNEWGAME, Constants.GAMESAVE)

        intent.putExtras(parameters)
        startActivity(intent)
        Animatoo.animateShrink(this@MainActivity)
    }
}