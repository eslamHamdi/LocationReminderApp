package com.udacity.project4.locationreminders.reminderslist

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSourceAndroid : ReminderDataSource {


//    TODO: Create a fake data source to act as a double to the real data source



    val item1 =ReminderDTO("1","mock","here",5.0,5.0,"a")
    val item2 =ReminderDTO("1","mock","here",6.0,5.0,"b")
    val item3 =ReminderDTO("1","mock","here",7.0,5.0,"c")

    var dataList = mutableListOf<ReminderDTO>(item1,item2,item3)




    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {

        return if (shouldReturnError)
        {
            Result.Error("error")
        }

        else{
            Result.Success(dataList)
        }



    }

    override suspend fun saveReminder(reminder: ReminderDTO) {

        dataList.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
       val item = dataList.find {
           it.id == id
        }
        if (shouldReturnError)
        {
            return Result.Error("error")

        }
        item?.let {
            return Result.Success(item)
        }

        return Result.Error("Could not find reminder")


    }

    override suspend fun deleteAllReminders() {

        dataList.clear()
    }

    fun DTOtoReminderDataItem(list:MutableList<ReminderDTO>):List<ReminderDataItem>
    {
        return list.map {
            //map the reminder data from the DB to the be ready to be displayed on the UI
            ReminderDataItem(
                    it.title,
                   it.description,
                   it.location,
                    it.latitude,
                   it.longitude,
                    it.id
            )
        }
    }


}