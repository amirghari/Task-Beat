package com.example.taskbeat.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "parliament_member_local",
    foreignKeys = [
        ForeignKey(
            entity = ParliamentMember::class,
            parentColumns = ["heteka_id"],
            childColumns = ["heteka_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["heteka_id"])]
)
data class ParliamentMemberLocal(
    @PrimaryKey
    @ColumnInfo(name = "heteka_id") val hetekaId: Int,
    @ColumnInfo(name = "favorite") val favorite: Boolean,
    @ColumnInfo(name = "note") val note: String?,
)