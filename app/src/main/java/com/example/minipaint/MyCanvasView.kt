package com.example.minipaint

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import kotlin.math.abs

// Define the stroke width for drawing
private const val STROKE_WIDTH = 12f
// List to store drawing paths
private val paths = mutableListOf<Path>()

// Define a list of predefined background colors
private val backgroundColors = arrayOf(
    Color.WHITE, Color.BLACK, Color.RED, Color.GREEN, Color.BLUE // Add more colors if needed
)

// Define a list of predefined draw colors
private val drawColors = arrayOf(
    Color.BLACK, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW // Add more colors if needed
)

// Custom View for drawing
class MyCanvasView(context: Context) : View(context) {
    // Canvas and Bitmap for drawing
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    private var currentBackgroundColor = Color.WHITE // Default background color
    private var drawColorIndex = 0 // Default: black

    // Path for drawing
    private var path = Path()

    // Variables to store touch events
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f
    private var currentX = 0f
    private var currentY = 0f

    // Touch tolerance for smoother drawing
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    // Paint properties for drawing
    private val paint = Paint().apply {
        color = drawColor
        isAntiAlias = true // Smooth out the edges
        isDither = true // For down-sampling
        style = Paint.Style.STROKE // Default: FILL
        strokeJoin = Paint.Join.ROUND // Default: MITER
        strokeCap = Paint.Cap.ROUND // Default: BUTT
        strokeWidth = context.resources.getDimensionPixelSize(R.dimen.small_font_size).toFloat() // Default: small stroke width
    }


    // Called when the size of the View changes
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        // Create a new Bitmap and Canvas when size changes
        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(currentBackgroundColor)

    }

    // Draw the content of the View
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw the Bitmap onto the Canvas
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)

    }

    // Handle touch events
    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        // React to touch events
        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }

    // Called when touch starts
    private fun touchStart() {
        // Reset the Path and move to touch position
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    // Called when touch moves
    private fun touchMove() {
        // Calculate distance moved
        val dx = abs(motionTouchEventX - currentX)
        val dy = abs(motionTouchEventY - currentY)
        // Check if movement is significant
        if (dx >= touchTolerance || dy >= touchTolerance) {
            // Draw a curve to the point midway between the last point and the current point
            path.quadTo(currentX, currentY, (motionTouchEventX + currentX) / 2,
                (motionTouchEventY + currentY) / 2)
            currentX = motionTouchEventX
            currentY = motionTouchEventY
            // Draw the path on the Canvas
            extraCanvas.drawPath(path, paint)
        }
        // Invalidate the View to force redraw
        invalidate()
    }

    // Called when touch ends
    private fun touchUp() {

        // Add the current path to the list of paths
        paths.add(Path(path))

        // Reset the Path
        path.reset()

    }

    // Show a color picker dialog
    fun showBackgroundColorPickerDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Select Background Color")
        builder.setItems(arrayOf("White", "Black", "Red", "Green", "Blue")) { _, which ->
            currentBackgroundColor = backgroundColors[which]
            // Update the background color
            extraCanvas.drawColor(currentBackgroundColor)
            // Redraw all the stored paths
            redrawPaths()
            // Invalidate the view to force redraw
            invalidate()
        }
        builder.create().show()
    }

    // Get the current draw color
    private val drawColor: Int
        get() = drawColors[drawColorIndex]

    // Show a color picker dialog for draw color
    fun showDrawColorPickerDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Select Draw Color")
        builder.setItems(arrayOf("Black", "Red", "Green", "Blue", "Yellow")) { _, which ->
            drawColorIndex = which
            paint.color = drawColor // Update the paint color
        }
        builder.create().show()
    }

    // Show a font size picker dialog
    fun showFontPickerDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Select Font Size")
        val fontSizes = arrayOf("Small", "Medium", "Large") // Define your font size options
        builder.setItems(fontSizes) { _, which ->
            val selectedFontSize = when (which) {
                0 -> resources.getDimensionPixelSize(R.dimen.small_font_size).toFloat()
                1 -> resources.getDimensionPixelSize(R.dimen.medium_font_size).toFloat()
                2 -> resources.getDimensionPixelSize(R.dimen.large_font_size).toFloat()
                else -> throw IllegalArgumentException("Invalid font size selection")
            }
            paint.strokeWidth = selectedFontSize // Update the paint text size
        }
        builder.create().show()
    }



    // Redraw all the stored paths
    private fun redrawPaths() {
        for (storedPath in paths) {
            extraCanvas.drawPath(storedPath, paint)
        }
        invalidate()
    }

    // Remove the last drawn path (undo)
    fun undoLastPath() {
        if (paths.isNotEmpty()) {
            paths.removeAt(paths.size - 1)
            clearCanvas()
            drawAllPaths()
        }
    }

    private fun clearCanvas() {
        extraCanvas.drawColor(currentBackgroundColor)
    }

    private fun drawAllPaths() {
        for (storedPath in paths) {
            extraCanvas.drawPath(storedPath, paint)
        }
        invalidate()
    }

}
