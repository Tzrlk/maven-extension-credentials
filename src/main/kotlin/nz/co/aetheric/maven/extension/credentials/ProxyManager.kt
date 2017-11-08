package nz.co.aetheric.maven.extension.credentials

import nz.co.aetheric.get
import nz.co.aetheric.contains
import org.apache.maven.AbstractMavenLifecycleParticipant
import org.apache.maven.execution.MavenSession
import org.apache.maven.settings.Proxy
import org.cfg4j.provider.ConfigurationProviderBuilder
import org.cfg4j.source.system.EnvironmentVariablesConfigurationSource
import org.codehaus.plexus.component.annotations.Component
import java.net.URI

@Component(role = AbstractMavenLifecycleParticipant::class, hint = "proxies")
class ProxyManager : AbstractMavenLifecycleParticipant() {

	private val RX_IP = Regex("""^\d+\.\d+\.\d+\.\d+$""")
	private val RX_LOCAL = Regex("""^localhost$""")

	private val config = ConfigurationProviderBuilder()
		.withConfigurationSource(EnvironmentVariablesConfigurationSource())
		.build()

	override fun afterSessionStart(session: MavenSession) {

		val noProxy = config["no_proxy", "NO_PROXY"]
			?.split(",")
			?.map { entry ->
				when (entry) {
					in RX_IP    -> entry
					in RX_LOCAL -> entry
					else        -> "*.$entry"
				}
			}
			?.joinToString("|")
			?: ""

		// Grab the cli proxy variables and attempt to turn them into proxies.
		mutableSetOf<Pair<String, String>>()
			.apply {
				config["http_proxy", "HTTP_PROXY"]?.let { add("http_proxy" to it) }
				config["https_proxy", "HTTPS_PROXY"]?.let { add("https_proxy" to it) }
				config["ftp_proxy", "FTP_PROXY"]?.let { add("ftp_proxy" to it) }
			}
			.map { it.first to URI.create(it.second) }
			.map { it.second.toProxy(it.first, noProxy) }
			.forEach { session.settings.proxies.add(it) }

	}

	fun URI.toProxy(name: String, noProxy: String = ""): Proxy = let { uri -> Proxy().apply {
		id = name
		host = uri.host
		isActive = true
		protocol = uri.scheme
		port = uri.port
		nonProxyHosts = noProxy
		uri.userInfo.split(":").let { ( user, pass ) ->
			username = user
			password = pass
		}
	}}

}
