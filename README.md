# PullToTransition
====

Overview

## Description

## Demo

## Usage

Wrap the your view with PullToTransitionLayout in your layout file.
```
    <!-- Wrap the your view with PullToTransitionLayout -->
    <kobashin.com.library.ptt.PullToTransitionLayout
            android:id="@+id/container_pull_transition"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:pull_target="@id/coordinator"
            app:finish_distance="100dp">
            
            <androidx.constraintlayout.widget.ConstraintLayout
                                android:layout_width="match_parent"
                                android:layout_height="match_parent">
            </androidx.constraintlayout.widget.ConstraintLayout>

    </kobashin.com.library.ptt.PullToTransitionLayout>
```

```
findViewById<PullToTransitionLayout>(R.id.container_pull_transition).callback = object : OnTransitionEvent {
            override fun onDragging(v: View, event: MotionEvent) {
                // PullToTransitionLayout bypass the TouchEvent's ACTION_MOVE
            }

            override fun onCancelTransition() {
                // TODO
            }

            override fun whetherStartTransition(v: View, event: MotionEvent): Boolean {
                // TODO
                return true
            }

            override fun onFinishTransition() {
                // TODO
            }
        }
```

## Install
In project build.gradle
```
  repositories {
    maven { url "https://shinjikobayashi.github.io/PullToTransition/repository" }
  }
```

In Module build.gradle
```
dependencies {
  implementation "com.kobashin:pull-to-transition:0.2.0"
}
```
## Contribution

## Licence

## Author

[ShinjiKobayashi](https://github.com/ShinjiKobayashi)
