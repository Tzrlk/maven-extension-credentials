package nz.co.aetheric

operator fun Regex.contains(text: CharSequence): Boolean
	= this.matches(text)
