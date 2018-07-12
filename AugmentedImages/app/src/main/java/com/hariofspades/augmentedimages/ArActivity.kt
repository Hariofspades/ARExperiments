package com.hariofspades.augmentedimages

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.support.design.widget.Snackbar
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.PixelCopy
import android.widget.Toast
import com.google.ar.core.*
import com.google.ar.core.exceptions.*
import com.hariofspades.augmentedimages.common.AugmentedImageNode
import com.hariofspades.augmentedimages.common.CameraPermissionHelper
import com.hariofspades.augmentedimages.common.FullScreenHelper
import kotlinx.android.synthetic.main.activity_ar.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*



class ArActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private var session: Session? = null

    private var installRequested: Boolean = false
    private var shouldConfigureSession: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)
        initializeSceneView()
        capture.setOnClickListener { takePhoto() }
    }

    private fun initializeSceneView() {
        arSceneView.scene.setOnUpdateListener { onUpdateFrame() }
    }

    private fun onUpdateFrame() {
        val frame = arSceneView.arFrame
        val updatedAugmentedImages = frame.getUpdatedTrackables(
                AugmentedImage::class.java)

        for (augmentedImage in updatedAugmentedImages) {
            if (augmentedImage.trackingState == TrackingState.TRACKING) {
                // Check camera image matches our reference image
                if (augmentedImage.name == "qrcode") {
                    val node = AugmentedImageNode(this,
                            layouts[Random().nextInt(layouts.size)])
                    node.image = augmentedImage
                    if (arSceneView.scene.children.size == 2) {
                        arSceneView.scene.addChild(node)
                    }
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (session == null) {
            var exception: Exception? = null
            var message: String? = null
            try {
                when (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
                    ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                        installRequested = true
                        return
                    }
                    else -> {  } //TODO("do the necessary")
                }

                // ARCore requires camera permissions to operate. If we did not yet obtain runtime
                // permission on Android M and above, now is a good time to ask the user for it.
                if (!CameraPermissionHelper.hasCameraPermission(this)) {
                    CameraPermissionHelper.requestCameraPermission(this)
                    return
                }

                if (!CameraPermissionHelper.hasWritePermission(this)) {
                    CameraPermissionHelper.requestWritePermission(this)
                    return
                }

                session = Session(/* context = */this)
            } catch (e: UnavailableArcoreNotInstalledException) {
                message = "Please install ARCore"
                exception = e
            } catch (e: UnavailableUserDeclinedInstallationException) {
                message = "Please install ARCore"
                exception = e
            } catch (e: UnavailableApkTooOldException) {
                message = "Please update ARCore"
                exception = e
            } catch (e: UnavailableSdkTooOldException) {
                message = "Please update this app"
                exception = e
            } catch (e: Exception) {
                message = "This device does not support AR"
                exception = e
            }

            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                Log.e(TAG, "Exception creating session", exception)
                return
            }

            shouldConfigureSession = true
        }

        if (shouldConfigureSession) {
            configureSession()
            shouldConfigureSession = false
            arSceneView.setupSession(session)
        }

        // Note that order matters - see the note in onPause(), the reverse applies here.
        try {
            session?.resume()
            arSceneView.resume()
        } catch (e: CameraNotAvailableException) {
            // In some cases (such as another camera app launching) the camera may be given to
            // a different app instead. Handle this properly by showing a message and recreate the
            // session at the next iteration.
            Toast.makeText(this, "Camera not available. Please restart the app.", Toast.LENGTH_LONG).show()
            session = null
            return
        }

    }

    override fun onPause() {
        super.onPause()
        if (session != null) {
            // Note that the order matters - GLSurfaceView is paused first so that it does not try
            // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
            // still call session.update() and get a SessionPausedException.
            arSceneView.pause()
            session?.pause()
        }
    }

    private fun configureSession() {
        val config = Config(session)
        if (!setupAugmentedImageDb(config)) {
            Toast.makeText(this, "Could not setup augmented image database", Toast.LENGTH_LONG).show()
        }
        config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
        session?.configure(config)
    }

    private fun setupAugmentedImageDb(config: Config): Boolean {
        val augmentedImageDatabase = AugmentedImageDatabase(session)

        val augmentedImageBitmap = loadAugmentedImage() ?: return false

        augmentedImageDatabase.addImage("qrcode", augmentedImageBitmap)

        config.augmentedImageDatabase = augmentedImageDatabase
        return true
    }

    private fun loadAugmentedImage(): Bitmap? {
        try {
            assets.open("qrcode.png").use { `is` -> return BitmapFactory.decodeStream(`is`) }
        } catch (e: IOException) {
            Log.e(TAG, "IO exception loading augmented image bitmap.", e)
        }

        return null
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, results: IntArray) {
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(
                    this, "Camera permissions are needed to run this application", Toast.LENGTH_LONG)
                    .show()
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this)
            }
            finish()
        }

    }

    private fun takePhoto() {
        val filename = generateFilename()
        val view = arSceneView
        val bitmap = Bitmap.createBitmap(view.width, view.height,
                Bitmap.Config.ARGB_8888)
        val handlerthread = HandlerThread("PixelCopier")
        handlerthread.start()
        PixelCopy.request(view, bitmap, {
            if (it == PixelCopy.SUCCESS) {
                try {
                    saveToBitmap(bitmap, filename)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                    return@request
                }
                val snack = Snackbar.make(findViewById(android.R.id.content),
                        "Photo saved", Snackbar.LENGTH_LONG)
                snack.setAction("open in photos") {
                    val photoFile = File(filename)
                    val photoURI = FileProvider.getUriForFile(this,
                            this.packageName + ".ar.hariofspades.provider",
                            photoFile)
                    val intent = Intent(Intent.ACTION_VIEW, photoURI)
                    intent.setDataAndType(photoURI, "image/*")
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivity(intent)
                }
                snack.show()
            } else {
                Toast.makeText(this, "Failed to copyPixels: $it", Toast.LENGTH_LONG).show()
            }
            handlerthread.quitSafely()
        }, Handler(handlerthread.looper))
    }

    private fun generateFilename(): String {
        val date = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() +
                File.separator + "Sceneform/" + date + "_screenshot.jpg"
    }

    private fun saveToBitmap(bitmap: Bitmap, filename: String) {
        val out = File(filename)
        if (!out.parentFile.exists()) {
            out.parentFile.mkdirs()
        }
        try {

            val outputStream = FileOutputStream(filename)
            val outputData = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputData)
            outputData.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()

        } catch (e: Exception) {
            e.printStackTrace()
            throw IOException("Failed to save bitmap to disk", e)
        }

    }

    companion object {

        private val layouts = intArrayOf(
                R.layout.bitcoin_layout,
                R.layout.build_time_layout,
                R.layout.lifecycle_layout,
                R.layout.love_jam_layout,
                R.layout.placard_layout,
                R.layout.plus_build,
                R.layout.shot_layout
        )
    }
}

