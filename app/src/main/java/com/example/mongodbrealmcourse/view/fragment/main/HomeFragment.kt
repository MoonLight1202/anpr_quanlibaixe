package com.example.mongodbrealmcourse.view.fragment.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.mongodbrealmcourse.viewmodel.listener.HomeListener
import com.example.mongodbrealmcourse.viewmodel.utils.getFileFromUri
import com.example.mongodbrealmcourse.model.webService.ApiService
import com.example.mongodbrealmcourse.model.webService.RetrofitHelper
import com.example.mongodbrealmcourse.R
import com.example.mongodbrealmcourse.databinding.FragmentHomeBinding
import com.example.mongodbrealmcourse.viewmodel.listener.HomeFragmentListener
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.BarcodeFormat
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.journeyapps.barcodescanner.BarcodeEncoder
import id.zelory.compressor.Compressor
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.mongo.MongoClient
import io.realm.mongodb.mongo.MongoDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.bson.Document
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.experimental.xor

class HomeFragment : Fragment() {
    private var homeListener: HomeListener? = null
    private var insertSuccessListener: HomeFragmentListener? = null
    private var binding: FragmentHomeBinding? = null
    private val API_TOKEN = "ddecd03711e795147f3feb345ec198eff5d957b6"
    private val Appid = "number-plates-ayumd"
    private lateinit var mongoDatabase: MongoDatabase
    private lateinit var mongoClient: MongoClient

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var app: App
    var isFrontCamera = false

    var vrn: String = ""
    var countryCode: String = ""
    var vehicleType: String = ""
    var score: String = ""
    var bounding: String = ""
    var time: String = ""
    var linkImage : String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (binding == null) {
            // Inflate the layout for this fragment
            binding = FragmentHomeBinding.inflate(inflater, container, false)
        } else {
            (binding!!.root.parent as? ViewGroup)?.removeView(binding!!.root)
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        OpenCVLoader.initDebug()

        cameraExecutor = Executors.newSingleThreadExecutor()
        Realm.init(this.requireContext())
        checkPermissions()

        binding?.ivGallery?.setOnClickListener {
            notifyInsertSuccess()
        }
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        fun newInstance(homeListener: HomeListener?, insertSuccessListener: HomeFragmentListener?): HomeFragment {
            val fragment = HomeFragment()
            fragment.setListener(homeListener, insertSuccessListener)
            return fragment
        }
    }

    fun setListener(homeListener: HomeListener?,insertSuccessListener: HomeFragmentListener? ) {
        this.homeListener = homeListener
        this.insertSuccessListener = insertSuccessListener
    }

    private fun notifyInsertSuccess() {
        insertSuccessListener?.onInsertSuccess()
    }


