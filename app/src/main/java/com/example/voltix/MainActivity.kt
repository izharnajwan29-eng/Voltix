package com.example.voltix

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File
import java.io.FileOutputStream
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import java.util.Locale

// ======================================================
//  TEMA & WARNA
// ======================================================
private val LightColors = lightColorScheme(
    primary = Color(0xFF5B2EFF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFECE8FF),
    onPrimaryContainer = Color(0xFF2A0F9E),
    secondary = Color(0xFFFF5A36),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE4DA),
    onSecondaryContainer = Color(0xFF8A2E10),
    tertiary = Color(0xFF12B5A6),
    background = Color(0xFFF7F7FC),
    onBackground = Color(0xFF14131F),
    surface = Color.White,
    onSurface = Color(0xFF14131F),
    surfaceVariant = Color(0xFFF1F0F9),
    onSurfaceVariant = Color(0xFF6B6985),
    outline = Color(0xFFE4E2F0),
    inverseSurface = Color(0xFF1B1A2B),
    inverseOnSurface = Color.White,
    error = Color(0xFFE5384B)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFB9A8FF),
    onPrimary = Color(0xFF260D8F),
    primaryContainer = Color(0xFF2F2078),
    onPrimaryContainer = Color(0xFFE6DFFF),
    secondary = Color(0xFFFF8F6B),
    onSecondary = Color(0xFF4A1604),
    secondaryContainer = Color(0xFF6B2A12),
    onSecondaryContainer = Color(0xFFFFDBCF),
    tertiary = Color(0xFF4FD8CB),
    background = Color(0xFF0C0B13),
    onBackground = Color(0xFFEDEBF7),
    surface = Color(0xFF16151F),
    onSurface = Color(0xFFEDEBF7),
    surfaceVariant = Color(0xFF221F30),
    onSurfaceVariant = Color(0xFFABA8C2),
    outline = Color(0xFF2E2C41),
    inverseSurface = Color(0xFFEDEBF7),
    inverseOnSurface = Color(0xFF14131F),
    error = Color(0xFFFF8A80)
)

val BrandBrush = Brush.linearGradient(listOf(Color(0xFF3A1BD0), Color(0xFF7B5CFF)))
val SuccessGreen = Color(0xFF1FA971)
val WarningOrange = Color(0xFFE08A00)
val StarYellow = Color(0xFFFFB400)

private val VoltixTypography = Typography(
    headlineMedium = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 26.sp, lineHeight = 32.sp, letterSpacing = (-0.6).sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp, letterSpacing = (-0.3).sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontSize = 15.sp, lineHeight = 22.sp, letterSpacing = 0.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, letterSpacing = 0.1.sp)
)

private val VoltixShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun VoltixTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = VoltixTypography,
        shapes = VoltixShapes,
        content = content
    )
}

// ======================================================
//  DATA
// ======================================================
data class Review(val name: String, val stars: Int, val text: String)

data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val price: Int,
    val oldPrice: Int,
    val emoji: String,
    val rating: Double,
    val sold: Int,
    val stock: Int, // stok awal
    val specs: Map<String, String>,
    val reviews: List<Review>
) {
    val discount: Int
        get() = if (oldPrice > price) ((oldPrice - price) * 100) / oldPrice else 0
}

data class OrderItem(
    val productId: Int,
    val name: String,
    val emoji: String,
    val price: Int,
    val qty: Int
)

class Order(
    val id: String,
    val items: List<OrderItem>,
    val total: Int,
    address: String,
    method: String,
    val direct: Boolean // true = dibuat lewat "Beli Sekarang"
) {
    var address by mutableStateOf(address)
    var method by mutableStateOf(method)

    // Menunggu Pembayaran -> Dikemas -> Dikirim -> Selesai (atau Dibatalkan)
    var status by mutableStateOf("Menunggu Pembayaran")
}

data class Cat(val emoji: String, val label: String, val key: String)

data class Promo(val tag: String, val title: String, val sub: String, val emoji: String, val brush: Brush)

const val ONGKIR = 20000

val paymentMethods = listOf(
    "Transfer Bank (BCA)",
    "E-Wallet (GoPay)",
    "COD (Bayar di tempat)"
)

fun methodEmoji(m: String) = when {
    m.startsWith("Transfer") -> "🏦"
    m.startsWith("E-Wallet") -> "👛"
    else -> "💵"
}

val categories = listOf(
    Cat("📱", "HP", "HP"),
    Cat("💻", "Laptop", "Laptop"),
    Cat("📲", "Tablet", "Tablet"),
    Cat("📷", "Kamera", "Kamera"),
    Cat("🎮", "Gaming", "Gaming"),
    Cat("🎧", "Aksesoris", "Aksesoris")
)

val promos = listOf(
    Promo("FLASH SALE", "Diskon hingga 30%", "Gadget pilihan, hanya minggu ini", "⚡",
        Brush.linearGradient(listOf(Color(0xFFFF6A3D), Color(0xFFFF9A5B)))),
    Promo("BARU", "Gaming Fest", "Konsol & aksesoris terbaru", "🎮",
        Brush.linearGradient(listOf(Color(0xFF4A1FE0), Color(0xFF7C4DFF)))),
    Promo("GRATIS", "Cashback Audio", "Headphone & TWS pilihan", "🎧",
        Brush.linearGradient(listOf(Color(0xFF0FA3B1), Color(0xFF3DD6C6))))
)

val products = listOf(
    Product(
        1, "Galaxy A55", "HP", 5999000, 6499000, "📱", 4.6, 1250, 10,
        mapOf("Layar" to "6.6\" AMOLED", "RAM" to "8 GB", "Memori" to "256 GB", "Baterai" to "5000 mAh"),
        listOf(Review("Rina", 5, "Layarnya bagus, baterai awet."), Review("Budi", 4, "Kamera lumayan untuk harga segini."))
    ),
    Product(
        2, "Redmi Note 13", "HP", 3199000, 3599000, "📱", 4.4, 2300, 5,
        mapOf("Layar" to "6.67\" AMOLED", "RAM" to "8 GB", "Memori" to "128 GB", "Baterai" to "5000 mAh"),
        listOf(Review("Sinta", 5, "Murah dan kencang."), Review("Dimas", 4, "Pengisian daya cepat."))
    ),
    Product(
        3, "Poco X6 Pro", "HP", 4599000, 4999000, "📱", 4.5, 980, 7,
        mapOf("Layar" to "6.67\" AMOLED 120Hz", "RAM" to "12 GB", "Memori" to "256 GB", "Baterai" to "5000 mAh"),
        listOf(Review("Agus", 5, "Performa gaming mantap."), Review("Putri", 4, "Layar halus."))
    ),
    Product(
        4, "Asus Vivobook 14", "Laptop", 7499000, 7999000, "💻", 4.3, 640, 3,
        mapOf("Layar" to "14\" FHD", "RAM" to "16 GB", "Memori" to "512 GB SSD", "Prosesor" to "Ryzen 5"),
        listOf(Review("Fajar", 4, "Cocok untuk kuliah."), Review("Lina", 4, "Ringan dibawa kemana-mana."))
    ),
    Product(
        5, "Lenovo IdeaPad Slim 3", "Laptop", 6899000, 7499000, "💻", 4.2, 520, 2,
        mapOf("Layar" to "15.6\" FHD", "RAM" to "8 GB", "Memori" to "512 GB SSD", "Prosesor" to "Core i5"),
        listOf(Review("Hendra", 4, "Performa stabil."), Review("Maya", 4, "Keyboard nyaman."))
    ),
    Product(
        6, "iPad 10th Gen", "Tablet", 6999000, 7499000, "📲", 4.7, 870, 6,
        mapOf("Layar" to "10.9\" Retina", "RAM" to "4 GB", "Memori" to "64 GB", "Kamera" to "12 MP"),
        listOf(Review("Tari", 5, "Mulus dan awet."), Review("Reza", 5, "Bagus untuk menggambar."))
    ),
    Product(
        7, "Galaxy Tab A9+", "Tablet", 3299000, 3699000, "📲", 4.3, 450, 9,
        mapOf("Layar" to "11\" 90Hz", "RAM" to "4 GB", "Memori" to "64 GB", "Baterai" to "7040 mAh"),
        listOf(Review("Wawan", 4, "Pas untuk nonton dan belajar."), Review("Dewi", 4, "Speaker kencang."))
    ),
    Product(
        8, "Canon EOS M50", "Kamera", 8999000, 9999000, "📷", 4.5, 310, 0, // contoh produk HABIS
        mapOf("Sensor" to "24.1 MP APS-C", "Video" to "4K", "Layar" to "3\" flip", "Lensa" to "15-45 mm"),
        listOf(Review("Yoga", 5, "Hasil foto tajam."), Review("Nia", 4, "Cocok untuk vlog."))
    ),
    Product(
        9, "GoPro Hero 12", "Kamera", 5499000, 5999000, "🎥", 4.6, 280, 4,
        mapOf("Video" to "5.3K 60fps", "Anti air" to "10 m", "Stabilisasi" to "HyperSmooth 6.0", "Baterai" to "1720 mAh"),
        listOf(Review("Ardi", 5, "Stabilisasinya gila."), Review("Salsa", 4, "Tahan banting."))
    ),
    Product(
        10, "PlayStation 5 Slim", "Gaming", 8499000, 8999000, "🎮", 4.8, 1500, 1,
        mapOf("Penyimpanan" to "1 TB SSD", "Resolusi" to "4K 120Hz", "Disc" to "Ya", "Controller" to "DualSense"),
        listOf(Review("Bayu", 5, "Loading game super cepat."), Review("Citra", 5, "Grafisnya memukau."))
    ),
    Product(
        11, "Nintendo Switch OLED", "Gaming", 4799000, 5199000, "🕹️", 4.7, 760, 6,
        mapOf("Layar" to "7\" OLED", "Penyimpanan" to "64 GB", "Mode" to "Handheld & TV", "Baterai" to "4.5-9 jam"),
        listOf(Review("Eko", 5, "Layar OLED cantik."), Review("Gita", 5, "Seru main bareng."))
    ),
    Product(
        12, "Sony WH-1000XM5", "Aksesoris", 4999000, 5699000, "🎧", 4.9, 1900, 8,
        mapOf("Tipe" to "Over-ear", "ANC" to "Ya", "Baterai" to "30 jam", "Koneksi" to "Bluetooth 5.2"),
        listOf(Review("Hana", 5, "Peredam bising terbaik."), Review("Ilham", 5, "Nyaman dipakai lama."))
    ),
    Product(
        13, "JBL Tune Beam", "Aksesoris", 899000, 1199000, "🎵", 4.4, 3200, 25,
        mapOf("Tipe" to "TWS in-ear", "ANC" to "Ya", "Baterai" to "48 jam (case)", "Koneksi" to "Bluetooth 5.3"),
        listOf(Review("Kiki", 4, "Suara jernih, harga bersahabat."), Review("Oscar", 5, "Baterai awet."))
    )
)

