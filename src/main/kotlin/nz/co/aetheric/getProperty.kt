package nz.co.aetheric

import org.cfg4j.provider.ConfigurationProvider

operator fun ConfigurationProvider.get(key: String, vararg keys: String): String?
	= ( arrayOf(key) + keys ).find { this[key]?.let { return it } ?: false }

operator fun ConfigurationProvider.get(key: String): String?
	= this[key, String::class.java]

inline operator fun <reified T> ConfigurationProvider.get(key: String): T?
	= this[key, T::class.java]

operator fun <T> ConfigurationProvider.get(key: String, type: Class<T>): T?
	= try { getProperty(key, type) } catch (error: NoSuchElementException) { null }
