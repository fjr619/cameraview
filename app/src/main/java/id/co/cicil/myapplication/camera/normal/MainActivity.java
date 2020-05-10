package id.co.cicil.myapplication.camera.normal;


import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicConvolve3x3;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraLogger;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.controls.Flash;
import id.co.cicil.myapplication.camera.utils.FileUtils;
import id.co.cicil.myapplication.camera.R;
import id.co.cicil.myapplication.camera.facedetection.core.model.Orientation;
import java.util.ArrayList;

/**
 * https://gist.github.com/vxhviet/873d142b41217739a1302d337b7285ba
 * https://natario1.github.io/CameraView/home
 * https://stackoverflow.com/questions/25679259/draw-rectagle-with-fill-outside-bounds
 * https://stackoverflow.com/questions/44628399/android-set-image-overlay-with-background-color-outside-of-bitmap-using-canvas
 */
public class MainActivity extends AppCompatActivity implements OnClickListener {

    private CameraView camera;

    private AppCompatImageButton capturePicture, cameraFlash, cameraSwitch;

    private boolean isCameraBack = true;

    private boolean isCameraFlashOff = true;

    private RelativeLayout layoutCamera, layoutPreview;

    private FrameLayout root;

    private AppCompatImageView imageView, imageUserCard, imageUserFace, imageUserCard2;

    private AppCompatButton btnBack;

    private ContentLoadingProgressBar progress;

    private boolean isFirstTime = true;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permission();

        camera = findViewById(R.id.cameraView);
        CameraLogger.setLogLevel(CameraLogger.LEVEL_VERBOSE);

        capturePicture = findViewById(R.id.capturePicture);
        capturePicture.setOnClickListener(this);

        cameraFlash = findViewById(R.id.cameraFlash);
        cameraFlash.setOnClickListener(this);

