package com.example.securechatapp.extension
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit

fun FragmentActivity.replaceFragment(
    containerId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = false,
    tag: String
) {
    if (supportFragmentManager.findFragmentByTag(tag) == null) {
        supportFragmentManager.commit {

            replace(containerId, fragment, tag)
            if (addToBackStack) {
                addToBackStack(tag)
            }
        }
    }
}

fun FragmentActivity.addFragment(
    containerId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = false,
    tag: String,
    enterAnim: Int = 0,
    exitAnim: Int = 0,
    popEnter: Int = 0,
    popExit: Int = 0,
) {
    if (supportFragmentManager.findFragmentByTag(tag) == null) {
        supportFragmentManager.commit {
            setCustomAnimations(enterAnim, exitAnim, popEnter, popExit)
            add(containerId, fragment, tag)
            if (addToBackStack) {
                addToBackStack(tag)
            }
        }
    }
}
