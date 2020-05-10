package id.co.cicil.myapplication.camera.facedetection.core

import android.R.color
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import id.co.cicil.myapplication.camera.facedetection.core.model.FaceBounds
import id.co.cicil.myapplication.camera.facedetection.core.model.Facing
import id.co.cicil.myapplication.camera.facedetection.core.model.Facing.BACK
import id.co.cicil.myapplication.camera.facedetection.core.model.Facing.FRONT
import id.co.cicil.myapplication.camera.facedetection.core.model.Orientation
import id.co.cicil.myapplication.camera.facedetection.core.model.Orientation.ANGLE_0
import id.co.cicil.myapplication.camera.facedetection.core.model.Orientation.ANGLE_180
import id.co.cicil.myapplication.camera.facedetection.core.model.Orientation.ANGLE_270
import id.co.cicil.myapplication.camera.facedetection.core.model.Orientation.ANGLE_90
import java.util.ArrayList

/**
 * Created by Franky Wijanarko on 08/05/2020.
 * franky.wijanarko@cicil.co.id
 */

/**
 * A view that renders the results of a face detection process. It contains a list of faces
 * bounds which it draws using a set of attributes provided by the camera: Its width, height,
 * orientation and facing. These attributes impact how the face bounds are drawn, especially
 * the scaling factor between this view and the camera view, and the mirroring of coordinates.
 */

/**
 * A view that renders the results of a face detection process. It contains a list of faces
 * bounds which it draws using a set of attributes provided by the camera: Its width, height,
 * orientation and facing. These attributes impact how the face bounds are drawn, especially
 * the scaling factor between this view and the camera view, and the mirroring of coordinates.
 */