fun categoryImage(category: String): Int = when (category) {
    "HP" -> R.drawable.img_phone
    "Laptop" -> R.drawable.img_laptop
    "Tablet" -> R.drawable.img_tablet
    "Kamera" -> R.drawable.img_camera
    "Gaming" -> R.drawable.img_gaming
    "Aksesoris" -> R.drawable.img_audio
    else -> R.drawable.img_phone
}

// Nama file JPG di res/drawable untuk tiap produk (id produk -> nama file tanpa .jpg)
private val photoNames = mapOf(
    1 to "galaxy_a55", 2 to "redmi_note_13", 3 to "poco_x6_pro",
    4 to "asus_vivobook_14", 5 to "lenovo_ideapad_slim_3",
    6 to "ipad_10th_gen", 7 to "galaxy_tab_a9_plus",
    8 to "canon_eos_m50", 9 to "gopro_hero_12",
    10 to "ps5_slim", 11 to "switch_oled",
    12 to "sony_wh1000xm5", 13 to "jbl_tune_beam"
)

/** Tampilkan foto JPG produk; kalau file-nya belum ada di drawable, pakai gambar kategori. */
@SuppressLint("DiscouragedApi")
@Composable
fun ProductPhoto(product: Product, modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val resources = LocalResources.current
    val res = remember(product.id) {
        val id = resources.getIdentifier(photoNames[product.id] ?: "", "drawable", ctx.packageName)
        if (id != 0) id else categoryImage(product.category)
    }
    Image(
        painter = painterResource(res),
        contentDescription = product.name,
        contentScale = ContentScale.Fit,
        modifier = modifier
    )
}

// Nama file logo PNG di res/drawable (tanpa .png). Ubah jika nama file logo kamu berbeda.
private const val LOGO_NAME = "logo_voltix"

@SuppressLint("DiscouragedApi")
@Composable
fun AppLogo(size: androidx.compose.ui.unit.Dp) {
    val ctx = LocalContext.current
    val resources = LocalResources.current
    val res = remember {
        listOf(LOGO_NAME, "voltix_logo", "logo", "voltix", "ic_voltix")
            .map { resources.getIdentifier(it, "drawable", ctx.packageName) }
            .firstOrNull { it != 0 } ?: R.drawable.ic_logo
    }
    Image(painter = painterResource(res), contentDescription = "Logo Voltix", modifier = Modifier.size(size))
}

@Composable
fun LogoImage(size: androidx.compose.ui.unit.Dp) {
    Image(
        painter = painterResource(R.drawable.ic_logo),
        contentDescription = "Logo Voltix",
        modifier = Modifier.size(size)
    )
}

fun rupiah(value: Int): String = "Rp " + String.format(Locale.US, "%,d", value).replace(',', '.')

fun terjual(n: Int): String = if (n >= 1000) "${n / 1000},${(n % 1000) / 100}rb" else "$n"

fun statusColor(status: String): Color = when (status) {
    "Menunggu Pembayaran" -> WarningOrange
    "Dikemas" -> Color(0xFF2F6BFF)
    "Dikirim" -> Color(0xFF8E44AD)
    "Selesai" -> SuccessGreen
    else -> Color(0xFF9AA0A6)
}

// ======================================================
//  NAVIGASI
// ======================================================
enum class NavTab(val label: String, val emoji: String) {
    Home("Beranda", "🏠"),
    Catalog("Katalog", "🛍️"),
    Orders("Pesanan", "📦"),
    Profile("Profil", "👤")
}

sealed class Screen {
    data object Compare : Screen()
    data object Cart : Screen()
    data object Checkout : Screen()
    data class Payment(val orderId: String) : Screen()
    data class Success(val orderId: String) : Screen()
    data object Favorites : Screen()
}

// Agar posisi layar tidak hilang saat rotasi (Activity di-recreate)
private val ScreenSaver = Saver<Screen?, String>(
    save = { s ->
        when (s) {
            is Screen.Payment -> "payment:${s.orderId}"
            is Screen.Success -> "success:${s.orderId}"
            Screen.Compare -> "compare"
            Screen.Cart -> "cart"
            Screen.Checkout -> "checkout"
            Screen.Favorites -> "favorites"
            null -> ""
        }
    },
    restore = { v ->
        when {
            v.startsWith("payment:") -> Screen.Payment(v.removePrefix("payment:"))
            v.startsWith("success:") -> Screen.Success(v.removePrefix("success:"))
            v == "compare" -> Screen.Compare
            v == "cart" -> Screen.Cart
            v == "checkout" -> Screen.Checkout
            v == "favorites" -> Screen.Favorites
            else -> null
        }
    }
)

// ======================================================
//  ACTIVITY
// ======================================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VoltixTheme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    val context = LocalContext.current
    val db = remember { VoltixDb.get(context) } // database lokal SQLite
    var currentUser by rememberSaveable { mutableStateOf<String?>(null) }
    var showSplash by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(1500.milliseconds)
        showSplash = false
    }

    val user = currentUser
    when {
        showSplash -> SplashScreen()
        user == null -> AuthScreen(db = db, onLoggedIn = { currentUser = it })
        else -> VoltixApp(email = user, onLogout = { currentUser = null })
    }
}

// ======================================================
//  SPLASH
// ======================================================
@Composable
fun SplashScreen() {
    Box(
        Modifier.fillMaxSize().background(BrandBrush),
        contentAlignment = Alignment.Center
    ) {
        Box(Modifier.size(320.dp).offset(x = 120.dp, y = (-230).dp).clip(CircleShape).background(Color.White.copy(alpha = 0.07f)))
        Box(Modifier.size(240.dp).offset(x = (-140).dp, y = 270.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.06f)))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier.size(104.dp).clip(RoundedCornerShape(34.dp)).background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) { LogoImage(64.dp) }
            Spacer(Modifier.height(20.dp))
            Text("Voltix", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp)
            Spacer(Modifier.height(4.dp))
            Text("Gadget impian, harga pintar", color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
        }
    }
}

