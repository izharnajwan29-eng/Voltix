package com.example.voltix

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

/**
 * Layar ke-2. Dibuka dari MainActivity memakai Explicit Intent.
 * Data masuk (extra): id produk, stok, daftar perbandingan, nama user.
 * Data keluar (setResult): daftar perbandingan terbaru + id produk "Beli Sekarang".
 */
class DetailActivity : ComponentActivity() {

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
        const val EXTRA_STOCK = "extra_stock"
        const val EXTRA_COMPARE = "extra_compare_ids"
        const val EXTRA_BUY_NOW = "extra_buy_now_id"
        const val EXTRA_USER = "extra_user_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Defensive programming: validasi data dari Intent
        val productId = intent.getIntExtra(EXTRA_PRODUCT_ID, -1)
        val product = products.firstOrNull { it.id == productId }
        if (product == null) {
            Toast.makeText(this, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val stock = intent.getIntExtra(EXTRA_STOCK, 0).coerceAtLeast(0)
        val userName = intent.getStringExtra(EXTRA_USER) ?: "Pengguna"
        val initialCompare = intent.getIntArrayExtra(EXTRA_COMPARE)?.toList() ?: emptyList()
        val db = VoltixDb.get(this)

        setResult(RESULT_OK, Intent().putExtra(EXTRA_COMPARE, initialCompare.toIntArray()))

        setContent {
            VoltixTheme {
                DetailHost(this, product, stock, userName, initialCompare, db)
            }
        }
    }
}

@Composable
private fun DetailHost(
    activity: DetailActivity,
    product: Product,
    stock: Int,
    userName: String,
    initialCompare: List<Int>,
    db: VoltixDb
) {
    val cs = MaterialTheme.colorScheme
    val context = LocalContext.current
    fun toast(msg: String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    // State aman rotasi
    val compare = rememberSaveable(
        saver = listSaver<SnapshotStateList<Int>, Int>(save = { it.toList() }, restore = { it.toMutableStateList() })
    ) { initialCompare.toMutableStateList() }
    var isFav by remember { mutableStateOf(db.isFavorite(product.id)) }
    val reviews = remember { db.reviews(product.id).toMutableStateList() }

    // Kirim hasil ke MainActivity setiap daftar perbandingan berubah
    LaunchedEffect(compare.toList()) {
        activity.setResult(
            ComponentActivityResult.OK,
            Intent().putExtra(DetailActivity.EXTRA_COMPARE, compare.toIntArray())
        )
    }

    Scaffold(containerColor = cs.background) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
            AppBar(title = "Detail Produk", onBack = { activity.finish() }) {
                RoundIconButton(
                    label = if (isFav) "❤️" else "🤍",
                    container = cs.surfaceVariant
                ) {
                    isFav = !isFav
                    db.setFavorite(product.id, isFav)
                }
            }
            Box(Modifier.weight(1f)) {
                DetailScreen(
                    product = product,
                    stock = stock,
                    inCompare = product.id in compare,
                    reviews = product.reviews + reviews,
                    onToggleCompare = {
                        if (product.id in compare) {
                            compare.remove(product.id)
                        } else if (compare.size >= 2) {
                            toast("Maksimal 2 produk untuk dibandingkan")
                        } else {
                            compare.add(product.id)
                            toast("Ditambahkan ke perbandingan")
                        }
                    },
                    onAddToCart = {
                        val have = db.cartQty(product.id)
                        if (have >= stock) {
                            toast("Stok ${product.name} tidak cukup")
                        } else {
                            db.setCart(product.id, have + 1)
                            toast("${product.name} masuk keranjang")
                        }
                    },
                    onBuyNow = {
                        activity.setResult(
                            ComponentActivityResult.OK,
                            Intent()
                                .putExtra(DetailActivity.EXTRA_COMPARE, compare.toIntArray())
                                .putExtra(DetailActivity.EXTRA_BUY_NOW, product.id)
                        )
                        activity.finish()
                    },
                    onSubmitReview = { stars, text ->
                        val r = Review(userName, stars, text)
                        db.addReview(product.id, r)
                        reviews.add(r)
                        toast("Ulasan terkirim")
                    }
                )
            }
        }
    }
}

private object ComponentActivityResult {
    const val OK = android.app.Activity.RESULT_OK
}