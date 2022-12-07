package com.example.ladm_u4_p1

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Build
import android.telephony.SmsMessage
import android.widget.Toast


class SmsReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context, p1: Intent) {
        val extras=p1.extras

        if(extras!=null){
            var sms=extras.get("pdus") as Array<Any>

            for(indice in sms.indices){
                val formato=extras.getString("format")

                var smsMensaje=if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    SmsMessage.createFromPdu(sms[indice] as ByteArray,formato)
                }else{
                    SmsMessage.createFromPdu(sms[indice] as ByteArray)
                }
                var celularOrigen=smsMensaje.originatingAddress
                var contenidoSMS=smsMensaje.messageBody.toString()

                //GUARDAR EN SQLITE
                try {
                    var bd=BaseDatos(p0,"entrantes",null,1)
                    var insertar=bd.writableDatabase
                    var SQL ="INSERT INTO ENTRANTES VALUES('${celularOrigen}','${contenidoSMS}')"
                    insertar.execSQL(SQL)
                    bd.close()
                }catch (err: SQLiteException){
                    Toast.makeText(p0,err.message, Toast.LENGTH_LONG).show()
                }

                Toast.makeText(p0,"entro al contenido ${contenidoSMS}", Toast.LENGTH_LONG).show()
            }
        }
    }
}