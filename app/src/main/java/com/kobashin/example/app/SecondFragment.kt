package com.kobashin.example.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import kobashin.com.library.ptt.OnTransitionEvent
import kobashin.com.library.ptt.PullToTransitionLayout
import kobashin.com.library.ptt.ext.dp
import kotlin.math.abs

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_second).setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        view.findViewById<PullToTransitionLayout>(R.id.container_pull_transition).callback = object : OnTransitionEvent {

            override fun onDragging(v: View, event: MotionEvent) {
                // PullToTransitionLayout bypass the TouchEvent's ACTION_MOVE
            }

            override fun onCancelTransition() {
                // TODO
            }

            override fun whetherStartTransition(
                v: View,
                event: MotionEvent,
                startX: Float,
                startY: Float
            ): Boolean {
                return event.action == MotionEvent.ACTION_MOVE && event.y - startY > 15.dp && abs(event.x - startX) < 10.dp
            }

            override fun onFinishTransition() {
                findNavController().popBackStack()
            }
        }
    }
}