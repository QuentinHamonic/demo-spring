package fr.diginamic.hello.services;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.diginamic.hello.Ville;
import fr.diginamic.hello.exceptions.VilleException;
import fr.diginamic.hello.repositories.VilleRepository;

@Service
public class VilleService {

    private final VilleRepository villeRepository;

    public VilleService(VilleRepository villeRepository) {
        this.villeRepository = villeRepository;
    }

    public List<Ville> getVilles() {
        return villeRepository.findAll();
    }

    public Ville getVilleById(int id) {
        return villeRepository.findById(id).orElse(null);
    }

    public Ville insertVille(Ville nouvelleVille) throws VilleException {
        validerVille(nouvelleVille);
        if (villeRepository.findByNom(nouvelleVille.getNom()) != null) {
            return null;
        }
        nouvelleVille.setId(0);
        return villeRepository.save(nouvelleVille);
    }

    public boolean updateVille(int id, Ville villeModifiee) throws VilleException {
        validerVille(villeModifiee);
        return villeRepository.findById(id)
                .map(ville -> {
                    ville.setNom(villeModifiee.getNom());
                    ville.setPopulation(villeModifiee.getPopulation());
                    villeRepository.save(ville);
                    return true;
                })
                .orElse(false);
    }

    private void validerVille(Ville ville) throws VilleException {
        if (ville.getNom() == null || ville.getNom().length() < 2) {
            throw new VilleException("Le nom de la ville doit contenir au moins 2 lettres");
        }
        if (Integer.parseInt(ville.getPopulation()) < 10) {
            throw new VilleException("La ville doit avoir au moins 10 habitants");
        }
    }

    public boolean deleteVille(int id) {
        return villeRepository.deleteById(id);
    }

    public List<Ville> getVillesParNom(String nom) throws VilleException {
        List<Ville> villes = villeRepository.findByNomStartingWith(nom);
        if (villes.isEmpty()) {
            throw new VilleException("Aucune ville dont le nom commence par " + nom + " n'a été trouvée");
        }
        return villes;
    }

    public List<Ville> getVillesParPopulationMin(int min) throws VilleException {
        List<Ville> villes = villeRepository.findByPopulationGreaterThan(min);
        if (villes.isEmpty()) {
            throw new VilleException("Aucune ville n'a une population supérieure à " + min);
        }
        return villes;
    }

    public List<Ville> getVillesParPopulationEntre(int min, int max) throws VilleException {
        List<Ville> villes = villeRepository.findByPopulationBetween(min, max);
        if (villes.isEmpty()) {
            throw new VilleException("Aucune ville n'a une population comprise entre " + min + " et " + max);
        }
        return villes;
    }

}
