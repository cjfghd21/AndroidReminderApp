package com.example.a436proj

data class SelectableGroups (val groups : List<Group>) {
    data class Group (val groupName : String,
                      val contacts : List<Contact>) {
        data class Contact (val name : String,
                            val timeSinceLastCall : String,
                            val timeSinceLastText : String)
    }
}