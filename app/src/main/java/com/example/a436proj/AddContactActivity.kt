package com.example.a436proj

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.a436proj.databinding.ActivityAddContactBinding
import com.example.a436proj.ui.home.HomeViewModel
import com.example.a436proj.SelectableGroups
import java.io.Serializable

class AddContactActivity : AppCompatActivity() {
    companion object {
        private const val PERMISSION = Manifest.permission.READ_CONTACTS
        private const val CONTENT_ITEM_TYPE = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_TYPE
    }

    private var contactList : MutableList<ContactDto> = ArrayList() //deprecated
//    lateinit

    private var contactLists : MutableList<SelectableGroups.Group.Contact> = ArrayList()
    private lateinit var binding : ActivityAddContactBinding
    private lateinit var contactAdapter : ContactAdapter
    var ourIntent = Intent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = ActivityAddContactBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        val root: View = binding.root
        //temporary button for testing sign out.

        /*val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/

        val recyclerView: RecyclerView = binding.contactList

        binding.contactList.layoutManager = LinearLayoutManager(applicationContext)

        requestPermissionLauncher.launch(PERMISSION)
        when {
            applicationContext.let { ContextCompat.checkSelfPermission(it, PERMISSION) } == PackageManager.PERMISSION_GRANTED -> {
                //startContactsActivity() //Duplicates contacts after moving to activity
            }
            shouldShowRequestPermissionRationale(PERMISSION) -> {
                requestPermissionLauncher.launch(PERMISSION)
            }
        }

        /*binding.btnReadContact.setOnClickListener{
            onReadButtonClick()
        }*/

        binding.backArrow.setOnClickListener {
            // get the checkedList and filter our result
            val returnedCheckedList = contactAdapter.getCheckedList()
            for (item in contactLists) {
                if (returnedCheckedList.contains(item.name)) {
                    contactLists.remove(item) // filtering list in a way that we only send what is selected.
                }
            }
            // make sure we are returning the list of checked items
            ourIntent.putExtra("OurData", contactLists as Serializable ) // pass our contactList.
            setResult(123, ourIntent)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.actionSearch)
        val searchView = searchItem.actionView

        (searchView as SearchView).setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filter(newText)
                return false
            }
        })
        return true
    }

    private fun filter(text: String) {
        // creating a new array list to filter our data.
        val filteredlist = ArrayList<ContactDto>()

        // running a for loop to compare elements.
        for (item in contactList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.name.lowercase().contains(text.lowercase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            contactAdapter.filterList(filteredlist)
        }
    }


    /*private fun onReadButtonClick() {
        Log.d("CREATION", "OnReadButtonClick enter")
        requestPermissionLauncher.launch(PERMISSION)
        when {
            applicationContext.let { ContextCompat.checkSelfPermission(it, PERMISSION) } == PackageManager.PERMISSION_GRANTED -> {
                Log.d("CREATION", "OnReadButtonClickFirst")
                startContactsActivity()
            }
            shouldShowRequestPermissionRationale(PERMISSION) -> {
                Log.d("CREATION", "OnReadButtonClickSecond")
                requestPermissionLauncher.launch(PERMISSION)
            }
        }
    }*/

    private val requestPermissionLauncher: ActivityResultLauncher<String> =

        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                isGranted ->
            if (isGranted) {
                startContactsActivity()
            } else {
                Toast.makeText(
                    applicationContext,
                    "cannot process without permission",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun startContactsActivity() {
        Log.d("CREATION", "startContactsActivity being called")
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MainActivity.CONTENT_ITEM_TYPE
        intent.addFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION)

        // resolveActivity allow framework to determine
        // app best suited to handle intent
        // ask to choose btw candidate if needed
        packageManager?.let {
            intent.resolveActivity(it)?.let {
                contactsLauncher.launch(intent)
            }
        }
        startOurContactProcessing() //////
    }

    private val contactsLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                activityResult ->
            startOurContactProcessing() ?:
            Toast.makeText(this, "None selected", Toast.LENGTH_SHORT).show()
        }

    @SuppressLint("Range")
    private fun startOurContactProcessing() {
        Log.d("CREATION", "startOutContactProcessing being called")
        val contacts = contentResolver?.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null)
        if (contacts != null) {
            Log.d("CREATION", "Do we have non null contacts?")
            while (contacts.moveToNext()) {
                Log.d("CREATION", "How many contacts do we have")
                val name = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val obj = ContactDto()
                obj.name = name
                obj.number = number

                Log.d("CREATION", obj.toString())
                contactList.add(obj)
                // add person to so eventually we can pass through intent
                val person = SelectableGroups.Group.Contact(name, "none", "none", "none", false)
                contactLists.add(person)
            }

//            ourIntent.putExtra("OurData", contactLists as Serializable ) // pass our contactList.
//            setResult(123, ourIntent)


            var clr = binding.contactList
            //findViewById<RecyclerView>(R.id.contact_list)
            Log.d("CREATION", contactList.toString())

            contactAdapter = ContactAdapter(contactList, applicationContext)
            clr.adapter = contactAdapter
            //clr.adapter = applicationContext.let { ContactAdapter(contactList, it) }
            contacts.close()
//            finish() // can we close heere?
        }
    }
}