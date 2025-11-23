package com.example.applionscuts.ui.theme.viewmodel

import androidx.lifecycle.*
import com.example.applionscuts.data.local.product.Product
import com.example.applionscuts.data.local.purchase.PurchaseEntity
import com.example.applionscuts.data.repository.ProductRepository
import com.example.applionscuts.data.repository.PurchaseRepository
import com.example.applionscuts.model.CartItem
import com.example.applionscuts.viewmodel.PurchaseViewModel
import kotlinx.coroutines.launch

class ProductViewModel(
    private val repo: ProductRepository,
    private val purchaseRepository: PurchaseRepository   // ⭐ NUEVO
) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _cartItems = MutableLiveData<List<CartItem>>(emptyList())
    val cartItems: LiveData<List<CartItem>> = _cartItems

    private val _totalPrice = MutableLiveData(0.0)
    val totalPrice: LiveData<Double> = _totalPrice

    private val _showCartDialog = MutableLiveData(false)
    val showCartDialog: LiveData<Boolean> = _showCartDialog

    private val _showPaymentDialog = MutableLiveData(false)
    val showPaymentDialog: LiveData<Boolean> = _showPaymentDialog

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastMessage

    private val _selectedProduct = MutableLiveData<Product?>()
    val selectedProduct: LiveData<Product?> = _selectedProduct

    init {
        loadProducts()
        addSampleProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _products.postValue(repo.getProducts())
        }
    }

    fun addSampleProducts() {
        viewModelScope.launch {
            if (repo.getProducts().isEmpty()) {
                val list = listOf(
                    Product(
                        name = "Polvo Texturizador",
                        brand = "Lions Basics",
                        price = 18000.0,
                        description = "Añade volumen y textura mate al instante.",
                        longDescription = "Añade volumen y textura mate al instante. Ideal para peinados casuales y desestructurados. Aplicar sobre cabello seco para un máximo efecto.",
                        imageResId = com.example.applionscuts.R.drawable.polvo,
                        stock = 30
                    ),
                    Product(
                        name = "Cera Moldeadora",
                        brand = "Lions Style",
                        price = 22000.0,
                        description = "Fijación media-alta con brillo sutil.",
                        longDescription = "Fijación media-alta con un acabado de brillo sutil. Perfecta para definir peinados clásicos o modernos sin dejar residuos. Se elimina fácilmente con agua.",
                        imageResId = com.example.applionscuts.R.drawable.cera,
                        stock = 30
                    ),
                    Product(
                        name = "Shampoo Anticaída",
                        brand = "Lions Care",
                        price = 27000.0,
                        description = "Fortalece el folículo piloso.",
                        longDescription = "Formulado con extractos naturales y biotina para fortalecer el folículo piloso, reducir la caída y promover un cuero cabelludo saludable. Uso diario.",
                        imageResId = com.example.applionscuts.R.drawable.shampoo,
                        stock = 30
                    ),
                    Product(
                        name = "Aceite para Barba",
                        brand = "Lions Beard",
                        price = 25000.0,
                        description = "Hidrata y suaviza la barba.",
                        longDescription = "Hidrata y suaviza la barba, previniendo la picazón y la resequedad. Con aroma a sándalo y bergamota.",
                        imageResId = com.example.applionscuts.R.drawable.aceite_barba,
                        stock = 30
                    ),
                    Product(
                        name = "Peine de Madera",
                        brand = "Lions Tools",
                        price = 15000.0,
                        description = "Anti-estático y duradero.",
                        longDescription = "Peine de madera de sándalo antiestático. Ideal para desenredar la barba y el cabello sin causar frizz.",
                        imageResId = com.example.applionscuts.R.drawable.peine_madera,
                        stock = 30
                    ),
                    Product(
                        name = "Tónico Crecimiento",
                        brand = "Lions Boost",
                        price = 45000.0,
                        description = "Estimula el crecimiento.",
                        longDescription = "Fórmula avanzada con Minoxidil al 5% y Biotina, diseñada para estimular el crecimiento del cabello y la barba.",
                        imageResId = com.example.applionscuts.R.drawable.tonico,
                        stock = 30
                    ),
                    Product(
                        name = "Aftershave Bálsamo",
                        brand = "Lions Care",
                        price = 23000.0,
                        description = "Calma e hidrata después del afeitado.",
                        longDescription = "Calma e hidrata la piel instantáneamente después del afeitado. Fórmula sin alcohol con aloe vera y manzanilla.",
                        imageResId = com.example.applionscuts.R.drawable.aftershave,
                        stock = 30
                    ),
                    Product(
                        name = "Navaja Clásica",
                        brand = "Lions Tools",
                        price = 35000.0,
                        description = "Afeitado preciso y al ras.",
                        longDescription = "Navaja de afeitar clásica (Shavette) de acero inoxidable. Para un afeitado preciso y al ras. (No incluye cuchillas).",
                        imageResId = com.example.applionscuts.R.drawable.navaja,
                        stock = 30
                    ),
                    Product(
                        name = "Gel de Afeitar",
                        brand = "Lions Style",
                        price = 19000.0,
                        description = "Gel transparente de alta precisión.",
                        longDescription = "Gel de afeitar transparente de alta precisión. Permite ver por dónde se afeita, ideal para perfilar barba y bigote.",
                        imageResId = com.example.applionscuts.R.drawable.gel,
                        stock = 30
                    ),
                    Product(
                        name = "Capa de Barbero",
                        brand = "Lions Gear",
                        price = 30000.0,
                        description = "Profesional e impermeable.",
                        longDescription = "Capa profesional de barbero con el logo de Supreme. Material ligero, impermeable y de secado rápido.",
                        imageResId = com.example.applionscuts.R.drawable.capa,
                        stock = 30
                    )
                )
                list.forEach { repo.addProduct(it) }
                loadProducts()
            }
        }
    }

    fun addToCart(product: Product) {
        if (product.stock <= 0) {
            showToast("Producto sin stock disponible")
            return
        }

        val updatedProduct = product.copy(stock = product.stock - 1)

        _products.value = _products.value.orEmpty().map {
            if (it.id == product.id) updatedProduct else it
        }

        val current = _cartItems.value.orEmpty().toMutableList()
        val existing = current.find { it.product.id == product.id }

        if (existing != null) {
            current[current.indexOf(existing)] = existing.copy(quantity = existing.quantity + 1)
        } else {
            current.add(CartItem(updatedProduct, 1))
        }

        _cartItems.value = current
        updateTotalPrice()
        showToast("Producto añadido al carrito")
    }

    private fun updateTotalPrice() {
        _totalPrice.value = _cartItems.value.orEmpty()
            .sumOf { it.product.price * it.quantity }
    }

    fun onShowCart() { _showCartDialog.value = true }
    fun onHideCart() { _showCartDialog.value = false }
    fun onShowPayment() { _showCartDialog.value = false; _showPaymentDialog.value = true }
    fun onHidePayment() { _showPaymentDialog.value = false }

    fun showToast(message: String) { _toastMessage.value = message }
    fun onToastShown() { _toastMessage.value = null }

    // ============================================================
    // 🔥 NUEVO: Registrar compra
    // ============================================================
    fun confirmPayment(
        userId: Int,                 // ⭐ AGREGADO
        nombre: String,
        dosApellidos: String,
        rut: String,
        numeroTarjeta: String,
        cvv: String,
        metodoEntrega: String,
        direccion: String?,
        purchaseViewModel: PurchaseViewModel
    ) {
        val cartList = _cartItems.value ?: emptyList()

        if (cartList.isEmpty()) {
            showToast("El carrito está vacío")
            return
        }

        val cartJson = cartList.joinToString(prefix = "[", postfix = "]") { item ->
            """
        {
            "name": "${item.product.name}",
            "quantity": ${item.quantity},
            "price": ${item.product.price}
        }
        """.trimIndent()
        }

        val cardMasked = "**** **** **** " + numeroTarjeta.takeLast(4)
        val cvvMasked = "***"

        purchaseViewModel.savePurchase(
            userId = userId,                        // ⭐ AGREGADO
            userName = nombre,
            userLastNames = dosApellidos,
            userRut = rut,
            cardNumber = cardMasked,
            cvv = cvvMasked,
            method = metodoEntrega,
            fullAddress = direccion,
            cartJson = cartJson,
            totalAmount = totalPrice.value ?: 0.0
        )

        _cartItems.value = emptyList()
        _totalPrice.value = 0.0

        onHidePayment()
        showToast("¡Pago procesado con éxito!")
    }



    fun increaseQuantity(productId: Int) {
        val current = _cartItems.value.orEmpty().toMutableList()
        val item = current.find { it.product.id == productId }
        if (item != null) {
            current[current.indexOf(item)] = item.copy(quantity = item.quantity + 1)
            _cartItems.value = current
            updateTotalPrice()
        }
    }

    fun decreaseQuantity(productId: Int) {
        val current = _cartItems.value.orEmpty().toMutableList()
        val item = current.find { it.product.id == productId }
        if (item != null) {
            if (item.quantity > 1) {
                current[current.indexOf(item)] = item.copy(quantity = item.quantity - 1)
            } else {
                current.remove(item)
            }
            _cartItems.value = current
            updateTotalPrice()
        }
    }

    fun removeItem(productId: Int) {
        val current = _cartItems.value.orEmpty().toMutableList()
        current.removeAll { it.product.id == productId }
        _cartItems.value = current
        updateTotalPrice()
    }

    fun onProductSelected(product: Product) {
        _selectedProduct.value = product
    }

    fun onDialogDismiss() {
        _selectedProduct.value = null
    }

    fun addProductToDatabase(product: Product) {
        viewModelScope.launch {
            repo.addProduct(product)
            loadProducts()
        }
    }

    fun deleteProductFromDatabase(product: Product) {
        viewModelScope.launch {
            repo.deleteProduct(product)
            loadProducts()
        }
    }
}
