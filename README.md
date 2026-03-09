# 💰 GastoIQ — Gestión Inteligente de Gastos Personales

Aplicación móvil Android para el control de gastos personales, presupuestos mensuales y metas de ahorro, desarrollada en **Kotlin** con arquitectura **MVVM**, persistencia local con **Room** y sincronización en la nube con **Supabase**.

---

## 📋 Descripción del Proyecto

GastoIQ resuelve la problemática de la **falta de control financiero personal** en estudiantes universitarios. Permite registrar gastos diarios organizados por categorías, establecer presupuestos mensuales por categoría con alertas visuales de consumo, crear metas de ahorro con seguimiento de progreso, y sincronizar toda la información con la nube para no perder datos.

La app funciona completamente **offline** (los datos se guardan en SQLite local) y cuando hay conexión a internet, se sincronizan automáticamente con **Supabase** (PostgreSQL en la nube).

---

## 🏗️ Arquitectura del Código

El proyecto implementa estrictamente el patrón **MVVM (Model-View-ViewModel)** con **Repository Pattern**:

```
com.example.gastoiq/
│
├── model/                    ← Entidades Room (7 tablas)
│   ├── Usuario.kt
│   ├── Categoria.kt
│   ├── Gasto.kt
│   ├── GastoConCategoria.kt
│   ├── Presupuesto.kt
│   ├── MetaAhorro.kt
│   ├── Etiqueta.kt
│   └── GastoEtiqueta.kt     ← Tabla intermedia N:M
│
├── data/                     ← Capa de datos (Room + Repository)
│   ├── AppDatabase.kt        ← Singleton RoomDatabase (7 entidades)
│   ├── AppRepository.kt      ← Repository Pattern (Local vs Remoto)
│   ├── UsuarioDao.kt
│   ├── CategoriaDao.kt
│   ├── GastoDao.kt           ← CRUD completo + queries con JOIN
│   ├── PresupuestoDao.kt
│   ├── MetaAhorroDao.kt
│   ├── EtiquetaDao.kt        ← Maneja relación N:M
│   └── SeedData.kt           ← Datos iniciales (categorías, etiquetas)
│
├── network/                  ← Comunicación con Supabase
│   ├── SupabaseClient.kt     ← Retrofit + OkHttp + Interceptor Auth
│   └── SupabaseApiService.kt ← Endpoints REST (GET, POST, PATCH, DELETE)
│
├── ui/                       ← ViewModels (lógica de negocio)
│   ├── MainViewModel.kt
│   ├── AddEditGastoViewModel.kt
│   ├── MetasViewModel.kt
│   ├── PresupuestosViewModel.kt
│   └── EstadisticasViewModel.kt
│
├── adapter/                  ← RecyclerView Adapters
│   ├── GastoAdapter.kt
│   ├── MetaAdapter.kt
│   └── PresupuestoAdapter.kt
│
├── utils/                    ← Utilidades
│   ├── NetworkMonitor.kt     ← Monitor de conectividad en tiempo real
│   └── UiState.kt            ← Sealed class (Loading/Success/Error/Idle)
│
├── GastoIQApp.kt             ← Application (Singleton DB + deviceId)
├── MainActivity.kt
├── AddEditGastoActivity.kt
├── DetailActivity.kt
├── MetasActivity.kt
├── PresupuestosActivity.kt
└── EstadisticasActivity.kt
```

### Flujo de datos (MVVM)

```
┌─────────────┐    observa LiveData    ┌──────────────────┐
│   ACTIVITY   │ ◄──────────────────── │    VIEWMODEL      │
│   (View)     │ ────────────────────► │  (Lógica)         │
└─────────────┘    envía acciones      └──────────────────┘
                                              │
                                              │ llama métodos
                                              ▼
                                       ┌──────────────────┐
                                       │   REPOSITORY      │
                                       │ (Local vs Remoto) │
                                       └──────────────────┘
                                          │             │
                                          ▼             ▼
                                    ┌──────────┐  ┌───────────┐
                                    │   ROOM    │  │ SUPABASE  │
                                    │ (SQLite)  │  │ (REST API)│
                                    └──────────┘  └───────────┘
```

---

## 🗃️ Diagrama de Base de Datos (DER)

