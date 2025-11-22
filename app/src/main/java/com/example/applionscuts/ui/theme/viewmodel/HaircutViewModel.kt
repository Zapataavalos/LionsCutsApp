package com.example.applionscuts.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.applionscuts.R
import com.example.applionscuts.model.Haircut

class HaircutViewModel : ViewModel() {

    // ---------- HAIRCUT SELECCIONADO PARA DETALLES ----------
    private val _selectedHaircut = MutableLiveData<Haircut?>(null)
    val selectedHaircut: LiveData<Haircut?> = _selectedHaircut

    fun onHaircutSelected(haircut: Haircut) {
        _selectedHaircut.value = haircut
    }

    fun onDialogDismiss() {
        _selectedHaircut.value = null
    }

    // ---------- LISTA COMPLETA DE SERVICIOS (CRUD) ----------
    private val _haircuts = MutableLiveData<List<Haircut>>(emptyList())
    val haircuts: LiveData<List<Haircut>> = _haircuts

    init {
        loadDefaultHaircuts() // cargamos tu catálogo original
    }

    private fun loadDefaultHaircuts() {
        _haircuts.value = listOf(
            Haircut(
                id = "1",
                name = "Buzz Cut",
                description = "Corte militar clásico.",
                longDescription = "El Buzz Cut es un corte simple y de bajo mantenimiento, realizado completamente a máquina.",
                price = 12000.0,
                imageResId = R.drawable.buzzcut
            ),
            Haircut(
                id = "2",
                name = "Mid Fade",
                description = "Desvanecido medio.",
                longDescription = "El Mid Fade ofrece un balance perfecto entre moderno y clásico.",
                price = 13000.0,
                imageResId = R.drawable.midfade
            ),
            Haircut(
                id = "3",
                name = "Burst Fade",
                description = "Desvanecido explosivo.",
                longDescription = "Perfecto para estilos como el mohicano o mullet moderno.",
                price = 13000.0,
                imageResId = R.drawable.burstfade
            ),
            Haircut(
                id = "4",
                name = "Taper Fade",
                description = "Degradado en patillas.",
                longDescription = "Sutil y elegante. Ideal para un look limpio.",
                price = 13000.0,
                imageResId = R.drawable.taper_fade
            ),
            Haircut(
                id = "5",
                name = "Mullet Moderno",
                description = "Corto adelante, largo atrás.",
                longDescription = "El clásico de los 80 reinventado.",
                price = 17000.0,
                imageResId = R.drawable.mullet
            ),
            Haircut(
                id = "6",
                name = "Slick Back",
                description = "Peinado hacia atrás.",
                longDescription = "Un look atemporal con fijación alta.",
                price = 13000.0,
                imageResId = R.drawable.slick_back
            ),
            Haircut(
                id = "7",
                name = "Quiff Texturizado",
                description = "Flequillo con volumen.",
                longDescription = "Un estilo moderno con textura casual.",
                price = 13000.0,
                imageResId = R.drawable.quiff
            ),
            Haircut(
                id = "8",
                name = "Corte Clásico",
                description = "Con tijera y peine.",
                longDescription = "Acabado natural para un look conservador.",
                price = 13000.0,
                imageResId = R.drawable.corte_clasico
            )
        )
    }

    // ---------- CRUD: AGREGAR SERVICIO ----------
    fun addHaircut(name: String, description: String, longDesc: String, price: Double, imageResId: Int) {
        val newHaircut = Haircut(
            id = System.currentTimeMillis().toString(),
            name = name,
            description = description,
            longDescription = longDesc,
            price = price,
            imageResId = imageResId
        )

        _haircuts.value = (_haircuts.value ?: emptyList()) + newHaircut
    }

    // ---------- CRUD: ELIMINAR SERVICIO ----------
    fun deleteHaircut(id: String) {
        _haircuts.value = _haircuts.value?.filter { it.id != id }
    }
}
