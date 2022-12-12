package com.example.securechatapp.extension

import androidx.fragment.app.Fragment
import androidx.fragment.app.commit

fun Fragment.replaceFragment(
    containerId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = false,
    tag: String,
    enterAnim: Int = 0,
    exitAnim: Int = 0,
    popEnter: Int = 0,
    popExit: Int = 0,
) {
    if (parentFragmentManager.findFragmentByTag(tag) == null) {

        parentFragmentManager.popBackStack()
        parentFragmentManager.commit {
            setCustomAnimations(enterAnim, exitAnim, popEnter, popExit)
            replace(containerId, fragment, tag)
            if (addToBackStack) {
                addToBackStack(tag)
            }
        }
    }
}

fun Fragment.addFragment(
    containerId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = false,
    tag: String,
    enterAnim: Int = 0,
    exitAnim: Int = 0,
    popEnter: Int = 0,
    popExit: Int = 0,
) {
    if (parentFragmentManager.findFragmentByTag(tag) == null) {
        parentFragmentManager.commit {
            setCustomAnimations(enterAnim, exitAnim, popEnter, popExit)
            add(containerId, fragment, tag)
            if (addToBackStack) {
                addToBackStack(tag)
            }
        }
    }
}

fun Fragment.addChildFragment(
    containerId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = false,
    tag: String,
    enterAnim: Int = 0,
    exitAnim: Int = 0,
    popEnter: Int = 0,
    popExit: Int = 0,
) {
    if (childFragmentManager.findFragmentByTag(tag) == null) {
        childFragmentManager.commit {
            setCustomAnimations(enterAnim, exitAnim, popEnter, popExit)
            add(containerId, fragment, tag)
            if (addToBackStack) {
                addToBackStack(tag)
            }
        }
    }
}

