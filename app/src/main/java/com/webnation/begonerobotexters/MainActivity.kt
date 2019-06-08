package com.webnation.begonerobotexters


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.telecom.TelecomManager
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.viewpager.widget.ViewPager
import com.greysonparrelli.permiso.Permiso
import com.webnation.begonerobotexters.adapters.ContentFragmentAdapter
import com.webnation.begonerobotexters.fragments.FragmentBlocked
import com.webnation.begonerobotexters.widgets.SlidingTabLayout
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val sharedPreferences : SharedPreferences by inject()
    lateinit var mDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val eulaAccepted = sharedPreferences.getBoolean(EULA_ACCEPTED, false)
        if (!eulaAccepted) {
            val i = Intent(this@MainActivity, EulaActivity::class.java)
            i.putExtra(keyFileName, FILE_EULA)
            i.putExtra(keyFirstTimeThrough,true)
            startActivityForResult(i,REQUEST_CODE_EULA)
        }

        setUpNavigationDrawer()
        setUpSlider()
        Permiso.getInstance().setActivity(this)
        getPermissions()

    }

    //sets up the view pager and the fragments.
    fun setUpSlider() {
        val vpPager = findViewById<View>(R.id.viewpager) as ViewPager
        val adapterViewPager = ContentFragmentAdapter(supportFragmentManager, this, 2)
        vpPager.adapter = adapterViewPager

        val slidingTabLayout = findViewById<View>(R.id.sliding_tabs) as SlidingTabLayout

        slidingTabLayout.setTextColor(ContextCompat.getColor(this, R.color.tab_text_color))
        slidingTabLayout.setTextColorSelected(ContextCompat.getColor(this, R.color.tab_text_color_selected))
        slidingTabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))

        slidingTabLayout.setDistributeEvenly()
        slidingTabLayout.setViewPager(vpPager)
        if (vpPager.currentItem > 0) {

            slidingTabLayout.setTabSelected(vpPager.currentItem)
        } else {
            slidingTabLayout.setTabSelected(0)
        }

        // Change indicator color
        slidingTabLayout.setCustomTabColorizer { ContextCompat.getColor(this,R.color.tab_indicator) }

    }

    private fun setUpNavigationDrawer() {

        setSupportActionBar(toolbar)

        mDrawerToggle = object: ActionBarDrawerToggle(this, drawer_layout, toolbar,R.string.drawer_open, R.string.drawer_close) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)

                invalidateOptionsMenu()
            }

            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)
                //getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu()
            }
        }

        mDrawerToggle.isDrawerIndicatorEnabled = true
        drawer_layout?.addDrawerListener(mDrawerToggle)

        navigation_view.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            when (menuItem.itemId) {
                R.id.navigation_item_1 -> {
                    val intent = Intent(this, EulaActivity::class.java)
                    intent.putExtra(keyFileName, "privacy")
                    startActivity(intent)
                }
                R.id.navigation_item_2 -> {
                    val intent = Intent(this, EulaActivity::class.java)
                    intent.putExtra(keyFileName, "eula")
                    startActivity(intent)
                }
            }
            if (navigation_view != null) {
                drawer_layout?.closeDrawer(navigation_view)
            }
            true
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        mDrawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EULA) {
            if (!isAppAsDefaultDialer(this)) {
                getPermissionToBeDefaultDialer(this)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mDrawerToggle.syncState()
    }

    fun getPermissionToBeDefaultDialer(activity : MainActivity) {
        val context = activity as Context
        val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
        intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,context.applicationContext.getPackageName())
        startActivityForResult(activity,intent, FragmentBlocked.RC_DEFAULT_PHONE,null)

    }


    fun getPermissions() {
        try {
            Permiso.getInstance().requestPermissions(object : Permiso.IOnPermissionResult {
                override fun onPermissionResult(resultSet: Permiso.ResultSet) {
                    if (resultSet.areAllPermissionsGranted()) {
                        Timber.d("Permissions granted")
                    } else {
                        if (!resultSet.isPermissionGranted(Manifest.permission.READ_CONTACTS)) {
                            val builder = AlertDialog.Builder(this@MainActivity)

                            // Set the alert dialog title
                            builder.setTitle(resources.getString(R.string.attention))

                            // Display a message on alert dialog
                            builder.setMessage(resources.getString(R.string.receive_messages_not_granted))

                            // Set a positive button and its click listener on alert dialog
                            builder.setPositiveButton(resources.getString(R.string.grant_permission)) {dialog, _ ->
                                ActivityCompat.requestPermissions(this@MainActivity,
                                    arrayOf(Manifest.permission.READ_CONTACTS),
                                    REQUEST_READ_CONTACTS);
                                dialog.dismiss()
                            }


                            // Display a negative button on alert dialog
                            builder.setNegativeButton(resources.getString(R.string.quit)){_,_ ->
                                android.os.Process.killProcess(android.os.Process.myPid())
                            }


                            // Finally, make the alert dialog using builder
                            val dialog: AlertDialog = builder.create()

                            // Display the alert dialog on app interface
                            dialog.show()

                        }
                        if (!resultSet.isPermissionGranted(Manifest.permission.READ_SMS)) {
                            val builder = AlertDialog.Builder(this@MainActivity)


                            // Set the alert dialog title
                            builder.setTitle(resources.getString(R.string.attention))

                            // Display a message on alert dialog
                            builder.setMessage(resources.getString(R.string.receive_messages_not_granted))

                            // Set a positive button and its click listener on alert dialog
                            builder.setPositiveButton(resources.getString(R.string.grant_permission)) {dialog, _ ->
                                ActivityCompat.requestPermissions(this@MainActivity,
                                    arrayOf(Manifest.permission.READ_SMS),
                                    REQUEST_READ_SMS);
                                dialog.dismiss()
                            }


                            // Display a negative button on alert dialog
                            builder.setNegativeButton(resources.getString(R.string.quit)){_,_ ->
                                android.os.Process.killProcess(android.os.Process.myPid())
                            }


                            // Finally, make the alert dialog using builder
                            val dialog: AlertDialog = builder.create()

                            // Display the alert dialog on app interface
                            dialog.show()

                        }
                        if (!resultSet.isPermissionGranted(Manifest.permission.RECEIVE_SMS) ||
                            !resultSet.isPermissionGranted(Manifest.permission.RECEIVE_MMS) ||
                            !resultSet.isPermissionGranted(Manifest.permission.RECEIVE_WAP_PUSH) ||
                            !resultSet.isPermissionGranted(Manifest.permission.SEND_SMS)) {
                            val builder = AlertDialog.Builder(this@MainActivity)

                            // Set the alert dialog title
                            builder.setTitle(resources.getString(R.string.attention))

                            // Display a message on alert dialog
                            builder.setMessage(resources.getString(R.string.receive_messages_not_granted))

                            // Set a positive button and its click listener on alert dialog
                            builder.setPositiveButton(resources.getString(R.string.grant_permission)) {dialog, _ ->
                                ActivityCompat.requestPermissions(this@MainActivity,
                                    arrayOf(
                                        Manifest.permission.RECEIVE_SMS,
                                        Manifest.permission.RECEIVE_MMS,
                                        Manifest.permission.RECEIVE_WAP_PUSH,
                                        Manifest.permission.SEND_SMS),
                                    REQUEST_RECEIVE_MESSAGES);
                                dialog.dismiss()
                            }


                            // Display a negative button on alert dialog
                            builder.setNegativeButton(resources.getString(R.string.quit)){_,_ ->
                                android.os.Process.killProcess(android.os.Process.myPid())
                            }


                            // Finally, make the alert dialog using builder
                            val dialog: AlertDialog = builder.create()

                            // Display the alert dialog on app interface
                            dialog.show()
                        }

                    }
                }

                override fun onRationaleRequested(callback: Permiso.IOnRationaleProvided, vararg permissions: String) {
                    Permiso.getInstance().showRationaleInDialog(resources.getString(R.string.why_permission),
                        resources.getString(R.string.why_permission_explanation),
                        resources.getString(R.string.i_agree), callback)
                }
            },
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.RECEIVE_MMS,
                Manifest.permission.RECEIVE_WAP_PUSH,
                Manifest.permission.SEND_SMS
            )

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }


    /**
     * Handle the user's permission choice
     * @param requestCode REQUEST_WRITE_STORAGE
     * @param permissions Will be write permission
     * @param grantResults
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Permiso.getInstance().onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    companion object {
        private val REQUEST_READ_SMS = 113
        private val REQUEST_READ_CONTACTS = 114
        private val REQUEST_RECEIVE_MESSAGES = 115
        private val REQUEST_SEND_SMS = 116

        const val EULA_ACCEPTED = "eulaAccepted"
        const val FILE_EULA = "eula"
        const val keyFileName = "filename"
        const val keyFirstTimeThrough ="firstTimeThrough"

        const val REQUEST_CODE_EULA = 123

        fun isAppAsDefaultDialer(context: Context): Boolean {

            val telecom = context.getSystemService(TelecomManager::class.java)

            val isDefault = context.applicationContext.getPackageName().equals(telecom.getDefaultDialerPackage())

            return (isDefault)
        }



    }
}
