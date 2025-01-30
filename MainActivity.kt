package com.example.autotoques

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var x: Int = 0
    private var y: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Solicitar permiso SYSTEM_ALERT_WINDOW (si es necesario)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, 1234)
        }
        // Solicitar permiso de accesibilidad
        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))

        val btnObtenerCoordenadas = findViewById<Button>(R.id.btnObtenerCoordenadas)
        btnObtenerCoordenadas.setOnClickListener {
            startService(Intent(this, OverlayView::class.java))
            finish()
        }

        val btnIniciar = findViewById<Button>(R.id.btnIniciar)
        btnIniciar.setOnClickListener {
            val cantidad = findViewById<EditText>(R.id.etCantidad).text.toString().toIntOrNull() ?: 0
            val intervalo = findViewById<EditText>(R.id.etIntervalo).text.toString().toLongOrNull() ?: 0L

            if(cantidad <= 0 || intervalo <= 0){
                Toast.makeText(this,"Introduce valores mayores a 0", Toast.LENGTH_SHORT).show()
            } else{
                val serviceIntent = Intent(this, AutoClickService::class.java)
                serviceIntent.putExtra("cantidad", cantidad)
                serviceIntent.putExtra("intervalo", intervalo)
                serviceIntent.putExtra("x", x)
                serviceIntent.putExtra("y", y)
                startService(serviceIntent)
            }
        }

        val btnCerrar = findViewById<Button>(R.id.btnCerrar)
        btnCerrar.setOnClickListener {
            stopService(Intent(this, AutoClickService::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        x = intent.getIntExtra("x",0)
        y = intent.getIntExtra("y",0)
    }
}