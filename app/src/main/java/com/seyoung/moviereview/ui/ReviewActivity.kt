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
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.seyoung.moviereview.model.ReviewData
import com.seyoung.moviereview.model.ReviewRepository
import java.io.File

class ReviewActivity : AppCompatActivity() {

    private lateinit var writeDay: TextView
    private lateinit var photoUploadRv: RecyclerView
    private lateinit var adapter: ImageAdapter
    private lateinit var infoBackBtn: ImageView
    private lateinit var ratingBar: RatingBar
    private lateinit var reviewTxt: TextView
    private lateinit var saveBtn: Button

    private val REQUEST_CODE = 100
    private val REQUEST_CAMERA = 101
    private val REQUEST_GALLERY = 102

    private val imageList = ArrayList<String>() // Bitmap 또는 Uri 저장
    private var photoUri: Uri? = null

    private var movieId: Int = -1
    private var movieTitle: String = ""
    private var posterUrl : String = ""

    private var position = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.review_activity)

        ratingBar = findViewById(R.id.ratingBar)
        reviewTxt = findViewById(R.id.review_txt)

        // 뒤로가기 버튼
        infoBackBtn = findViewById(R.id.info_back_btn)
        infoBackBtn.setOnClickListener {
            finish()
        }

        // DailyMovieChart에서 넘어온 데이터 값 받기
        // 영화 id
        movieId = intent.getIntExtra("movieId", -1)

        // 영화 제목
        if (intent.getStringExtra("movieTitle") != null) {
            movieTitle = intent.getStringExtra("movieTitle")!!
        } else {
            movieTitle = ""
        }

        // 영화 포스터
        if (intent.getStringExtra("posterPath") != null) {
            posterUrl = intent.getStringExtra("posterPath")!!
            Log.d("POSTER", posterUrl)
        } else {
            posterUrl = ""
        }

        // 리스트 position 값
        position = intent.getIntExtra("reviewPosition",-1)

        // 저장 버튼 클릭 시, WriteFragment의 리스트로 노출 시키기
        // 사용자가 작성한 리뷰를 하나의 객체로 만들어서 리스트에 저장
        saveBtn = findViewById(R.id.save_btn)
        saveBtn.setOnClickListener {
            // 리뷰 객체 생성
            val review = ReviewData(
                // 입력값 가져오기
                reviewText = reviewTxt.text.toString(),     // ex> 영화 재미있음
                rating = ratingBar.rating,                  // 4.5
                writeDate = writeDay.text.toString(),       // 2026-05-12
                imageList = imageList,                      // [...]

                movieId = movieId,
                movieTitle = movieTitle,
                posterUrl = posterUrl
            )

            // 리스트에 추가
            if(position == -1){
                Log.d("POSITION", "새 리뷰 추가")
                ReviewRepository.reviewList.add(review)
            }else{
                Log.d("POSITION", "기존 리뷰 수정 : $position")
                ReviewRepository.reviewList[position] = review
            }

            Log.d("POSTER", posterUrl)

            customAlert()
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

        setEditData()

//        Log.d("EDIT", intent.getStringExtra("reviewText") ?: "null")
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

//    private fun openGallery() {
//        // 갤러리에서 업로드한 사진이 intent로 값을 보낼 때 리스트에 노출이 안됨
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        startActivityForResult(intent, REQUEST_GALLERY)
//    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            // 권한 유지
            flags = Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
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
                    photoUri?.let {
                        imageList.add(it.toString())
                    }
                }

//                REQUEST_GALLERY -> {
//                    val uri = data?.data
//                    if (uri != null) {
//                        imageList.add(uri.toString())
//                    }
//                }

                REQUEST_GALLERY -> {
                    val uri = data?.data
                    if (uri != null) {
                        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        imageList.add(uri.toString())
                    }
                }
            }

            adapter.notifyDataSetChanged()

            Log.d("카메라", "data: $data")
            Log.d("카메라", "extras: ${data?.extras}")
        }
    }

    // 리뷰 저장 버튼 클릭 시, 커스텀 다이얼로그 띄우기
    fun customAlert() {
        val dialogView = layoutInflater.inflate(R.layout.check_alert, null)
        val text_1 = dialogView.findViewById<TextView>(R.id.txt_1)
        val okBtn = dialogView.findViewById<Button>(R.id.ok_btn)

        text_1.text = "저장이 완료 되었습니다."

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        alertDialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )

        okBtn.setOnClickListener {
            alertDialog.dismiss()
            finish()
        }

        alertDialog.show()
    }

    // ReviewPreviewActivity 에서 넘어온 데이터 값 받기
    private fun setEditData() {

        val reviewText = intent.getStringExtra("reviewText")

        if (reviewText != null) {
            reviewTxt.text = reviewText

            ratingBar.rating =
                intent.getFloatExtra("rating", 0f)

            val images =
                intent.getStringArrayListExtra("imageList")
                    ?: arrayListOf()

            imageList.clear()
            imageList.addAll(images)

            adapter.notifyDataSetChanged()
        }
    }
}