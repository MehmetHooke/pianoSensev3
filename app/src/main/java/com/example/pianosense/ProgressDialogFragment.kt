package com.example.pianosense

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

class ProgressDialogFragment(private val onTimeout: () -> Unit) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // ProgressBar tasarımı için XML dosyasını bağla
        return inflater.inflate(R.layout.fragment_progress_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable=false
        // 10 saniye sonra Dialog'u kapat ve onTimeout() işlemini çalıştır
        Handler(Looper.getMainLooper()).postDelayed({
            dismiss() // Dialog'u kapat
            onTimeout() // Sonuç ekranına geçişi tetikle
        }, 5000) // 5 saniye
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        // Dialog'un arka planını şeffaf yap
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}