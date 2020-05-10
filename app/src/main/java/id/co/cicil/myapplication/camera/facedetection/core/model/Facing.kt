package id.co.cicil.myapplication.camera.facedetection.core.model

/**
 * Created by Franky Wijanarko on 08/05/2020.
 * franky.wijanarko@cicil.co.id
 */

enum class Facing(val value: Int) {
    BACK(0),
    FRONT(1)
}

internal fun Boolean.convertToFacing() = when (this) {
    true -> Facing.BACK
    false -> Facing.FRONT
}