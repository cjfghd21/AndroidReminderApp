package com.example.a436proj

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.a436proj.databinding.ActivityGroupBinding
import com.google.firebase.auth.FirebaseAuth
import java.io.Serializable

class GroupActivity : AppCompatActivity() {

    var list = mutableListOf<ExpandableGroupModel>()
    private lateinit var groupRV : GroupRecyclerViewAdapter
    private lateinit var viewModel : GroupViewModel
    private lateinit var pref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityGroupBinding.inflate(layoutInflater)

        supportActionBar!!.title = "Groups"

        setContentView(binding.root)

        /*for (i in 1..4) {
            list.add(ExpandableGroupModel(ExpandableGroupModel.PARENT, SelectableGroups.Group(i.toString(),
                mutableListOf(
                    SelectableGroups.Group.Contact("Marcus Brooks",  "Tell Mom Happy Birthday.\nHomework is due on Thursday\nSet an alarm for tonight.", "(123)456-7890", false),
                    SelectableGroups.Group.Contact("Anthony Kim",  "TEST REMINDER TEXT", "(123)456-7890",false),
                    SelectableGroups.Group.Contact("Yun Chang",  "Tell Mom Happy Birthday.\nHomework is due on Thursday\nSet an alarm for tonight.", "(123)456-7890",false),
                    SelectableGroups.Group.Contact("Cheolhong Ahn",  "Tell Mom Happy Birthday.\nHomework is due on Thursday\nSet an alarm for tonight.", "(123)456-7890",false)
                ))))
        }*/

        viewModel = ViewModelProvider(this)[GroupViewModel::class.java]

        if (!viewModel.groupsInitialzed.value!!) {
            viewModel.groups.value = list
            viewModel.groupsInitialzed.value = true
        }

        groupRV = GroupRecyclerViewAdapter(this, list, this).also {
            binding.list.adapter = it
            binding.list.setHasFixedSize(true)
        }

        groupRV.settingsClickListener = GroupRecyclerViewAdapter.OnSettingsClickListener { model, position ->
            var groupSettingsIntent = Intent(this, GroupSettingsActivity::class.java)
            groupSettingsIntent.putExtra("groupName", model.groupParent.groupName)
            groupSettingsIntent.putExtra("contactsList",  (model.groupParent.contacts as Serializable))
            groupSettingsIntent.putExtra("groupIndex", position)
            startActivityForResult(groupSettingsIntent, 0)
        }

        viewModel.groups.observe(this) {
            groupRV.updateGroupModelList(viewModel.groups.value!!)
        }
    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //For Chris: This if block is what gets run if the GroupSettingsActivity returns normally using the back button
                //We update the viewModel's groups list at the groupIndex that we get from the GroupSettingsActivity with the
                //new value of the contacts that we got from the GroupSettingsActivity. Then we update the RecyclerView
                viewModel.groups.value!![data?.extras?.get("groupIndex") as Int].groupParent.contacts = data?.extras?.get("resultContactsList") as MutableList<SelectableGroups.Group.Contact>
                groupRV.updateGroupModelList(viewModel.groups.value!!)
            }

            if (resultCode == 1) {
                //For Chris: This block of code is what gets run if the group has been deleted.
                //Here we update the viewModel by removing the group in the groups list at the index received from
                //the GroupSettingsActivity. Then we update the RecyclerView
                var index = data?.extras?.get("groupIndex") as Int
                viewModel.groups.value!!.removeAt(index)
                groupRV.updateGroupModelList(viewModel.groups.value!!, true, index)

                //For Chris: Connect the back end here. Update firebase with the new value of viewModel.groups.value!!
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id : Int = item.itemId

        if (id == R.id.add) {
            var builder = AlertDialog.Builder(this)
            builder.setTitle("Add Group")

            var inputEditText = EditText(this)
            inputEditText.inputType = InputType.TYPE_CLASS_TEXT

            builder.setView(inputEditText)

            builder.setPositiveButton("Add Group") { dialog, which ->
                if(inputEditText.text.toString() == "") {
                    Toast.makeText(
                        this,
                        "Group name can't be empty.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    //For Chris: Here we create a new group within the add() call and update the viewModel's list
                    //of groups. Then we update the RecyclerView using groupRV.updateGroupModelList() with the
                    //new value of the viewModel.groups
                    viewModel.groups.value!!.add(ExpandableGroupModel(ExpandableGroupModel.PARENT,
                        SelectableGroups.Group(inputEditText.text.toString(),
                            mutableListOf<SelectableGroups.Group.Contact>())))
                    groupRV.updateGroupModelList(viewModel.groups.value!!)

                    //For Chris: Connect the back end here. Update firebase with the new value of viewModel.groups.value!!
                }
            }

            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }

            builder.show()
        }

        if (id == R.id.sign_out) {
            var builder = AlertDialog.Builder(this)
            builder.setTitle("Sign Out?")

            builder.setPositiveButton("Sign Out") { dialog, which ->
                pref = getSharedPreferences("Credentials", Context.MODE_PRIVATE) //shared ref
                with(pref.edit()){
                    putString("email", "")
                    putString("password", "")
                    apply()
                }
                FirebaseAuth.getInstance().signOut(); //unauthorize current user out from firebase
                finishAffinity()
                startActivity(Intent(this, AccessActivity::class.java))
            }

            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }

            builder.show()
        }


        return true
    }
    override fun onBackPressed() {
    }
}