        cameraSwitch = findViewById(R.id.cameraSwitch);
        cameraSwitch.setOnClickListener(this);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        layoutCamera = findViewById(R.id.layoutCamera);
        layoutPreview = findViewById(R.id.layoutPreview);
        root = findViewById(R.id.root);
        imageView = findViewById(R.id.imageView);
        root.bringChildToFront(layoutCamera);
        imageUserCard = findViewById(R.id.imageUserCard);
        imageUserFace = findViewById(R.id.imageUserFace);
        imageUserCard2 = findViewById(R.id.imageUserCard2);
        progress = findViewById(R.id.progress);
        camera.setLifecycleOwner(this);
        camera.startAutoFocus(camera.getWidth() / 2F, camera.getHeight() / 2F);
        camera.addCameraListener(new Listener());
    }

    private void permission() {

        int permissionStatus = ContextCompat.checkSelfPermission(this, permission.CAMERA);
        boolean permissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission.CAMERA);
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(permission.CAMERA);
        if (VERSION.SDK_INT < VERSION_CODES.Q) {
            permissionStatus += ContextCompat.checkSelfPermission(this, permission.READ_EXTERNAL_STORAGE) +
                    ContextCompat.checkSelfPermission(this, permission.WRITE_EXTERNAL_STORAGE);

            permissionRationale = permissionRationale || ActivityCompat
                    .shouldShowRequestPermissionRationale(this, permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, permission.WRITE_EXTERNAL_STORAGE);

            permissions.add(permission.READ_EXTERNAL_STORAGE);
            permissions.add(permission.WRITE_EXTERNAL_STORAGE);
        }

        if (permissionStatus
                != PackageManager.PERMISSION_GRANTED) {

            if (permissionRationale) {
                ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), 1001);
            } else {
                ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), 1001);
            }
        }
    }

    private class Listener extends CameraListener {

        @Override
        public void onCameraOpened(@NonNull final CameraOptions options) {
            super.onCameraOpened(options);

//            ArrayList<AspectRatio> a = new ArrayList<>(options.getSupportedPictureAspectRatios());
//            AspectRatio a1 = a.get(a.size() - 1);
//            Log.e("tag", "aspek " + a1.getX() + " " + a1.getY());
//            SizeSelector ratio = SizeSelectors.aspectRatio(AspectRatio.of(9, 16), 0);
//            camera.setPictureSize(ratio);
//            camera.setPreviewStreamSize(ratio);

            Log.e("CameraEngine", "onCameraOpened" + camera.getFlash() + " " + camera.getFacing());
            isCameraBack = camera.getFacing() == Facing.BACK;
            setDrawableCameraFlash();

            if (isCameraBack && !options.getSupportedFlash().isEmpty()) {
                cameraFlash.setVisibility(View.VISIBLE);
            } else {
                cameraFlash.setVisibility(View.INVISIBLE);
            }

            if (isCameraBack) {
                imageUserCard.setVisibility(View.VISIBLE);
                imageUserFace.setVisibility(View.GONE);
                imageUserCard2.setVisibility(View.GONE);
            } else {
                imageUserCard.setVisibility(View.GONE);
                imageUserFace.setVisibility(View.VISIBLE);
                imageUserCard2.setVisibility(View.VISIBLE);
            }

            if (!options.getSupportedFacing().contains(Facing.FRONT)) {
                cameraSwitch.setVisibility(View.GONE);
            }


        }

        @SuppressLint("WrongThread")
        @Override
        public void onPictureTaken(@NonNull final PictureResult result) {
            super.onPictureTaken(result);
            root.bringChildToFront(progress);
            Toast.makeText(MainActivity.this,
                    "poto diambil " + result.getRotation() + " " + result.getSize().getHeight() + " " + result
                            .getSize().getWidth(), Toast.LENGTH_SHORT).show();
            result.toBitmap(bitmap -> {
                try {
                    bitmap = getScaledBitmap(bitmap, 1800, result);

                    //save it
                    FileUtils.writeQ(MainActivity.this, bitmap, CompressFormat.JPEG, 100);
                    imageView.setImageBitmap(bitmap);
                    root.bringChildToFront(layoutPreview);

                    Log.e("PicturePreview",
                            "The picture full size is " + bitmap.getHeight() + "x" + bitmap.getWidth());
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "GAGAL SAVE!!", Toast.LENGTH_SHORT).show();

                }
            });
        }

        @Override
        public void onCameraError(@NonNull final CameraException exception) {
            super.onCameraError(exception);
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.capturePicture:
                capturePicture();
                break;
            case R.id.cameraSwitch:
                cameraSwitch();
                break;
            case R.id.cameraFlash:
                toogleCameraFlash();
                break;
            case R.id.btnBack: {
                imageView.setImageBitmap(null);
                root.bringChildToFront(layoutCamera);
                break;
            }
        }
    }

    private void capturePicture() {
        if (camera.isTakingPicture()) {
            return;
        }
        camera.takePicture();
    }

    private void cameraSwitch() {
        if (camera.isTakingPicture()) {
            return;
        }
        camera.toggleFacing();
    }

    private void toogleCameraFlash() {
        isCameraFlashOff = camera.getFlash() != Flash.OFF;
        setDrawableCameraFlash();
    }

    private void setDrawableCameraFlash() {
        if (isCameraFlashOff) {
            camera.setFlash(Flash.OFF);
            cameraFlash.setImageResource(R.drawable.ic_flash_off);
        } else {
            camera.setFlash(Flash.ON);
            cameraFlash.setImageResource(R.drawable.ic_flash_on);
        }
    }

    Bitmap cropToAspectRatio(Bitmap image, int previewWidth, int previewHeight) {
        int width = image.getWidth();
        int height = image.getHeight();
        float imageAspectRatio = (float) width / (float) height;
        float aspectRatio = (float) previewWidth / (float) previewHeight;
        if (width > height) {
            aspectRatio = (float) previewHeight / (float) previewWidth;
        }

        if (imageAspectRatio == aspectRatio) {
            return image;
        }

        int newWidth = (int) (height * aspectRatio);
        if (newWidth < width) {
            int margin = (width - newWidth) / 2;
            return Bitmap.createBitmap(image, margin, 0, newWidth, height);
        } else {
            int newHeight = (int) (width / aspectRatio);
            int margin = (height - newHeight) / 2;
            return Bitmap.createBitmap(image, 0, margin, width, newHeight);
        }
    }

    private Bitmap getScaledBitmap(Bitmap bitmap, int threshold, PictureResult result)
            throws IllegalArgumentException, NullPointerException {

        boolean isUsedLibsBitmap = true;

        if (bitmap == null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeByteArray(result.getData(), 0, result.getData().length, options);
            isUsedLibsBitmap = false;
        }

        bitmap = loadBitmapSharp2(bitmap);

        // Get the dimensions of the original bitmap
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Calculate the target dimensions
        int newWidth = 0, newHeight = 0;
        if (width > height && width > threshold) {
            newWidth = threshold;
            newHeight = (int) (height * (float) newWidth / width);
        }

        if (width > height && width <= threshold) {
            newWidth = width;
            newHeight = height;
        }

        if (width < height && height > threshold) {
            newHeight = threshold;
            newWidth = (int) (width * (float) newHeight / height);
        }

        if (width < height && height <= threshold) {
            newWidth = width;
            newHeight = height;
        }

        if (width == height && width > threshold) {
            newWidth = threshold;
            newHeight = newWidth;
        }

        if (width == height && width <= threshold) {
            newWidth = width;
            newHeight = height;
        }

        Log.e("aaa", "width =" + width + " height =" + height);
        Log.e("aaaa", newWidth + " " + newHeight);

        // create empty scaled bitmap
        float ratioX = newWidth / (float) bitmap.getWidth();
        float ratioY = newHeight / (float) bitmap.getHeight();
        float middleX = 0;
        float middleY = 0;
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Config.ARGB_8888);

        // use canvas to draw scaled bitmap
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        if (!isUsedLibsBitmap) {
            // rotate scaled bitmap
            scaleMatrix = new Matrix();
            Orientation orientation = Orientation.convertToOrientation(result.getRotation());
            scaleMatrix.postRotate(Orientation.getOrientationInt(orientation));
            scaledBitmap = Bitmap
                    .createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), scaleMatrix,
                            true);
        }

