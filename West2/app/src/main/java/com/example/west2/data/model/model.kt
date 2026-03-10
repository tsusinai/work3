package com.example.west2.data.model


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


//这是日记主表
@Entity(tableName = "journals")
data class Journal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,                      //日记标题
    val content: String,                    //日记内容
    val createTime: String ,     //创建日期
    val updateTime: String ,      //日记更新日期
    val city:String
)

enum class ImageType { LOCAL, NETWORK } //定义本地图片LOCAL和NETWORK网络图片

//图片表
@Entity(
    tableName = "journal_images",
    foreignKeys = [ForeignKey(
        entity = Journal::class, // 关联的父表实体类（日记表）
        parentColumns = ["id"], // 父表（journals）中关联的列（日记ID）
        childColumns = ["journalId"], // 子表（journal_images）中关联的列（关联的日记ID）
        onDelete = ForeignKey.CASCADE // 日记删除时，关联图片也删除
    )]
)
data class JournalImage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val journalId: Int, // 关联的日记ID
    val imageType: ImageType, // 图片类型
    val imagePathOrUrl: String // 本地路径或网络URL
) 

data class City(
    val name: String,
    val id: String,
)


data class Weather(
    val fxDate: String,
    val textDay:String,
    val textNight: String,
    val tempMax:Int,
    val tempMin:Int,
)

data class HeFenWeather(
    val code: String,
    val daily: List<Weather>,
)

data class HeFenWeatherCity(
    val code: String,
    val location:List<City>,
)

