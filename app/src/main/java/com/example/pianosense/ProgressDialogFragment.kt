package com.example.pianosense

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

class ProgressDialogFragment(private val onTimeout: () -> Unit) : DialogFragment() {

    private val handler = Handler(Looper.getMainLooper())
    private val timeoutRunnable = Runnable {
        if (isAdded) {
            dismiss()
            onTimeout()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // ProgressBar tasarımı için XML dosyasını bağla
        return inflater.inflate(R.layout.fragment_progress_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        // Belirli bir süre sonra timeout çalışsın (örneğin 5 saniye)
        handler.postDelayed(timeoutRunnable, 10000)
    }

    // İşlem tamamlandığında timeout'u iptal etmek için bu metodu çağırın
    fun cancelTimeout() {
        handler.removeCallbacks(timeoutRunnable)
    }

    override fun onDestroyView() {
        handler.removeCallbacks(timeoutRunnable)
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}
