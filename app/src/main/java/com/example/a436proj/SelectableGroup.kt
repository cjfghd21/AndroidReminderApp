package com.example.a436proj

import java.io.Serializable

data class SelectableGroups (var groups : List<Group>) : Serializable {
    data class Group (var groupName : String,
                      var contacts : List<Contact>) : Serializable {
        data class Contact (var name : String,
                            var timeSinceLastCall : String,
                            var timeSinceLastText : String,
                            var reminderText : String,
                            var groupSettingsIsChecked : Boolean = false) : Serializable
    }
}