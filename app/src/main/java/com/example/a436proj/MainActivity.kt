package com.example.a436proj

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.a436proj.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION = Manifest.permission.READ_CONTACTS
        private const val CONTENT_ITEM_TYPE = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_TYPE
    }



    // registering permission launcher callback,
    // asks grant permission and then call passed callback with grant status
    // launched using ActivityResultLauncher.launch method
    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted ->
                if (isGranted) {
                    startContactsActivity()
                } else {
                    Toast.makeText(
                        this,
                        "cannot process without permission",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

    private val contactsLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            activityResult ->
            startOurContactProcessing() ?:
            Toast.makeText(this, "None selected", Toast.LENGTH_SHORT).show()
        }

    @SuppressLint("Range")
    private fun startOurContactProcessing() {
        val contactList : MutableList<ContactDto> = ArrayList()
        val contacts = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null)
        if (contacts != null) {
            while (contacts.moveToNext()) {
                val name = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val obj = ContactDto()
                obj.name = name
                obj.number = number


                contactList.add(obj)
            }
            var clr = findViewById<RecyclerView>(R.id.contact_list)
            clr.adapter = ContactAdapter(contactList, this)
            contacts.close()
        }
    }

    private fun startContactsActivity() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = CONTENT_ITEM_TYPE
        intent.addFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION)

        // resolveActivity allow framework to determine
        // app best suited to handle intent
        // ask to choose btw candidate if needed
        intent.resolveActivity(packageManager)?.let {
            contactsLauncher.launch(intent)
        }
    }

    private lateinit var binding: ActivityMainBinding

    var cols = listOf<String>(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
        ContactsContract.CommonDataKinds.Phone._ID,
    ).toTypedArray()

    private fun onReadButtonClick() {
        when {
            checkSelfPermission(PERMISSION) == PackageManager.PERMISSION_GRANTED -> {
                startContactsActivity()
            }
            shouldShowRequestPermissionRationale(PERMISSION) -> {
                requestPermissionLauncher.launch(PERMISSION)
            }
        }
    }


    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) //        setContentView(R.layout.activity_main)

        var clr = findViewById<RecyclerView>(R.id.contact_list)
        clr.layoutManager = LinearLayoutManager(this)




        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // call read contacts
        val btn_read_real = findViewById<Button>(R.id.btn_read_contact)
        btn_read_real.setOnClickListener {
            //// new
            onReadButtonClick()
            //// below is old.. leave for temp
            // verify permission
//            val contactList : MutableList<ContactDto> = ArrayList()
//            val contacts = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null)
//            if (contacts != null) {
//                while (contacts.moveToNext()) {
//                    val name = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
//                    val number = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
//                    val obj = ContactDto()
//                    obj.name = name
//                    obj.number = number
//
//
//                    contactList.add(obj)
//                }
//                var clr = findViewById<RecyclerView>(R.id.contact_list)
//                clr.adapter = ContactAdapter(contactList, this)
//                contacts.close()
//            }
        }
    }


    class ContactAdapter(items : List<ContactDto>, ctx: Context) : RecyclerView.Adapter<ContactAdapter.ViewHolder>(){

        private var list = items
        private var context = ctx

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ContactAdapter.ViewHolder, position: Int) {
            holder.name.text = list[position].name
            holder.number.text = list[position].name

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactAdapter.ViewHolder {
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.contact_child,parent,false))
        }


        class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
            val name = this.itemView.findViewById<TextView>(R.id.tv_name)
            val number = v.findViewById<TextView>(R.id.tv_number)

        }
    }
}
