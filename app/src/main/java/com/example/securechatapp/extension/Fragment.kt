package com.example.securechatapp.extension

import androidx.fragment.app.Fragment
import androidx.fragment.app.commit

fun Fragment.replaceFragment(
    containerId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = false,
    tag: String
) {
    if (parentFragmentManager.findFragmentByTag(tag) == null) {

        parentFragmentManager.popBackStack()

        parentFragmentManager.commit {
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
    tag: String
) {
    if (parentFragmentManager.findFragmentByTag(tag) == null) {
        parentFragmentManager.commit {
            add(containerId, fragment, tag)
            if (addToBackStack) {
                addToBackStack(tag)
            }
        }
    }
}

fun Fragment.replaceChildFragment(
    containerId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = false,
    tag: String,
    enterAnim: Int,
    exitAnim: Int,
    popEnter: Int,
    popExit: Int,
) {
    if (childFragmentManager.findFragmentByTag(tag) == null) {
        childFragmentManager.commit {
            setCustomAnimations(enterAnim, exitAnim, popEnter, popExit)
            replace(containerId, fragment, tag)
            if (addToBackStack) {
                addToBackStack(tag)
            }
        }
    }
}

