package com.seyoung.moviereview.ui

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.seyoung.moviereview.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import java.io.File

class ReviewActivity : AppCompatActivity() {

    private lateinit var writeDay: TextView
    private lateinit var photoUploadRv: RecyclerView
    private lateinit var adapter: ImageAdapter
    private lateinit var infoBackBtn: ImageView
    private lateinit var ratingBar: RatingBar
    private lateinit var reviewTxt: TextView

    private val REQUEST_CODE = 100
    private val REQUEST_CAMERA = 101
    private val REQUEST_GALLERY = 102

    private val imageList = ArrayList<Any>() // Bitmap 또는 Uri 저장
    private lateinit var photoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.review_activity)

        // 뒤로가기 버튼
        infoBackBtn = findViewById(R.id.info_back_btn)
        infoBackBtn.setOnClickListener {
            finish()
        }

        // 작성 날짜를 오늘 날짜로 받아오기
        writeDay = findViewById(R.id.write_day)
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy - MM - dd")
        val formattedDate = currentDate.format(formatter)
        writeDay.text = formattedDate

        // 사진 추가 버튼 클릭 시, 카메라 또는 앨범에서 업로드 받기
        // 권한 받고 사진 업로드 하면 addPhoto 이미지 안보이게
//        addPhoto = findViewById(R.id.add_photo)
//        addPhoto.setOnClickListener {
//            if (checkPermission()) {
//                showImagePickerDialog()
//            } else {
//                requestPermission()
//            }
//        }

        photoUploadRv = findViewById(R.id.photo_upload_rv)
        photoUploadRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        // Activity에서 클릭 처리
        adapter = ImageAdapter(imageList, object : ImageAdapter.OnAddClickListener {
            override fun onAddPhotoClick() {
                if (checkPermission()) {
                    showImagePickerDialog()
                } else {
                    requestPermission()
                }
            }
        })
        photoUploadRv.adapter = adapter
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
                ),
                REQUEST_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                REQUEST_CODE
            )
        }
    }

    private fun showImagePickerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.camera_popup, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val cameraBtn = dialogView.findViewById<ImageView>(R.id.camera_btn)
        val albumBtn = dialogView.findViewById<ImageView>(R.id.album_btn)

        cameraBtn.setOnClickListener {
            dialog.dismiss()
            openCamera()
        }

        albumBtn.setOnClickListener {
            dialog.dismiss()
            openGallery()
        }
        dialog.show()
    }

    private fun openCamera() {
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        startActivityForResult(intent, REQUEST_CAMERA)

        // 카메라로 3장째 사진 찍고 업로드 시, 앱 강제종료 됨
        // Bitmap = 메모리 많이 먹고, RecyclerView = 재사용 + 재측정 반복 + NestedScrollView 사용
        // 카메라도 Uri로 받기
        val file = File.createTempFile("photo_", ".jpg", cacheDir)
        photoUri = FileProvider.getUriForFile(this, "com.seyoung.moviereview.fileprovider", file)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {

//                REQUEST_CAMERA -> {
//                    val bitmap = data?.extras?.get("data") as? Bitmap
//                    if (bitmap != null) {
//                        imageList.add(bitmap)
//                    }
//                }

                REQUEST_CAMERA -> {
                    imageList.add(photoUri)
                }

                REQUEST_GALLERY -> {
                    val uri = data?.data
                    if (uri != null) {
                        imageList.add(uri)
                    }
                }
            }

            adapter.notifyDataSetChanged()

            Log.d("카메라", "data: $data")
            Log.d("카메라", "extras: ${data?.extras}")
        }
    }
}