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

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var msjmanager = SmsManager.getDefault()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mostrarTodo()

        println("PERMISOS"+(PermissionChecker.checkSelfPermission(this,"android.permission.SEND_SMS")== PermissionChecker.PERMISSION_GRANTED))
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
            enviarSMS(binding.txtTelef,binding.txtMensaje)


        }
    }
    fun enviarSMS(telefono: EditText, mensaje: EditText){
        msjmanager.sendTextMessage(telefono.text.toString(),null,mensaje.text.toString(),null,null)
        insertarFB(telefono.text.toString(),mensaje.text.toString())
        limpiarCampos()
        Toast.makeText(this,"MENSAJE ENVIADO",Toast.LENGTH_LONG).show()
    }

    fun mostrarTodo(){
        FirebaseFirestore.getInstance().collection("smsenviados")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException!=null){
                    aler("NO SE REALIZO LA CONSULTA")
                    return@addSnapshotListener
                }
                var lista =ArrayList<String>()
                for (doc in querySnapshot!!){
                    var cad = doc.getString("Telefono:")+"\n"+doc.getString("Mensaje:")
                    lista.add(cad)
                }
                binding.listMsj.adapter=
                    ArrayAdapter<String>(this, R.layout.simple_list_item_1,lista)
            }
    }

    fun insertarFB(telefono:String,mensaje:String){
        var datos = hashMapOf(
            "telefono" to telefono,
            "mensaje" to mensaje
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