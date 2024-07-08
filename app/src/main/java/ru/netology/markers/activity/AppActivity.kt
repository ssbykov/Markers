package ru.netology.markers.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.yandex.mapkit.MapKitFactory
import ru.netology.markers.BuildConfig
import ru.netology.markers.R
import ru.netology.markers.databinding.ActivityAppBinding

class AppActivity : AppCompatActivity() {

    lateinit var binding: ActivityAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(R.id.mapsFragment)
    }
}