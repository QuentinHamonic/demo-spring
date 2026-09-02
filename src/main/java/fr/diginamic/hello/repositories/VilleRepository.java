package fr.diginamic.hello.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import fr.diginamic.hello.Ville;
import jakarta.annotation.PostConstruct;

@Repository
public class VilleRepository {

    private List<Ville> villes = new ArrayList<>();

    private int nextId = 1;

    @PostConstruct
    public void initData() {
        save(new Ville("Nice", "343000"));
        save(new Ville("Carcassonne", "47800"));
        save(new Ville("Narbonne", "53400"));
        save(new Ville("Lyon", "484000"));
        save(new Ville("Foix", "9700"));
        save(new Ville("Pau", "77200"));
        save(new Ville("Marseille", "850700"));
        save(new Ville("Tarbes", "40600"));
    }

    public List<Ville> findAll() {
        return villes;
    }

    public Optional<Ville> findById(int id) {
        return villes.stream()
                .filter(ville -> ville.getId() == id)
                .findFirst();
    }

    public Ville findByNom(String nom) {
        for (Ville ville : villes) {
            if (ville.getNom().equalsIgnoreCase(nom)) {
                return ville;
            }
        }
        return null;
    }

    public List<Ville> findByNomStartingWith(String debut) {
        return villes.stream()
                .filter(ville -> ville.getNom().toLowerCase().startsWith(debut.toLowerCase()))
                .toList();
    }

    public List<Ville> findByPopulationGreaterThan(int min) {
        return villes.stream()
                .filter(ville -> Integer.parseInt(ville.getPopulation()) > min)
                .toList();
    }

    public List<Ville> findByPopulationBetween(int min, int max) {
        return villes.stream()
                .filter(ville -> Integer.parseInt(ville.getPopulation()) > min)
                .filter(ville -> Integer.parseInt(ville.getPopulation()) < max)
                .toList();
    }

    public Ville save(Ville ville) {
        if (ville.getId() == 0) {
            ville.setId(nextId++);
            villes.add(ville);
        }
        return ville;
    }

    public boolean deleteById(int id) {
        return findById(id)
                .map(ville -> {
                    villes.remove(ville);
                    return true;
                })
                .orElse(false);
    }

}