```
┌────────────────┐       ┌────────────────┐       ┌────────────────┐
│   USUARIOS     │       │  CATEGORIAS    │       │  ETIQUETAS     │
├────────────────┤       ├────────────────┤       ├────────────────┤
│ id (PK)        │       │ id (PK)        │       │ id (PK)        │
│ nombre         │       │ nombre         │       │ nombre         │
│ email          │       │ color          │       │ color          │
│ fechaRegistro  │       │ icono          │       └───────┬────────┘
└───────┬────────┘       └───────┬────────┘               │
        │ 1                      │ 1                      │
        │                        │                        │ N
        │ N                      │ N                      │
┌───────▼────────┐       ┌───────▼────────┐       ┌──────▼─────────────┐
│    GASTOS      │       │                │       │  GASTO_ETIQUETA    │
├────────────────┤       │                │       │  (Tabla intermedia)│
│ id (PK)        │◄──────┤                │       ├────────────────────┤
│ monto          │       │                │       │ gastoId (FK, PK)   │
│ descripcion    │       │                │       │ etiquetaId (FK, PK)│
│ fecha          │       │                │       └────────────────────┘
│ notas          │       │                │               ▲ N
│ usuarioId (FK) │       │                │               │
│ categoriaId(FK)├───────┘                │               │
│ sincronizado   │────────────────────────┘               │
└───────┬────────┘                                        │
        │ N ──────────────────────────────────────────────┘
        │
┌───────▼────────────┐           ┌────────────────────┐
│  PRESUPUESTOS      │           │  METAS_AHORRO      │
├────────────────────┤           ├────────────────────┤
│ id (PK)            │           │ id (PK)            │
│ montoLimite        │           │ nombre             │
│ mes                │           │ montoObjetivo      │
│ usuarioId (FK)     │           │ montoActual        │
│ categoriaId (FK)   │           │ fechaLimite        │
└────────────────────┘           │ usuarioId (FK)     │
                                 └────────────────────┘
```

### Relaciones

| Relación | Tipo | Implementación |
|----------|------|----------------|
| Usuario → Gastos | 1:N | ForeignKey con CASCADE |
| Usuario → Presupuestos | 1:N | ForeignKey con CASCADE |
| Usuario → MetasAhorro | 1:N | ForeignKey con CASCADE |
| Categoria → Gastos | 1:N | ForeignKey con CASCADE |
| Categoria → Presupuestos | 1:N | ForeignKey con CASCADE |
| Gasto ↔ Etiqueta | **N:M** | Tabla intermedia `gasto_etiqueta` con PK compuesta |

**7 entidades** — 5 relaciones **1:N** — 1 relación **N:M**

---

## ✅ Requisitos Técnicos Implementados

### A. Modelo de Datos (Persistencia)

| Requisito | Estado | Implementación |
|-----------|--------|----------------|
| Room Database | ✅ | `AppDatabase.kt` — Singleton con 7 entidades |
| Mínimo 5 entidades relacionadas | ✅ | 7 entidades: Usuario, Categoria, Gasto, Presupuesto, MetaAhorro, Etiqueta, GastoEtiqueta |
| Relaciones 1:N | ✅ | ForeignKeys con CASCADE en Gasto, Presupuesto, MetaAhorro |
| Relación N:M | ✅ | Gasto ↔ Etiqueta via tabla intermedia `gasto_etiqueta` |
| CRUD completo | ✅ | Insert, Select, Update, Delete en todas las entidades principales |

### B. Networking (API REST)

| Requisito | Estado | Implementación |
|-----------|--------|----------------|
| Consumo de servicio externo (Retrofit) | ✅ | `SupabaseClient.kt` + `SupabaseApiService.kt` |
| Listar, filtrar y enviar datos | ✅ | GET, POST, PATCH, DELETE a Supabase REST API |
| Indicadores de carga | ✅ | `UiState.Loading` → ProgressBar en cada pantalla |
| Mensajes sin conexión | ✅ | `NetworkMonitor.kt` → Chip "En línea / Sin conexión" |
| Modo offline | ✅ | Datos se guardan en Room primero (offline-first) |

### C. Arquitectura y Buenas Prácticas

| Requisito | Estado | Implementación |
|-----------|--------|----------------|
| Patrón MVVM | ✅ | 5 ViewModels: Main, AddEditGasto, Metas, Presupuestos, Estadisticas |
| Repository Pattern | ✅ | `AppRepository.kt` — abstrae Room (local) vs Supabase (remoto) |
| Corrutinas | ✅ | `viewModelScope.launch(Dispatchers.IO)` en todas las operaciones |
| Material Design 3 | ✅ | MaterialToolbar, MaterialCardView, MaterialButton, Chip, ChipGroup, ExtendedFAB, Snackbar, TextInputLayout, MaterialDivider, AppBarLayout |
| ViewBinding | ✅ | Habilitado en `build.gradle.kts`, usado en todas las Activities |
| RecyclerView optimizado | ✅ | 3 Adapters con ViewHolder pattern + swipe-to-delete |

