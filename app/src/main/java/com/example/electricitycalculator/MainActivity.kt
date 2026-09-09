package com.example.electricitycalculator

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var rateDay: EditText
    private lateinit var rateNight: EditText
    private lateinit var result: TextView

    private fun EditText.value(): Double? =
        text.toString().trim().replace(',', '.').toDoubleOrNull()

    private fun money(v: Double) = String.format(Locale.US, "%.2f", v)
    private fun kwh(v: Double) = String.format(Locale.US, "%.3f", v)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rateDay = findViewById(R.id.etRateDay)
        rateNight = findViewById(R.id.etRateNight)
        result = findViewById(R.id.tvResult)

        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        rateDay.setText(prefs.getString("rateDay", ""))
        rateNight.setText(prefs.getString("rateNight", ""))

        findViewById<Button>(R.id.btnSaveRates).setOnClickListener {
            val d = rateDay.value()
            val n = rateNight.value()
            if (d == null || n == null || d <= 0 || n <= 0) {
                Toast.makeText(this, "Введите корректные тарифы", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            prefs.edit().putString("rateDay", rateDay.text.toString())
                .putString("rateNight", rateNight.text.toString()).apply()
            Toast.makeText(this, "Тарифы сохранены", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnCalcMeter).setOnClickListener {
            val rd = rateDay.value() ?: 0.0
            val rn = rateNight.value() ?: 0.0
            val dp = findViewById<EditText>(R.id.etDayPrev).value()
            val dc = findViewById<EditText>(R.id.etDayCurrent).value()
            val np = findViewById<EditText>(R.id.etNightPrev).value()
            val nc = findViewById<EditText>(R.id.etNightCurrent).value()

            if (rd <= 0 || rn <= 0 || dp == null || dc == null || np == null || nc == null) {
                showError("Заполните тарифы и все показания")
                return@setOnClickListener
            }
            if (dc < dp || nc < np) {
                showError("Текущие показания не могут быть меньше предыдущих")
                return@setOnClickListener
            }

            val day = dc - dp
            val night = nc - np
            val dayCost = day * rd
            val nightCost = night * rn

            result.text = "РАСЧЁТ\n\n" +
                    "☀️ День: ${kwh(day)} кВт⋅ч × ${money(rd)} = ${money(dayCost)} грн\n" +
                    "🌙 Ночь: ${kwh(night)} кВт⋅ч × ${money(rn)} = ${money(nightCost)} грн\n\n" +
                    "⚡ Всего: ${kwh(day + night)} кВт⋅ч\n" +
                    "💰 К оплате: ${money(dayCost + nightCost)} грн"
        }

        findViewById<Button>(R.id.btnCalcMoney).setOnClickListener {
            val rd = rateDay.value() ?: 0.0
            val rn = rateNight.value() ?: 0.0
            val md = findViewById<EditText>(R.id.etMoneyDay).value() ?: 0.0
            val mn = findViewById<EditText>(R.id.etMoneyNight).value() ?: 0.0

            if (rd <= 0 || rn <= 0) {
                showError("Введите корректные тарифы")
                return@setOnClickListener
            }
            if (md < 0 || mn < 0) {
                showError("Сумма не может быть отрицательной")
                return@setOnClickListener
            }

            val day = md / rd
            val night = mn / rn

            result.text = "РАСЧЁТ ПО СУММЕ\n\n" +
                    "☀️ День: ${money(md)} грн → ${kwh(day)} кВт⋅ч\n" +
                    "🌙 Ночь: ${money(mn)} грн → ${kwh(night)} кВт⋅ч\n\n" +
                    "⚡ Всего: ${kwh(day + night)} кВт⋅ч"
        }

        findViewById<Button>(R.id.btnClear).setOnClickListener {
            listOf(
                R.id.etDayPrev, R.id.etDayCurrent,
                R.id.etNightPrev, R.id.etNightCurrent,
                R.id.etMoneyDay, R.id.etMoneyNight
            ).forEach { findViewById<EditText>(it).text.clear() }
            result.text = "Результат появится здесь"
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
