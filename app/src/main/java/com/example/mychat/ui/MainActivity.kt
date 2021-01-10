package com.example.mychat.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mychat.R
import com.example.mychat.model.entity.Message
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()
    var messageList = listOf<Pair<Int, Message>>()
    private val messageAdapter = MessageListAdapter()
    private lateinit var recipient: String
    private lateinit var recipientName: String
    private lateinit var yourNikeName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        yourNikeName = intent.getStringExtra(NAME_KEY) ?: ""
        recipient = intent.getStringExtra(CONTACT_KEY) ?: ""
        recipientName = intent.getStringExtra(CONTACT_NAME_KEY) ?: ""

        findViewById<Toolbar>(R.id.toolbar).subtitle = recipientName

        viewModel.sync()

        findViewById<RecyclerView>(R.id.recycler).apply {
            layoutManager = LinearLayoutManager(this.context)
            (layoutManager as LinearLayoutManager).stackFromEnd = true
            adapter = messageAdapter
        }

        findViewById<ImageView>(R.id.sender).setOnClickListener {
            val message = findViewById<EditText>(R.id.editText).text
            viewModel.sendMessage(message.toString(), yourNikeName, recipient)
            message.clear()
        }

        findViewById<ImageView>(R.id.deleteChatButton).setOnClickListener {
            viewModel.deleteChat()
        }

        viewModel.liveData.observe(this, { list ->
            messageAdapter.notifyChanges(messageList, list)
            messageList = list
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
            oldList: List<Pair<Int, Message>>,
            newList: List<Pair<Int, Message>>
        ) {
            val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    oldList[oldItemPosition].second == newList[newItemPosition].second

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    oldList[oldItemPosition] == newList[newItemPosition]

                override fun getOldListSize() = oldList.size
                override fun getNewListSize() = newList.size
            })
            diff.dispatchUpdatesTo(this)
        }

        open inner class BaseMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            open fun bind(message: Message) {}
        }

        inner class YourMessageHolder(inflater: LayoutInflater, parent: ViewGroup) :
            BaseMessageHolder(inflater.inflate(R.layout.your_message_layout, parent, false)) {

            private var messageTextView = itemView.findViewById<TextView>(R.id.yourMessage)

            override fun bind(message: Message) {
                messageTextView?.text = message.message
            }
        }

        inner class AnotherUserMessageHolder(i: LayoutInflater, p: ViewGroup) :
            BaseMessageHolder(i.inflate(R.layout.another_user_message_layout, p, false)) {

            private var messageTextView = itemView.findViewById<TextView>(R.id.messageAnotherUser)

            override fun bind(message: Message) {
                messageTextView.text = message.message
            }
        }
    }

    companion object {
        private const val NAME_KEY = "Name"
        private const val CONTACT_NAME_KEY = "Contact name"
        private const val CONTACT_KEY = "Contact"

        @JvmStatic
        fun getIntent(context: Context, name: String, contact: String, contactName: String) =
            Intent(context, MainActivity::class.java).putExtra(NAME_KEY, name)
                .putExtra(CONTACT_KEY, contact).putExtra(CONTACT_NAME_KEY, contactName)
    }
}