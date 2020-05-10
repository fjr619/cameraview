package id.co.cicil.myapplication.camera.facedetection.core.model

import id.co.cicil.myapplication.camera.facedetection.core.model.Orientation.ANGLE_0
import id.co.cicil.myapplication.camera.facedetection.core.model.Orientation.ANGLE_180
import id.co.cicil.myapplication.camera.facedetection.core.model.Orientation.ANGLE_270
import id.co.cicil.myapplication.camera.facedetection.core.model.Orientation.ANGLE_90

/**
 * Created by Franky Wijanarko on 08/05/2020.
 * franky.wijanarko@cicil.co.id
 */

enum class Orientation {

    ANGLE_0,
    ANGLE_90,
    ANGLE_180,
    ANGLE_270;

    companion object {
        @JvmStatic
        fun convertToOrientation(value: Int): Orientation {
            return when (value) {
                0 -> ANGLE_0
                90 -> ANGLE_90
                180 -> ANGLE_180
                270 -> ANGLE_270
                else -> ANGLE_270
            }
        }

        @JvmStatic
        fun getOrientationInt(orientation: Orientation): Int {
            return when (orientation) {
                ANGLE_0 -> 0
                ANGLE_90 -> 90
                ANGLE_180 -> 180
                ANGLE_270 -> 270
            }
        }
    }
}

internal fun Int.convertToOrientation() = when (this) {
    0 -> ANGLE_0
    90 -> ANGLE_90
    180 -> ANGLE_180
    270 -> ANGLE_270
    else -> ANGLE_270
}
