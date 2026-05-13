package com.inovagab.app.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.NumberFormat
import java.util.Locale

class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 8) text.text.substring(0..7) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1 || i == 3) out += "/"
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 3) return offset + 1
                if (offset <= 8) return offset + 2
                return 10
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                if (offset <= 10) return offset - 2
                return 8
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

class CurrencyVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text.trim()
        if (originalText.isEmpty()) {
            return TransformedText(
                text = AnnotatedString("R$ 0,00"),
                offsetMapping = object : OffsetMapping {
                    override fun originalToTransformed(offset: Int) = 7
                    override fun transformedToOriginal(offset: Int) = 0
                }
            )
        }

        val doubleValue = (originalText.toDoubleOrNull() ?: 0.0) / 100
        val formatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        val formattedText = formatter.format(doubleValue)

        return TransformedText(
            text = AnnotatedString(formattedText),
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    return formattedText.length
                }
                override fun transformedToOriginal(offset: Int): Int {
                    return originalText.length
                }
            }
        )
    }
}

object FormatUtils {
    fun formatToDouble(centsString: String): Double {
        if (centsString.isEmpty()) return 0.0
        return (centsString.toDoubleOrNull() ?: 0.0) / 100
    }

    fun doubleToCentsString(value: Double): String {
        val cents = (value * 100).toLong()
        return cents.toString()
    }

    fun formatDateString(rawDigits: String): String {
        val trimmed = if (rawDigits.length >= 8) rawDigits.substring(0..7) else rawDigits
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1 || i == 3) out += "/"
        }
        return out
    }
    
    fun removeNonDigits(text: String): String {
        return text.filter { it.isDigit() }
    }

    fun filterDateInput(text: String): String {
        val digits = text.filter { it.isDigit() }.take(8)
        var result = ""
        for (i in digits.indices) {
            val c = digits[i]
            val valid = when (i) {
                0 -> c in '0'..'3'
                1 -> if (digits[0] == '3') c in '0'..'1' else if (digits[0] == '0') c in '1'..'9' else c in '0'..'9'
                2 -> c in '0'..'1'
                3 -> if (digits[2] == '1') c in '0'..'2' else if (digits[2] == '0') c in '1'..'9' else c in '0'..'9'
                4 -> c in '1'..'2'
                else -> c in '0'..'9'
            }
            if (valid) {
                result += c
            } else {
                break
            }
        }
        return result
    }
}
