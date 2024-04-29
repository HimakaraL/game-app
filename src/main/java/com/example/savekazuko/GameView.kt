package com.example.savekazuko
//Creating Gameplay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View

class GameView(var c: Context, var gameTask: GameTask) : View(c) {
    private var paint: Paint? = null
    private var speed = 1
    private var time = 0
    private var score = 0
    private var kazukoPosition = 0
    private val drops = ArrayList<HashMap<String, Any>>()
    var viewWidth = 0
    var viewHeight = 0

    init {
        paint = Paint()
    }

    //Create UI
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        viewWidth = this.measuredWidth
        viewHeight = this.measuredHeight

        if (time % 800 < 10 + speed) {
            val map = HashMap<String, Any>()
            map["lane"] = (0..2).random()
            map["startTime"] = time
            drops.add(map)
        }

        time = time + 10 + speed
        val kazukoWidth = viewWidth / 3
        val kazukoHeight = kazukoWidth + 10
        paint!!.style = Paint.Style.FILL
        val kazuko = resources.getDrawable(R.drawable.kazuko, null)

        kazuko.setBounds(
            kazukoPosition * viewWidth / 3 + viewWidth / 15 + 25,
            viewHeight - 2 - kazukoHeight,
            kazukoPosition * viewWidth / 3 + viewWidth / 15 + kazukoWidth - 25,
            viewHeight - 2
        )
        kazuko.draw(canvas)
        var highScore = 0

        for (i in drops.indices) {
            try {
                val dropX = drops[i]["lane"] as Int * viewWidth / 3 + viewWidth / 15
                var dropY = time - drops[i]["startTime"] as Int
                val drop = resources.getDrawable(R.drawable.drop, null)
                val scaleFactor = 0.5f // Adjust this scale factor to change the size of the drop
                val dropWidth = (viewWidth / 3 * scaleFactor).toInt()
                val dropHeight = (dropWidth + 10 * scaleFactor).toInt()

                drop.setBounds(
                    dropX + 25, dropY - dropHeight, dropX + dropWidth - 25, dropY
                )
                drop.draw(canvas)
                //logics
                if (drops[i]["lane"] as Int == kazukoPosition) {
                    if (dropY > viewHeight - 2 - kazukoHeight
                        && dropY < viewHeight - 2
                    ) {
                        gameTask.closeGame(score)
                    }
                }
                if (dropY > viewHeight + kazukoHeight) {
                    drops.removeAt(i)
                    score++
                    speed = 1 + Math.abs(score / 10)

                    if (score > highScore) {
                        highScore = score
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        paint!!.color = Color.BLACK
        paint!!.textSize = 40f

        canvas.drawText("Score : $score", 80f, 80f, paint!!)
        //canvas.drawText("Speed : $speed", 380f, 80f, myPaint!!)
        invalidate()
    }

    //Track User Inputs
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y
        val halfScreenWidth = viewWidth / 2
        val laneWidth = viewWidth / 3

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (touchX < halfScreenWidth) {
                    //left
                    if (kazukoPosition > 0) {
                        kazukoPosition--
                    }
                } else if (touchX > halfScreenWidth) {
                    // right
                    if (kazukoPosition < 2) {
                        kazukoPosition++
                    }
                }
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
            }
        }
        return true
    }

    //Reset the Game
    fun resetGame() {
        time = 0
        score = 0
        drops.clear()
        invalidate()
    }
}
