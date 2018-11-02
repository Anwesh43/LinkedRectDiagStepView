package com.anwesh.uiprojects.linkedrectdiagstepview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.rectdiagstepview.RectDiagStepView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RectDiagStepView.create(this)
    }
}