    private fun checkPermissions() {

        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Toast.makeText(requireActivity(), "Permission Granted", Toast.LENGTH_SHORT).show()
                startCamera()
                initListeners()
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(
                    requireActivity(),
                    "Permission Denied\n$deniedPermissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        TedPermission.create()
            .setPermissionListener(permissionlistener)
            .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .check();

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initListeners() = try {
        binding?.apply {
            ivTakePhoto.setOnClickListener {
                takePhoto()
                //capturePhoto()
            }
            ivSwitchCamera.setOnClickListener {
                isFrontCamera = !isFrontCamera
                startCamera()
                ivSwitchCamera.setImageDrawable(
                    resources.getDrawable(
                        if (isFrontCamera)
                            R.drawable.ic_outline_camera_front_24
                        else
                            R.drawable.ic_outline_camera_rear_24
                    )
                )
            }
        }
    } catch (e: Exception) {
        Log.d(ContentValues.TAG, "Photo capture failed: ${e.message}")
    }

    @SuppressLint("NewApi")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding?.viewFinder?.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector =
                if (isFrontCamera)
                    CameraSelector.DEFAULT_FRONT_CAMERA
                else
                    CameraSelector.DEFAULT_BACK_CAMERA
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                Log.d(ContentValues.TAG, "Use case binding failed ${e.message}")
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }


    @SuppressLint("NewApi")
    private fun takePhoto() = try {
        val imageCapture = imageCapture ?: throw IOException("Không thể kết nối máy ảnh")
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }
        val outputOptions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            ImageCapture.OutputFileOptions
                .Builder(
                    requireContext().contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                ).build()
        else
            ImageCapture.OutputFileOptions
                .Builder(
                    requireContext().contentResolver,
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL),
                    contentValues
                ).build()
        Log.d(ContentValues.TAG, "URI: ${contentValues}")

        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.d(ContentValues.TAG, "Chụp ảnh thất bại: ${exc.message}")
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Log.d(ContentValues.TAG, "Ảnh chụp thành công: ${output.savedUri}")
                    Toast.makeText(
                        requireContext(),
                        "Chụp ảnh thành công: ${output.savedUri}",
                        Toast.LENGTH_SHORT
                    ).show()
                    uploadImageToServerAndGetResults(output.savedUri)
                    Glide.with(this@HomeFragment.requireContext())
                        .asGif()
                        .load(output.savedUri) // Thay đổi thành đường dẫn hoặc resource của tệp GIF của bạn
                        .into(binding!!.imgRssult)


                }
            }
        )
    } catch (e: Exception) {
        Log.d(ContentValues.TAG, "Chụp ảnh thất bại: ${e.message}")
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadImageToServerAndGetResults(savedUri: Uri?) {
        if (savedUri != null) {
            binding?.progressBar?.visibility = View.VISIBLE
            val apiService: ApiService = RetrofitHelper.getInstance().create(ApiService::class.java)
            GlobalScope.launch(Dispatchers.IO) {
                val imgFile = requireContext().getFileFromUri(savedUri)
                val compressedImageFile = Compressor.compress(requireContext(), imgFile)
                val imageFilePart = MultipartBody.Part.createFormData(
                    "upload",
                    compressedImageFile.name,
                    RequestBody.create(
                        MediaType.parse("image/*"),
                        compressedImageFile
                    )
                )
                val response =
                    apiService.getNumberPlateDetails(
                        token = "TOKEN $API_TOKEN",
                        imagePart = imageFilePart
                    )
                if (response.isSuccessful && response.body() != null) {
                    if ((response.body()?.results?.size ?: 0) > 0) {
                        withContext(Dispatchers.Main) {
                            binding?.progressBar?.visibility = View.GONE
                            // Set variables for vehicle data.
                            vrn = response.body()?.results?.get(0)?.plate.toString().uppercase()
                            countryCode = response.body()?.results?.get(0)?.region?.code.toString()
                                .uppercase()
                            vehicleType = response.body()?.results?.get(0)?.vehicle?.type.toString()
                                .uppercase()

                            score = response.body()?.results?.get(0)?.score.toString()

                            bounding = response.body()?.results?.get(0)?.box.toString()

                            time = getCurrentDateTime()


                            Log.d(ContentValues.TAG, response.body()?.results.toString())


                            updateNumberplate(vrn, countryCode, time)
                            var link = uploadImage(savedUri)

                            // Tạo một Rect để định nghĩa hình chữ nhật
                            val left = response.body()!!.results.get(0).box!!.xmin
                            val top = response.body()!!.results.get(0).box!!.ymin
                            val right = response.body()!!.results.get(0).box!!.xmax
                            val bottom = response.body()!!.results.get(0).box!!.ymax

//               //Hiển thị Bitmap đã vẽ lên ImageView hoặc lớp khác
                            Log.d("TAG_DD", left.toString()+top.toString()+right.toString()+bottom.toString())

                            val options = BitmapFactory.Options()
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888 // Cấu hình Bitmap

                            val bitmap = BitmapFactory.decodeFile(compressedImageFile.absolutePath, options)
                            val mat = Mat(bitmap.height, bitmap.width, CvType.CV_8UC3)
                            Utils.bitmapToMat(bitmap, mat)

                            left?.let { top?.let { it1 ->
                                right?.let { it2 ->
                                    bottom?.let { it3 ->
                                        drawRectangleOnImage(mat, it,
                                            it1, it2, it3
                                        )
                                    }
                                }
                            } }

                            val resultBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888)
                            Utils.matToBitmap(mat, resultBitmap)

                            Glide.with(this@HomeFragment.requireContext())
                                .load(resultBitmap)
                                .into(binding?.imgRssult!!)

                            val cutbienso = cropBitmap(bitmap,Rect(left!!,top!!, right!!, bottom!!))
                            Glide.with(this@HomeFragment.requireContext())
                                .load(cutbienso)
                                .into(binding?.imgRssultCrop!!)

                            // Save image to firebase
                            val storageReference = FirebaseStorage.getInstance().getReference("uploads")
                            val imageFileName = "${System.currentTimeMillis()}.${getFileExtension(savedUri)}"
                            val fileReference = storageReference.child(imageFileName)

                            fileReference.putFile(savedUri)
                                .addOnSuccessListener { taskSnapshot ->
                                    val downloadUrlTask = taskSnapshot.storage.downloadUrl

                                    downloadUrlTask.addOnCompleteListener { urlTask ->
                                        if (urlTask.isSuccessful) {
                                            // Đường dẫn URL của tệp tin trên Firebase Storage
                                            val downloadUrl = urlTask.result.toString()
                                            // Bạn có thể làm bất cứ điều gì với đường dẫn tải về, như hiển thị nó hoặc lưu vào cơ sở dữ liệu.
                                            Log.d("LOGINN", "Upload successful. URL: $downloadUrl")

                                            // lưu vào mongodb
                                            app = App(AppConfiguration.Builder(Appid).build())
                                            val user = app.currentUser()
                                            if (user != null) {
                                                mongoClient = user.getMongoClient("mongodb-atlas")
                                            }
                                            mongoDatabase = mongoClient.getDatabase("number-plates-data")
                                            val mongoCollection = mongoDatabase.getCollection("infoPlate")

                                            val document = Document()
                                            document.put("id_user", "")
                                            document.put("info_plate", response.body()?.results?.get(0)?.plate.toString())
                                            document.put("region", response.body()?.results?.get(0)?.region.toString())
                                            document.put("type_car", response.body()?.results?.get(0)?.vehicle?.type.toString())
                                            document.put("pay", "false")
                                            document.put("accuracy", response.body()?.results?.get(0)?.score.toString())
                                            document.put("img_plate", downloadUrl?:"" )
                                            document.put("img_plate_cut", downloadUrl?:"")
                                            document.put("time_create", time)

                                            val addObj = mongoCollection.insertOne(document)
                                            addObj.getAsync { task ->
                                                if (task.isSuccess) {
                                                    Log.v("TAG_G", "Inserted Info Plate Data")
                                                    notifyInsertSuccess()
                                                    try {
                                                        val infoPlate = "Biển số: "+response.body()?.results?.get(0)?.plate.toString()+"\n"+"Thời gian: "+ time+"\n"+"Loại xe: Xe máy"
                                                        val barcodeEncoder = BarcodeEncoder()
                                                        val bitmapVeXe =
                                                            barcodeEncoder.encodeBitmap(decrypt(infoPlate,"PLATEDETECH"), BarcodeFormat.QR_CODE, 400, 400)
                                                        binding!!.ivQRVeXe.setImageBitmap(bitmapVeXe)
                                                        binding!!.ivQRVeXe.visibility = View.VISIBLE
                                                        binding!!.scrollView.post {
                                                            binding!!.scrollView.fullScroll(View.FOCUS_DOWN)
                                                        }
                                                    } catch (e: Exception) {
                                                        Log.d("TAG_A", e.toString())
                                                    }
                                                    //homeListener?.reloadData()
                                                } else {
                                                    Log.v("TAG_G", "Error" + task.error.toString())
                                                }
                                            }
                                        } else {
                                            // Xử lý khi không thể lấy đường dẫn URL
                                            Log.d("LOGINN", "Failed to get download URL")
                                        }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    // Upload thất bại
                                    Log.d("LOGINN", "Upload failed: ${e.message}")
                                }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@HomeFragment.requireContext(),"Không phát hiện biển số trong ảnh", Toast.LENGTH_SHORT).show()
                            binding?.progressBar?.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun uploadImage(imageUri: Uri): String? {
        val storageReference = FirebaseStorage.getInstance().getReference("uploads")
        val imageFileName = "${System.currentTimeMillis()}.${getFileExtension(imageUri)}"
        val fileReference = storageReference.child(imageFileName)

        fileReference.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                val downloadUrlTask = taskSnapshot.storage.downloadUrl

                downloadUrlTask.addOnCompleteListener { urlTask ->
                    if (urlTask.isSuccessful) {
                        // Đường dẫn URL của tệp tin trên Firebase Storage
                        val downloadUrl = urlTask.result.toString()
                        // Bạn có thể làm bất cứ điều gì với đường dẫn tải về, như hiển thị nó hoặc lưu vào cơ sở dữ liệu.
                        linkImage = downloadUrl
                        Log.d("LOGINN", "Upload successful. URL: $downloadUrl")
                    } else {
                        // Xử lý khi không thể lấy đường dẫn URL
                        Log.d("LOGINN", "Failed to get download URL")
                    }
                }
            }
            .addOnFailureListener { e ->
                // Upload thất bại
                Log.d("LOGINN", "Upload failed: ${e.message}")
            }
        return linkImage
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = this@HomeFragment.requireActivity().contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    fun cropBitmap(sourceBitmap: Bitmap, box: Rect): Bitmap {
        // Tạo một Bitmap mới với kích thước của hình chữ nhật (box)
        val croppedBitmap = Bitmap.createBitmap(box.width(), box.height(), Bitmap.Config.ARGB_8888)

        // Tạo một Canvas để vẽ hình chữ nhật vào Bitmap mới
        val canvas = android.graphics.Canvas(croppedBitmap)
        val srcRect = Rect(box.left, box.top, box.right, box.bottom)
        val destRect = Rect(0, 0, box.width(), box.height())

        // Vẽ hình chữ nhật từ sourceBitmap vào croppedBitmap
        canvas.drawBitmap(sourceBitmap, srcRect, destRect, null)

        return croppedBitmap
    }

    fun bitmapToBase64(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    private fun updateNumberplate(vrn: String, country_code: String, time: String) {
        val scoree = (score.toFloat()*100).toInt()
        binding?.apply {
            tvNumberplate.text = vrn
            tvCountryPrefix.text = country_code
            tvTimeCreate.text = "Thời gian: "+time
            tvVehicleScore.text = "Độ chính xác: "+ scoree+"%"
            tvLocation.text = "Quốc gia: "+country_code
        }

    }
    fun drawRectangleOnImage(image: Mat, left: Int, top: Int, right: Int, bottom: Int) {
        val rect = org.opencv.core.Rect(left, top, right - left, bottom - top)
        Imgproc.rectangle(image, rect, Scalar(255.0, 0.0, 0.0), 2)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDateTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")
        return currentDateTime.format(formatter)
    }

    fun decrypt(encryptedText: String, key: String): String {
        val encryptedBytes = android.util.Base64.decode(encryptedText, android.util.Base64.DEFAULT)
        val keyBytes = key.toByteArray(Charsets.UTF_8)

        for (i in encryptedBytes.indices) {
            encryptedBytes[i] = encryptedBytes[i] xor keyBytes[i % keyBytes.size]
        }

        return String(encryptedBytes, Charsets.UTF_8)
    }

}