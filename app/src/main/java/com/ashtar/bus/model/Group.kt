package com.ashtar.bus.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class Group(
    val id: Int,
    val sort: Int,
    val name: String
)

@Entity(tableName = "group")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo("sort")
    val sort: Int,
    @ColumnInfo("name")
    val name: String
)

fun Group.toEntity(): GroupEntity =
    GroupEntity(
        id = id,
        sort = sort,
        name = name
    )

fun List<GroupEntity>.toGroupList(): List<Group> = this.map {
    Group(
        id = it.id,
        sort = it.sort,
        name = it.name
    )
}