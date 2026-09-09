package fr.diginamic.hello.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import fr.diginamic.hello.repositories.UtilisateurRepository;
import jakarta.annotation.PostConstruct;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder encoder;

    @PostConstruct
    public void initUtilisateurs() {
        if (utilisateurRepository.findByUsername("gdupont").isEmpty()) {
            utilisateurRepository.save(new Utilisateur(
                    "gdupont", encoder.encode("user1234"), new Role("ROLE_USER")));
        }
        if (utilisateurRepository.findByUsername("aduval").isEmpty()) {
            utilisateurRepository.save(new Utilisateur(
                    "aduval", encoder.encode("admin1234"), new Role("ROLE_ADMIN")));
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return utilisateurRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur inconnu."));
    }
}
