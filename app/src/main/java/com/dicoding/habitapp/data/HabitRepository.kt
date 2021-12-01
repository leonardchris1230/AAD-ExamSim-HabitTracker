package com.dicoding.habitapp.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.dicoding.habitapp.utils.HabitSortType
import com.dicoding.habitapp.utils.SortUtils
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HabitRepository(private val habitDao: HabitDao, private val executor: ExecutorService) {

    companion object {

        const val PAGE_SIZE = 30
        const val PLACEHOLDERS = true
        @Volatile
        private var instance: HabitRepository? = null

        fun getInstance(context: Context): HabitRepository {
            return instance ?: synchronized(this) {
                if (instance == null) {
                    val database = HabitDatabase.getInstance(context)
                    instance = HabitRepository(
                        database.habitDao(),
                        Executors.newSingleThreadExecutor()
                    )
                }
                return instance as HabitRepository
            }

        }
    }

    //TODO 4 : Use SortUtils.getSortedQuery to create sortable query and build paged list
    fun getHabits(filter: HabitSortType): LiveData<PagedList<Habit>> {
        val query = SortUtils.getSorteredQuery(filter)
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(PLACEHOLDERS)
            .setPageSize(PAGE_SIZE)
            .build()
        return LivePagedListBuilder(habitDao.getHabits(query), config).build()
    }

    //TODO 5 : Complete other function inside repository
    fun getHabitById(habitId: Int): LiveData<Habit> {return habitDao.getHabitById(habitId)}

    fun insertHabit(newHabit: Habit) {
        executor.execute{
            habitDao.insertHabit(newHabit)
        }
    }

    fun deleteHabit(habit: Habit) { executor.execute { habitDao.deleteHabit(habit)} }

    fun getRandomHabitByPriorityLevel(level: String): LiveData<Habit> { return habitDao.getRandomHabitByPriorityLevel(level)}
}