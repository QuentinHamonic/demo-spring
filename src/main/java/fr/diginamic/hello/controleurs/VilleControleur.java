package fr.diginamic.hello.controleurs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.diginamic.hello.Ville;
import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/villes")
public class VilleControleur {

    private List<Ville> villes = new ArrayList<>();

    private int nextId = 1;

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
        for (Ville ville : villes) {
            ville.setId(nextId++);
        }
    }

    @GetMapping
    public List<Ville> getVilles() {
        return villes;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ville> getVille(@PathVariable int id) {
        for (Ville ville : villes) {
            if (ville.getId() == id) {
                return ResponseEntity.ok(ville);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<String> insertVille(@RequestBody Ville nouvelleVille) {
        for (Ville ville : villes) {
            if (ville.getNom().equalsIgnoreCase(nouvelleVille.getNom())) {
                return ResponseEntity.badRequest().body("La ville existe déjà");
            }
        }
        nouvelleVille.setId(nextId++);
        villes.add(nouvelleVille);
        return ResponseEntity.ok("Ville insérée avec succès");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateVille(@PathVariable int id, @RequestBody Ville villeModifiee) {
        for (Ville ville : villes) {
            if (ville.getId() == id) {
                ville.setNom(villeModifiee.getNom());
                ville.setPopulation(villeModifiee.getPopulation());
                return ResponseEntity.ok("Ville modifiée avec succès");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ville introuvable");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVille(@PathVariable int id) {
        for (Ville ville : villes) {
            if (ville.getId() == id) {
                villes.remove(ville);
                return ResponseEntity.ok("Ville supprimée avec succès");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ville introuvable");
    }

}
