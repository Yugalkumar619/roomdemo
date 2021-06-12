package com.yugal.roomdemo

import android.util.Patterns
import androidx.lifecycle.*
import com.yugal.roomdemo.db.Subscriber
import com.yugal.roomdemo.db.SubscriberRepository
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SubscriberViewModel(private  val repository: SubscriberRepository) : ViewModel() {

    val subscribers = repository.subscribers

    private var isUpdateOrDelete = false
    private lateinit var subscriberToUpdateOrDelete : Subscriber

    val inputName = MutableLiveData<String?>()

    val inputEmail = MutableLiveData<String?>()

    val saveOrUpdateButtonText = MutableLiveData<String>()

    val clearAllOrDeleteButtonText = MutableLiveData<String>()

    val message : LiveData<Event<String>>
    get() = statusMessage

    private val statusMessage = MutableLiveData<Event<String>>()

    init {
        saveOrUpdateButtonText.value = "Save"
        clearAllOrDeleteButtonText.value = "Clear All"
    }

    fun saveOrUpdate(){
        if(inputName.value == null){
            statusMessage.value = Event("Please enter subscriber's name")
        }else if(inputEmail.value == null){
            statusMessage.value = com.yugal.roomdemo.Event("Please enter subscriber's email")
        }else if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()){
            statusMessage.value = Event("Please enter a correct email address")
        }else {

            if (isUpdateOrDelete) {
                subscriberToUpdateOrDelete.name = inputName.value!!
                subscriberToUpdateOrDelete.email = inputEmail.value!!
                update(subscriberToUpdateOrDelete)
            } else {
                val name: String = inputName.value!!
                val email: String = inputEmail.value!!
                insert(Subscriber(0, name, email))
                inputName.value = null
                inputEmail.value = null
            }
        }
    }

    fun clearAllOrDelete(){
        clearAll()
    }
    fun initUpdateAndDelete(subscriber: Subscriber){
        inputName.value = subscriber.name
        inputEmail.value = subscriber.email
        isUpdateOrDelete = true
        subscriberToUpdateOrDelete = subscriber
        saveOrUpdateButtonText.value = "Update"
        clearAllOrDeleteButtonText.value = "Delete"
    }

    fun insert(subscriber: Subscriber) : Job = viewModelScope.launch {
        val newRowId: Long = repository.insert(subscriber)
        if(newRowId>-1) {
            statusMessage.value = Event("Subscriber Inserted Successfully $newRowId")
        }else{
            statusMessage.value = Event("Error Occurred")
        }
        }

    fun update(subscriber: Subscriber) : Job = viewModelScope.launch {
        val noOfRows: Int = repository.update(subscriber)
        if(noOfRows>0) {
            inputName.value = null
            inputEmail.value = null
            isUpdateOrDelete = false
            saveOrUpdateButtonText.value = "Save"
            clearAllOrDeleteButtonText.value = "Clear All"
            statusMessage.value = Event("$noOfRows Row Updated Successfully")
        }else{
            statusMessage.value = Event("Error Occurred")
        }
    }
    fun delete(subscriber: Subscriber) : Job = viewModelScope.launch {
        val noOfRowsDeleted = repository.delete(subscriber)

        if(noOfRowsDeleted>0) {
            inputName.value = null
            inputEmail.value = null
            isUpdateOrDelete = false
            saveOrUpdateButtonText.value = "Save"
            clearAllOrDeleteButtonText.value = "Clear All"
            statusMessage.value = Event("$noOfRowsDeleted Deleted Successfully")
        }else{
            statusMessage.value = Event("Error Occurred")
        }
    }

    fun clearAll(): Job = viewModelScope.launch {
        if(isUpdateOrDelete){
            delete(subscriberToUpdateOrDelete)
        }else{
            val noOfRowsDeleted =repository.deleteAll()
            if(noOfRowsDeleted>0) {
                statusMessage.value = Event("$noOfRowsDeleted Subscriber Deleted Successfully")
            }else{
                statusMessage.value = Event("Error Occurred")
            }
        }
    }


    fun getSaveSubscribers() = liveData{
       repository.subscribers.collect {
           emit(it)
       }
    }

}