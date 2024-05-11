package com.example.minipaint

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.widget.LinearLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val myCanvasView = MyCanvasView(this)
        myCanvasView.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN
        myCanvasView.contentDescription = getString(R.string.canvasContentDescription)

        val baclgroundButton = Button(this)
        baclgroundButton.text = "Select Background Color"
        baclgroundButton.setOnClickListener {
            myCanvasView.showBackgroundColorPickerDialog()
        }

        val colorButton = Button(this)
        colorButton.text = "Select Color"
        colorButton.setOnClickListener {
            myCanvasView.showDrawColorPickerDialog()
        }

        val fontButton = Button(this)
        fontButton.text = "Select Font"
        fontButton.setOnClickListener {
            myCanvasView.showFontPickerDialog()
        }

        val undoButton = Button(this)
        undoButton.text = "Undo"
        undoButton.setOnClickListener {
            myCanvasView.undoLastPath()
        }

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(baclgroundButton)
        layout.addView(colorButton)
        layout.addView(fontButton)
        layout.addView(undoButton)
        layout.addView(myCanvasView)

        setContentView(layout)
    }
}



