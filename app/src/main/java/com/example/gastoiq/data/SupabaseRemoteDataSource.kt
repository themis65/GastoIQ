package com.example.gastoiq.data

import com.example.gastoiq.model.CategoriaRemote
import com.example.gastoiq.model.GastoRemote
import com.example.gastoiq.model.MetaAhorroRemote
import com.example.gastoiq.model.PresupuestoRemote
import com.example.gastoiq.model.UsuarioRemote
import com.example.gastoiq.network.SupabaseManager
import io.github.jan.supabase.postgrest.from

class SupabaseRemoteDataSource {

    suspend fun upsertUsuario(usuario: UsuarioRemote): UsuarioRemote {
        return SupabaseManager.client
            .from("usuarios")
            .upsert(usuario) { select() }
            .decodeSingle<UsuarioRemote>()
    }

    suspend fun upsertCategoria(categoria: CategoriaRemote): CategoriaRemote {
        return SupabaseManager.client
            .from("categorias")
            .upsert(categoria) { select() }
            .decodeSingle<CategoriaRemote>()
    }

    suspend fun upsertGasto(gasto: GastoRemote): GastoRemote {
        return SupabaseManager.client
            .from("gastos")
            .upsert(gasto) { select() }
            .decodeSingle<GastoRemote>()
    }

    suspend fun upsertPresupuesto(presupuesto: PresupuestoRemote): PresupuestoRemote {
        return SupabaseManager.client
            .from("presupuestos")
            .upsert(presupuesto) { select() }
            .decodeSingle<PresupuestoRemote>()
    }

    suspend fun upsertMeta(meta: MetaAhorroRemote): MetaAhorroRemote {
        return SupabaseManager.client
            .from("metas_ahorro")
            .upsert(meta) { select() }
            .decodeSingle<MetaAhorroRemote>()
    }

    suspend fun fetchUsuarios(): List<UsuarioRemote> {
        return SupabaseManager.client
            .from("usuarios")
            .select()
            .decodeList<UsuarioRemote>()
    }

    suspend fun fetchCategorias(): List<CategoriaRemote> {
        return SupabaseManager.client
            .from("categorias")
            .select()
            .decodeList<CategoriaRemote>()
    }

    suspend fun fetchGastos(): List<GastoRemote> {
        return SupabaseManager.client
            .from("gastos")
            .select()
            .decodeList<GastoRemote>()
    }

    suspend fun fetchPresupuestos(): List<PresupuestoRemote> {
        return SupabaseManager.client
            .from("presupuestos")
            .select()
            .decodeList<PresupuestoRemote>()
    }

    suspend fun fetchMetas(): List<MetaAhorroRemote> {
        return SupabaseManager.client
            .from("metas_ahorro")
            .select()
            .decodeList<MetaAhorroRemote>()
    }
}