@file:Suppress("unused")

package fr.delcey.dailynino.ui.utils


import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal
import java.util.TreeMap

@Suppress("unused")
sealed class NativeText {

    protected abstract val textColorRes: Int?

    data object Nothing : NativeText() {
        override val textColorRes: Int? = null
        override fun toCharSequenceInternal(context: Context): CharSequence = ""
    }

    data class Simple(val text: String, @ColorRes override val textColorRes: Int? = null) : NativeText() {
        override fun toCharSequenceInternal(context: Context): CharSequence = text
    }

    data class Html(val html: String, @ColorRes override val textColorRes: Int? = null) : NativeText() {
        override fun toCharSequenceInternal(context: Context): CharSequence = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
    }

    data class Resource(@StringRes val id: Int, @ColorRes override val textColorRes: Int? = null) : NativeText() {
        override fun toCharSequenceInternal(context: Context): CharSequence = context.getString(id)
    }

    data class Plural(
        @PluralsRes val id: Int,
        val number: Int,
        val args: List<Any>,
        @ColorRes override val textColorRes: Int? = null
    ) : NativeText() {
        override fun toCharSequenceInternal(context: Context): CharSequence = context.resources.getQuantityString(
            id,
            number,
            *args.toTypedArray()
        )
    }

    data class Argument(
        @StringRes val id: Int,
        val arg: Any,
        @ColorRes override val textColorRes: Int? = null
    ) : NativeText() {
        override fun toCharSequenceInternal(context: Context): CharSequence = if (arg is NativeText) {
            context.getString(id, arg.toCharSequence(context))
        } else {
            context.getString(id, arg)
        }
    }

    data class Arguments(
        @StringRes val id: Int,
        val args: List<Any>,
        @ColorRes override val textColorRes: Int? = null
    ) : NativeText() {
        override fun toCharSequenceInternal(context: Context): CharSequence = context.getString(id, *args.toTypedArray())
    }

    data class Multi(
        val text: List<NativeText>,
        @ColorRes override val textColorRes: Int? = null
    ) : NativeText() {
        override fun toCharSequenceInternal(context: Context): CharSequence = StringBuilder().apply {
            for (item in text) {
                append(item.toCharSequence(context))
            }
        }
    }

    data class Date(
        val temporal: Temporal,
        @StringRes
        val temporalFormatterPatternStringRes: Int,
        @ColorRes override val textColorRes: Int? = null
    ) : NativeText() {
        companion object {
            private val dateTimeFormatters = TreeMap<String, DateTimeFormatter>()
        }

        override fun toCharSequenceInternal(context: Context): CharSequence {
            val pattern = context.getString(temporalFormatterPatternStringRes)
            val formatter = dateTimeFormatters.getOrPut(pattern) {
                DateTimeFormatter.ofPattern(pattern)
            }

            return formatter.format(temporal)
        }
    }

    protected abstract fun toCharSequenceInternal(context: Context): CharSequence

    fun toCharSequence(context: Context): CharSequence {
        val charSequence = toCharSequenceInternal(context)

        return textColorRes?.let { textColorRes ->
            SpannableString(charSequence).apply {
                setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, textColorRes)),
                    0,
                    length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        } ?: charSequence
    }
}

fun TextView.setText(nativeText: NativeText?) {
    text = nativeText?.toCharSequence(context)
}

fun TextView.setTextOrHide(nativeText: NativeText?) {
    val resolved = nativeText?.toCharSequence(context)
    isVisible = !resolved.isNullOrBlank()
    text = resolved
}

/**
 * Show the NativeText as Toast and return the instance of the shown toast, should you cancel it
 */
fun NativeText.showAsToast(context: Context, duration: Int = Toast.LENGTH_LONG): Toast =
    Toast.makeText(context, toCharSequence(context), duration).also {
        it.show()
    }