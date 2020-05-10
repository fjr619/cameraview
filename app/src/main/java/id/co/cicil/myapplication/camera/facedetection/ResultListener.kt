package id.co.cicil.myapplication.camera.facedetection

import com.google.firebase.ml.vision.face.FirebaseVisionFace

/**
 * Created by Franky Wijanarko on 08/05/2020.
 * franky.wijanarko@cicil.co.id
 */
interface ResultListener {

    fun onSucceed(list: MutableList<FirebaseVisionFace>)
    fun onFailed(e: Throwable)
}