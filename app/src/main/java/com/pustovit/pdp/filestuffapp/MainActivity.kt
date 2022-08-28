package com.pustovit.pdp.filestuffapp

import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.pustovit.pdp.filestuffapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var appBarConfiguration: AppBarConfiguration? = null
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).also {
            binding = it
            setContentView(it.root)
            setSupportActionBar(it.appBarMain.toolbar)
            initViews(it)
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return appBarConfiguration?.let {
            navController.navigateUp(it) || super.onSupportNavigateUp()
        } ?: super.onSupportNavigateUp()
    }

    private fun initViews(binding: ActivityMainBinding) {
        setAppNavigation(binding)
        setNavigationHeaderLayout(binding)
        setFabButton(binding)
    }

    private fun setAppNavigation(binding: ActivityMainBinding) {
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_share, R.id.nav_save, R.id.nav_slideshow
            ), drawerLayout
        ).also {
            this.appBarConfiguration = it
        }
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun setNavigationHeaderLayout(binding: ActivityMainBinding) {
        val headerImageView = binding.navView.getHeaderView(0).findViewById<ImageView>(R.id.headerImageView)
        Glide.with(this)
            .asBitmap()
            .load(R.drawable.my_photo)
            .circleCrop()
            .into(headerImageView)
    }

    private fun setFabButton(binding: ActivityMainBinding) {
        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

}