package com.ancientlore.lexio

import android.net.Uri
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import java.util.regex.Pattern

object Utils {
	private val okHttpClient: OkHttpClient by lazy { OkHttpClient() }

	fun getTranslation(text: String, onSuccess: Runnable1<String>, onFailure: Runnable1<Throwable>?) {
		val url = getTranslateUrl(text, Consts.LANG_EN, Consts.LANG_RU)
		val request = okhttp3.Request.Builder().url(url).build()

		okHttpClient.newCall(request).enqueue(object : Callback {
			@Throws(IOException::class)
			override fun onResponse(call: Call, response: Response) { // Response example: [[["слово","word",null,null,1]],null,"en"]
				if (response.isSuccessful) {
					response.body()?.let {
						val translation = parseGoogleTranslateResponse(it.string())
						onSuccess.run(translation)
					} ?: onFailure?.run(IOException("Response has empty body. $response"))
				}
				else onFailure?.run(IOException("Recieved unsuccessful response: $response"))
			}
			override fun onFailure(call: Call, e: IOException) {
				onFailure?.run(e)
			}
		})
	}

	private fun getTranslateUrl(text: String, sourceLang: String, targetLang: String) =
		StringBuilder("https://translate.googleapis.com/translate_a/single?client=gtx&sl=")
				.append(sourceLang).append("&tl=")
				.append(targetLang).append("&dt=t&q=")
				.append(Uri.encode(text)).toString()

	private fun parseGoogleTranslateResponse(rawText: String): String {
		val pattern = Pattern.compile("\"(.*?)\"")
		val matcher = pattern.matcher(rawText)
		return if (matcher.find()) matcher.group(1) else ""
	}
}