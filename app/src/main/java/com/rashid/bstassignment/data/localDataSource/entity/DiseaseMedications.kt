package com.rashid.bstassignment.data.localDataSource.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName

@Entity(tableName = "disease_medications")
data class DiseaseMedications(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @field:TypeConverters(Converters::class)
    val problems: List<Problem>,
)

data class Problem(
    @field:TypeConverters(Converters::class)
    val Diabetes: List<Diabete>,
    @field:TypeConverters(Converters::class)
    val Asthma: List<Map<String, Any>>,
)

data class Diabete(
    @field:TypeConverters(Converters::class)
    val medications: List<Medication>,
    @field:TypeConverters(Converters::class)
    val labs: List<Lab>,
)

data class Medication(
    @field:TypeConverters(Converters::class)
    val medicationsClasses: List<MedicationsClass>,
)

data class MedicationsClass(
    @field:TypeConverters(Converters::class)
    val className: List<ClassName>,
    @field:TypeConverters(Converters::class)
    val className2: List<ClassName2>,
)

data class ClassName(
    @field:TypeConverters(Converters::class)
    val associatedDrug: List<AssociatedDrug>,
    @SerializedName("associatedDrug#2")
    @field:TypeConverters(Converters::class)
    val associatedDrug2: List<AssociatedDrug2>,
)

data class AssociatedDrug(
    val name: String,
    val dose: String,
    val strength: String,
)

data class AssociatedDrug2(
    val name: String,
    val dose: String,
    val strength: String,
)

data class ClassName2(
    @field:TypeConverters(Converters::class)
    val associatedDrug: List<AssociatedDrug3>,
    @SerializedName("associatedDrug#2")
    @field:TypeConverters(Converters::class)
    val associatedDrug2: List<AssociatedDrug22>,
)

data class AssociatedDrug3(
    val name: String,
    val dose: String,
    val strength: String,
)

data class AssociatedDrug22(
    val name: String,
    val dose: String,
    val strength: String,
)

data class Lab(
    @SerializedName("missing_field")
    val missingField: String,
)
