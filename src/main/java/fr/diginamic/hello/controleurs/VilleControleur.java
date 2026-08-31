package fr.diginamic.hello.controleurs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.diginamic.hello.Ville;
import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/villes")
public class VilleControleur {

    private List<Ville> villes = new ArrayList<>();

    @PostConstruct
    public void initData() {
        villes.add(new Ville("Nice", "343000"));
        villes.add(new Ville("Carcassonne", "47800"));
        villes.add(new Ville("Narbonne", "53400"));
        villes.add(new Ville("Lyon", "484000"));
        villes.add(new Ville("Foix", "9700"));
        villes.add(new Ville("Pau", "77200"));
        villes.add(new Ville("Marseille", "850700"));
        villes.add(new Ville("Tarbes", "40600"));
    }

    @GetMapping
    public List<Ville> getVilles() {
        return villes;
    }

    @PostMapping
    public ResponseEntity<String> insertVille(@RequestBody Ville nouvelleVille) {
        for (Ville ville : villes) {
            if (ville.getNom().equalsIgnoreCase(nouvelleVille.getNom())) {
                return ResponseEntity.badRequest().body("La ville existe déjà");
            }
        }
        villes.add(nouvelleVille);
        return ResponseEntity.ok("Ville insérée avec succès");
    }

}
