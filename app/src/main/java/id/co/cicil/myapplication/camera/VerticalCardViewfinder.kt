package id.co.cicil.myapplication.camera

import android.R.attr
import android.R.color
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Paint.Align.CENTER
import android.graphics.Paint.Style.FILL
import android.graphics.Paint.Style.STROKE
import android.graphics.Point
import android.graphics.PorterDuff.Mode.CLEAR
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import java.util.ArrayList

/**
 * Created by Franky Wijanarko on 09/05/2020.
 * franky.wijanarko@cicil.co.id
 * https://stackoverflow.com/questions/25679259/draw-rectagle-with-fill-outside-bounds
 */

class VerticalCardViewfinder @JvmOverloads constructor(
    val ctx: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(ctx, attrs, defStyleAttr) {

    var rect = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mShow = true

    fun showGuideLine(show: Boolean) {
        this.mShow = show
        invalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (mShow) {
            //5.60 × 53.98 mm (3.370 × 2.125 in)
            val cardAspectRatio = 1.586f

            // set up some constants
            val leftDraw = 0f
            val rightDraw = width.toFloat()
            val topDraw = ctx.resources.getDimension(R.dimen.margin_medium)
            val bottomDraw =
                (height - ctx.resources.getDimension(R.dimen.height_camera_control) - ctx.resources.getDimension(R.dimen.margin_medium))

            val heightCard = bottomDraw - topDraw
            val widthCard = heightCard / cardAspectRatio

//            Log.e("TAG", "xx canvas $width $height")
//            Log.e("TAG", "xx area draw ${rightDraw - leftDraw}, ${bottomDraw - topDraw}")
//            Log.e("TAG", "xx card size $widthCard $heightCard")

            val leftCard = (rightDraw - leftDraw) / 2 - widthCard / 2
            val rightCard = leftCard + widthCard
            val topCard = topDraw
            val bottomCard = bottomDraw

            rect = RectF(
                leftCard,
                topCard,
                rightCard,
                bottomCard
            )

            val radius = 30.0f

            // first create an off-screen bitmap and its canvas
            val bitmap = Bitmap.createBitmap(width, height, ARGB_8888)
            val auxCanvas = Canvas(bitmap)

            // then fill the bitmap with the desired outside color
            paint.color = Color.BLACK
            paint.alpha = 205
            paint.style = FILL
            auxCanvas.drawPaint(paint)

            // then punch a transparent hole in the shape of the rect
            paint.xfermode = PorterDuffXfermode(CLEAR)
            auxCanvas.drawRoundRect(rect, radius, radius, paint)

            // then draw the white rect border (being sure to get rid of the xfer mode!)
            paint.xfermode = null
            paint.color = Color.WHITE
            paint.style = STROKE
            paint.strokeWidth = 5f
            // paint.pathEffect = DashPathEffect(floatArrayOf(40f, 20f), 0f)
            auxCanvas.drawRoundRect(rect, radius, radius, paint)

            // finally, draw the whole thing to the original canvas
            canvas.drawBitmap(bitmap, 0f, 0f, paint)

            val paintText = Paint()
            paintText.color = Color.WHITE
            paintText.textSize = 50f
            paintText.textAlign = CENTER

            canvas.drawText(
                "Position Card in this Frame",
                (width / 2).toFloat(),
                (height - ctx.resources.getDimension(R.dimen.height_camera_control) - ctx.resources.getDimension(R.dimen.margin_xsmall)),
                paintText
            )

            bitmap.recycle()
        }
    }
}