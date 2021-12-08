package com.example.rrin.finalapp

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class ReadTextActivity : AppCompatActivity() {

    lateinit var imageView: ImageView;
    lateinit var textView: TextView;
    lateinit var textViewTranslated: TextView;
    val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_text)
        imageView = findViewById(R.id.imageView)
        textView = findViewById(R.id.textView)
        textViewTranslated = findViewById(R.id.textViewTranslated)

        // grant camera permission
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_IMAGE_CAPTURE);
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.buttonOpenCamera -> {
                // open camera
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try {
                    this.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                } catch (e: ActivityNotFoundException) {
                    // display error state to the user
                }
            }
            R.id.buttonTranslate -> {
                // translate a string of text to french
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(TranslateLanguage.FRENCH)
                    .build()
                val englishFrenchTranslator = Translation.getClient(options)
                var conditions = DownloadConditions.Builder()
                    .requireWifi()
                    .build()
                englishFrenchTranslator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener {
                        englishFrenchTranslator.translate(textView.text.toString())
                            .addOnSuccessListener { translatedText ->
                                // display translated text
                                textViewTranslated.text = translatedText
                            }
                            .addOnFailureListener { exception ->
                                textViewTranslated.text = "Failed: could not translate text"
                            }
                    }
                    .addOnFailureListener { exception ->
                        // failed
                    }
            }
            R.id.buttonReturn -> {
                // close activity and return to main activity
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // capture image
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            // display image
            imageView.setImageBitmap(imageBitmap)
            // create instance of ml kit textrecognizer
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            // prepare input image
            val image = InputImage.fromBitmap(imageBitmap,0)
            // process image
            val result = recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // display text
                    textView.text = visionText.text
                    // clear translated text
                    textViewTranslated.text = ""
                }
                .addOnFailureListener { e ->
                    textView.text = "Failed: could not read text"
                }
        }

    }
}