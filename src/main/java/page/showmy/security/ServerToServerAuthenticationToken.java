package page.showmy.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class ServerToServerAuthenticationToken extends AbstractAuthenticationToken {

    public ServerToServerAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null; // No credentials, authenticated by API key
    }

    @Override
    public Object getPrincipal() {
        return "server-to-server"; // Placeholder principal
    }
}
