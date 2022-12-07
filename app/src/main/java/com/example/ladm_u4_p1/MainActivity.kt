package com.example.ladm_u4_p1

import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val siPermiso=1
    val siPermisoReciver=2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.RECEIVE_SMS),siPermisoReciver
            )
        }



        button.setOnClickListener{
            if(ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.SEND_SMS),siPermiso
                )
            }else{
                envioSMS()
            }
        }
        textview.setOnClickListener {
            try {
                var cursor=BaseDatos(this,"entrantes",null,1)
                    .readableDatabase
                    .rawQuery("SELECT * FROM ENTRANTES",null)
                var ultimo=""
                if(cursor.moveToFirst()){
                    do{
                        ultimo= "ultimo mensaje ${cursor.getString(0)} \n" +
                                "mensaje sms ${cursor.getString(1)}"
                    }while (cursor.moveToNext())
                }else{
                    ultimo="sin mensajes aun, tabla vacia"
                }
                textview.setText(ultimo)
            }catch (err: SQLiteException){
                Toast.makeText(this,err.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==siPermiso){
            envioSMS()
        }
        if(requestCode==siPermiso){
            mensajeRecibir()
        }
    }

    private fun mensajeRecibir() {
        AlertDialog.Builder(this).setMessage("se otorgo recibir").show()
    }

    private fun envioSMS() {
        SmsManager.getDefault().sendTextMessage(et_mensaje.text.toString(),null,
            et_mensaje.text.toString(),null,null)
        Toast.makeText(this,"se envio el sms", Toast.LENGTH_LONG).show()

    }
}