package fr.diginamic.hello.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemoireService implements UserDetailsService {

    private List<Utilisateur> utilisateurs = new ArrayList<>();

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (utilisateurs.isEmpty()) {
            utilisateurs.add(new Utilisateur("gdupont", encoder.encode("user1234"), new Role("ROLE_USER")));
            utilisateurs.add(new Utilisateur("aduval", encoder.encode("admin1234"), new Role("ROLE_ADMIN")));
        }

        return utilisateurs.stream()
                .filter(utilisateur -> utilisateur.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur inconnu."));
    }
}
