package com.example.a436proj.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.a436proj.ContactDto
import com.example.a436proj.MainActivity
import com.example.a436proj.R
import com.example.a436proj.databinding.FragmentHomeBinding
import com.google.android.gms.auth.api.signin.GoogleSignInClient

class HomeFragment : Fragment() {

    companion object {
        private const val PERMISSION = Manifest.permission.READ_CONTACTS
        private const val CONTENT_ITEM_TYPE = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_TYPE
    }


    private lateinit var oneTapClient: GoogleSignInClient
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun startContactsActivity() {
        Log.d("CREATION", "startContactsActivity being called")
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MainActivity.CONTENT_ITEM_TYPE
        intent.addFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION)

        // resolveActivity allow framework to determine
        // app best suited to handle intent
        // ask to choose btw candidate if needed
        activity?.packageManager?.let {
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
            Toast.makeText(context, "None selected", Toast.LENGTH_SHORT).show()
        }

    @SuppressLint("Range")
    private fun startOurContactProcessing() {
        Log.d("CREATION", "startOutContactProcessing being called")
        val contactList : MutableList<ContactDto> = ArrayList()
        val contacts = activity?.contentResolver?.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null)
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
            }
            var clr = _binding?.contactList
                //findViewById<RecyclerView>(R.id.contact_list)
            Log.d("CREATION", contactList.toString())
            clr?.adapter = context?.let { ContactAdapter(contactList, it) }
            contacts.close()
        }
    }

    private val requestPermissionLauncher: ActivityResultLauncher<String> =

        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                isGranted ->
            if (isGranted) {
                startContactsActivity()
            } else {
                Toast.makeText(
                    context,
                    "cannot process without permission",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    private fun onReadButtonClick() {
        Log.d("CREATION", "OnReadButtonClick enter")
//        startContactsActivity()
        requestPermissionLauncher.launch(PERMISSION)
//        startContactsActivity()
        when {
            context?.let { checkSelfPermission(it, PERMISSION) } == PackageManager.PERMISSION_GRANTED -> {
                Log.d("CREATION", "OnReadButtonClickFirst")
                startContactsActivity()
            }
            shouldShowRequestPermissionRationale(PERMISSION) -> {
                Log.d("CREATION", "OnReadButtonClickSecond")
                requestPermissionLauncher.launch(PERMISSION)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //temporary button for testing sign out.

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        val recyclerView: RecyclerView = binding.contactList

        _binding!!.contactList.layoutManager = LinearLayoutManager(context)
        _binding!!.btnReadContact.setOnClickListener{
            onReadButtonClick()
        }
        Log.d("CREATION", "onCreateView is being called")



        return root
    }



    ////////////////////////



    class ContactAdapter(items : List<ContactDto>, ctx: Context) : RecyclerView.Adapter<ContactAdapter.ViewHolder>(){

        private var list = items
        private var context = ctx

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ContactAdapter.ViewHolder, position: Int) {
            holder.name.text = list[position].name
            holder.number.text = list[position].number

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactAdapter.ViewHolder {
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.contact_child,parent,false))
        }


        class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
            val name = this.itemView.findViewById<TextView>(R.id.tv_name)
            val number = v.findViewById<TextView>(R.id.tv_number)

        }
    }


    ///////////


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}