class FaceBoundsOverlay @JvmOverloads constructor(
    val ctx: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : View(ctx, attrs, defStyleAttr) {

    private val facesBounds: MutableList<FaceBounds> = mutableListOf()

    private val anchorPaint = Paint()
    private val idPaint = Paint()
    private val boundsPaint = Paint()
    private val correctPaint = Paint()
    val listPosition = ArrayList<Int>()

    var cameraPreviewWidth: Float = 0f
    var cameraPreviewHeight: Float = 0f
    var cameraOrientation: Orientation =
        ANGLE_270
    var cameraFacing: Facing =
        BACK

    init {
        anchorPaint.color = ContextCompat.getColor(context, android.R.color.holo_blue_dark)

        idPaint.color = ContextCompat.getColor(context, android.R.color.holo_blue_dark)
        idPaint.textSize = 40f

        boundsPaint.style = Paint.Style.STROKE
        boundsPaint.color = ContextCompat.getColor(context, android.R.color.holo_red_light)
        boundsPaint.strokeWidth = 4f

        correctPaint.color = ContextCompat.getColor(ctx, color.holo_green_light)
        correctPaint.style = Paint.Style.STROKE
        correctPaint.alpha = 100
        correctPaint.strokeWidth = 10f
    }

    /**
     * Repopulates the face bounds list, and calls for a redraw of the view.
     */
    fun updateFaces(bounds: List<FaceBounds>, listPosition: ArrayList<Int>) {
        this.listPosition.clear()
        this.listPosition.addAll(listPosition)
        facesBounds.clear()
        facesBounds.addAll(bounds)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (cameraOrientation == ANGLE_0 || cameraOrientation == ANGLE_180) {
            return
        }

        facesBounds.forEach {
            val centerX = computeFaceBoundsCenterX(canvas.width.toFloat(), scaleX(it.box.exactCenterX(), canvas))
            val centerY = computeFaceBoundsCenterY(canvas.height.toFloat(), scaleY(it.box.exactCenterY(), canvas))
//            drawAnchor(canvas, centerX, centerY)
//            drawId(canvas, it.id.toString(), centerX, centerY)
            drawBounds(it.box, canvas, centerX, centerY)
        }
    }

    /**
     * Calculates the center of the face bounds's X coordinate depending on the camera's facing
     * and orientation. A change in the facing results in mirroring the coordinate.
     */
    private fun computeFaceBoundsCenterX(viewWidth: Float, scaledCenterX: Float) = when {
        cameraFacing == FRONT && cameraOrientation == ANGLE_270 -> viewWidth - scaledCenterX
        cameraFacing == FRONT && cameraOrientation == ANGLE_90 -> scaledCenterX
        cameraFacing == BACK && cameraOrientation == ANGLE_270 -> viewWidth - scaledCenterX
        cameraFacing == BACK && cameraOrientation == ANGLE_90 -> scaledCenterX
        else -> scaledCenterX
    }

    /**
     * Calculates the center of the face bounds's Y coordinate depending on the camera's facing
     * and orientation. A change in the facing results in mirroring the coordinate.
     */
    private fun computeFaceBoundsCenterY(viewHeight: Float, scaledCenterY: Float) = when {
        cameraFacing == FRONT && cameraOrientation == ANGLE_270 -> scaledCenterY
        cameraFacing == FRONT && cameraOrientation == ANGLE_90 -> viewHeight - scaledCenterY
        cameraFacing == BACK && cameraOrientation == ANGLE_270 -> viewHeight - scaledCenterY
        cameraFacing == BACK && cameraOrientation == ANGLE_90 -> scaledCenterY
        else -> scaledCenterY
    }

    /**
     * Draws an anchor/dot at the center of a face
     */
    private fun drawAnchor(canvas: Canvas, centerX: Float, centerY: Float) {
        canvas.drawCircle(
            centerX,
            centerY,
            ANCHOR_RADIUS,
            anchorPaint)
    }

    /**
     * Draws/Writes the face's id
     */
    private fun drawId(canvas: Canvas, id: String, centerX: Float, centerY: Float) {
        canvas.drawText(
            "face id $id",
            centerX - ID_OFFSET,
            centerY + ID_OFFSET,
            idPaint)
    }

    /**
     * Draws bounds around a face as a rectangle
     */
    private fun drawBounds(box: Rect, canvas: Canvas, centerX: Float, centerY: Float) {
        val xOffset = scaleX(box.width() / 2.0f, canvas)
        val yOffset = scaleY(box.height() / 2.0f, canvas)
        val left = centerX - xOffset
        val right = centerX + xOffset
        val top = centerY - yOffset
        val bottom = centerY + yOffset

        val rectA = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        val rectB = Rect(
            listPosition[0],
            listPosition[2],
            listPosition[1],
            listPosition[3]
        )

        if (rectB.contains(rectA)) {
            canvas.drawRect(
                rectB,
                correctPaint
            )
        } else {
            canvas.drawRect(
                rectA,
                boundsPaint)
        }
    }

    /**
     * Adjusts a horizontal value x from the camera preview scale to the view scale
     */
    private fun scaleX(x: Float, canvas: Canvas) =
        x * (canvas.width.toFloat() / cameraPreviewWidth)

    /**
     * Adjusts a vertical value y from the camera preview scale to the view scale
     */
    private fun scaleY(y: Float, canvas: Canvas) =
        y * (canvas.height.toFloat() / cameraPreviewHeight)

    fun removeFaces() {
        this.listPosition.clear()
        facesBounds.clear()
        invalidate()
    }

    companion object {
        private const val ANCHOR_RADIUS = 10f
        private const val ID_OFFSET = 50f
    }
}


//        val rectA = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
//
//        val rectB = Rect(
//            listPosition[0],
//            listPosition[2],
//            listPosition[1],
//            listPosition[3]
//        )
//
//        if (rectB.contains(rectA)) {
//            val myPaint = Paint()
//            myPaint.color = ContextCompat.getColor(ctx, color.holo_green_light)
//            myPaint.alpha = 100
//            myPaint.strokeWidth = 10f
//            canvas.drawRect(
//                listPosition.get(0).toFloat(),
//                listPosition.get(2).toFloat(),
//                listPosition.get(1).toFloat(),
//                listPosition.get(3).toFloat(),
//                myPaint
//            )
//
//            Log.e(
//                MainActivity2.TAG,
//                "NICE"
//            )