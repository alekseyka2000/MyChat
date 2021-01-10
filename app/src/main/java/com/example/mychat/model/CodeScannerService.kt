package com.example.mychat.model

import android.content.Context
import com.budiyev.android.codescanner.*
import com.budiyev.android.codescanner.CodeScanner.ALL_FORMATS
import com.budiyev.android.codescanner.CodeScanner.CAMERA_BACK
import com.google.zxing.BarcodeFormat

class CodeScannerService(context: Context, scannerView: CodeScannerView) {
    private var codeScanner = CodeScanner(context, scannerView).apply {
        camera = CAMERA_BACK
        formats = listOf(BarcodeFormat.QR_CODE)
        autoFocusMode = AutoFocusMode.SAFE
        scanMode = ScanMode.SINGLE
        isAutoFocusEnabled = true
        isFlashEnabled = false
    }

    fun getCodeScanner() = codeScanner
}