//        return scaledBitmap;

        // crop scaled bitmap into preview aspect ratio
        return cropToAspectRatio(scaledBitmap, camera.getWidth(), camera.getHeight());
    }

    public Bitmap doSharpen(Bitmap original, float[] radius) {
        Bitmap bitmap = Bitmap.createBitmap(
                original.getWidth(), original.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript rs = RenderScript.create(this);

        Allocation allocIn = Allocation.createFromBitmap(rs, original);
        Allocation allocOut = Allocation.createFromBitmap(rs, bitmap);

        ScriptIntrinsicConvolve3x3 convolution
                = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
        convolution.setInput(allocIn);
        convolution.setCoefficients(radius);
        convolution.forEach(allocOut);

        allocOut.copyTo(bitmap);
        rs.destroy();

        return bitmap;
    }

    // high contrast
    private Bitmap loadBitmapSharp(Bitmap bitmap) {
        float[] sharp = {-0.60f, -0.60f, -0.60f, -0.60f, 5.81f, -0.60f,
                -0.60f, -0.60f, -0.60f};
//you call the method above and just paste the bitmap you want to apply it and the float of above
        return doSharpen(bitmap, sharp);
    }

    // medium
    private Bitmap loadBitmapSharp1(Bitmap bitmap) {
        float[] sharp = {0.0f, -1.0f, 0.0f, -1.0f, 5.0f, -1.0f, 0.0f, -1.0f,
                0.0f

        };
//you call the method above and just paste the bitmap you want to apply it and the float of above
        return doSharpen(bitmap, sharp);
    }

    // low
    private Bitmap loadBitmapSharp2(Bitmap bitmap) {
        float[] sharp = {-0.15f, -0.15f, -0.15f, -0.15f, 2.2f, -0.15f, -0.15f,
                -0.15f, -0.15f
        };
        //you call the method above and just paste the bitmap you want to apply it and the float of above
        return doSharpen(bitmap, sharp);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean valid = true;

        for (int grantResult : grantResults) {
            Log.e("TAG", "grantResult =" + grantResult);
            valid = valid && grantResult == PackageManager.PERMISSION_GRANTED;
        }

        for (String permission : permissions) {
            Log.e("TAG", "permission =" + permission);
        }
        Log.e("TAG", "permission size =" + permissions.length);

        if (!valid) {
            camera.close();
            permission();
        }

        Log.e("TAG", "valid =" + valid);
        if (valid && !camera.isOpened()) {
            camera.open();
        }
    }
}