---

## 📱 Módulos de la Aplicación

### Pantalla Principal (MainActivity)
- Lista de gastos recientes con tarjetas de colores por categoría
- Total del mes actual en tiempo real
- Chip indicador de conexión (online/offline)
- Botón de sincronización con Supabase
- Navegación a Presupuestos, Metas y Estadísticas
- Swipe-to-delete para eliminar gastos
- FAB extendido para agregar nuevo gasto

### Agregar/Editar Gasto (AddEditGastoActivity)
- Formulario con monto, descripción, categoría y notas
- Selector de categorías con iconos
- ChipGroup con etiquetas seleccionables (relación N:M)
- Modo crear y modo editar (detecta por Intent extras)
- Validación de campos obligatorios

### Detalle del Gasto (DetailActivity)
- Vista completa del gasto con header de color
- Muestra categoría con icono y color
- Lista de etiquetas asignadas (Chips)
- Fecha y notas del gasto

### Presupuestos (PresupuestosActivity)
- Crear presupuestos mensuales por categoría
- Barra de progreso que muestra gastado vs límite
- Colores dinámicos: verde (<70%), naranja (70-90%), rojo (>90%)
- Swipe-to-delete para eliminar presupuestos

### Metas de Ahorro (MetasActivity)
- Crear metas con nombre, monto objetivo y fecha límite
- Campo inline para agregar ahorros incrementales
- Barra de progreso con porcentaje
- DatePicker para seleccionar fecha límite
- Swipe-to-delete para eliminar metas

### Estadísticas (EstadisticasActivity)
- Total general del mes
- Desglose de gastos por categoría
- Barras de progreso con porcentaje y colores por categoría

---

## 🔧 Tecnologías Utilizadas

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Kotlin | 2.0.21 | Lenguaje principal |
| Android SDK | 36 | Compilación y target |
| Room | 2.6.1 | Base de datos local (SQLite) |
| KSP | 2.0.21-1.0.28 | Procesador de anotaciones Room |
| Retrofit | 2.9.0 | Cliente HTTP para Supabase |
| OkHttp | 4.12.0 | Interceptor de autenticación |
| Gson | 2.9.0 | Serialización JSON |
| LiveData | 2.7.0 | Datos observables lifecycle-aware |
| ViewModel | 2.7.0 | Lógica de negocio desacoplada |
| Flow | Kotlin stdlib | Streams reactivos desde Room |
| Coroutines | Kotlin stdlib | Operaciones asíncronas |
| Material Design 3 | 1.12.0 | Componentes de UI modernos |
| ViewBinding | Built-in | Acceso type-safe a vistas |
| Supabase | REST API | Backend-as-a-Service (PostgreSQL) |

---

## ☁️ Configuración de Supabase

