package com.yugal.roomdemo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
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

    init {
        saveOrUpdateButtonText.value = "Save"
        clearAllOrDeleteButtonText.value = "Clear All"
    }

    fun saveOrUpdate(){
        if(isUpdateOrDelete){
            subscriberToUpdateOrDelete.name = inputName.value!!
            subscriberToUpdateOrDelete.email = inputEmail.value!!
            update(subscriberToUpdateOrDelete)
        }else{
            val name : String = inputName.value!!
            val email: String = inputEmail.value!!
            insert(Subscriber(0,name,email))
            inputName.value = null
            inputEmail.value = null
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
            repository.insert(subscriber)
        }

    fun update(subscriber: Subscriber) : Job = viewModelScope.launch {
        repository.update(subscriber)
        inputName.value = null
        inputEmail.value = null
        isUpdateOrDelete = false
        saveOrUpdateButtonText.value = "Save"
        clearAllOrDeleteButtonText.value = "Clear All"
    }
    fun delete(subscriber: Subscriber) : Job = viewModelScope.launch {
        repository.delete(subscriber)
        inputName.value = null
        inputEmail.value = null
        isUpdateOrDelete = false
        saveOrUpdateButtonText.value = "Save"
        clearAllOrDeleteButtonText.value = "Clear All"
    }

    fun clearAll(): Job = viewModelScope.launch {
        if(isUpdateOrDelete){
            delete(subscriberToUpdateOrDelete)
        }else{
            repository.deleteAll()
        }
    }


    fun getSaveSubscribers() = liveData{
       repository.subscribers.collect {
           emit(it)
       }
    }

}