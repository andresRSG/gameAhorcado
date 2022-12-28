package com.example.ahorcadogame

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ahorcadogame.databinding.ActivityPlayBinding
import com.example.ahorcadogame.models.LettersCheck
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class PlayActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityPlayBinding
    private val KEYBOARD = HashMap<Int, Char>()
    private val LOG_TAG = this.javaClass.simpleName
    private var mDictionary: ArrayList<String>? = null
    private val guess = 0.toChar()
    private var flagGameOver = false
    
    private lateinit var listLettersCheck:ArrayList<Char>

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dictionary()



        val chars =  "Palabra".toCharArray()

        val auxArrayLetters:ArrayList<LettersCheck> = ArrayList()

        for(item in chars){
            auxArrayLetters.add(LettersCheck(item))
        }

        binding.rvLetters.layoutManager = LinearLayoutManager(this@PlayActivity,LinearLayoutManager.HORIZONTAL ,false)
        binding.rvLetters.adapter = AdapterLetter(this@PlayActivity, auxArrayLetters)

        //Setting up the keyboard
        for (id in KEYBOARD.keys) {
            val button = findViewById(id) as Button
            button.isEnabled = true
            button.isSoundEffectsEnabled = false
            button.setText(
                KEYBOARD.get(id)
                    .toString()
            )
            button.setBackgroundColor(getColor(R.color.ah_yellowD))
            button.setOnClickListener(this)
        }


    }

    /*public void initGame() {
        EvilHangman evilHangman = new EvilHangman(dictionary, letters);
    }*/
    fun updateGame(guessedLetter: Char) {

    }

    override fun onClick(view: View) {
        val id = view.id
        if (KEYBOARD.keys.contains(id)) {
            val button = findViewById(id) as Button
            button.text = ""
            button.isEnabled = false

//            listLettersCheck =
//            val guessedLetter: Char = KEYBOARD.get(id)?.toChar() ?:
//            updateGame(guessedLetter)
        }
    }


    fun makeDictionary() {
        try {
            val wordStream = assets.open("dictionary.txt")
            val inb = BufferedReader(InputStreamReader(wordStream))
            var line: String? = null
            mDictionary = ArrayList<String>()
            while (inb.readLine().also { line = it } != null) {
                val word = line!!.trim { it <= ' ' }
                mDictionary!!.add(word)
            }
        } catch (e: IOException) {
            Log.d(LOG_TAG, "IOException")
        }
    }

    private fun dictionary(){
        KEYBOARD.put(R.id.button1, 'a')
        KEYBOARD.put(R.id.button2, 'b')
        KEYBOARD.put(R.id.button3, 'c')
        KEYBOARD.put(R.id.button4, 'd')
        KEYBOARD.put(R.id.button5, 'e')
        KEYBOARD.put(R.id.button6, 'f')
        KEYBOARD.put(R.id.button7, 'g')
        KEYBOARD.put(R.id.button8, 'h')
        KEYBOARD.put(R.id.button9, 'i')
        KEYBOARD.put(R.id.button10, 'j')
        KEYBOARD.put(R.id.button11, 'k')
        KEYBOARD.put(R.id.button12, 'l')
        KEYBOARD.put(R.id.button13, 'm')
        KEYBOARD.put(R.id.button14, 'n')
        KEYBOARD.put(R.id.button15, 'o')
        KEYBOARD.put(R.id.button16, 'p')
        KEYBOARD.put(R.id.button17, 'q')
        KEYBOARD.put(R.id.button18, 'r')
        KEYBOARD.put(R.id.button19, 's')
        KEYBOARD.put(R.id.button20, 't')
        KEYBOARD.put(R.id.button21, 'u')
        KEYBOARD.put(R.id.button22, 'v')
        KEYBOARD.put(R.id.button23, 'w')
        KEYBOARD.put(R.id.button24, 'x')
        KEYBOARD.put(R.id.button25, 'y')
        KEYBOARD.put(R.id.button26, 'z')
    }

}