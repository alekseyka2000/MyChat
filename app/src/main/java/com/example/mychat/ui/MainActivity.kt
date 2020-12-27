package com.example.mychat.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mychat.R
import com.example.mychat.di.repositoryModule
import com.example.mychat.model.entity.Message
import com.example.mychat.model.entity.MessageForDB
import com.example.mychat.model.entity.PushMessage
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.context.startKoin

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()
    var messageList = mutableListOf<Pair<Int, PushMessage>>(Pair(1, PushMessage(Message("1", "2"), "3")))
    private val messageAdapter = MessageListAdapter()

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initKoin()

        viewModel.getToken()

        findViewById<RecyclerView>(R.id.recycler).apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, true)
            adapter = messageAdapter
        }

        findViewById<ImageView>(R.id.sender).setOnClickListener {
            val message = findViewById<EditText>(R.id.editText).text
            viewModel.sendMessage(message.toString())
            message.clear()
        }

        viewModel.liveData1.observe(this, { list ->
            val newMessageList = mutableListOf<Pair<Int, PushMessage>>(Pair(1, PushMessage(Message("1", list), "3")))
            messageAdapter.notifyChanges(messageList, newMessageList)
            messageList = newMessageList
        })
    }

    inner class MessageListAdapter : RecyclerView.Adapter<MessageListAdapter.BaseMessageHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseMessageHolder {
            val inflater = LayoutInflater.from(parent.context)
            return if (viewType == 1) YourMessageHolder(inflater, parent)
            else AnotherUserMessageHolder(inflater, parent)
        }

        override fun onBindViewHolder(holder: BaseMessageHolder, position: Int) {
            holder.bind(messageList[position].second)
        }

        override fun getItemViewType(position: Int) = messageList[position].first

        override fun getItemCount() = messageList.size

        fun notifyChanges(
            oldList: List<Pair<Int, PushMessage>>,
            newList: List<Pair<Int, PushMessage>>
        ) {
            val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return oldList[oldItemPosition].second == newList[newItemPosition].second
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    return oldList[oldItemPosition] == newList[newItemPosition]
                }

                override fun getOldListSize() = oldList.size
                override fun getNewListSize() = newList.size
            })
            diff.dispatchUpdatesTo(this)
        }

        open inner class BaseMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            open fun bind(message: PushMessage) {}
        }

        inner class YourMessageHolder(inflater: LayoutInflater, parent: ViewGroup) :
            BaseMessageHolder(inflater.inflate(R.layout.your_message_layout, parent, false)) {

            private var messageTextView: TextView? = null

            init {
                messageTextView = itemView.findViewById(R.id.yourMessage)
            }

            override fun bind(message: PushMessage) {
                messageTextView?.text = message.data.message
            }
        }

        inner class AnotherUserMessageHolder(inflater: LayoutInflater, parent: ViewGroup) :
            BaseMessageHolder(
                inflater.inflate(
                    R.layout.another_user_message_layout,
                    parent,
                    false
                )
            ) {

            private var messageTextView: TextView? = null

            init {
                messageTextView = itemView.findViewById(R.id.messageAnotherUser)
            }

            override fun bind(message: PushMessage) {
                messageTextView?.text = message.data.message
            }
        }
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@MainActivity)
            modules(repositoryModule)
        }
    }
}