package com.example.gastoiq.ui.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private fun isoFormatter(): SimpleDateFormat {
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
}

fun Long.toIsoString(): String {
    return try {
        isoFormatter().format(Date(this))
    } catch (_: Exception) {
        ""
    }
}

fun String?.isoToMillis(): Long {
    return try {
        if (this.isNullOrBlank()) {
            System.currentTimeMillis()
        } else {
            isoFormatter().parse(this)?.time ?: System.currentTimeMillis()
        }
    } catch (_: Exception) {
        System.currentTimeMillis()
    }
}

fun Usuario.toRemote() = UsuarioRemote(
    id = remoteId,
    nombre = nombre,
    correo = correo,
    updatedAt = updatedAt.toIsoString(),
    deleted = isDeleted
)

fun UsuarioRemote.toLocal() = Usuario(
    remoteId = id,
    nombre = nombre,
    correo = correo,
    updatedAt = updatedAt.isoToMillis(),
    isSynced = true,
    isDeleted = deleted
)

fun Categoria.toRemote() = CategoriaRemote(
    id = remoteId,
    usuarioId = usuarioRemoteId,
    nombre = nombre,
    tipo = tipo,
    updatedAt = updatedAt.toIsoString(),
    deleted = isDeleted
)

fun CategoriaRemote.toLocal() = Categoria(
    remoteId = id,
    usuarioRemoteId = usuarioId,
    nombre = nombre,
    tipo = tipo,
    updatedAt = updatedAt.isoToMillis(),
    isSynced = true,
    isDeleted = deleted
)

fun Gasto.toRemote() = GastoRemote(
    id = remoteId,
    usuarioId = usuarioRemoteId,
    categoriaId = categoriaRemoteId,
    descripcion = descripcion,
    monto = monto,
    fecha = fecha,
    updatedAt = updatedAt.toIsoString(),
    deleted = isDeleted
)

fun GastoRemote.toLocal() = Gasto(
    remoteId = id,
    usuarioRemoteId = usuarioId,
    categoriaRemoteId = categoriaId,
    descripcion = descripcion,
    monto = monto,
    fecha = fecha,
    updatedAt = updatedAt.isoToMillis(),
    isSynced = true,
    isDeleted = deleted
)

fun Presupuesto.toRemote() = PresupuestoRemote(
    id = remoteId,
    usuarioId = usuarioRemoteId,
    categoriaId = categoriaRemoteId,
    nombre = nombre,
    montoLimite = montoLimite,
    gastado = gastado,
    mes = mes,
    anio = anio,
    updatedAt = updatedAt.toIsoString(),
    deleted = isDeleted
)

fun PresupuestoRemote.toLocal() = Presupuesto(
    remoteId = id,
    usuarioRemoteId = usuarioId,
    categoriaRemoteId = categoriaId,
    nombre = nombre,
    montoLimite = montoLimite,
    gastado = gastado,
    mes = mes,
    anio = anio,
    updatedAt = updatedAt.isoToMillis(),
    isSynced = true,
    isDeleted = deleted
)

fun MetaAhorro.toRemote() = MetaAhorroRemote(
    id = remoteId,
    usuarioId = usuarioRemoteId,
    nombre = nombre,
    descripcion = descripcion,
    montoObjetivo = montoObjetivo,
    montoActual = montoActual,
    fechaLimite = fechaLimite,
    updatedAt = updatedAt.toIsoString(),
    deleted = isDeleted
)

fun MetaAhorroRemote.toLocal() = MetaAhorro(
    remoteId = id,
    usuarioRemoteId = usuarioId,
    nombre = nombre,
    descripcion = descripcion,
    montoObjetivo = montoObjetivo,
    montoActual = montoActual,
    fechaLimite = fechaLimite,
    updatedAt = updatedAt.isoToMillis(),
    isSynced = true,
    isDeleted = deleted
)