package com.example.applionscuts.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.applionscuts.R
import com.example.applionscuts.model.Haircut

class HaircutViewModel : ViewModel() {

    // Este LiveData guarda el corte que el usuario quiere ver en detalle
    private val _selectedHaircut = MutableLiveData<Haircut?>(null)
    val selectedHaircut: LiveData<Haircut?> = _selectedHaircut

    // Función llamada por la Vista cuando se presiona "Ver más"
    fun onHaircutSelected(haircut: Haircut) {
        _selectedHaircut.value = haircut
    }

    // Función llamada por la Vista para cerrar el diálogo
    fun onDialogDismiss() {
        _selectedHaircut.value = null
    }

    val haircuts = liveData {
        val list = listOf(
            Haircut(
                id = "1",
                name = "Buzz Cut",
                description = "Corte militar clásico.",
                longDescription = "El Buzz Cut es un corte simple y de bajo mantenimiento, realizado completamente a máquina. Ideal para un look limpio y fresco en cualquier temporada.",
                price = 12000.0,
                imageResId = R.drawable.buzzcut
            ),
            Haircut(
                id = "2",
                name = "Mid Fade",
                description = "Desvanecido medio.",
                longDescription = "El Mid Fade (desvanecido medio) ofrece un balance perfecto entre un look conservador y moderno. El degradado comienza a mitad de la cabeza.",
                price = 13000.0,
                imageResId = R.drawable.midfade
            ),
            Haircut(
                id = "3",
                name = "Burst Fade",
                description = "Desvanecido explosivo.",
                longDescription = "Perfecto para estilos como el mohicano o el 'mullet' moderno. El Burst Fade se concentra alrededor de la oreja, creando un efecto semicircular.",
                price = 13000.0,
                imageResId = R.drawable.burstfade
            ),
            Haircut(
                id = "4",
                name = "Taper Fade",
                description = "Degradado en patillas.",
                longDescription = "Un corte sutil y elegante. A diferencia del fade, el 'taper' solo degrada las patillas y la nuca, manteniendo más longitud en los lados.",
                price = 13000.0,
                imageResId = R.drawable.taper_fade
            ),
            Haircut(
                id = "5",
                name = "Mullet Moderno",
                description = "Corto adelante, largo atrás.",
                longDescription = "El clásico de los 80 reinventado. Se combina con un fade en los lados para un look atrevido y lleno de textura.",
                price = 17000.0,
                imageResId = R.drawable.mullet
            ),
            Haircut(
                id = "6",
                name = "Slick Back",
                description = "Peinado hacia atrás.",
                longDescription = "Un look atemporal que requiere longitud en la parte superior. Se peina todo hacia atrás, usualmente con un producto de fijación media o alta.",
                price = 13000.0,
                imageResId = R.drawable.slick_back
            ),
            Haircut(
                id = "7",
                name = "Quiff Texturizado",
                description = "Flequillo con volumen.",
                longDescription = "El Quiff consiste en un flequillo voluminoso peinado hacia arriba y atrás. La versión texturizada le da un toque más casual y moderno.",
                price = 13000.0,
                imageResId = R.drawable.quiff
            ),
            Haircut(
                id = "8",
                name = "Corte Clásico",
                description = "Con tijera y peine.",
                longDescription = "El corte tradicional. Ideal para quienes prefieren un look más conservador, realizado principalmente con tijera para un acabado natural.",
                price = 13000.0,
                imageResId = R.drawable.corte_clasico
            )
        )
        emit(list)
    }
}