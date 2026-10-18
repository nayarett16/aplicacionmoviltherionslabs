package cl.duoc.aplicacionmoviltherionslabs.data

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import cl.duoc.aplicacionmoviltherionslabs.MainActivity

object NotificationHelper {
    private const val CHANNEL_ID = "solicitudes_channel"
    private const val CHANNEL_NAME = "Notificaciones de Solicitudes"

    @SuppressLint("MissingPermission")
    fun mostrarNotificacionSolicitud(
        context: Context,
        titulo: String,
        mensaje: String,
        solicitudId: String
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para avisos de solicitudes de actividades"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("PANTALLA_DESTINO", "detalle_solicitud")
            putExtra("SOLICITUD_ID", solicitudId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            solicitudId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(solicitudId.hashCode(), notification)
    }
}