package com.example.gastoiq.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gastoiq.model.Usuario
import com.example.gastoiq.model.Categoria
import com.example.gastoiq.model.Gasto
import com.example.gastoiq.model.Presupuesto
import com.example.gastoiq.model.MetaAhorro

@Database(
    entities = [
        Usuario::class,
        Categoria::class,
        Gasto::class,
        Presupuesto::class,
        MetaAhorro::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun gastoDao(): GastoDao
    abstract fun presupuestoDao(): PresupuestoDao
    abstract fun metaAhorroDao(): MetaAhorroDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gastoiq_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
