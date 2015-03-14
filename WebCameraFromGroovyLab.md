# Introduction #

`The Web camera toolbox` **`webCamera.jar`** `is based on project`

`https://github.com/sarxos/webcam-capture`

`Using the Web Camera is very simple:`

`1. Install ` **`webcam.jar`** ` as GroovyLab toolbox.`

`2. Start the Web camera viewing by executing`

```
 wcam = new com.github.sarxos.webcam.WebcamViewer()
```


`The Java programming interface of the project mentioned above, can be utilized from GroovyLab, to perform for example tasks as image capturing and motion detection.`


## `Motion detection with GroovyLab` ##

```


import java.io.IOException

import com.github.sarxos.webcam.Webcam
import com.github.sarxos.webcam.WebcamMotionDetector
import com.github.sarxos.webcam.WebcamMotionEvent
import com.github.sarxos.webcam.WebcamMotionListener


    detector = new WebcamMotionDetector(Webcam.getDefault())
    detector.setInterval(100)  // one check per 100 ms
    motionListenerObj = new MotionListener()   // the motion listener object
    detector.addMotionListener(motionListenerObj)
    detector.start()
        

class MotionListener implements WebcamMotionListener {
        void  motionDetected(  WebcamMotionEvent  wme) {
                //println("Detected motion I, alarm turn on you have");
  //java.awt.Toolkit.getDefaultToolkit().beep()
  org.sound.SoundUtils.tone(400,1000)

        }
}

}

```