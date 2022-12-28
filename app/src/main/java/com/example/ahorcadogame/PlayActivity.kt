package com.example.ahorcadogame

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.ahorcadogame.databinding.ActivityPlayBinding
import com.example.ahorcadogame.models.ButtonActivate
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

@Suppress("CAST_NEVER_SUCCEEDS")
class PlayActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityPlayBinding
    private val KEYBOARD = HashMap<Int, Char>()
    private val KEYBOARDCTO = HashMap<Char, Int>()
    private val LOG_TAG = this.javaClass.simpleName

    private var listLettersCheck:ArrayList<Char> = ArrayList()
    private var mArrayLetters:ArrayList<LettersCheck> = ArrayList()
    private lateinit var info:ResponserServiceLetter
    private lateinit var chars:CharArray
    private var lives:Int = 6
    private var listButtons: ArrayList<ButtonActivate> = ArrayList()


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
                        lives = 6
                        binding.tvLives.text = getString(R.string.lives, lives)
                        info = it
                        info.category.let {itC -> binding.tvCategory.text = getString(R.string.category, itC)}
                        info.word?.let { itW ->
                            Log.d(Constants.LOGTAG, "Respuesta del servidor: ${itW}")

                            chars =  itW.toCharArray()
                            updateImage()

                            mArrayLetters.clear()
                            listLettersCheck.clear()

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
        val default:Char = '0'
        if(guessedLetter == default){
            //no existe
        }else{
            listLettersCheck.add(guessedLetter)
            var correct = false
            for(item in mArrayLetters){
                if(item.letter == guessedLetter){
                    item.isCheck = true
                    correct = true
                }
            }
            binding.rvLetters.adapter = AdapterLetter(this@PlayActivity, mArrayLetters)
            binding.rvLetters.adapter?.notifyDataSetChanged()

            if(!correct){
                lives -= 1
                updateImage()

            }


            verifyVictoryOrLose()
        }

    }



    fun verifyVictoryOrLose(){
        if(lives == 1)
            binding.tvLives.text = getString(R.string.lives1)
        else
            binding.tvLives.text = getString(R.string.lives, lives)

        if(lives == 0){
            //Perdiste
            Toast.makeText(this@PlayActivity, "Perdite", Toast.LENGTH_LONG)
            println("Perdiste")
            openDialogWL(false,false)
        }else{
            var allLettersCheck:Boolean = true
            for(item in mArrayLetters){
                if(!item.isCheck){
                    allLettersCheck = false
                    break
                }
            }

            if(allLettersCheck){
                //your Win
                openDialogWL(true,false)

                Toast.makeText(this@PlayActivity, "Ganaste", Toast.LENGTH_LONG)
            }
        }

    }

    fun openDialogWL( win:Boolean, leave:Boolean){

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_finish_game)
        dialog.setCancelable(false)
        val text = dialog.findViewById<TextView>(R.id.text_description_dialog)
        val buttonPlay = dialog.findViewById<Button>(R.id.btPlay)
        val buttonExit = dialog.findViewById<Button>(R.id.btExit)
        val buttonSave = dialog.findViewById<Button>(R.id.btSave)
        val cBg = dialog.findViewById<ConstraintLayout>(R.id.cDialog)
        val vBg = dialog.findViewById<View>(R.id.viewDialog)

        if(leave){
            text.text = getString(R.string.leave)
            buttonSave.visibility = View.VISIBLE
            vBg.visibility = View.GONE
        }else{
            if(win){
                text.text = getString(R.string.win)
                cBg.setBackgroundResource(R.drawable.image_avada_kadabra)
            }else{
                text.text = getString(R.string.lose)
                cBg.setBackgroundResource(R.drawable.image_avada_kadabra)
            }
        }


        buttonExit.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        buttonPlay.setOnClickListener {
            dialog.dismiss()
            if(!leave){
                newGame()
            }
        }

        buttonSave.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()

    }

    fun newGame(){
        binding.pbConexion.visibility = View.VISIBLE
        activateButtons()
        callLetter()
    }


    fun updateImage(){
        when(lives){
            0 ->
                binding.ivState.setImageResource(R.drawable.ah_1)
            1 ->
                binding.ivState.setImageResource(R.drawable.ah_2)
            2 ->
                binding.ivState.setImageResource(R.drawable.ah_3)
            3 ->
                binding.ivState.setImageResource(R.drawable.ah_4)
            4 ->
                binding.ivState.setImageResource(R.drawable.ah_5)
            5 ->
                binding.ivState.setImageResource(R.drawable.ah_6)
            6 ->
                binding.ivState.setImageResource(R.drawable.ah_7)

        }
    }

    override fun onClick(view: View) {
        val id = view.id
        if (KEYBOARD.keys.contains(id)) {
            fresh(true)
            val timer = object: CountDownTimer(1000, 100) {
                override fun onTick(millisUntilFinished: Long) {}

                override fun onFinish() {
                    fresh(false)
                }
            }
            timer.start()


            val button = findViewById(id) as Button
            button.text = ""
            button.isEnabled = false
            val guessedLetter: Char = (KEYBOARD.get(id)?: updateGame(guessedLetter = "0" as Char)) as Char
            updateGame(guessedLetter)
        }

    }


    private fun fresh(f:Boolean){
        if(f){
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }else{
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    fun activateButtons(){
        listButtons.add(ButtonActivate('a' ,true));listButtons.add(ButtonActivate('b' ,true));listButtons.add(ButtonActivate('c' ,true))
        listButtons.add(ButtonActivate('d',true));listButtons.add(ButtonActivate('e',true));listButtons.add(ButtonActivate('f',true))
        listButtons.add(ButtonActivate('g',true));listButtons.add(ButtonActivate('h',true));listButtons.add(ButtonActivate('i',true))
        listButtons.add(ButtonActivate('j',true));listButtons.add(ButtonActivate('k',true));listButtons.add(ButtonActivate('l',true))
        listButtons.add(ButtonActivate('m',true));listButtons.add(ButtonActivate('n',true));listButtons.add(ButtonActivate('o',true))
        listButtons.add(ButtonActivate('p',true));listButtons.add(ButtonActivate('q',true));listButtons.add(ButtonActivate('r',true))
        listButtons.add(ButtonActivate('s',true));listButtons.add(ButtonActivate('t',true));listButtons.add(ButtonActivate('u',true))
        listButtons.add(ButtonActivate('v',true));listButtons.add(ButtonActivate('w',true));listButtons.add(ButtonActivate('x',true))
        listButtons.add(ButtonActivate('y',true));listButtons.add(ButtonActivate('z',true))

        for (item in listButtons ){
            if(KEYBOARDCTO.keys.contains(item.idButton )){
                val idB: Int? = KEYBOARDCTO.get(key = item.idButton)
                val button = findViewById(idB!!) as Button
                button.isEnabled = true
                button.text = item.idButton.toString()
            }
        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onBackPressed() {
        openDialogWL(false,true)
        Animatoo.animateSwipeLeft(this@PlayActivity)
    }


    private fun dictionary(){
        KEYBOARD.put(R.id.button1, 'a');KEYBOARD.put(R.id.button2, 'b');KEYBOARD.put(R.id.button3, 'c');KEYBOARD.put(R.id.button4, 'd')
        KEYBOARD.put(R.id.button5, 'e');KEYBOARD.put(R.id.button6, 'f');KEYBOARD.put(R.id.button7, 'g');KEYBOARD.put(R.id.button8, 'h')
        KEYBOARD.put(R.id.button9, 'i');KEYBOARD.put(R.id.button10, 'j');KEYBOARD.put(R.id.button11, 'k');KEYBOARD.put(R.id.button12, 'l')
        KEYBOARD.put(R.id.button13, 'm');KEYBOARD.put(R.id.button14, 'n');KEYBOARD.put(R.id.button15, 'o');KEYBOARD.put(R.id.button16, 'p')
        KEYBOARD.put(R.id.button17, 'q');KEYBOARD.put(R.id.button18, 'r');KEYBOARD.put(R.id.button19, 's');KEYBOARD.put(R.id.button20, 't')
        KEYBOARD.put(R.id.button21, 'u');KEYBOARD.put(R.id.button22, 'v');KEYBOARD.put(R.id.button23, 'w');KEYBOARD.put(R.id.button24, 'x')
        KEYBOARD.put(R.id.button25, 'y');KEYBOARD.put(R.id.button26, 'z')

        KEYBOARDCTO.put('a',R.id.button1);KEYBOARDCTO.put('b',R.id.button2);KEYBOARDCTO.put('c',R.id.button3);KEYBOARDCTO.put('d',R.id.button4)
        KEYBOARDCTO.put('e',R.id.button5);KEYBOARDCTO.put('f',R.id.button6);KEYBOARDCTO.put('g',R.id.button7);KEYBOARDCTO.put('h',R.id.button8)
        KEYBOARDCTO.put('i',R.id.button9);KEYBOARDCTO.put('j',R.id.button10);KEYBOARDCTO.put('k',R.id.button11);KEYBOARDCTO.put('l',R.id.button12)
        KEYBOARDCTO.put('m',R.id.button13);KEYBOARDCTO.put('n',R.id.button14);KEYBOARDCTO.put('o',R.id.button15);KEYBOARDCTO.put('p',R.id.button16)
        KEYBOARDCTO.put('q',R.id.button17);KEYBOARDCTO.put('r',R.id.button18);KEYBOARDCTO.put('s',R.id.button19);KEYBOARDCTO.put('t',R.id.button20)
        KEYBOARDCTO.put('u',R.id.button21);KEYBOARDCTO.put('v',R.id.button22);KEYBOARDCTO.put('w',R.id.button23);KEYBOARDCTO.put('x',R.id.button24)
        KEYBOARDCTO.put('y',R.id.button25);KEYBOARDCTO.put('z',R.id.button26)

    }


}