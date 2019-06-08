package com.webnation.begonerobotexters

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Html
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.webnation.begonerobotexters.viewmodel.EulaViewModel
import kotlinx.android.synthetic.main.eula.*
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.inject


class EulaActivity : AppCompatActivity() {

    private val sharedPreferences: SharedPreferences by inject()
    private var firstTimeThrough = false

    public override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.eula)

        // Lazy Inject ViewModel
        val eulaViewModel: EulaViewModel by viewModel()

        val fileName: String?
        if (intent.extras != null) {
            fileName = intent.extras.getString(MainActivity.keyFileName)
            firstTimeThrough = intent.extras.getBoolean(MainActivity.keyFirstTimeThrough)
        } else {
            fileName = MainActivity.FILE_EULA
        }

        runBlocking { eula.text = Html.fromHtml(eulaViewModel.getText(fileName),Html.FROM_HTML_MODE_LEGACY)}

        if (!firstTimeThrough) {
            done.visibility = View.GONE
        }

        done.setOnClickListener {
            sharedPreferences.edit().putBoolean(MainActivity.EULA_ACCEPTED, true).apply()
            finish()
        }

    }

    override fun onBackPressed() {
        if (!firstTimeThrough) {
            super.onBackPressed()
        }
    }

}