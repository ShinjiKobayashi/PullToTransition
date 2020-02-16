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

```
MIT License

Copyright (c) 2020 Shinji Kobayashi

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## Author

[ShinjiKobayashi](https://github.com/ShinjiKobayashi)
