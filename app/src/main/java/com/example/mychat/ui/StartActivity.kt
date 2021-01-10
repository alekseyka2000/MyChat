package com.example.mychat.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.budiyev.android.codescanner.*
import com.example.mychat.R
import com.example.mychat.model.CodeScannerService
import com.example.mychat.model.entity.Contact
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import org.koin.android.viewmodel.ext.android.viewModel

class StartActivity : AppCompatActivity() {

    private val startViewModel: StartViewModel by viewModel()
    private lateinit var toolbar: Toolbar
    private var contactList = listOf<Contact>()
    private val contactAdapter = ContactAdapter()
    private var showDeleteContactChooser = false
    private lateinit var sharedPref: SharedPreferences
    private lateinit var youName: String
    private lateinit var codeScanner: CodeScanner

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        sharedPref = applicationContext.getSharedPreferences("my name", 0)
        youName =
            sharedPref.getString(getString(R.string.set_name), "Set you name") ?: "Set you name"

        toolbar = findViewById<Toolbar>(R.id.toolbarStartActivity).apply {
            subtitle = youName
            inflateMenu(R.menu.start_activity_toolbar_menu)
            setOnMenuItemClickListener { item: MenuItem? ->
                when (item?.itemId) {
                    R.id.addContactItem -> addContact()
                    R.id.setName -> setYouName()
                    R.id.deleteContactItem -> deleteContact()
                }
                true
            }
        }

        findViewById<RecyclerView>(R.id.contactsRecycler).apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = contactAdapter
        }

        findViewById<Button>(R.id.contactButton).apply {
            text = "Show your contact"
        }.setOnClickListener {
            val qrCode = this.layoutInflater.inflate(R.layout.contact_layout, null)
            qrCode.findViewById<ImageView>(R.id.qrCodeImageView)
                .setImageBitmap(generateQRCode("${startViewModel.getYouContact()}_separator_$youName"))

            MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.you_contact))
                .setView(qrCode)
                .setPositiveButton(resources.getString(R.string.positive_button_text)) { dialog, _ ->
                    dialog.cancel()
                }
                .create()
                .show()
        }

        startViewModel.fetchContactsList()

        startViewModel.contactLiveData.observe(this, {
            contactAdapter.updateList(contactList, it)
            contactList = it
            Log.d(TAG, contactList.toString())
        })
    }

    fun onItemClick(contact: Contact) {
        startActivity(MainActivity.getIntent(this, youName, contact.contact, contact.nickname))
    }

    @SuppressLint("InflateParams")
    private fun deleteContact() {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.delete_contact))
            .setMessage(resources.getString(R.string.delete_contact_question))
            .setView(this.layoutInflater.inflate(R.layout.alter_dialog_delete_contact, null))
            .setPositiveButton(resources.getString(R.string.positive_button_text)) { dialog, _ ->
                if ((dialog as? AlertDialog)?.findViewById<CheckBox>(R.id.deleteContactCheckBox)?.isClickable == true) {
                    startViewModel.deleteAllContact()
                }
                //delete
            }
            .setNegativeButton(resources.getString(R.string.negative_button_text)) { dialog, _ ->
                dialog.cancel()
            }
            .create()
            .show()
    }


    private fun addContact() {
        var name = ""
        var contact = ""
        setupPermissions()
        val scannerLayout = this.layoutInflater.inflate(R.layout.code_scaner, null)
        val scannerView = scannerLayout.findViewById<CodeScannerView>(R.id.scanner_view)
        codeScanner = getCodeScanner(this, scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                contact = it.text.split("_separator_")[0]
                name = it.text.split("_separator_")[1]
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Log.d(TAG, "Camera initialization error: ${it.message}")
                Toast.makeText(
                    this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        scannerView?.setOnClickListener { codeScanner.startPreview() }

        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.set_name))
            .setView(scannerLayout)
            .setPositiveButton(resources.getString(R.string.positive_button_text)) { _, _ ->
                if (!startViewModel.checkContact(contact)) {
                    startViewModel.addNewContact(name, contact)
                    Toast.makeText(this, "You add contact: $name", Toast.LENGTH_LONG).show()
                }
                codeScanner.releaseResources()
            }
            .setNegativeButton(resources.getString(R.string.negative_button_text)) { dialog, _ ->
                codeScanner.releaseResources()
                dialog.cancel()
            }
            .create()
            .show()
    }

    private fun getCodeScanner(startActivity: StartActivity, scannerView: CodeScannerView) =
        CodeScannerService(startActivity, scannerView).getCodeScanner()

    @SuppressLint("CutPasteId", "InflateParams")
    private fun setYouName() {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.set_name))
            .setView(this.layoutInflater.inflate(R.layout.alter_dialog_add_contact, null))
            .setPositiveButton(resources.getString(R.string.positive_button_text)) { dialog, _ ->
                youName =
                    (dialog as? AlertDialog)?.findViewById<EditText>(R.id.nameContactEditText)?.text.toString()
                with(sharedPref.edit()) {
                    putString(getString(R.string.set_name), youName)
                    apply()
                }
                toolbar.subtitle = youName
            }
            .setNegativeButton(resources.getString(R.string.negative_button_text)) { dialog, _ ->
                dialog.cancel()
            }
            .create()
            .show()
    }

    inner class ContactAdapter : RecyclerView.Adapter<ContactAdapter.ContactHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ContactHolder(LayoutInflater.from(parent.context), parent)

        override fun onBindViewHolder(holder: ContactHolder, position: Int) {
            holder.apply {
                bind(contactList[position].nickname)
                itemView.setOnClickListener { onItemClick(contactList[position]) }
            }
        }

        override fun getItemCount() = contactList.size

        fun updateList(oldList: List<Contact>, newList: List<Contact>) {
            val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return oldList[oldItemPosition].id == newList[newItemPosition].id
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    return oldList[oldItemPosition].id == newList[newItemPosition].id
                }

                override fun getOldListSize() = oldList.size
                override fun getNewListSize() = newList.size
            })
            diff.dispatchUpdatesTo(this)
        }

        inner class ContactHolder(inflater: LayoutInflater, parent: ViewGroup) :
            RecyclerView.ViewHolder(inflater.inflate(R.layout.contact_item, parent, false)) {

            private val contactTextView = itemView.findViewById<TextView>(R.id.contactItemName)

            fun bind(name: String) {
                contactTextView?.text = name
            }
        }
    }

    private fun generateQRCode(text: String): Bitmap {
        val width = 700
        val height = 700
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val codeWriter = MultiFormatWriter()
        try {
            val bitMatrix = codeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        } catch (e: WriterException) {
            Log.d(TAG, "generateQRCode: ${e.message}")
        }
        return bitmap
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission to record denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.CAMERA),
            RECORD_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                }
            }
        }
    }

    companion object {
        const val TAG = "FirebaseService"
        const val RECORD_REQUEST_CODE = 101
    }
}