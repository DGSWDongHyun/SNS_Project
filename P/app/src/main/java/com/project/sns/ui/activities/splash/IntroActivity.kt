package com.project.sns.ui.activities.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.project.sns.R
import com.project.sns.ui.activities.register.RegisterActivity

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        Handler().postDelayed({
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }, 2500)

    }
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.visible, R.anim.invisible);
    }
}