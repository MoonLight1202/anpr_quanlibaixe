package com.example.mongodbrealmcourse.view.fragment.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.mongodbrealmcourse.R
import com.example.mongodbrealmcourse.view.adapter.HomePagerAdapter
import com.example.mongodbrealmcourse.databinding.FragmentMainBinding
import com.example.mongodbrealmcourse.viewmodel.listener.HomeFragmentListener
import com.example.mongodbrealmcourse.viewmodel.listener.HomeListener
import com.google.android.material.navigation.NavigationBarView


class MainFragment : Fragment(), NavigationBarView.OnItemSelectedListener {
    var hasInitializedRootView = false
    lateinit var navController: NavController
    private var binding: FragmentMainBinding? = null
    private var homeFragment: HomeFragment? = null
    private var libraryFragment: LibraryFragment? = null
    private var chartFragment: ChartFragment? = null
    private var userFragment: UserFragment? = null
    private var moreFragment: MoreFragment? = null
    private var posHome = 0
    private var posLibrary = 1
    private var posChart = 2
    private var posUser = 3
    private var posMore = 4
    private var posTabSelected = 0
        set(value) {
            field = value
            binding?.viewPager?.setCurrentItem(value, false)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            posTabSelected = savedInstanceState.getInt("posTabSelected")

            childFragmentManager.apply {
                homeFragment =
                    findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + posHome) as? HomeFragment
                libraryFragment =
                    findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + posLibrary) as? LibraryFragment
                chartFragment =
                    findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + posChart) as? ChartFragment
                userFragment =
                    findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + posUser) as? UserFragment
                moreFragment =
                    findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + posMore) as? MoreFragment
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null) {
            // Inflate the layout for this fragment
            binding = FragmentMainBinding.inflate(inflater, container, false)
        } else {
            // Do not inflate the layout again.
            // The returned View of onCreateView will be added into the fragment.
            // However it is not allowed to be added twice even if the parent is same.
            // So we must remove rootView from the existing parent view group
            // (it will be added back).
            (binding!!.root.parent as? ViewGroup)?.removeView(binding!!.root)
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!hasInitializedRootView) {
            hasInitializedRootView = true
            navController = Navigation.findNavController(view)
            initUI()
        }
    }
    private fun initUI(){
        if (activity == null || context == null) return
        if (homeFragment == null)
            homeFragment = HomeFragment.newInstance(homeListener, insertData)
        else homeFragment!!.setListener(homeListener, insertData)

        if (userFragment == null)
            userFragment = UserFragment.newInstance(homeListener)
        else userFragment!!.setListener(homeListener)

        if (chartFragment == null)
            chartFragment = ChartFragment.newInstance(homeListener)
        else chartFragment!!.setListener(homeListener)

        if(libraryFragment == null)
            libraryFragment = LibraryFragment.newInstance(homeListener)
        else libraryFragment!!.setListener(homeListener)

        if(moreFragment == null)
            moreFragment = MoreFragment.newInstance(homeListener)
        else moreFragment!!.setListener(homeListener)

        val adapter = HomePagerAdapter(requireActivity())
        adapter.addPager(homeFragment!!)
        adapter.addPager(libraryFragment!!)
        adapter.addPager(chartFragment!!)
        adapter.addPager(userFragment!!)
        adapter.addPager(moreFragment!!)


        binding?.apply {
            viewPager.adapter = adapter
            viewPager.offscreenPageLimit = adapter.itemCount
            viewPager.isUserInputEnabled = false
            viewPager.isSaveEnabled = false

            navigation.setOnItemSelectedListener(this@MainFragment)
            navigation.selectedItemId = when (posTabSelected) {
                posHome -> R.id.action_home
                posLibrary -> R.id.action_libry
                posChart -> R.id.action_chart
                posUser -> R.id.action_user
                else -> R.id.action_more
            }
            navigation.itemIconTintList = null
        }
    }
    private val insertData = object : HomeFragmentListener {

        override fun onInsertSuccess(id_user: String) {
            Log.d("TAG_AA", "Main")
            libraryFragment?.loadData(id_user)
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                if (posTabSelected != posHome) {
                    posTabSelected = posHome
                }
                return true
            }
            R.id.action_libry -> {
                if (posTabSelected != posLibrary) {
                    posTabSelected = posLibrary
                }
                return true
            }
            R.id.action_chart -> {
                if (posTabSelected != posChart) {
                    posTabSelected = posChart
                }
                return true
            }
            R.id.action_user -> {
                if (posTabSelected != posUser) {
                    posTabSelected = posUser
                }
                return true
            }
            R.id.action_more -> {
                if (posTabSelected != posMore) {
                    posTabSelected = posMore
                }
                return true
            }
        }
        return false
    }
    private val homeListener = object : HomeListener {
        override fun loginListener(isLogin: Boolean) {
            Log.d("TAG_D", "login")
        }

        override fun scrollUp() {
            binding?.navigation?.visibility = View.GONE
        }

        override fun scrollDown() {
            binding?.navigation?.visibility = View.VISIBLE
        }
    }

}