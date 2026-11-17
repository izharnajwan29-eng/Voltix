package com.example.voltix

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Database lokal (SQLite) aplikasi Voltix.
 * Tabel: users, cart, favorites, stock, orders, reviews.
 */
class VoltixDb private constructor(ctx: Context) :
    SQLiteOpenHelper(ctx.applicationContext, "voltix.db", null, 1) {

    companion object {
        @Volatile private var instance: VoltixDb? = null
        fun get(ctx: Context): VoltixDb =
            instance ?: synchronized(this) { instance ?: VoltixDb(ctx).also { instance = it } }
    }

    private val tables = listOf("users", "cart", "favorites", "stock", "orders", "reviews")

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE users(email TEXT PRIMARY KEY, password TEXT NOT NULL)")
        db.execSQL("CREATE TABLE cart(product_id INTEGER PRIMARY KEY, qty INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE favorites(product_id INTEGER PRIMARY KEY)")
        db.execSQL("CREATE TABLE stock(product_id INTEGER PRIMARY KEY, qty INTEGER NOT NULL)")
        db.execSQL(
            "CREATE TABLE orders(id TEXT PRIMARY KEY, items TEXT NOT NULL, total INTEGER NOT NULL, " +
                    "address TEXT NOT NULL, method TEXT NOT NULL, status TEXT NOT NULL, direct INTEGER NOT NULL)"
        )
        db.execSQL(
            "CREATE TABLE reviews(rid INTEGER PRIMARY KEY AUTOINCREMENT, product_id INTEGER NOT NULL, " +
                    "name TEXT NOT NULL, stars INTEGER NOT NULL, text TEXT NOT NULL)"
        )
        db.insert("users", null, cv("email" to "admin@voltix.com", "password" to "admin123"))
        products.forEach { db.insert("stock", null, cv("product_id" to it.id, "qty" to it.stock)) }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        tables.forEach { db.execSQL("DROP TABLE IF EXISTS $it") }
        onCreate(db)
    }

    private fun cv(vararg p: Pair<String, Any>) = ContentValues().apply {
        p.forEach { (k, v) ->
            when (v) {
                is Int -> put(k, v)
                is Long -> put(k, v)
                else -> put(k, v.toString())
            }
        }
    }

    private fun inTransaction(block: SQLiteDatabase.() -> Unit) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.block()
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    // ---------------- USERS ----------------
    fun userExists(email: String): Boolean =
        readableDatabase.rawQuery("SELECT 1 FROM users WHERE email=?", arrayOf(email)).use { it.moveToFirst() }

    fun addUser(email: String, password: String): Boolean =
        writableDatabase.insert("users", null, cv("email" to email, "password" to password)) != -1L

    fun checkLogin(email: String, password: String): Boolean =
        readableDatabase.rawQuery(
            "SELECT 1 FROM users WHERE email=? AND password=?", arrayOf(email, password)
        ).use { it.moveToFirst() }

    // ---------------- CART ----------------
    fun loadCart(): Map<Int, Int> {
        val m = linkedMapOf<Int, Int>()
        readableDatabase.rawQuery("SELECT product_id, qty FROM cart", null).use {
            while (it.moveToNext()) m[it.getInt(0)] = it.getInt(1)
        }
        return m
    }

    fun cartQty(productId: Int): Int = loadCart()[productId] ?: 0

    fun setCart(productId: Int, qty: Int) {
        if (qty <= 0) writableDatabase.delete("cart", "product_id=?", arrayOf("$productId"))
        else writableDatabase.replace("cart", null, cv("product_id" to productId, "qty" to qty))
    }

    fun replaceCart(map: Map<Int, Int>) = inTransaction {
        delete("cart", null, null)
        map.forEach { (id, q) -> if (q > 0) insert("cart", null, cv("product_id" to id, "qty" to q)) }
    }

    fun clearCart() { writableDatabase.delete("cart", null, null) }

    // ---------------- FAVORITES ----------------
    fun favorites(): List<Int> {
        val l = mutableListOf<Int>()
        readableDatabase.rawQuery("SELECT product_id FROM favorites ORDER BY rowid", null).use {
            while (it.moveToNext()) l.add(it.getInt(0))
        }
        return l
    }

    fun isFavorite(productId: Int): Boolean = productId in favorites()

    fun setFavorite(productId: Int, on: Boolean) {
        if (on) writableDatabase.replace("favorites", null, cv("product_id" to productId))
        else writableDatabase.delete("favorites", "product_id=?", arrayOf("$productId"))
    }

    fun replaceFavorites(list: List<Int>) = inTransaction {
        delete("favorites", null, null)
        list.forEach { insert("favorites", null, cv("product_id" to it)) }
    }

    // ---------------- STOCK ----------------
    fun loadStock(): Map<Int, Int> {
        val m = linkedMapOf<Int, Int>()
        readableDatabase.rawQuery("SELECT product_id, qty FROM stock", null).use {
            while (it.moveToNext()) m[it.getInt(0)] = it.getInt(1)
        }
        // Defensive: produk yang belum ada di tabel memakai stok awal
        products.forEach { if (!m.containsKey(it.id)) m[it.id] = it.stock }
        return m
    }

    fun replaceStock(map: Map<Int, Int>) = inTransaction {
        map.forEach { (id, q) -> replace("stock", null, cv("product_id" to id, "qty" to q)) }
    }

    // ---------------- ORDERS (CRUD) ----------------
    private fun encodeItems(items: List<OrderItem>) =
        items.joinToString("\n") { listOf(it.productId, it.name, it.emoji, it.price, it.qty).joinToString("\t") }

    private fun decodeItems(raw: String): List<OrderItem> =
        raw.split("\n").filter { it.isNotBlank() }.mapNotNull { line ->
            val f = line.split("\t")
            if (f.size < 5) return@mapNotNull null
            val id = f[0].toIntOrNull() ?: return@mapNotNull null
            val price = f[3].toIntOrNull() ?: return@mapNotNull null
            val qty = f[4].toIntOrNull() ?: return@mapNotNull null
            OrderItem(id, f[1], f[2], price, qty)
        }

    /** CREATE */
    fun insertOrder(o: Order) {
        writableDatabase.replace(
            "orders", null,
            cv(
                "id" to o.id, "items" to encodeItems(o.items), "total" to o.total,
                "address" to o.address, "method" to o.method, "status" to o.status,
                "direct" to (if (o.direct) 1 else 0)
            )
        )
    }

    /** READ */
    fun loadOrders(): List<Order> {
        val list = mutableListOf<Order>()
        readableDatabase.rawQuery(
            "SELECT id, items, total, address, method, status, direct FROM orders ORDER BY rowid", null
        ).use { c ->
            while (c.moveToNext()) {
                val order = Order(
                    id = c.getString(0), items = decodeItems(c.getString(1)), total = c.getInt(2),
                    address = c.getString(3), method = c.getString(4), direct = c.getInt(6) == 1
                )
                order.status = c.getString(5)
                list.add(order)
            }
        }
        return list
    }

    /** UPDATE */
    fun updateOrder(o: Order) {
        writableDatabase.update(
            "orders",
            cv("address" to o.address, "method" to o.method, "status" to o.status),
            "id=?", arrayOf(o.id)
        )
    }

    /** DELETE */
    fun deleteOrder(id: String) { writableDatabase.delete("orders", "id=?", arrayOf(id)) }

    // ---------------- REVIEWS ----------------
    fun reviews(productId: Int): List<Review> {
        val l = mutableListOf<Review>()
        readableDatabase.rawQuery(
            "SELECT name, stars, text FROM reviews WHERE product_id=? ORDER BY rid", arrayOf("$productId")
        ).use { while (it.moveToNext()) l.add(Review(it.getString(0), it.getInt(1), it.getString(2))) }
        return l
    }

    fun addReview(productId: Int, r: Review) {
        writableDatabase.insert(
            "reviews", null,
            cv("product_id" to productId, "name" to r.name, "stars" to r.stars, "text" to r.text)
        )
    }
}