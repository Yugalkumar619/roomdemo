package com.yugal.roomdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.yugal.roomdemo.databinding.ActivityMainBinding
import com.yugal.roomdemo.db.SubscriberDatabase
import com.yugal.roomdemo.db.SubscriberRepository
import kotlinx.coroutines.InternalCoroutinesApi

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var subscriberViewModel: SubscriberViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val dao = SubscriberDatabase.getInstance(application).subscriberDAO
        val repository = SubscriberRepository(dao)
        val factory = SubscriberViewModelFactory(repository)
        subscriberViewModel = ViewModelProvider(this, factory).get(SubscriberViewModel::class.java)
        binding.myViewModel = subscriberViewModel
        binding.lifecycleOwner = this
        initRecyclerView()
    }

    private fun initRecyclerView(){
        binding.subscriberRecyclerView.layoutManager = LinearLayoutManager(this)
        dispalaySubscribersList()
    }

    private fun dispalaySubscribersList(){
        subscriberViewModel.getSaveSubscribers().observe(this, Observer{
            binding.subscriberRecyclerView.adapter = MyRecyclerViewAdapter(it)
        })
    }
}