package com.example.shortnews

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.kakao.sdk.common.util.Utility
import com.example.shortnews.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val keyHash = Utility.getKeyHash(this)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.areNotificationsEnabled()) {
            // 알림 권한이 없는 경우, 권한 요청을 시작
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            startActivity(intent)
        }
        val channel = NotificationChannel(
            "Shortnews", // 채널 ID (임의로 지정)
            "ShortnewsChannel",     // 채널 이름
            NotificationManager.IMPORTANCE_HIGH// 채널 중요도
        )
        channel.setShowBadge(true)
        channel.enableLights(true)
        channel.enableVibration(true)
        // Head-up 알림 설정
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        channel.enableVibration(true)

        notificationManager.createNotificationChannel(channel)

//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//        val fragment = NewsContentFragment()
//        val tag = "NewsContentFragment"
//        fragmentTransaction.add(R.id.newsContentLayout, fragment, tag)
//        fragmentTransaction.commit()


    } // onCreate

} // MainActivity

