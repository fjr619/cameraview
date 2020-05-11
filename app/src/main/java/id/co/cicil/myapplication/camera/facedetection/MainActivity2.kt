package id.co.cicil.myapplication.camera.facedetection

import android.graphics.RectF
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraOptions
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Facing.BACK
import com.otaliastudios.cameraview.controls.Facing.FRONT
import com.otaliastudios.cameraview.frame.Frame
import com.otaliastudios.cameraview.frame.FrameProcessor
import id.co.cicil.myapplication.camera.R.layout
import id.co.cicil.myapplication.camera.facedetection.core.FaceDetector
import id.co.cicil.myapplication.camera.facedetection.core.model.Size
import kotlinx.android.synthetic.main.activity_main2.cameraView
import kotlinx.android.synthetic.main.activity_main2.facesBoundsOverlay
import kotlinx.android.synthetic.main.activity_main2.selfieCardViewfinder
import kotlinx.android.synthetic.main.activity_main2.verticalCardViewfinder
import kotlinx.android.synthetic.main.view_camera_control.cameraSwitch
import kotlinx.android.synthetic.main.view_camera_control.capturePicture
import kotlinx.android.synthetic.main.view_camera_guideline.imageUserFace

/**
 * Created by Franky Wijanarko on 09/05/2020.
 * franky.wijanarko@cicil.co.id
 *
 * https://medium.com/androidiots/firebase-ml-kit-101-face-detection-5057190e58c0
 * https://github.com/husaynhakeem/android-face-detector
 * https://github.com/hitanshu-dhawan/FirebaseMLKit
 * https://github.com/crysxd/Object-Tracking-Demo/
 */

class MainActivity2 : AppCompatActivity() /*, FrameProcessor*/, OnClickListener {

    val listPosition = ArrayList<Int>()
    private val faceDetector: FaceDetector by lazy {
        FaceDetector(facesBoundsOverlay)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main2)
        cameraView.setLifecycleOwner(this)
        cameraView.addCameraListener(Listener())
        cameraSwitch.setOnClickListener(this)
        capturePicture.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            cameraSwitch -> {
                cameraView.toggleFacing()
            }
            capturePicture -> {
                if (cameraView.facing == BACK) Log.e("TAG","xx rect top ${verticalCardViewfinder.rect.top}")
                else
                    Log.e("TAG","xx rect top ${selfieCardViewfinder.rectSelfie.top}")
            }
        }
    }

    private val mFrameProcessor =
        FrameProcessor { frame: Frame ->
            if (cameraView.facing == BACK)
                Log.e("TAG", "xx rect top ${verticalCardViewfinder.rect.top}")
            else
                Log.e("TAG", "xx rect top ${selfieCardViewfinder.rectSelfie.top} ")

            faceDetector.process(
                id.co.cicil.myapplication.camera.facedetection.core.model.Frame(
                    data = frame.getData(),
                    rotation = frame.rotationToUser,
                    size = Size(frame.size.width, frame.size.height),
                    format = frame.format,
                    isCameraFacingBack = cameraView.facing == Facing.BACK
                ), selfieCardViewfinder.rectSelfie
            )
        }

    inner class Listener : CameraListener() {
        override fun onCameraOpened(options: CameraOptions) {
            super.onCameraOpened(options)
            cameraView.addFrameProcessor(mFrameProcessor)
        }

        override fun onCameraClosed() {
//            faceDetector.remove()
            cameraView.removeFrameProcessor(mFrameProcessor)
            super.onCameraClosed()

            if (cameraView.facing == FRONT) {
                verticalCardViewfinder.visibility = GONE
                selfieCardViewfinder.visibility = VISIBLE
            } else {
                verticalCardViewfinder.visibility = VISIBLE
                selfieCardViewfinder.visibility = GONE
            }
        }
    }

//    override fun process(frame: Frame) {
//
//        val width = frame.size.width
//        val height = frame.size.height
//
//        Log.e("TAG", "$width $height ${frame.rotationToUser / 90}")
//
//        val metaData = FirebaseVisionImageMetadata.Builder().setWidth(width).setHeight(height)
//            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
//            .setRotation(if (cameraView.facing == Facing.FRONT) FirebaseVisionImageMetadata.ROTATION_270 else FirebaseVisionImageMetadata.ROTATION_90)
//            .build()
//
//        val firebaseVisionImage =  FirebaseVisionImage.fromByteArray(frame.getData(), metaData)
//        val options = FirebaseVisionFaceDetectorOptions.Builder()
//            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
//            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
//            .setClassificationMode(FirebaseVisionFaceDetectorOptions.NO_CLASSIFICATIONS)
//            .setMinFaceSize(0.15f)
//            .enableTracking()
//            .build()
//
//        val faceDetector = FirebaseVision.getInstance().getVisionFaceDetector(options)
//        faceDetector.detectInImage(firebaseVisionImage)
//            .addOnSuccessListener { faceList ->
//                imageView.setImageBitmap(null)
//                Log.e("TAG", "addOnSuccessListener "+imageView.top)
//
//                val bitmap = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888)
//                val canvas = Canvas(bitmap)
//
//                val facePaint = Paint()
//                facePaint.color = Color.RED
//                facePaint.style = Paint.Style.STROKE
//                facePaint.strokeWidth = 4F
//
//                for (face in faceList) {
//
//                    customView.setMarker(listPosition, face)
//                    canvas.drawRect(face.boundingBox, facePaint)
//                    if (cameraView.facing == Facing.FRONT) {
//                        val matrix = Matrix()
//                        matrix.preScale(-1F, 1F)
//                        val flippedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
//                        imageView.setImageBitmap(flippedBitmap)
//                    } else {
//                        imageView.setImageBitmap(bitmap)
//                    }
//                }
//
//            }
//            .addOnFailureListener{
//                Log.e("TAG", "addOnFailureListener")
//            }
//    }
}