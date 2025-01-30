package com.example.autotoques

import android.view.accessibility.GestureDescription // Importa la clase correcta  // en rojo
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.graphics.Path
import android.graphics.Rect   //  en gris
import android.os.Build        // en gris
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo   // en gris
import android.view.accessibility.AccessibilityWindowInfo   // en gris

class AutoClickService : AccessibilityService() {
    private var cantidad = 0
    private var intervalo = 0L
    private val handler = Handler(Looper.getMainLooper())
    private var contador = 0
    private var x = 0
    private var y = 0
    private var isRunning = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo()
        info.flags = AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE or AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK // Escucha todos los eventos
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.notificationTimeout = 100
        this.setServiceInfo(info)

    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // No necesitamos procesar eventos de accesibilidad directamente aquí en este ejemplo
        //pero se podria utilizar para parar el servicio si se abre una app especifica.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        cantidad = intent?.getIntExtra("cantidad", 0) ?: 0
        intervalo = intent?.getLongExtra("intervalo", 0L) ?: 0L
        x = intent?.getIntExtra("x", 0) ?: 0
        y = intent?.getIntExtra("y", 0) ?: 0

        if(!isRunning){
            isRunning = true
            iniciarAutoClick()
        }

        return START_STICKY // Reiniciar el servicio si se detiene inesperadamente
    }

    private fun iniciarAutoClick() {
        if(cantidad > 0 && intervalo > 0){
            handler.postDelayed(object : Runnable {
                override fun run() {
                    realizarClick(x, y)
                    contador++
                    if (contador < cantidad) {
                        handler.postDelayed(this, intervalo)
                    } else{
                        contador = 0
                        isRunning = false
                        stopSelf()
                    }
                }
            }, intervalo)
        } else {
            stopSelf()
        }
    }

    private fun realizarClick(x: Int, y: Int) {
        val path = Path()
        path.moveTo(x.toFloat(), y.toFloat())
        val builder = GestureDescription.Builder()
        val gestureDescription = builder.addStroke(GestureDescription.StrokeDescription(path, 0, 10)).build()
        //dispatchGesture(gestureDescription, object : GestureResultCallback() {
        dispatchGesture(gestureDescription, object : AccessibilityService.GestureResultCallback() {

            override fun onCompleted(gestureDescription: GestureDescription) {
                super.onCompleted(gestureDescription)
                Log.d("AutoClickService", "Click realizado en ($x, $y)")
            }

            override fun onCancelled(gestureDescription: GestureDescription) {
                super.onCancelled(gestureDescription)
                Log.e("AutoClickService", "Click cancelado")
            }
        }, null)

    }

    override fun onInterrupt() {
        // Se llama cuando el sistema interrumpe el servicio
        isRunning = false
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        isRunning = false
    }
}