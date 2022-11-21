package com.example.a436proj

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.a436proj.databinding.ActivityGroupBinding
import java.io.Serializable

class GroupActivity : AppCompatActivity() {

    var list = mutableListOf<ExpandableGroupModel>()
    private lateinit var groupRV : GroupRecyclerViewAdapter
    private lateinit var viewModel : GroupViewModel

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

        groupRV = GroupRecyclerViewAdapter(this, list).also {
            binding.list.adapter = it
            binding.list.setHasFixedSize(true)
        }

        groupRV.settingsClickListener = GroupRecyclerViewAdapter.OnSettingsClickListener { model, position ->
            var groupSettingsIntent = Intent(this, GroupSettingsActivity::class.java)
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
                viewModel.groups.value!![data?.extras?.get("groupIndex") as Int].groupParent.contacts = data?.extras?.get("resultContactsList") as MutableList<SelectableGroups.Group.Contact>
                groupRV.updateGroupModelList(viewModel.groups.value!!)
            }

            if (resultCode == RESULT_CANCELED) {
                
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
                viewModel.groups.value!!.add(ExpandableGroupModel(ExpandableGroupModel.PARENT,
                    SelectableGroups.Group(inputEditText.text.toString(),
                        mutableListOf<SelectableGroups.Group.Contact>())))
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

            }

            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }

            builder.show()
        }


        return true
    }
}