package com.example.ladm_u4_p1

import android.R
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import com.example.ladm_u4_p1.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var listaIds= ArrayList<String>()
    var siPermiso=1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mostrar()

        binding.botonEnviar.setOnClickListener {
            if(binding.txtTelef.text.isEmpty()){
                AlertDialog.Builder(this).setTitle("ERROR")
                    .setMessage("Debes ingresar un número de teléfono")
                    .setNeutralButton("OK"){d,i->}
                    .show()
                return@setOnClickListener
            }
            if(binding.txtMensaje.text.isEmpty()){
                AlertDialog.Builder(this).setTitle("ERROR")
                    .setMessage("Debes ingresar un mensaje")
                    .setNeutralButton("OK"){d,i->}
                    .show()
                return@setOnClickListener
            }
            if(ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.SEND_SMS),siPermiso
                )
            }else{
                envioSMS()
            }


        }
    }


    private fun mostrar() {
        FirebaseFirestore.getInstance()
            .collection("smsenviados")
            .addSnapshotListener { value, error ->
                if(error!=null){
                    aler("NO SE PUDO REALIZAR LA CONSULTA")
                    return@addSnapshotListener
                }
                var lista= ArrayList<String>() //lista se comporta como un curso
                listaIds.clear()

                for(documento in value!!){
                    val cadena= documento.get("telefono").toString()+"\n"+
                            documento.getString("mensaje")+"\n"+
                            documento.getDate("registrado")
                    lista.add(cadena)
                    listaIds.add(documento.id)
                }
                binding.listMsj.adapter=ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,lista)
            }
    }
    private fun envioSMS() {
        SmsManager.getDefault().sendTextMessage(binding.txtTelef.text.toString(),null,
            binding.txtMensaje.text.toString(),null,null)
        insertarFB(binding.txtTelef.text.toString(),binding.txtMensaje.text.toString())
        Toast.makeText(this,"se envio el sms",Toast.LENGTH_LONG).show()
        limpiarCampos()

    }
    fun insertarFB(telefono:String,mensaje:String){
        var datos = hashMapOf(
            "telefono" to telefono,
            "mensaje" to mensaje,
            "registrado" to Date()

        )
        FirebaseFirestore.getInstance().collection("smsenviados")
            .add(datos)
            .addOnSuccessListener {
                println("SE INSERTO CORRECTAMENTE")
            }
            .addOnFailureListener {
                aler(it.message!!)
            }
    }

    fun limpiarCampos(){
        binding.txtTelef.setText("")
        binding.txtMensaje.setText("")
    }

    fun aler(m:String){
        AlertDialog.Builder(this)
            .setTitle("Atención")
            .setMessage(m)
            .setPositiveButton("OK"){d,i->}
            .show()
    }
}