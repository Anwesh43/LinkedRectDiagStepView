package com.anwesh.uiprojects.rectdiagstepview

/**
 * Created by anweshmishra on 02/11/18.
 */

import android.content.Context
import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Canvas

val nodes : Int = 5

fun Canvas.drawRDSNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    paint.strokeWidth = Math.min(w, h) / 60
    paint.strokeCap = Paint.Cap.ROUND
    paint.color = Color.parseColor("#43A047")
    val size : Float = gap / 3
    save()
    translate(gap * i + gap, h / 2)
    for (j in 0..1) {
        val sc : Float = Math.min(0.5f, Math.max(0f, scale - 0.5f * j)) * 2
        val a : Float = 2 * size * (1 - j) + size * j * Math.sqrt(2.0).toFloat()
        save()
        for (k in 0..3) {
            save()
            rotate(90f * k)
            save()
            translate(size, size)
            rotate(45f * j)
            drawLine(0f, 0f, -a * sc, 0f)
            restore()
            restore()
        }
        restore()
    }
    restore()
}

class RectDiagStepView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}