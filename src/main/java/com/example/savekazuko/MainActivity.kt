package com.example.savekazuko

import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.content.SharedPreferences

class MainActivity : AppCompatActivity(), GameTask {
    lateinit var baseLayout: LinearLayout
    lateinit var startBtn: Button
    lateinit var gameView: GameView
    lateinit var score: TextView
    lateinit var logo: ImageView
    lateinit var mediaPlayer: MediaPlayer
    lateinit var sharedPreferences: SharedPreferences
    lateinit var howToPlayBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startBtn = findViewById(R.id.startBtn)
        baseLayout = findViewById(R.id.baseLayout)
        score = findViewById(R.id.score)
        logo = findViewById(R.id.logo)
        gameView = GameView(this, this)
        mediaPlayer = MediaPlayer()
        howToPlayBtn = findViewById(R.id.howToPlayBtn)

        sharedPreferences = getSharedPreferences("saved_Prefs", Context.MODE_PRIVATE)
        var highScore = sharedPreferences.getInt("highScore", 0)
        score.text = "High Score: $highScore"

        startBtn.setOnClickListener {
            gameView.setBackgroundResource(R.drawable.background)
            baseLayout.addView(gameView)
            startBtn.visibility = View.GONE
            score.visibility = View.GONE
            logo.visibility = View.GONE
            howToPlayBtn.visibility = View.GONE
            playSound("GameSong.mp3")
        }

        howToPlayBtn.setOnClickListener {
            val intent = Intent(this, HowToPlay::class.java)
            startActivity(intent)
        }
    }

    //Sound play
    private fun playSound(fileName: String) {
        try {
            val assetDescriptor: AssetFileDescriptor = assets.openFd(fileName)
            mediaPlayer.reset()
            mediaPlayer.setDataSource(
                assetDescriptor.fileDescriptor,
                assetDescriptor.startOffset,
                assetDescriptor.length
            )
            mediaPlayer.prepare()
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener {
                mediaPlayer.reset()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //When Game is finished
    override fun closeGame(gameScore: Int) {
        var highScore = sharedPreferences.getInt("highScore", 0)
        if (gameScore > highScore) {
            sharedPreferences.edit().putInt("highScore", gameScore).apply()
            score.text = "New High Score: $gameScore"
        } else {
            score.text = "Score: $gameScore"
        }

        gameView.resetGame()
        score.text
        baseLayout.removeView(gameView)
        logo.visibility = View.VISIBLE
        startBtn.visibility = View.VISIBLE
        score.visibility = View.VISIBLE
        howToPlayBtn.visibility = View.VISIBLE

        //Sounds
        mediaPlayer.setOnCompletionListener(null) // Remove the completion listener
        mediaPlayer.stop()
        mediaPlayer.reset() // Reset the MediaPlayer to its uninitialized state
        mediaPlayer.release() // Release the resources
        mediaPlayer = MediaPlayer() // Reinitialize the MediaPlayer
    }
}
