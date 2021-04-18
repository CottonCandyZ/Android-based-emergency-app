package com.example.emergency.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import cn.leancloud.AVUser
import com.example.emergency.R
import com.example.emergency.databinding.ActivityMainBinding
import com.example.emergency.model.EmergencyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val emergencyViewModel: EmergencyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AVUser.currentUser() == null) {
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
            finish()
        }

        // enable dataBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val host =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = host.navController
        setUpBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        emergencyViewModel.refresh()
        emergencyViewModel.initLiveData()
    }

    private fun setUpBottomNavigation() {
        val barConfiguration =
            AppBarConfiguration.Builder(binding.bottomNavigationView.menu).build()
        NavigationUI.setupActionBarWithNavController(
            this,
            navController,
            barConfiguration
        )
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
    }

    // 点击屏幕外侧以取消焦点
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }


    // 改变底部导航栏是否可见
    fun setBottomNavigationVisibility(visible: Boolean) {
        with(binding.bottomNavigationView) {
            visibility = if (visible) View.VISIBLE else View.GONE
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }

    override fun onPause() {
        super.onPause()
        emergencyViewModel.unsubscribe()
    }

}