package nz.co.aetheric.maven.extension.credentials

import org.apache.maven.AbstractMavenLifecycleParticipant
import org.apache.maven.execution.MavenSession
import org.codehaus.plexus.component.annotations.Component

@Component(role = AbstractMavenLifecycleParticipant::class, hint = "credentials")
class CredentialManager : AbstractMavenLifecycleParticipant() {

	override fun afterSessionStart(session: MavenSession) {

		session.settings.servers.forEach { server ->
			// TODO: search credential management for appropriate settings, and add them to the server config.
			server.configuration // inspect this for credential helper info.
		}

	}

}
