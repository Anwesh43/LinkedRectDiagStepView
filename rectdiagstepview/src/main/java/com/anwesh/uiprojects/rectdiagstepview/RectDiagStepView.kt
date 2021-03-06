package com.anwesh.uiprojects.rectdiagstepview

/**
 * Created by anweshmishra on 02/11/18.
 */

import android.app.Activity
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
            drawLine(0f, 0f, -a * sc, 0f, paint)
            restore()
            restore()
        }
        restore()
    }
    restore()
}

class RectDiagStepView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += 0.05f * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var animated : Boolean = false) {

        fun animate(cb : () -> Unit, viewcb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    viewcb()
                } catch(ex : Exception) {

                }
            }
        }

        fun start(startcb : () -> Unit) {
            if (!animated) {
                animated = true
                startcb()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class RDSNode(var i : Int, val state : State = State()) {

        private var next : RDSNode? = null
        private var prev : RDSNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = RDSNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawRDSNode(i, state.scale, paint)
            prev?.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : RDSNode {
            var curr : RDSNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class RectDiagStep(var i : Int) {

        private var curr : RDSNode = RDSNode(0)

        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : RectDiagStepView) {

        private val animator : Animator = Animator()
        private val rds : RectDiagStep = RectDiagStep(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#BDBDBD"))
            rds.draw(canvas, paint)
            animator.animate({
                rds.update {i, scl ->
                    animator.stop()
                }
            }, {
                view.invalidate()
            })
        }

        fun handleTap() {
            rds.startUpdating {
                animator.start {
                    view.postInvalidate()
                }
            }
        }
    }

    companion object {
        fun create(activity : Activity) : RectDiagStepView {
            val view : RectDiagStepView = RectDiagStepView(activity)
            activity.setContentView(view)
            return view
        }
    }
}