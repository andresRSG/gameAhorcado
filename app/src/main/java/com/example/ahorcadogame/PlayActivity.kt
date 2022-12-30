package com.example.ahorcadogame

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.*
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.ahorcadogame.databinding.ActivityPlayBinding
import com.example.ahorcadogame.models.*
import com.example.ahorcadogame.service.API
import com.example.ahorcadogame.util.Constants
import com.example.ahorcadogame.util.preferences
import com.google.gson.Gson
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

    private var STATEGAME:Int = Constants.PLAY

    private  var sound:Boolean = Constants.ACTIVATE
    private  var vibrate:Boolean = Constants.ACTIVATE

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userS = preferences.user
        val gson = Gson()
        val userApp: UserApp = gson.fromJson(userS, UserApp::class.java)

        sound = userApp.sound!!
        vibrate = userApp.vibrate!!


        dictionary()

        val bundle = intent.extras
        val isNewGame = bundle?.getBoolean(Constants.IDNEWGAME)

        isNewGame.let {
            if(it == Constants.NEWGAME) newGame()
            else recoveryGame()
        }

        //Setting up the keyboard
        for (id in KEYBOARD.keys) {
            val button = findViewById(id) as Button
            button.isEnabled = true
            button.setText(
                KEYBOARD.get(id)
                    .toString()
            )
            button.setBackgroundColor(getColor(R.color.ah_yellowD))
            button.setOnClickListener(this)
        }

    }

    private fun recoveryGame(){
        try {
            var gameS = preferences.game
            val gson = Gson()
            val game:Game = gson.fromJson(gameS,Game::class.java)

            this.info = game.info!!
            this.lives = game.lives!!
            this.mArrayLetters = game.listLettersCheck!!
            this.listButtons = game.listButtonActivate!!

            activateButtons()

            customView()

        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }
    }

    fun customView(){
        binding.rvLetters.layoutManager = LinearLayoutManager(this@PlayActivity,LinearLayoutManager.HORIZONTAL ,false)
        binding.rvLetters.adapter = AdapterLetter(this@PlayActivity, mArrayLetters)
        info.category.let {itC ->
            binding.tvCategory.text = getString(R.string.category, itC)
        }
        if(lives == 1)
            binding.tvLives.text = getString(R.string.lives1)
        else
            binding.tvLives.text = getString(R.string.lives, lives)

        updateImage()
        binding.pbConexion.visibility = View.GONE
    }

    fun showDialogNoConection(serviceFailure: Boolean){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_conection_network)
        dialog.setCancelable(false)
        val textDialog = dialog.findViewById<TextView>(R.id.text_description_dialog)
        val buttonExit = dialog.findViewById<Button>(R.id.btExit)
        if(serviceFailure) textDialog.text = getString(R.string.no_conection_service)
        else textDialog.text = getString(R.string.no_conection_network)
        buttonExit.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()

    }

    fun callLetter(){
        if(!isNetworkAvailable()){
            binding.pbConexion.visibility = View.GONE
            showDialogNoConection(false)
            return
        }

        val call = Constants.getRetrofit().create(API::class.java).getLetterService()

        CoroutineScope(Dispatchers.IO).launch {

            call.enqueue(object : Callback<ResponserServiceLetter>{
                override fun onResponse(
                    call: Call<ResponserServiceLetter>,
                    response: Response<ResponserServiceLetter>
                ) {

                    response.body()?.let { it ->
                        lives = 6
                        info = it
                        mArrayLetters.clear()
                        listLettersCheck.clear()

                        info.word?.let { itW ->
                            chars =  itW.toCharArray()
                            updateImage()

                            for(item in chars){
                                mArrayLetters.add(LettersCheck(item))
                            }

                            customView()

                        }



                    }?:run{
                        showDialogNoConection(true)
                        binding.pbConexion.visibility = View.GONE
                    }

                }

                override fun onFailure(
                    call: Call<ResponserServiceLetter>,
                    t: Throwable
                ) {
                    binding.pbConexion.visibility = View.GONE
                    showDialogNoConection(true)
                }

            })

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateGame(guessedLetter: Char) {
        val default = '0' //is Char
        if(guessedLetter == default){
            //no existe: la ñ, por ejemplo
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
                if(vibrate) vibratePhone()
                lives -= 1
                updateImage()
            }
            verifyVictoryOrLose()
        }

    }

    fun vibratePhone() {
        val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (Build.VERSION.SDK_INT >= 26) {
            vibratorService.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibratorService.vibrate(200)
        }
    }

    fun verifyVictoryOrLose(){
        if(lives == 1)
            binding.tvLives.text = getString(R.string.lives1)
        else
            binding.tvLives.text = getString(R.string.lives, lives)

        if(lives == 0){
            //your lose
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
            }
        }

    }

    fun openDialogWL( win:Boolean, leave:Boolean){

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_finish_game)
        dialog.setCancelable(false)
        val text = dialog.findViewById<TextView>(R.id.text_description_dialog)
        val textAnswer = dialog.findViewById<TextView>(R.id.tvAnswer)
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
                textAnswer.visibility = View.VISIBLE
                textAnswer.text = getString(R.string.answer, info.word)

            }
        }


        buttonExit.setOnClickListener {
            dialog.dismiss()
            STATEGAME = Constants.TERMINATE
            preferences.game = null
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

    private fun newGame(){
        //Activando todos los botones
        listButtons.add(ButtonActivate('a' ,true));listButtons.add(ButtonActivate('b' ,true));listButtons.add(ButtonActivate('c' ,true))
        listButtons.add(ButtonActivate('d',true));listButtons.add(ButtonActivate('e',true));listButtons.add(ButtonActivate('f',true))
        listButtons.add(ButtonActivate('g',true));listButtons.add(ButtonActivate('h',true));listButtons.add(ButtonActivate('i',true))
        listButtons.add(ButtonActivate('j',true));listButtons.add(ButtonActivate('k',true));listButtons.add(ButtonActivate('l',true))
        listButtons.add(ButtonActivate('m',true));listButtons.add(ButtonActivate('n',true));listButtons.add(ButtonActivate('o',true))
        listButtons.add(ButtonActivate('p',true));listButtons.add(ButtonActivate('q',true));listButtons.add(ButtonActivate('r',true))
        listButtons.add(ButtonActivate('s',true));listButtons.add(ButtonActivate('t',true));listButtons.add(ButtonActivate('u',true))
        listButtons.add(ButtonActivate('v',true));listButtons.add(ButtonActivate('w',true));listButtons.add(ButtonActivate('x',true))
        listButtons.add(ButtonActivate('y',true));listButtons.add(ButtonActivate('z',true))

        binding.pbConexion.visibility = View.VISIBLE
        activateButtons()
        callLetter()
    }


    fun updateImage(){
        when(lives){
            0 -> binding.ivState.setImageResource(R.drawable.ah_1)
            1 -> binding.ivState.setImageResource(R.drawable.ah_2)
            2 -> binding.ivState.setImageResource(R.drawable.ah_3)
            3 -> binding.ivState.setImageResource(R.drawable.ah_4)
            4 -> binding.ivState.setImageResource(R.drawable.ah_5)
            5 -> binding.ivState.setImageResource(R.drawable.ah_6)
            6 -> binding.ivState.setImageResource(R.drawable.ah_7)
        }
    }

    override fun onClick(view: View) {
        val id = view.id
        if (KEYBOARD.keys.contains(id)) {
            fresh(true)

            val timer = object: CountDownTimer(1000, 100) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() { fresh(false) }
            }; timer.start()

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
        for (item in listButtons ){
            if(KEYBOARDCTO.keys.contains(item.idButton )){
                val idB: Int? = KEYBOARDCTO.get(key = item.idButton)
                val button = findViewById(idB!!) as Button
                button.isEnabled = true
                button.text = item.idButton.toString()
            }
        }

    }


//CICLO DE VIDA DE LA ACTIVIDAD:
    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        when(STATEGAME){
            Constants.PLAY -> saveGame()
            Constants.TERMINATE ->{/*Don't save game*/}
        }
        super.onPause()
    }

    override fun onBackPressed() {
        openDialogWL(false,true)
        Animatoo.animateSwipeLeft(this@PlayActivity)
    }

    fun saveGame(){
        val gson = Gson()
        val game = Game(info= this.info, lives= this.lives, listLettersCheck= this.mArrayLetters, listButtonActivate= this.listButtons)
        val jsonGame = gson.toJson(game)
        preferences.game = jsonGame
    }


    /**Diccionarios
     * En este método se inicializan dos diccionarios
     * KEYBOARD: Guardara como clave el id del boton del sistema y
     * como contenido tendra la letra a la que hace referencia en el teclado
     * Función: Servira para detectar cuando el usuario de clic e inhabilitarlo
     *
     * KEYBOARDCTO: Guardará como clave la letra a la que hace referencia y
     * como contenido el id del boton del sistema
     * Función: Es facilitar la habilitación de los botones para una nueva partida en esta actividad
     * o cuando el usuario haya guardado una partida y se requiera poner el teclado en el estado
     * en que fue guardado.
     * */
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

    fun isNetworkAvailable(): Boolean {
        val cm = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

//    fun confettiStart() {
//        konfettiView.build()
//            .addColors(
//                resources.getColor(R.color.yellow_c),
//                resources.getColor(R.color.green_c),
//                resources.getColor(R.color.blue_c),
//                resources.getColor(R.color.grey_c),
//                resources.getColor(R.color.orange_c),
//                resources.getColor(R.color.purple_c)
//            )
//            .setDirection(0.0, 359.0)
//            .setSpeed(1f, 12f)
//            .setFadeOutEnabled(true)
//            .setTimeToLive(2000L)
//            .addShapes(Shape.Square.INSTANCE)
//            .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
//            .streamFor(500, 1000L)
//    }




}