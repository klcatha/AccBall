package com.example.kengomaruyama.accball

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener
                    , SurfaceHolder.Callback{

    private var surfaceWidth: Int = 0
    private var surfaceHeight: Int = 0

    private val radius = 50.0f;
    private val coef = 1000.0f

    private var ballx: Float = 0f
    private var bally: Float = 0f
    private var vx: Float = 0f
    private var vy: Float = 0f
    private var time: Long = 0L

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        surfaceWidth = width
        surfaceHeight = height
        ballx = (width / 2).toFloat()
        bally = (height / 2).toFloat()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE)
                as SensorManager
        sensorManager.unregisterListener(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE)
                as SensorManager
        val accSensor = sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(
                this, accSensor,
                SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (time == 0L) time = System.currentTimeMillis()
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = -event.values[0]
            val y = event.values[1]

            var t = (System.currentTimeMillis() - time).toFloat()
            time = System.currentTimeMillis()
            t /= 1000.0f

            val dx = vx * t + x * t * t / 2.0f
            val dy = vy * t + y * t * t / 2.0f
            ballx += dx * coef
            bally += dy * coef
            vx += x * t
            vy += y * t

            if (ballx - radius < 0 && vx < 0){
                vx = -vx / 1.5f
                ballx = radius
            } else if (ballx + radius > surfaceWidth && vx > 0) {
                vx = -vx / 1.5f
                ballx = surfaceWidth - radius
            }
            if (bally - radius < 0 && vy < 0) {
                vy = -vy / 1.5f
                bally = radius
            } else if (bally + radius > surfaceHeight && vy > 0) {
                vy = -vy / 1.5f
                bally = surfaceHeight - radius
            }

            drawCanvas()
        }
    }

    private fun drawCanvas() {
        val canvas = surfaceView.holder.lockCanvas()
        canvas.drawColor(Color.YELLOW)
        canvas.drawCircle(ballx,bally,radius, Paint().apply {
            color = Color.MAGENTA
        })
        surfaceView.holder.unlockCanvasAndPost(canvas)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)
        val holder = surfaceView.holder
        holder.addCallback(this)
    }
}
