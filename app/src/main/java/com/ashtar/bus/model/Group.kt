package com.ashtar.bus.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class Group(
    val id: Int,
    val name: String
)

@Entity(tableName = "group")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo("name")
    val name: String
)

fun Group.toEntity(): GroupEntity =
    GroupEntity(
        id = id,
        name = name
    )

fun List<GroupEntity>.toGroupList(): List<Group> = this.map {
    Group(
        id = it.id,
        name = it.name
    )
}