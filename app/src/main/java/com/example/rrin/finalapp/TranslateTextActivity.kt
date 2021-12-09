package com.example.rrin.finalapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.*

class TranslateTextActivity : AppCompatActivity() {
    private lateinit var textViewTranslated: TextView
    private lateinit var textViewInput: TextView
    private lateinit var spinnerLanguages: Spinner
    private lateinit var allLang: MutableMap<String,String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translate_text)

        textViewTranslated = findViewById(R.id.textViewTranslated)
        textViewInput = findViewById(R.id.editTextTextMultiLine)
        spinnerLanguages = findViewById(R.id.spinnerLangSelect)

        allLang = mutableMapOf<String, String>()
        for (lang in TranslateLanguage.getAllLanguages())
        {
            val loc = Locale(lang)
            allLang[loc.displayLanguage] = lang
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, allLang.keys.toList()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLanguages.adapter = adapter
    }

    fun onClick(view: android.view.View) {
        when (view.id) {
            R.id.buttonTranslate -> {
                val translateTo = TranslateLanguage.fromLanguageTag(allLang[spinnerLanguages.selectedItem.toString()])
                // translate a string of text to french
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(translateTo)
                    .build()
                val translator = Translation.getClient(options)
                var conditions = DownloadConditions.Builder()
                    .requireWifi()
                    .build()
                translator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener {
                        translator.translate(textViewInput.text.toString())
                            .addOnSuccessListener { translatedText ->
                                // display translated text
                                textViewTranslated.text = translatedText
                            }
                            .addOnFailureListener { exception ->
                                textViewTranslated.text = getString(R.string.failed_translate)
                            }
                    }
                    .addOnFailureListener { exception ->
                        textViewTranslated.text = getString(R.string.failed_translate)
                    }
            }
            R.id.buttonReturn2 -> {
                // close activity and return to main activity
                finish()
            }
        }
    }
}