### 1. Crear proyecto
1. Ir a [supabase.com](https://supabase.com) y crear una cuenta
2. Crear nuevo proyecto (región: South America - São Paulo)

### 2. Crear tablas
Ejecutar en **SQL Editor > New Query**:

```sql
-- 1. Tabla: usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id TEXT PRIMARY KEY,
    nombre TEXT NOT NULL,
    email TEXT NOT NULL,
    fecha_registro TEXT NOT NULL
);

-- 2. Tabla: categorias
CREATE TABLE IF NOT EXISTS categorias (
    id TEXT PRIMARY KEY,
    nombre TEXT NOT NULL,
    color TEXT NOT NULL DEFAULT '#9E9E9E',
    icono TEXT NOT NULL DEFAULT '📦'
);

-- 3. Tabla: etiquetas
CREATE TABLE IF NOT EXISTS etiquetas (
    id TEXT PRIMARY KEY,
    nombre TEXT NOT NULL,
    color TEXT NOT NULL DEFAULT '#9E9E9E'
);

-- 4. Tabla: gastos
CREATE TABLE IF NOT EXISTS gastos (
    id TEXT PRIMARY KEY,
    monto DOUBLE PRECISION NOT NULL,
    descripcion TEXT NOT NULL,
    fecha TEXT NOT NULL,
    notas TEXT DEFAULT '',
    usuario_id TEXT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    categoria_id TEXT NOT NULL REFERENCES categorias(id) ON DELETE CASCADE,
    sincronizado BOOLEAN DEFAULT true
);

-- 5. Tabla: presupuestos
CREATE TABLE IF NOT EXISTS presupuestos (
    id TEXT PRIMARY KEY,
    monto_limite DOUBLE PRECISION NOT NULL,
    mes TEXT NOT NULL,
    usuario_id TEXT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    categoria_id TEXT NOT NULL REFERENCES categorias(id) ON DELETE CASCADE
);

-- 6. Tabla: metas_ahorro
CREATE TABLE IF NOT EXISTS metas_ahorro (
    id TEXT PRIMARY KEY,
    nombre TEXT NOT NULL,
    monto_objetivo DOUBLE PRECISION NOT NULL,
    monto_actual DOUBLE PRECISION DEFAULT 0.0,
    fecha_limite TEXT NOT NULL,
    usuario_id TEXT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE
);

-- 7. Tabla: gasto_etiqueta (relacion N:M)
CREATE TABLE IF NOT EXISTS gasto_etiqueta (
    gasto_id TEXT NOT NULL REFERENCES gastos(id) ON DELETE CASCADE,
    etiqueta_id TEXT NOT NULL REFERENCES etiquetas(id) ON DELETE CASCADE,
    PRIMARY KEY (gasto_id, etiqueta_id)
);

-- Row Level Security
ALTER TABLE usuarios ENABLE ROW LEVEL SECURITY;
ALTER TABLE categorias ENABLE ROW LEVEL SECURITY;
ALTER TABLE etiquetas ENABLE ROW LEVEL SECURITY;
ALTER TABLE gastos ENABLE ROW LEVEL SECURITY;
ALTER TABLE presupuestos ENABLE ROW LEVEL SECURITY;
ALTER TABLE metas_ahorro ENABLE ROW LEVEL SECURITY;
ALTER TABLE gasto_etiqueta ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Allow all" ON usuarios FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Allow all" ON categorias FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Allow all" ON etiquetas FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Allow all" ON gastos FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Allow all" ON presupuestos FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Allow all" ON metas_ahorro FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Allow all" ON gasto_etiqueta FOR ALL USING (true) WITH CHECK (true);

-- Datos iniciales
INSERT INTO categorias (id, nombre, color, icono) VALUES
    ('cat_comida','Comida','#FF6B6B','🍔'),
    ('cat_transporte','Transporte','#4ECDC4','🚌'),
    ('cat_ocio','Ocio','#A78BFA','🎮'),
    ('cat_educacion','Educación','#FACC15','📚'),
    ('cat_salud','Salud','#4CAF50','💊'),
    ('cat_compras','Compras','#FF9800','🛒'),
    ('cat_servicios','Servicios','#2196F3','📱'),
    ('cat_otros','Otros','#9E9E9E','📦')
ON CONFLICT (id) DO NOTHING;

INSERT INTO etiquetas (id, nombre, color) VALUES
    ('et_urgente','Urgente','#F44336'),
    ('et_recurrente','Recurrente','#FF9800'),
    ('et_opcional','Opcional','#4CAF50'),
    ('et_fijo','Fijo','#2196F3'),
    ('et_variable','Variable','#9C27B0')
ON CONFLICT (id) DO NOTHING;
```

### 3. Configurar credenciales en Android
Editar `app/build.gradle.kts`:

```kotlin
buildConfigField("String", "SUPABASE_URL", "\"https://TU_PROYECTO.supabase.co\"")
buildConfigField("String", "SUPABASE_ANON_KEY", "\"TU_ANON_KEY\"")
```

Los valores se obtienen en **Project Settings > API** del dashboard de Supabase.

---

---

## 🚀 Generar APK

**Debug (para la defensa):**
- Android Studio → Build → Build Bundle(s) / APK(s) → Build APK(s)
- Ruta: `app/build/outputs/apk/debug/app-debug.apk`

**Release (firmado):**
- Android Studio → Build → Generate Signed Bundle / APK → APK → Crear keystore → Finish
- Ruta: `app/release/app-release.apk`

---

## 👥 Integrantes

| Nombre | GitHub |
|--------|--------|
| Bryan Taco | @themis65 |
| Brayan Chango | @abruuzii |
| Benjamin Proaño | @s4kenza |