// ======================================================
//  LOGIN / DAFTAR (SIMULASI LOKAL)
// ======================================================
@Composable
fun AuthScreen(
    db: VoltixDb,
    onLoggedIn: (String) -> Unit
) {
    val context = LocalContext.current
    val cs = MaterialTheme.colorScheme
    var isRegister by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize().background(cs.background).verticalScroll(rememberScrollState())
    ) {
        // Header: logo saja, tanpa warna latar ungu
        Box(
            Modifier.fillMaxWidth().padding(top = 64.dp, bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            AppLogo(130.dp)
        }

        Column(Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            Text(
                if (isRegister) "Buat akun baru" else "Selamat Datang",
                fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.6).sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                if (isRegister) "Daftar terlebih dahulu untuk memulai belanja" else "Masuk untuk melanjutkan belanja",
                color = cs.onSurfaceVariant, fontSize = 14.sp
            )
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; error = "" },
                label = { Text("Email") },
                leadingIcon = { Text("✉️") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = fieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; error = "" },
                label = { Text("Password") },
                leadingIcon = { Text("🔒") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = fieldColors(),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { showPassword = !showPassword }) {
                        Text(if (showPassword) "Sembunyi" else "Lihat", fontSize = 12.sp)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            if (isRegister) {
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = confirm,
                    onValueChange = { confirm = it; error = "" },
                    label = { Text("Konfirmasi password") },
                    leadingIcon = { Text("🔒") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = fieldColors(),
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (error.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Text("⚠️ $error", color = cs.error, fontSize = 13.sp)
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    val mail = email.trim().lowercase()
                    if (mail.isEmpty() || password.isEmpty()) {
                        error = "Email dan password wajib diisi"
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                        error = "Format email tidak valid"
                    } else if (isRegister) {
                        if (password.length < 6) {
                            error = "Password minimal 6 karakter"
                        } else if (password != confirm) {
                            error = "Konfirmasi password tidak sama"
                        } else if (db.userExists(mail)) {
                            error = "Email sudah terdaftar"
                        } else {
                            db.addUser(mail, password)
                            Toast.makeText(context, "Registrasi berhasil, silakan masuk", Toast.LENGTH_SHORT).show()
                            isRegister = false
                            password = ""
                            confirm = ""
                        }
                    } else {
                        if (db.checkLogin(mail, password)) {
                            onLoggedIn(mail)
                        } else {
                            error = "Email atau password salah"
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(54.dp)
            ) { Text(if (isRegister) "Daftar" else "Masuk", fontWeight = FontWeight.Bold, fontSize = 16.sp) }

            Spacer(Modifier.height(4.dp))
            TextButton(
                onClick = {
                    isRegister = !isRegister
                    error = ""
                    password = ""
                    confirm = ""
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(if (isRegister) "Sudah punya akun? Masuk" else "Belum punya akun? Daftar")
            }

            if (!isRegister) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Akun demo: admin@voltix.com / admin123",
                    fontSize = 12.sp,
                    color = cs.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ======================================================
//  KOMPONEN UMUM
// ======================================================
// ======================================================
//  DATA PENGIRIMAN (nama | no. HP | alamat) — disimpan sebagai satu teks, jadi database tidak berubah
// ======================================================
data class ShipInfo(val name: String, val phone: String, val address: String)

fun encodeShip(name: String, phone: String, address: String): String =
    "${name.trim()}|${phone.trim()}|${address.trim()}"

fun parseShip(raw: String): ShipInfo {
    val parts = raw.split("|", limit = 3)
    // Data lama (satu kolom alamat saja) dianggap sebagai alamat
    return if (parts.size == 3) ShipInfo(parts[0].trim(), parts[1].trim(), parts[2].trim())
    else ShipInfo("", "", raw.trim())
}

fun shipError(raw: String): String? {
    val s = parseShip(raw)
    val digits = s.phone.count { it.isDigit() }
    return when {
        s.name.isBlank() -> "Nama penerima wajib diisi"
        digits !in 9..15 -> "No. HP tidak valid (9-15 digit)"
        s.address.length < 10 -> "Alamat wajib diisi lengkap (minimal 10 karakter)"
        else -> null
    }
}

fun shipDisplay(raw: String): String {
    val s = parseShip(raw)
    return if (s.name.isBlank() && s.phone.isBlank()) s.address else "${s.name} • ${s.phone}\n${s.address}"
}

/** Tiga kolom: nama, no. HP, alamat pengiriman. `onChange` menerima teks gabungan siap simpan. */
@Composable
fun ShippingFields(value: String, onChange: (String) -> Unit) {
    val init = remember { parseShip(value) }
    var name by rememberSaveable { mutableStateOf(init.name) }
    var phone by rememberSaveable { mutableStateOf(init.phone) }
    var addr by rememberSaveable { mutableStateOf(init.address) }
    fun push() = onChange(encodeShip(name, phone, addr))

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it.replace("|", ""); push() },
            label = { Text("Nama penerima") },
            leadingIcon = { Text("👤") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            shape = RoundedCornerShape(16.dp),
            colors = fieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { v ->
                phone = v.filter { it.isDigit() || it == '+' || it == ' ' || it == '-' }.take(18); push()
            },
            label = { Text("No. HP") },
            leadingIcon = { Text("📞") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            shape = RoundedCornerShape(16.dp),
            colors = fieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = addr,
            onValueChange = { addr = it.replace("|", ""); push() },
            label = { Text("Alamat pengiriman") },
            placeholder = { Text("Jalan, nomor rumah, kelurahan, kota, kode pos") },
            minLines = 3,
            shape = RoundedCornerShape(16.dp),
            colors = fieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun Line() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)))
}

@Composable
fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline
)

@Composable
fun Chip(label: String, selected: Boolean, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val bg by animateColorAsState(if (selected) cs.primary else cs.surface, label = "chipBg")
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .border(1.dp, if (selected) cs.primary else cs.outline, RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        Text(
            label,
            color = if (selected) cs.onPrimary else cs.onSurface,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/** Chip untuk baris urutan: lebarnya dibagi rata, jadi tidak perlu digeser. */
@Composable
fun SortChip(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val bg by animateColorAsState(if (selected) cs.primary else cs.surface, label = "sortChipBg")
    Box(
        modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .border(1.dp, if (selected) cs.primary else cs.outline, RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color = if (selected) cs.onPrimary else cs.onSurface,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RoundIconButton(label: String, container: Color, badge: Int = 0, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Box(Modifier.size(46.dp)) {
        Box(
            Modifier.fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(container)
                .border(1.dp, cs.outline, RoundedCornerShape(16.dp))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) { Text(label, fontSize = 18.sp) }
        if (badge > 0) {
            Box(
                Modifier.align(Alignment.TopEnd).offset(x = 5.dp, y = (-5).dp).size(20.dp)
                    .clip(CircleShape).background(cs.secondary)
                    .border(2.dp, cs.background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (badge > 9) "9+" else "$badge",
                    color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AppBar(title: String, onBack: () -> Unit, trailing: @Composable RowScope.() -> Unit = {}) {
    val cs = MaterialTheme.colorScheme
    Row(
        Modifier.fillMaxWidth().background(cs.background).padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(cs.surface)
                .border(1.dp, cs.outline, RoundedCornerShape(14.dp)).clickable { onBack() },
            contentAlignment = Alignment.Center
        ) { Text("←", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.width(12.dp))
        Text(
            title, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.3).sp,
            modifier = Modifier.weight(1f)
        )
        trailing()
    }
}

@Composable
fun SectionHeader(title: String, action: String? = null, onAction: () -> Unit = {}) {
    val cs = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            title, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.3).sp,
            modifier = Modifier.weight(1f)
        )
        if (action != null) {
            Text(
                action, color = cs.primary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { onAction() }.padding(6.dp)
            )
        }
    }
}

@Composable
fun FloatingNavBar(selected: NavTab, onSelect: (NavTab) -> Unit) {
    val cs = MaterialTheme.colorScheme
    Box(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 20.dp, vertical = 10.dp)) {
        Surface(
            shape = RoundedCornerShape(30.dp),
            color = cs.surface,
            shadowElevation = 12.dp,
            modifier = Modifier.fillMaxWidth().border(1.dp, cs.outline, RoundedCornerShape(30.dp))
        ) {
            Row(
                Modifier.padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavTab.entries.forEach { t ->
                    val sel = t == selected
                    val bg by animateColorAsState(if (sel) cs.primary else Color.Transparent, label = "navBg")
                    Row(
                        Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(bg)
                            .clickable { onSelect(t) }
                            .padding(horizontal = if (sel) 18.dp else 14.dp, vertical = 11.dp)
                            .animateContentSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(t.emoji, fontSize = 18.sp)
                        if (sel) {
                            Spacer(Modifier.width(8.dp))
                            Text(t.label, color = cs.onPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionCard(title: String? = null, content: @Composable ColumnScope.() -> Unit) {
    val cs = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        border = BorderStroke(1.dp, cs.outline.copy(alpha = 0.7f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (title != null) Text(title, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.2).sp)
            content()
        }
    }
}

@Composable
fun PriceRow(label: String, value: String, bold: Boolean = false) {
    val cs = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth()) {
        Text(
            label, modifier = Modifier.weight(1f),
            color = if (bold) cs.onSurface else cs.onSurfaceVariant,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            value,
            color = if (bold) cs.primary else cs.onSurface,
            fontWeight = if (bold) FontWeight.ExtraBold else FontWeight.Medium,
            fontSize = if (bold) 17.sp else 14.sp
        )
    }
}

@Composable
fun EmptyState(emoji: String, title: String, subtitle: String, actionLabel: String? = null, onAction: () -> Unit = {}) {
    val cs = MaterialTheme.colorScheme
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier.size(120.dp).clip(CircleShape).background(cs.primaryContainer),
            contentAlignment = Alignment.Center
        ) { Text(emoji, fontSize = 52.sp) }
        Spacer(Modifier.height(20.dp))
        Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, letterSpacing = (-0.3).sp)
        Spacer(Modifier.height(4.dp))
        Text(subtitle, color = cs.onSurfaceVariant, textAlign = TextAlign.Center, fontSize = 14.sp)
        if (actionLabel != null) {
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onAction,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.height(50.dp)
            ) { Text(actionLabel, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun StepBar(current: Int) {
    val cs = MaterialTheme.colorScheme
    val labels = listOf("Keranjang", "Checkout", "Bayar")
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        labels.forEachIndexed { i, label ->
            val active = i + 1 <= current
            Box(
                Modifier.size(24.dp).clip(CircleShape).background(if (active) cs.primary else cs.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "${i + 1}", fontSize = 12.sp, fontWeight = FontWeight.Bold,
                    color = if (active) cs.onPrimary else cs.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(4.dp))
            Text(
                label, fontSize = 12.sp,
                fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                color = if (active) cs.onSurface else cs.onSurfaceVariant
            )
            if (i < labels.lastIndex) {
                Box(
                    Modifier.weight(1f).padding(horizontal = 6.dp).height(2.dp)
                        .background(if (i + 1 < current) cs.primary else cs.outline)
                )
            }
        }
    }
}

@Composable
fun PaymentMethodPicker(selected: String, onSelect: (String) -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        paymentMethods.forEach { m ->
            val sel = m == selected
            Row(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (sel) cs.primaryContainer else cs.surface)
                    .border(1.5.dp, if (sel) cs.primary else cs.outline, RoundedCornerShape(14.dp))
                    .clickable { onSelect(m) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(methodEmoji(m), fontSize = 22.sp)
                Spacer(Modifier.width(12.dp))
                Text(m, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                RadioButton(selected = sel, onClick = { onSelect(m) })
            }
        }
    }
}

@Composable
fun StatusPill(status: String) {
    val c = statusColor(status)
    Text(
        status,
        color = c,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.clip(RoundedCornerShape(50)).background(c.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
fun OrderProgress(status: String) {
    val steps = listOf("Dikemas", "Dikirim", "Selesai")
    val idx = steps.indexOf(status)
    if (idx < 0) return
    val cs = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        steps.forEachIndexed { i, label ->
            val done = i <= idx
            Box(Modifier.size(10.dp).clip(CircleShape).background(if (done) SuccessGreen else cs.outline))
            Spacer(Modifier.width(4.dp))
            Text(label, fontSize = 11.sp, color = if (done) cs.onSurface else cs.onSurfaceVariant)
            if (i < steps.lastIndex) {
                Box(
                    Modifier.weight(1f).padding(horizontal = 6.dp).height(2.dp)
                        .background(if (i < idx) SuccessGreen else cs.outline)
                )
            }
        }
    }
}

// ======================================================
//  KARTU PRODUK
// ======================================================
@Composable
fun ProductGridCard(
    product: Product,
    stock: Int,
    isFav: Boolean,
    onFav: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        border = BorderStroke(1.dp, cs.outline.copy(alpha = 0.7f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(8.dp)) {
            Box(
                Modifier.fillMaxWidth().height(130.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.verticalGradient(listOf(cs.primaryContainer.copy(alpha = 0.55f), cs.surfaceVariant))),
                contentAlignment = Alignment.Center
            ) {
                ProductPhoto(product, Modifier.size(96.dp))
                if (stock <= 0) {
                    Box(
                        Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "HABIS", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
                if (product.discount > 0) {
                    Text(
                        "-${product.discount}%",
                        color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
                            .clip(RoundedCornerShape(50)).background(cs.secondary)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Box(
                    Modifier.align(Alignment.TopEnd).padding(8.dp).size(32.dp).clip(CircleShape)
                        .background(cs.surface.copy(alpha = 0.92f)).clickable { onFav() },
                    contentAlignment = Alignment.Center
                ) { Text(if (isFav) "❤️" else "🤍", fontSize = 14.sp) }
            }
            Column(Modifier.padding(start = 6.dp, end = 6.dp, top = 10.dp, bottom = 6.dp)) {
                Text(
                    product.category.uppercase(), fontSize = 10.sp, color = cs.onSurfaceVariant,
                    fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp
                )
                Text(
                    product.name, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    maxLines = 2, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.heightIn(min = 40.dp)
                )
                Spacer(Modifier.height(4.dp))
                Text(rupiah(product.price), color = cs.primary, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                if (product.discount > 0) {
                    Text(
                        rupiah(product.oldPrice), fontSize = 11.sp, color = cs.onSurfaceVariant,
                        textDecoration = TextDecoration.LineThrough
                    )
                } else {
                    Spacer(Modifier.height(15.dp))
                }
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("★", fontSize = 12.sp, color = StarYellow)
                    Text(" ${product.rating}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("  ·  ${terjual(product.sold)} terjual", fontSize = 11.sp, color = cs.onSurfaceVariant)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    when {
                        stock <= 0 -> "Stok habis"
                        stock <= 3 -> "Sisa $stock!"
                        else -> "Stok $stock"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        stock <= 0 -> cs.error
                        stock <= 3 -> WarningOrange
                        else -> cs.onSurfaceVariant
                    }
                )
            }
        }
    }
}

@Composable
fun ProductGrid(
    list: List<Product>,
    stocks: Map<Int, Int>,
    favorites: List<Int>,
    onFav: (Int) -> Unit,
    onProduct: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        list.chunked(2).forEach { row ->
            Row(
                Modifier.height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { p ->
                    ProductGridCard(
                        product = p,
                        stock = stocks[p.id] ?: 0,
                        isFav = p.id in favorites,
                        onFav = { onFav(p.id) },
                        onClick = { onProduct(p.id) },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

// ======================================================
//  APLIKASI UTAMA
// ======================================================
@Composable
fun VoltixApp(email: String, onLogout: () -> Unit) {
    val userName = remember(email) { email.substringBefore("@").replaceFirstChar { it.uppercase() } }
    var tab by rememberSaveable { mutableStateOf(NavTab.Home) }
    var screen: Screen? by rememberSaveable(stateSaver = ScreenSaver) { mutableStateOf(null) }
    var query by rememberSaveable { mutableStateOf("") }
    var catFilter by rememberSaveable { mutableStateOf<String?>(null) }
    var sort by rememberSaveable { mutableIntStateOf(0) }
    var savedAddress by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current
    val db = remember { VoltixDb.get(context) }
    // Foto profil: satu state dipakai bersama oleh Beranda dan Profil
    var avatar by remember(email) { mutableStateOf(loadAvatar(avatarFileFor(context, email))) }
    // Semua data penting dibaca dari database, jadi aman saat rotasi & aplikasi dibuka ulang
    val compareIds: SnapshotStateList<Int> = rememberSaveable(
        saver = listSaver(save = { it.toList() }, restore = { it.toMutableStateList() })
    ) { mutableStateListOf() }
    val favorites = remember { db.favorites().toMutableStateList() }
    val cart = remember { mutableStateMapOf<Int, Int>().apply { putAll(db.loadCart()) } }
    val orders = remember { db.loadOrders().toMutableStateList() }
    val stocks = remember { mutableStateMapOf<Int, Int>().apply { putAll(db.loadStock()) } }

    // Simpan otomatis ke database setiap kali state berubah
    LaunchedEffect(Unit) { snapshotFlow { cart.toMap() }.collect { db.replaceCart(it) } }
    LaunchedEffect(Unit) { snapshotFlow { favorites.toList() }.collect { db.replaceFavorites(it) } }
    LaunchedEffect(Unit) { snapshotFlow { stocks.toMap() }.collect { db.replaceStock(it) } }
    val cs = MaterialTheme.colorScheme

    fun toast(msg: String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    fun nextOrderId(): String {
        val last = orders.maxOfOrNull { it.id.removePrefix("VLX").toIntOrNull() ?: 1000 } ?: 1000
        return "VLX${last + 1}"
    }

    fun toggleFav(id: Int) {
        if (id in favorites) favorites.remove(id) else favorites.add(id)
    }

    fun goBack() {
        when (screen) {
            is Screen.Checkout -> screen = Screen.Cart
            is Screen.Success -> { screen = null; tab = NavTab.Home }
            is Screen.Payment -> { screen = null; tab = NavTab.Orders }
            null -> tab = NavTab.Home
            else -> screen = null
        }
    }

    // BELI SEKARANG: langsung ke pembayaran tanpa keranjang/checkout
    fun buyNow(p: Product) {
        if ((stocks[p.id] ?: 0) <= 0) {
            toast("Stok ${p.name} habis")
            return
        }
        val order = Order(
            id = nextOrderId(),
            items = listOf(OrderItem(p.id, p.name, p.emoji, p.price, 1)),
            total = p.price + ONGKIR,
            address = savedAddress,
            method = paymentMethods[0],
            direct = true
        )
        orders.add(order); db.insertOrder(order)
        screen = Screen.Payment(order.id)
    }

    // Proses pembayaran (simulasi): cek stok -> kurangi stok -> status Dikemas
    fun pay(order: Order) {
        val lacking = order.items.firstOrNull { (stocks[it.productId] ?: 0) < it.qty }
        if (lacking != null) {
            toast("Maaf, stok ${lacking.name} tidak cukup")
            return
        }
        order.items.forEach { stocks[it.productId] = (stocks[it.productId] ?: 0) - it.qty }
        order.status = "Dikemas"
        db.updateOrder(order)                                     // UPDATE
        screen = Screen.Success(order.id)
    }

    // Explicit Intent: buka DetailActivity + kirim data, lalu terima hasilnya kembali
    val detailLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        cart.clear(); cart.putAll(db.loadCart())
        favorites.clear(); favorites.addAll(db.favorites())
        val data = result.data
        data?.getIntArrayExtra(DetailActivity.EXTRA_COMPARE)?.let { ids ->
            compareIds.clear(); compareIds.addAll(ids.toList())
        }
        val buyId = data?.getIntExtra(DetailActivity.EXTRA_BUY_NOW, -1) ?: -1
        if (buyId != -1) products.firstOrNull { it.id == buyId }?.let { buyNow(it) }
    }

    fun openDetail(id: Int) {
        val intent = Intent(context, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_PRODUCT_ID, id)
            putExtra(DetailActivity.EXTRA_STOCK, stocks[id] ?: 0)
            putExtra(DetailActivity.EXTRA_COMPARE, compareIds.toIntArray())
            putExtra(DetailActivity.EXTRA_USER, userName)
        }
        detailLauncher.launch(intent)
    }

    BackHandler(enabled = screen != null || tab != NavTab.Home) { goBack() }

    val cartItems: List<Pair<Product, Int>> = cart.entries.mapNotNull { (id, q) ->
        products.firstOrNull { it.id == id }?.let { p -> p to q }
    }
    val cartCount = cart.values.sum()

    Scaffold(
        containerColor = cs.background,
        bottomBar = {
            if (screen == null) {
                FloatingNavBar(selected = tab, onSelect = { tab = it })
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            val s = screen
            if (s == null) {
                // ----- HALAMAN TAB -----
                when (tab) {
                    NavTab.Home -> HomeScreen(
                        userName = userName,
                        avatar = avatar,
                        cartCount = cartCount,
                        stocks = stocks,
                        favorites = favorites,
                        onSearch = { tab = NavTab.Catalog },
                        onCart = { screen = Screen.Cart },
                        onCategory = { catFilter = it; query = ""; tab = NavTab.Catalog },
                        onProduct = { openDetail(it) },
                        onFav = { toggleFav(it) }
                    )

                    NavTab.Catalog -> CatalogScreen(
                        query = query,
                        onQuery = { query = it },
                        category = catFilter,
                        onCategory = { catFilter = it },
                        sort = sort,
                        onSort = { sort = it },
                        cartCount = cartCount,
                        onCart = { screen = Screen.Cart },
                        stocks = stocks,
                        favorites = favorites,
                        onFav = { toggleFav(it) },
                        onProduct = { openDetail(it) }
                    )

                    NavTab.Orders -> OrdersScreen(
                        orders = orders,
                        onPay = { screen = Screen.Payment(it.id) },
                        onDelete = { o -> orders.remove(o); db.deleteOrder(o.id); toast("Pesanan dihapus") }, // DELETE
                        onShop = { tab = NavTab.Home }
                    )

                    NavTab.Profile -> ProfileScreen(
                        userName = userName,
                        email = email,
                        avatar = avatar,
                        onAvatarChange = { avatar = it },
                        ordersCount = orders.size,
                        favCount = favorites.size,
                        cartCount = cartCount,
                        savedAddress = savedAddress,
                        onSaveAddress = { savedAddress = it; toast("Alamat disimpan") },
                        onFavorites = { screen = Screen.Favorites },
                        onOrders = { tab = NavTab.Orders },
                        onLogout = onLogout
                    )
                }

                if (tab != NavTab.Profile && compareIds.isNotEmpty()) {
                    CompareTray(
                        count = compareIds.size,
                        onOpen = { screen = Screen.Compare },
                        onClear = { compareIds.clear() },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            } else {
                // ----- HALAMAN DALAM (punya tombol kembali) -----
                val title = when (s) {
                    is Screen.Compare -> "Bandingkan Produk"
                    is Screen.Cart -> "Keranjang"
                    is Screen.Checkout -> "Checkout"
                    is Screen.Payment -> "Pembayaran"
                    is Screen.Success -> "Pembayaran"
                    is Screen.Favorites -> "Produk Favorit"
                }
                Column(Modifier.fillMaxSize()) {
                    AppBar(title = title, onBack = { goBack() })
                    Box(Modifier.weight(1f).fillMaxWidth()) {
                        when (s) {
                            is Screen.Compare -> CompareScreen(
                                items = products.filter { it.id in compareIds },
                                onClear = { compareIds.clear(); screen = null }
                            )

                            is Screen.Cart -> CartScreen(
                                items = cartItems,
                                stocks = stocks,
                                onInc = { p ->
                                    val have = cart[p.id] ?: 0
                                    if (have < (stocks[p.id] ?: 0)) cart[p.id] = have + 1
                                    else toast("Stok ${p.name} hanya ${stocks[p.id] ?: 0}")
                                },
                                onDec = { p ->
                                    val have = cart[p.id] ?: 0
                                    if (have <= 1) cart.remove(p.id) else cart[p.id] = have - 1
                                },
                                onRemove = { p -> cart.remove(p.id) },
                                onCheckout = { screen = Screen.Checkout },
                                onShop = { screen = null; tab = NavTab.Home }
                            )

                            is Screen.Checkout -> CheckoutScreen(
                                items = cartItems,
                                initialAddress = savedAddress,
                                onSubmit = { address, method ->
                                    val orderItems = cartItems.map { (p, q) ->
                                        OrderItem(p.id, p.name, p.emoji, p.price, q)
                                    }
                                    val subtotal = orderItems.sumOf { it.price * it.qty }
                                    val order = Order(
                                        id = nextOrderId(),
                                        items = orderItems,
                                        total = subtotal + ONGKIR,
                                        address = address,
                                        method = method,
                                        direct = false
                                    )
                                    orders.add(order); db.insertOrder(order)
                                    cart.clear(); db.clearCart()
                                    if (savedAddress.isBlank()) savedAddress = address
                                    screen = Screen.Payment(order.id)
                                }
                            )

                            is Screen.Payment -> {
                                val order = orders.firstOrNull { it.id == s.orderId }
                                if (order == null) {
                                    EmptyState("🤔", "Pesanan tidak ditemukan", "Coba cek lagi di halaman Pesanan")
                                } else {
                                    PaymentScreen(
                                        order = order,
                                        onPay = { address, method ->
                                            order.address = address
                                            order.method = method
                                            if (savedAddress.isBlank()) savedAddress = address
                                            pay(order)
                                        },
                                        onLater = { screen = null; tab = NavTab.Orders }
                                    )
                                }
                            }

                            is Screen.Success -> {
                                val order = orders.firstOrNull { it.id == s.orderId }
                                if (order != null) {
                                    SuccessScreen(
                                        order = order,
                                        onOrders = { screen = null; tab = NavTab.Orders },
                                        onHome = { screen = null; tab = NavTab.Home }
                                    )
                                }
                            }

                            is Screen.Favorites -> FavoritesScreen(
                                list = products.filter { it.id in favorites },
                                stocks = stocks,
                                favorites = favorites,
                                onFav = { toggleFav(it) },
                                onProduct = { openDetail(it) },
                                onShop = { screen = null; tab = NavTab.Catalog }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ======================================================
//  TRAY PERBANDINGAN
// ======================================================
@Composable
fun CompareTray(count: Int, onOpen: () -> Unit, onClear: () -> Unit, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    Surface(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 12.dp).fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = cs.inverseSurface,
        shadowElevation = 10.dp
    ) {
        Row(
            Modifier.padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (count == 2) "⚖️ Siap dibandingkan (2/2)" else "⚖️ Pilih 1 produk lagi ($count/2)",
                color = cs.inverseOnSurface, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onClear) { Text("Reset", color = cs.inverseOnSurface.copy(alpha = 0.7f)) }
            Button(
                onClick = onOpen,
                enabled = count == 2,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = cs.primary, contentColor = cs.onPrimary)
            ) { Text("Lihat", fontWeight = FontWeight.Bold) }
        }
    }
}

// ======================================================
//  BERANDA
// ======================================================
@Composable
fun HomeScreen(
    userName: String,
    avatar: ImageBitmap?,
    cartCount: Int,
    stocks: Map<Int, Int>,
    favorites: List<Int>,
    onSearch: () -> Unit,
    onCart: () -> Unit,
    onCategory: (String) -> Unit,
    onProduct: (Int) -> Unit,
    onFav: (Int) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        // Header
        Row(
            Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(48.dp).clip(CircleShape).background(BrandBrush),
                contentAlignment = Alignment.Center
            ) {
                if (avatar != null) {
                    Image(
                        bitmap = avatar,
                        contentDescription = "Foto profil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(userName.take(1).uppercase(), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Selamat datang 👋", color = cs.onSurfaceVariant, fontSize = 12.sp)
                Text(
                    userName, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.3).sp,
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
            RoundIconButton("🛒", cs.surface, cartCount, onCart)
        }

        Spacer(Modifier.height(16.dp))

        // Search
        Row(
            Modifier.padding(horizontal = 20.dp).fillMaxWidth().height(54.dp)
                .clip(RoundedCornerShape(18.dp)).background(cs.surface)
                .border(1.dp, cs.outline, RoundedCornerShape(18.dp))
                .clickable { onSearch() }.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🔍")
            Spacer(Modifier.width(10.dp))
            Text("Cari HP, laptop, kamera...", color = cs.onSurfaceVariant, fontSize = 14.sp, modifier = Modifier.weight(1f))
            Box(
                Modifier.size(34.dp).clip(RoundedCornerShape(11.dp)).background(cs.primaryContainer),
                contentAlignment = Alignment.Center
            ) { Text("⚡", fontSize = 14.sp) }
        }

        Spacer(Modifier.height(20.dp))

        // Promo
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(promos) { p ->
                Box(
                    Modifier.width(300.dp).height(156.dp).clip(RoundedCornerShape(28.dp)).background(p.brush)
                ) {
                    Box(Modifier.size(170.dp).offset(x = 200.dp, y = (-50).dp).clip(CircleShape).background(Color.White.copy(alpha = 0.12f)))
                    Box(Modifier.size(100.dp).offset(x = (-24).dp, y = 110.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.10f)))
                    Column(Modifier.align(Alignment.CenterStart).padding(start = 20.dp).fillMaxWidth(0.66f)) {
                        Text(
                            p.tag, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.clip(RoundedCornerShape(50))
                                .background(Color.White.copy(alpha = 0.25f))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            p.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold,
                            lineHeight = 26.sp, letterSpacing = (-0.4).sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(p.sub, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                    }
                    Text(p.emoji, fontSize = 64.sp, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 18.dp))
                }
            }
        }

        // Kategori
        Spacer(Modifier.height(26.dp))
        SectionHeader("Kategori")
        Spacer(Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(categories) { c ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clip(RoundedCornerShape(20.dp)).clickable { onCategory(c.key) }
                ) {
                    Box(
                        Modifier.size(64.dp).clip(RoundedCornerShape(22.dp))
                            .background(cs.surface).border(1.dp, cs.outline, RoundedCornerShape(22.dp)),
                        contentAlignment = Alignment.Center
                    ) { Text(c.emoji, fontSize = 28.sp) }
                    Spacer(Modifier.height(6.dp))
                    Text(c.label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Terlaris
        Spacer(Modifier.height(26.dp))
        SectionHeader("🔥 Terlaris", "Lihat semua", onSearch)
        Spacer(Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products.sortedByDescending { it.sold }.take(6)) { p ->
                ProductGridCard(
                    product = p,
                    stock = stocks[p.id] ?: 0,
                    isFav = p.id in favorites,
                    onFav = { onFav(p.id) },
                    onClick = { onProduct(p.id) },
                    modifier = Modifier.width(170.dp)
                )
            }
        }

        // Rekomendasi
        Spacer(Modifier.height(26.dp))
        SectionHeader("✨ Rekomendasi untukmu")
        Spacer(Modifier.height(12.dp))
        Box(Modifier.padding(horizontal = 20.dp)) {
            ProductGrid(
                list = products.sortedByDescending { it.rating }.take(6),
                stocks = stocks,
                favorites = favorites,
                onFav = onFav,
                onProduct = onProduct
            )
        }
        Spacer(Modifier.height(90.dp))
    }
}

// ======================================================
//  KATALOG (cari, filter, urutkan)
// ======================================================
@Composable
fun CatalogScreen(
    query: String,
    onQuery: (String) -> Unit,
    category: String?,
    onCategory: (String?) -> Unit,
    sort: Int,
    onSort: (Int) -> Unit,
    cartCount: Int,
    onCart: () -> Unit,
    stocks: Map<Int, Int>,
    favorites: List<Int>,
    onFav: (Int) -> Unit,
    onProduct: (Int) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val sortLabels = listOf("Terlaris", "Termurah", "Termahal", "Rating")

    val filtered = products
        .filter {
            (category == null || it.category == category) &&
                    (query.isBlank() || it.name.contains(query.trim(), ignoreCase = true))
        }
        .let { list ->
            when (sort) {
                0 -> list.sortedByDescending { it.sold }
                1 -> list.sortedBy { it.price }
                2 -> list.sortedByDescending { it.price }
                else -> list.sortedByDescending { it.rating }
            }
        }

    Column(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 10.dp)) {
            Row(Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Katalog Produk", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.6).sp, modifier = Modifier.weight(1f))
                RoundIconButton("🛒", cs.surface, cartCount, onCart)
            }
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = query,
                onValueChange = onQuery,
                placeholder = { Text("Cari produk...") },
                leadingIcon = { Text("🔍") },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        TextButton(onClick = { onQuery("") }) { Text("✕") }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = fieldColors(),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(10.dp))
            Row(
                Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Chip("Semua", category == null) { onCategory(null) }
                categories.forEach { c ->
                    Chip("${c.emoji} ${c.label}", category == c.key) { onCategory(c.key) }
                }
            }
        }

        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(vertical = 8.dp)) {
            Text("${filtered.size} produk ditemukan", color = cs.onSurfaceVariant, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sortLabels.forEachIndexed { i, label ->
                    SortChip(label, sort == i, Modifier.weight(1f)) { onSort(i) }
                }
            }
            Spacer(Modifier.height(14.dp))
            if (filtered.isEmpty()) {
                Column(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🔎", fontSize = 56.sp)
                    Text("Produk tidak ditemukan", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("Coba kata kunci atau kategori lain", color = cs.onSurfaceVariant, fontSize = 13.sp)
                }
            } else {
                Box(Modifier.padding(horizontal = 20.dp)) {
                    ProductGrid(filtered, stocks, favorites, onFav, onProduct)
                }
            }
            Spacer(Modifier.height(90.dp))
        }
    }
}

// ======================================================
//  DETAIL PRODUK (+ BELI SEKARANG)
// ======================================================
@Composable
fun DetailScreen(
    product: Product,
    stock: Int,
    inCompare: Boolean,
    reviews: List<Review>,
    onToggleCompare: () -> Unit,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit,
    onSubmitReview: (Int, String) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    var stars by rememberSaveable { mutableIntStateOf(5) }
    var reviewText by rememberSaveable { mutableStateOf("") }

    Column(Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            // Hero
            Box(
                Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 4.dp).height(270.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Brush.verticalGradient(listOf(cs.primaryContainer, cs.surfaceVariant))),
                contentAlignment = Alignment.Center
            ) {
                Box(Modifier.size(240.dp).offset(x = 110.dp, y = (-70).dp).clip(CircleShape).background(cs.surface.copy(alpha = 0.35f)))
                ProductPhoto(product, Modifier.size(200.dp))
                if (product.discount > 0) {
                    Text(
                        "-${product.discount}%",
                        color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
                            .clip(RoundedCornerShape(50)).background(cs.secondary)
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }

            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(product.name, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.6).sp)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(rupiah(product.price), fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = cs.primary)
                    if (product.discount > 0) {
                        Spacer(Modifier.width(10.dp))
                        Text(
                            rupiah(product.oldPrice), color = cs.onSurfaceVariant, fontSize = 14.sp,
                            textDecoration = TextDecoration.LineThrough,
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "⭐ ${product.rating}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clip(RoundedCornerShape(50)).background(cs.surfaceVariant)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                    Text(
                        "Terjual ${terjual(product.sold)}", fontSize = 13.sp,
                        modifier = Modifier.clip(RoundedCornerShape(50)).background(cs.surfaceVariant)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                    val stockColor = if (stock <= 0) cs.error else if (stock <= 3) WarningOrange else SuccessGreen
                    Text(
                        if (stock <= 0) "Stok habis" else "Stok $stock",
                        color = stockColor, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.clip(RoundedCornerShape(50)).background(stockColor.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
                Chip(if (inCompare) "✓ Dalam perbandingan" else "⚖️ Bandingkan produk", inCompare, onToggleCompare)

                SectionCard("Spesifikasi") {
                    product.specs.entries.forEachIndexed { i, (k, v) ->
                        if (i > 0) Line()
                        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                            Text(k, modifier = Modifier.weight(1f), color = cs.onSurfaceVariant, fontSize = 14.sp)
                            Text(v, modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        }
                    }
                }

                SectionCard("Ulasan (${reviews.size})") {
                    reviews.forEachIndexed { i, r ->
                        if (i > 0) Line()
                        Row(Modifier.padding(vertical = 4.dp)) {
                            Box(
                                Modifier.size(36.dp).clip(CircleShape).background(cs.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    r.name.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold, color = cs.onPrimaryContainer
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Row {
                                    Text(r.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Text("★".repeat(r.stars), color = Color(0xFFFFB400), fontSize = 13.sp)
                                }
                                Text(r.text, fontSize = 13.sp, color = cs.onSurfaceVariant)
                            }
                        }
                    }
                }

                SectionCard("Tulis ulasan") {
                    Row {
                        (1..5).forEach { star ->
                            Text(
                                if (star <= stars) "★" else "☆",
                                fontSize = 32.sp,
                                color = Color(0xFFFFB400),
                                modifier = Modifier.clickable { stars = star }.padding(end = 4.dp)
                            )
                        }
                    }
                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        label = { Text("Bagikan pengalamanmu") },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            if (reviewText.isNotBlank()) {
                                onSubmitReview(stars, reviewText.trim())
                                reviewText = ""
                                stars = 5
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Kirim ulasan") }
                }
            }
        }

        // Bar aksi bawah
        Surface(
            color = cs.surface, shadowElevation = 16.dp,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onAddToCart,
                    enabled = stock > 0,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f).height(54.dp)
                ) { Text("🛒 Keranjang", fontWeight = FontWeight.SemiBold) }
                Button(
                    onClick = onBuyNow,
                    enabled = stock > 0,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1.2f).height(54.dp)
                ) { Text(if (stock > 0) "Beli Sekarang" else "Habis", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

// ======================================================
//  PERBANDINGAN
// ======================================================
@Composable
fun CompareScreen(items: List<Product>, onClear: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    if (items.size < 2) {
        EmptyState("⚖️", "Belum ada yang dibandingkan", "Pilih 2 produk dari halaman detail")
        return
    }
    val a = items[0]
    val b = items[1]
    val keys = (a.specs.keys + b.specs.keys).distinct()

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(a, b).forEach { p ->
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cs.surface)
                ) {
                    Column(Modifier.padding(14.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            Modifier.size(72.dp).clip(RoundedCornerShape(18.dp)).background(cs.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) { ProductPhoto(p, Modifier.size(52.dp)) }
                        Spacer(Modifier.height(8.dp))
                        Text(p.name, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = 14.sp)
                        Text(rupiah(p.price), color = cs.primary, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    }
                }
            }
        }

        SectionCard("Perbandingan") {
            CompareRow("Harga", rupiah(a.price), rupiah(b.price), a.price < b.price, b.price < a.price)
            Line()
            CompareRow("Rating", "⭐ ${a.rating}", "⭐ ${b.rating}", a.rating > b.rating, b.rating > a.rating)
            keys.forEach { k ->
                Line()
                CompareRow(k, a.specs[k] ?: "-", b.specs[k] ?: "-", leftWins = false, rightWins = false)
            }
        }
        Text(
            "Nilai berwarna ungu = lebih unggul",
            fontSize = 12.sp, color = cs.onSurfaceVariant, modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        OutlinedButton(
            onClick = onClear, shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) { Text("Hapus perbandingan") }
    }
}

@Composable
fun CompareRow(label: String, left: String, right: String, leftWins: Boolean, rightWins: Boolean) {
    val cs = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, modifier = Modifier.weight(0.8f), color = cs.onSurfaceVariant, fontSize = 13.sp)
        Text(
            left, modifier = Modifier.weight(1f), fontSize = 13.sp,
            fontWeight = if (leftWins) FontWeight.ExtraBold else FontWeight.Normal,
            color = if (leftWins) cs.primary else cs.onSurface
        )
        Text(
            right, modifier = Modifier.weight(1f), fontSize = 13.sp,
            fontWeight = if (rightWins) FontWeight.ExtraBold else FontWeight.Normal,
            color = if (rightWins) cs.primary else cs.onSurface
        )
    }
}

// ======================================================
//  FAVORIT
// ======================================================
@Composable
fun FavoritesScreen(
    list: List<Product>,
    stocks: Map<Int, Int>,
    favorites: List<Int>,
    onFav: (Int) -> Unit,
    onProduct: (Int) -> Unit,
    onShop: () -> Unit
) {
    if (list.isEmpty()) {
        EmptyState("🤍", "Belum ada favorit", "Tekan ikon hati pada produk untuk menyimpannya", "Cari produk", onShop)
    } else {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            ProductGrid(list, stocks, favorites, onFav, onProduct)
        }
    }
}

// ======================================================
//  KERANJANG
// ======================================================
@Composable
fun CartScreen(
    items: List<Pair<Product, Int>>,
    stocks: Map<Int, Int>,
    onInc: (Product) -> Unit,
    onDec: (Product) -> Unit,
    onRemove: (Product) -> Unit,
    onCheckout: () -> Unit,
    onShop: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    if (items.isEmpty()) {
        EmptyState("🛒", "Keranjang masih kosong", "Yuk cari gadget impianmu", "Belanja sekarang", onShop)
        return
    }
    val subtotal = items.sumOf { (p, q) -> p.price * q }
    val totalQty = items.sumOf { (_, q) -> q }

    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StepBar(1)
            items.forEach { (p, q) ->
                Card(
                    Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cs.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(64.dp).clip(RoundedCornerShape(14.dp)).background(cs.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) { ProductPhoto(p, Modifier.size(44.dp)) }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(p.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(rupiah(p.price), color = cs.primary, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            Text("Stok: ${stocks[p.id] ?: 0}", fontSize = 11.sp, color = cs.onSurfaceVariant)
                            Spacer(Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier.size(30.dp).clip(CircleShape).background(cs.surfaceVariant)
                                        .clickable { onDec(p) },
                                    contentAlignment = Alignment.Center
                                ) { Text("−", fontWeight = FontWeight.Bold) }
                                Text(
                                    "$q", modifier = Modifier.padding(horizontal = 14.dp),
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    Modifier.size(30.dp).clip(CircleShape).background(cs.primary)
                                        .clickable { onInc(p) },
                                    contentAlignment = Alignment.Center
                                ) { Text("+", color = cs.onPrimary, fontWeight = FontWeight.Bold) }
                            }
                        }
                        Box(
                            Modifier.size(38.dp).clip(CircleShape).clickable { onRemove(p) },
                            contentAlignment = Alignment.Center
                        ) { Text("🗑️", fontSize = 18.sp) }
                    }
                }
            }
        }

        Surface(
            color = cs.surface, shadowElevation = 16.dp,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PriceRow("Subtotal ($totalQty barang)", rupiah(subtotal), bold = true)
                Button(
                    onClick = onCheckout, shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) { Text("Checkout", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
            }
        }
    }
}

// ======================================================
//  CHECKOUT
// ======================================================
@Composable
fun CheckoutScreen(
    items: List<Pair<Product, Int>>,
    initialAddress: String,
    onSubmit: (address: String, method: String) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    var address by rememberSaveable { mutableStateOf(initialAddress) }
    var method by rememberSaveable { mutableStateOf(paymentMethods[0]) }
    var error by rememberSaveable { mutableStateOf("") }

    val subtotal = items.sumOf { (p, q) -> p.price * q }

    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            StepBar(2)

            SectionCard("Alamat pengiriman") {
                ShippingFields(address) { address = it; error = "" }
                if (error.isNotEmpty()) Text("⚠️ $error", color = cs.error, fontSize = 13.sp)
            }

            SectionCard("Metode pembayaran") {
                PaymentMethodPicker(method) { method = it }
            }

            SectionCard("Ringkasan pesanan") {
                items.forEach { (p, q) ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("${p.emoji} ${p.name} × $q", modifier = Modifier.weight(1f), fontSize = 14.sp)
                        Text(rupiah(p.price * q), fontSize = 14.sp)
                    }
                }
                Line()
                PriceRow("Subtotal", rupiah(subtotal))
                PriceRow("Ongkos kirim", rupiah(ONGKIR))
                Line()
                PriceRow("Total", rupiah(subtotal + ONGKIR), bold = true)
            }
        }

        Surface(
            color = cs.surface, shadowElevation = 16.dp,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Total", fontSize = 12.sp, color = cs.onSurfaceVariant)
                    Text(rupiah(subtotal + ONGKIR), fontWeight = FontWeight.ExtraBold, color = cs.primary, fontSize = 18.sp)
                }
                Button(
                    onClick = {
                        val err = shipError(address)
                        if (err != null) {
                            error = err
                        } else {
                            onSubmit(address, method)
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.height(52.dp)
                ) { Text("Lanjut ke pembayaran", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

// ======================================================
//  PEMBAYARAN
// ======================================================
@Composable
fun PaymentScreen(order: Order, onPay: (address: String, method: String) -> Unit, onLater: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    var address by rememberSaveable(order.id) { mutableStateOf(order.address) }
    var method by rememberSaveable(order.id) { mutableStateOf(order.method) }
    var error by rememberSaveable { mutableStateOf("") }

    val instruksi = when {
        method.startsWith("Transfer") -> "Transfer ke Virtual Account BCA\n8077 0812 3456 7890\na.n. Voltix"
        method.startsWith("E-Wallet") -> "Bayar ke GoPay\n0812-0000-0000\na.n. Voltix"
        else -> "Siapkan uang tunai pas saat kurir tiba"
    }
    val subtotal = order.total - ONGKIR

    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (!order.direct) StepBar(3)

            Box(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(BrandBrush).padding(20.dp)
            ) {
                Column {
                    Text("Total pembayaran", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                    Text(rupiah(order.total), color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "No. pesanan ${order.id}" + if (order.direct) "  •  Beli langsung" else "",
                        color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp
                    )
                }
            }

            SectionCard("Ringkasan pesanan") {
                order.items.forEach {
                    Row(Modifier.fillMaxWidth()) {
                        Text("${it.emoji} ${it.name} × ${it.qty}", modifier = Modifier.weight(1f), fontSize = 14.sp)
                        Text(rupiah(it.price * it.qty), fontSize = 14.sp)
                    }
                }
                Line()
                PriceRow("Subtotal", rupiah(subtotal))
                PriceRow("Ongkos kirim", rupiah(ONGKIR))
            }

            if (order.direct) {
                SectionCard("Alamat pengiriman") {
                    ShippingFields(address) { address = it; error = "" }
                    if (error.isNotEmpty()) Text("⚠️ $error", color = cs.error, fontSize = 13.sp)
                }
                SectionCard("Metode pembayaran") {
                    PaymentMethodPicker(method) { method = it }
                }
            } else {
                SectionCard("Pengiriman & pembayaran") {
                    Text("📍 ${shipDisplay(order.address)}", fontSize = 14.sp)
                    Text("${methodEmoji(order.method)} ${order.method}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }

            SectionCard("Cara membayar") {
                Text(instruksi, fontSize = 14.sp, color = cs.onSurfaceVariant)
            }
        }

        Surface(
            color = cs.surface, shadowElevation = 16.dp,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Button(
                    onClick = {
                        val err = if (order.direct) shipError(address) else null
                        if (err != null) {
                            error = err
                        } else {
                            onPay(if (order.direct) address else order.address, if (order.direct) method else order.method)
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text(
                        if (method.startsWith("COD")) "Konfirmasi pesanan" else "Bayar sekarang (simulasi)",
                        fontWeight = FontWeight.Bold, fontSize = 16.sp
                    )
                }
                TextButton(onClick = onLater, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Bayar nanti")
                }
            }
        }
    }
}

// ======================================================
//  PEMBAYARAN BERHASIL
// ======================================================
@Composable
fun SuccessScreen(order: Order, onOrders: () -> Unit, onHome: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    var started by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }
    val scale by animateFloatAsState(
        targetValue = if (started) 1f else 0.2f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        Box(
            Modifier.size(110.dp).scale(scale).clip(CircleShape).background(SuccessGreen),
            contentAlignment = Alignment.Center
        ) { Text("✓", color = Color.White, fontSize = 60.sp, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(16.dp))
        Text("Pembayaran Berhasil!", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = SuccessGreen)
        Text("Terima kasih sudah berbelanja di Voltix", color = cs.onSurfaceVariant, fontSize = 14.sp)
        Spacer(Modifier.height(20.dp))

        SectionCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("No. pesanan", color = cs.onSurfaceVariant, modifier = Modifier.weight(1f))
                Text(order.id, fontWeight = FontWeight.ExtraBold)
            }
            Line()
            order.items.forEach {
                Row(Modifier.fillMaxWidth()) {
                    Text("${it.emoji} ${it.name} × ${it.qty}", modifier = Modifier.weight(1f), fontSize = 14.sp)
                    Text(rupiah(it.price * it.qty), fontSize = 14.sp)
                }
            }
            Line()
            PriceRow("Total dibayar", rupiah(order.total), bold = true)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Status", color = cs.onSurfaceVariant, modifier = Modifier.weight(1f))
                StatusPill(order.status)
            }
            Text("📍 ${shipDisplay(order.address)}", fontSize = 12.sp, color = cs.onSurfaceVariant)
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onOrders, shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Lihat pesanan saya", fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = onHome, shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Lanjut belanja") }
    }
}

// ======================================================
//  PESANAN (riwayat + status)
// ======================================================
@Composable
fun OrdersScreen(orders: List<Order>, onPay: (Order) -> Unit, onDelete: (Order) -> Unit, onShop: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val db = VoltixDb.get(LocalContext.current)
    val filters = listOf("Semua", "Belum Bayar", "Dikemas", "Dikirim", "Selesai", "Dibatalkan")
    var filter by rememberSaveable { mutableStateOf("Semua") }

    val shown = orders.reversed().filter {
        filter == "Semua" || it.status == (if (filter == "Belum Bayar") "Menunggu Pembayaran" else filter)
    }

    Column(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().background(cs.surface).padding(16.dp)) {
            Text("Pesanan Saya", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(10.dp))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                filters.forEach { f -> Chip(f, filter == f) { filter = f } }
            }
        }

        if (orders.isEmpty()) {
            EmptyState("📦", "Belum ada pesanan", "Pesanan yang kamu buat akan muncul di sini", "Mulai belanja", onShop)
        } else if (shown.isEmpty()) {
            EmptyState("🗂️", "Tidak ada pesanan", "Tidak ada pesanan dengan status \"$filter\"")
        } else {
            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                shown.forEach { order ->
                    Card(
                        Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = cs.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(order.id, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
                                StatusPill(order.status)
                            }
                            order.items.forEach {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(cs.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) { products.firstOrNull { p -> p.id == it.productId }?.let { p -> ProductPhoto(p, Modifier.size(28.dp)) } }
                                    Spacer(Modifier.width(10.dp))
                                    Text("${it.name} × ${it.qty}", modifier = Modifier.weight(1f), fontSize = 14.sp)
                                    Text(rupiah(it.price * it.qty), fontSize = 13.sp, color = cs.onSurfaceVariant)
                                }
                            }
                            Line()
                            PriceRow("Total", rupiah(order.total), bold = true)
                            Text(
                                "${methodEmoji(order.method)} ${order.method}",
                                fontSize = 12.sp, color = cs.onSurfaceVariant
                            )
                            OrderProgress(order.status)

                            when (order.status) {
                                "Menunggu Pembayaran" -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { order.status = "Dibatalkan"; db.updateOrder(order) },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1f)
                                    ) { Text("Batalkan") }
                                    Button(
                                        onClick = { onPay(order) },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1.4f)
                                    ) { Text("Bayar sekarang") }
                                }

                                "Dikemas", "Dikirim" -> OutlinedButton(
                                    onClick = {
                                        order.status = if (order.status == "Dikemas") "Dikirim" else "Selesai"; db.updateOrder(order)
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text("Demo: lanjut ke status berikutnya") }

                                "Selesai", "Dibatalkan" -> Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        if (order.status == "Selesai") "✓ Pesanan selesai" else "✕ Pesanan dibatalkan",
                                        color = statusColor(order.status), fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedButton(onClick = { onDelete(order) }, shape = RoundedCornerShape(12.dp)) {
                                        Text("🗑️ Hapus")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ======================================================
//  PROFIL
// ======================================================
@Composable
fun ProfileScreen(
    userName: String,
    email: String,
    avatar: ImageBitmap?,
    onAvatarChange: (ImageBitmap?) -> Unit,
    ordersCount: Int,
    favCount: Int,
    cartCount: Int,
    savedAddress: String,
    onSaveAddress: (String) -> Unit,
    onFavorites: () -> Unit,
    onOrders: () -> Unit,
    onLogout: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    var address by rememberSaveable(savedAddress) { mutableStateOf(savedAddress) }
    var showLogout by rememberSaveable { mutableStateOf(false) }
    var showAbout by rememberSaveable { mutableStateOf(false) }

    // Foto profil dari galeri (disimpan di penyimpanan internal app, satu file per akun)
    val context = LocalContext.current
    val avatarFile = remember(email) { avatarFileFor(context, email) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val img = saveAvatar(context, uri, avatarFile)
            if (img != null) onAvatarChange(img)
            else Toast.makeText(context, "Gagal memuat foto", Toast.LENGTH_SHORT).show()
        }
    }
    val pickPhoto = {
        picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Column(
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                .background(BrandBrush)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.size(88.dp)) {
                Box(
                    Modifier.fillMaxSize().clip(CircleShape).background(Color.White)
                        .clickable { pickPhoto() },
                    contentAlignment = Alignment.Center
                ) {
                    val img = avatar
                    if (img != null) {
                        Image(
                            bitmap = img,
                            contentDescription = "Foto profil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            userName.take(1).uppercase(), fontSize = 38.sp, fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF5B2EFF)
                        )
                    }
                }
                Box(
                    Modifier.align(Alignment.BottomEnd).size(30.dp).clip(CircleShape)
                        .background(cs.secondary).border(2.dp, Color.White, CircleShape)
                        .clickable { pickPhoto() },
                    contentAlignment = Alignment.Center
                ) { Text("📷", fontSize = 13.sp) }
            }
            Spacer(Modifier.height(10.dp))
            Text(userName, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Text(email, color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { pickPhoto() }) {
                    Text("Ganti foto", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                if (avatar != null) {
                    TextButton(onClick = { avatarFile.delete(); onAvatarChange(null) }) {
                        Text("Hapus", color = Color.White.copy(alpha = 0.75f), fontSize = 13.sp)
                    }
                }
            }
        }

        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(
                    "$ordersCount" to "Pesanan",
                    "$favCount" to "Favorit",
                    "$cartCount" to "Keranjang"
                ).forEach { (value, label) ->
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cs.surface)
                    ) {
                        Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = cs.primary)
                            Text(label, fontSize = 12.sp, color = cs.onSurfaceVariant)
                        }
                    }
                }
            }

            SectionCard("Alamat utama") {
                ShippingFields(address) { address = it }
                Button(
                    onClick = {
                        val err = shipError(address)
                        if (err != null) Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                        else onSaveAddress(address)
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Simpan alamat") }
                Text(
                    "Alamat ini dipakai otomatis saat Beli Sekarang.",
                    fontSize = 12.sp, color = cs.onSurfaceVariant
                )
            }

            Card(
                Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cs.surface)
            ) {
                Column {
                    ProfileMenuRow("❤️", "Produk Favorit", onFavorites)
                    Line()
                    ProfileMenuRow("📦", "Pesanan Saya", onOrders)
                    Line()
                    ProfileMenuRow("ℹ️", "Tentang Voltix") { showAbout = true }
                    Line()
                    ProfileMenuRow("🚪", "Keluar") { showLogout = true }
                }
            }
            Spacer(Modifier.height(80.dp))
        }
    }

    if (showLogout) {
        AlertDialog(
            onDismissRequest = { showLogout = false },
            title = { Text("Keluar dari akun?") },
            text = { Text("Kamu harus masuk lagi untuk melanjutkan belanja.") },
            confirmButton = { TextButton(onClick = { showLogout = false; onLogout() }) { Text("Keluar") } },
            dismissButton = { TextButton(onClick = { showLogout = false }) { Text("Batal") } }
        )
    }
    if (showAbout) {
        AlertDialog(
            onDismissRequest = { showAbout = false },
            title = { Text("Voltix ⚡") },
            text = { Text("Aplikasi katalog dan belanja gadget (versi simulasi).\nVersi 1.0") },
            confirmButton = { TextButton(onClick = { showAbout = false }) { Text("Tutup") } }
        )
    }
}

private fun avatarFileFor(context: Context, email: String): File =
    File(context.filesDir, "avatar_${email.hashCode()}.jpg")

private fun loadAvatar(file: File): ImageBitmap? =
    if (file.exists()) BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap() else null

/** Salin foto pilihan dari galeri ke file internal (diperkecil & dirotasi sesuai EXIF), lalu kembalikan bitmap-nya. */
@SuppressLint("ExifInterface")
private fun saveAvatar(context: Context, uri: Uri, file: File): ImageBitmap? {
    return try {
        val resolver = context.contentResolver
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var sample = 1
        while (bounds.outWidth / sample > 1024 || bounds.outHeight / sample > 1024) sample *= 2

        var bmp = resolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
        } ?: return null

        val degrees = resolver.openInputStream(uri)?.use {
            when (android.media.ExifInterface(it).getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, 1)) {
                6 -> 90f
                3 -> 180f
                8 -> 270f
                else -> 0f
            }
        } ?: 0f
        if (degrees != 0f) {
            val m = android.graphics.Matrix().apply { postRotate(degrees) }
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, m, true)
        }

        FileOutputStream(file).use { bmp.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        bmp.asImageBitmap()
    } catch (e: Exception) {
        android.util.Log.w("Voltix", "Gagal menyimpan foto profil", e)
        null
    }
}

@Composable
fun ProfileMenuRow(emoji: String, title: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, fontSize = 20.sp)
        Spacer(Modifier.width(14.dp))
        Text(title, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
        Text("›", fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}