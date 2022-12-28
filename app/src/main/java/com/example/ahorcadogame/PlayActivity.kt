package com.example.ahorcadogame

import android.annotation.SuppressLint
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
import com.example.ahorcadogame.models.ResponserServiceLetter
import com.example.ahorcadogame.service.API
import com.example.ahorcadogame.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

@Suppress("CAST_NEVER_SUCCEEDS")
class PlayActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityPlayBinding
    private val KEYBOARD = HashMap<Int, Char>()
    private val LOG_TAG = this.javaClass.simpleName

    private var listLettersCheck:ArrayList<Char> = ArrayList()
    private var mArrayLetters:ArrayList<LettersCheck> = ArrayList()
    private lateinit var info:ResponserServiceLetter
    private lateinit var chars:CharArray
    private var lives:Int = 6


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dictionary()

        callLetter()

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

    fun callLetter(){
        val call = Constants.getRetrofit().create(API::class.java).getLetterService()

        CoroutineScope(Dispatchers.IO).launch {

            call.enqueue(object : Callback<ResponserServiceLetter>{
                override fun onResponse(
                    call: Call<ResponserServiceLetter>,
                    response: Response<ResponserServiceLetter>
                ) {

                    response.body()?.let { it ->
                        binding.pbConexion.visibility = View.GONE

                        info = it
                        info.category.let {itC -> binding.tvCategory.text = getString(R.string.category, itC)}
                        info.word?.let { itW ->
                            chars =  itW.toCharArray()
                            for(item in chars){
                                mArrayLetters.add(LettersCheck(item))
                            }
                            binding.rvLetters.layoutManager = LinearLayoutManager(this@PlayActivity,LinearLayoutManager.HORIZONTAL ,false)
                            binding.rvLetters.adapter = AdapterLetter(this@PlayActivity, mArrayLetters)
                        }

                    }?:run{
                        binding.pbConexion.visibility = View.GONE

                    }

                }

                override fun onFailure(
                    call: Call<ResponserServiceLetter>,
                    t: Throwable
                ) {
                    TODO("Not yet implemented")
                }

            })

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateGame(guessedLetter: Char) {
        var default:Char = '0'
        if(guessedLetter == default){
            //no existe
        }else{
            listLettersCheck.add(guessedLetter)

            for(item in mArrayLetters){
                if(item.letter == guessedLetter){
                    item.isCheck = true
                }
            }
            binding.rvLetters.adapter = AdapterLetter(this@PlayActivity, mArrayLetters)
            binding.rvLetters.adapter?.notifyDataSetChanged()

        }

    }

    override fun onClick(view: View) {
        val id = view.id
        if (KEYBOARD.keys.contains(id)) {

            val button = findViewById(id) as Button
            button.text = ""
            button.isEnabled = false
            val guessedLetter: Char = (KEYBOARD.get(id)?.toChar() ?: updateGame(guessedLetter = "0" as Char)) as Char
            updateGame(guessedLetter)
        }
    }

    private fun dictionary(){
        KEYBOARD.put(R.id.button1, 'a');KEYBOARD.put(R.id.button2, 'b');KEYBOARD.put(R.id.button3, 'c');KEYBOARD.put(R.id.button4, 'd')
        KEYBOARD.put(R.id.button5, 'e');KEYBOARD.put(R.id.button6, 'f');KEYBOARD.put(R.id.button7, 'g');KEYBOARD.put(R.id.button8, 'h')
        KEYBOARD.put(R.id.button9, 'i');KEYBOARD.put(R.id.button10, 'j');KEYBOARD.put(R.id.button11, 'k');KEYBOARD.put(R.id.button12, 'l')
        KEYBOARD.put(R.id.button13, 'm');KEYBOARD.put(R.id.button14, 'n');KEYBOARD.put(R.id.button15, 'o');KEYBOARD.put(R.id.button16, 'p')
        KEYBOARD.put(R.id.button17, 'q');KEYBOARD.put(R.id.button18, 'r');KEYBOARD.put(R.id.button19, 's');KEYBOARD.put(R.id.button20, 't')
        KEYBOARD.put(R.id.button21, 'u');KEYBOARD.put(R.id.button22, 'v');KEYBOARD.put(R.id.button23, 'w');KEYBOARD.put(R.id.button24, 'x')
        KEYBOARD.put(R.id.button25, 'y');KEYBOARD.put(R.id.button26, 'z')
    }

}