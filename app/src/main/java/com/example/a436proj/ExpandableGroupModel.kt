package com.example.a436proj

class ExpandableGroupModel {
    companion object {
        const val PARENT = 1
        const val CHILD = 2
    }

    lateinit var groupParent : SelectableGroups.Group
    var type : Int
    lateinit var groupChild : SelectableGroups.Group.Contact
    var isExpanded : Boolean
    private var isCloseShown : Boolean

    constructor(type : Int, groupParent : SelectableGroups.Group,
                isExpanded : Boolean = false, isCloseShown : Boolean = false) {
        this.type = type
        this.groupParent = groupParent
        this.isExpanded = isExpanded
        this.isCloseShown = isCloseShown
    }

    constructor(type : Int, groupChild : SelectableGroups.Group.Contact,
                isExpanded : Boolean = false, isCloseShown : Boolean = false) {
        this.type = type
        this.groupChild = groupChild
        this.isExpanded = isExpanded
        this.isCloseShown = isCloseShown
    }
}