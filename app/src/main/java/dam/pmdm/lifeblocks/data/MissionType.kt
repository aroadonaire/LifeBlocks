package dam.pmdm.lifeblocks.data

enum class MissionType {
    LOGRO,       // Tienen final y se archivan al 100%
    ACUMULATIVA, // Nunca terminan, escalan en fases (100h -> 250h...)
    HABITO       // Se reinician (ej: semanal o diariamente)
}