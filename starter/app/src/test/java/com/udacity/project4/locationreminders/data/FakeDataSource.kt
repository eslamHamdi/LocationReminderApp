package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {


//    TODO: Create a fake data source to act as a double to the real data source

    var dataList = mutableListOf<ReminderDTO>()

    override suspend fun getReminders(): Result<List<ReminderDTO>> {

        return Result.Success(dataList)
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {

        dataList.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
       val item = dataList.find {
           it.id == id
        }
        if (id != null)
        {
            return Result.Success(item!!)
        }else
            return Result.Error("error")

    }

    override suspend fun deleteAllReminders() {

        dataList.clear()
    }


}