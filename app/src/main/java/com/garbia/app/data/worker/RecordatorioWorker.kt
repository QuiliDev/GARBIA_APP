package com.garbia.app.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.garbia.app.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RecordatorioWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID   = "garbia_recordatorio"
        const val NOTIF_ID     = 1001
        const val WORK_NAME    = "recordatorio_diario"
    }

    override suspend fun doWork(): Result {
        crearCanalSiNecesario()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("¡Recuerda reciclar hoy! ♻️")
            .setContentText("Escanea un residuo y mantén tu racha activa.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        manager.notify(NOTIF_ID, notif)
        return Result.success()
    }

    private fun crearCanalSiNecesario() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Recordatorio diario",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Recuérdame reciclar cada día" }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
