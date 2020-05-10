package id.co.cicil.myapplication.camera.facedetection.core.model

import android.graphics.Rect

/**
 * Created by Franky Wijanarko on 08/05/2020.
 * franky.wijanarko@cicil.co.id
 */

data class FaceBounds(val id: Int, val box: Rect)

data class Frame(
    val data: ByteArray?,
    val rotation: Int,
    val size: Size,
    val format: Int,
    val isCameraFacingBack: Boolean)

data class Size(val width: Int, val height: Int)