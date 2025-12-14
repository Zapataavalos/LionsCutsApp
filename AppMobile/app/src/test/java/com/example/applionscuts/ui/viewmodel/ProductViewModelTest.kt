package com.example.applionscuts.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.applionscuts.data.local.product.Product
import com.example.applionscuts.data.repository.ProductRepository
import com.example.applionscuts.data.repository.PurchaseRepository
import com.example.applionscuts.ui.theme.viewmodel.ProductViewModel
import com.example.applionscuts.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ProductViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ProductViewModel

    private val productRepository: ProductRepository = mock()
    private val purchaseRepository: PurchaseRepository = mock()

    @Before
    fun setUp() = runTest {
        whenever(productRepository.getProducts())
            .thenReturn(emptyList())

        viewModel = ProductViewModel(
            repo = productRepository,
            purchaseRepository = purchaseRepository
        )
    }


    // ---------------------------------------------------
    // ADD TO CART
    // ---------------------------------------------------

    @Test
    fun addToCart_productoConStock_agregaItem() {
        val product = Product(
            id = 1,
            name = "Producto",
            brand = "Marca",
            price = 1000.0,
            description = "",
            longDescription = "",
            imageResId = 0,
            stock = 5
        )

        viewModel.addToCart(product)

        assertEquals(1, viewModel.cartItems.value?.size)
        assertEquals(1, viewModel.cartItems.value?.first()?.quantity)
        assertEquals(1000.0, viewModel.totalPrice.value)
    }

    @Test
    fun addToCart_productoSinStock_muestraToast() {
        val product = Product(
            id = 1,
            name = "Producto",
            brand = "Marca",
            price = 1000.0,
            description = "",
            longDescription = "",
            imageResId = 0,
            stock = 0
        )

        viewModel.addToCart(product)

        assertEquals(
            "Producto sin stock disponible",
            viewModel.toastMessage.value
        )
    }

    // ---------------------------------------------------
    // CANTIDAD
    // ---------------------------------------------------

    @Test
    fun increaseQuantity_incrementaCantidad() {
        val product = Product(
            id = 1,
            name = "Producto",
            brand = "Marca",
            price = 1000.0,
            description = "",
            longDescription = "",
            imageResId = 0,
            stock = 5
        )

        viewModel.addToCart(product)
        viewModel.increaseQuantity(1)

        assertEquals(2, viewModel.cartItems.value?.first()?.quantity)
        assertEquals(2000.0, viewModel.totalPrice.value)
    }

    @Test
    fun decreaseQuantity_conCantidadMayorAUno_reduceCantidad() {
        val product = Product(
            id = 1,
            name = "Producto",
            brand = "Marca",
            price = 1000.0,
            description = "",
            longDescription = "",
            imageResId = 0,
            stock = 5
        )

        viewModel.addToCart(product)
        viewModel.increaseQuantity(1)
        viewModel.decreaseQuantity(1)

        assertEquals(1, viewModel.cartItems.value?.first()?.quantity)
        assertEquals(1000.0, viewModel.totalPrice.value)
    }

    @Test
    fun decreaseQuantity_conCantidadUno_eliminaItem() {
        val product = Product(
            id = 1,
            name = "Producto",
            brand = "Marca",
            price = 1000.0,
            description = "",
            longDescription = "",
            imageResId = 0,
            stock = 5
        )

        viewModel.addToCart(product)
        viewModel.decreaseQuantity(1)

        assertTrue(viewModel.cartItems.value.isNullOrEmpty())
        assertEquals(0.0, viewModel.totalPrice.value)
    }

    // ---------------------------------------------------
    // REMOVE ITEM
    // ---------------------------------------------------

    @Test
    fun removeItem_eliminaProducto() {
        val product = Product(
            id = 1,
            name = "Producto",
            brand = "Marca",
            price = 1000.0,
            description = "",
            longDescription = "",
            imageResId = 0,
            stock = 5
        )

        viewModel.addToCart(product)
        viewModel.removeItem(1)

        assertTrue(viewModel.cartItems.value.isNullOrEmpty())
        assertEquals(0.0, viewModel.totalPrice.value)
    }

    // ---------------------------------------------------
    // SELECCION PRODUCTO
    // ---------------------------------------------------

    @Test
    fun onProductSelected_asignaProducto() {
        val product = Product(
            id = 1,
            name = "Producto",
            brand = "Marca",
            price = 1000.0,
            description = "",
            longDescription = "",
            imageResId = 0,
            stock = 5
        )

        viewModel.onProductSelected(product)

        assertEquals(product, viewModel.selectedProduct.value)
    }

    @Test
    fun onDialogDismiss_limpiaSeleccion() {
        viewModel.onDialogDismiss()
        assertNull(viewModel.selectedProduct.value)
    }
}
