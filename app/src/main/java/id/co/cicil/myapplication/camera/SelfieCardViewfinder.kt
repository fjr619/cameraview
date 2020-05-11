package id.co.cicil.myapplication.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Paint.Style.STROKE
import android.graphics.PorterDuff.Mode.ADD
import android.graphics.PorterDuff.Mode.CLEAR
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main2.selfieCardViewfinder

/**
 * Created by Franky Wijanarko on 10/05/2020.
 * franky.wijanarko@cicil.co.id
 */

class SelfieCardViewfinder @JvmOverloads constructor(
    val ctx: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(ctx, attrs, defStyleAttr) {

    var rectSelfie = RectF()
        private set
    var rectCard = RectF()
        private set

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val bitmap = Bitmap.createBitmap(width, height, ARGB_8888)
        val auxCanvas = Canvas(bitmap)

        val radius = 30.0f
        val cardAspectRatio = 1.586f
        val selfieAspectRatio = 1.23f

        var leftDraw = 0f
        var rightDraw = width.toFloat()
        var topDraw = ctx.resources.getDimension(R.dimen.margin_xsmall)
        var bottomDraw =
            (height - ctx.resources.getDimension(R.dimen.height_camera_control))

        //SELFIE

        val heightSelfie  = (bottomDraw * 0.6f) - topDraw
        val widthSelfie = heightSelfie / selfieAspectRatio

        val leftSelfie = ((rightDraw - leftDraw) / 2) - (widthSelfie / 2)
        val rightSelfie = leftSelfie + widthSelfie
        val topSelfie = topDraw
        val bottomSelfie = bottomDraw * 0.6f

        rectSelfie = RectF(
            leftSelfie,
            topSelfie,
            rightSelfie,
            bottomSelfie
        )

        //CARD
        leftDraw += ctx.resources.getDimension(R.dimen.margin_xlarge)
        rightDraw -= ctx.resources.getDimension(R.dimen.margin_xlarge)
        topDraw = (bottomDraw + ctx.resources.getDimension(R.dimen.margin_xsmall)) * 0.6f

        val widthCard = rightDraw - leftDraw
        val heightCard = widthCard / cardAspectRatio

        val leftCard = leftDraw
        val rightCard = rightDraw
        val topCard = topDraw + (bottomDraw - topDraw) / 2 - (heightCard / 2)
        val bottomCard = topCard + heightCard

        rectCard = RectF(
            leftCard,
            topCard,
            rightCard,
            bottomCard
        )

        val outerPaint = Paint()
        outerPaint.color = Color.BLACK // mention any background color
        outerPaint.alpha = 150

        outerPaint.xfermode = PorterDuffXfermode(ADD)
        outerPaint.isAntiAlias = true
        auxCanvas.drawRect(0.0f, 0.0f, width.toFloat(), height.toFloat(), outerPaint)

        val innerPaint = Paint()
        innerPaint.color = Color.TRANSPARENT
        innerPaint.xfermode = PorterDuffXfermode(CLEAR)
        innerPaint.isAntiAlias = true
        auxCanvas.drawOval(rectSelfie, innerPaint)
        auxCanvas.drawRoundRect(rectCard, radius, radius,  innerPaint)

        val strokePaint = Paint()
        strokePaint.color = Color.WHITE
        strokePaint.style = STROKE
        strokePaint.strokeWidth = 8f
//        strokePaint.pathEffect = DashPathEffect(floatArrayOf(40f, 20f), 0f)
        auxCanvas.drawOval(rectSelfie, strokePaint)
        auxCanvas.drawRoundRect(rectCard, radius, radius,  strokePaint)

        canvas.drawBitmap(bitmap, 0f, 0f, strokePaint)

        bitmap.recycle()
    }
}