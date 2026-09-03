package fr.diginamic.hello.services;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.diginamic.hello.Ville;
import fr.diginamic.hello.exceptions.VilleException;
import fr.diginamic.hello.repositories.VilleDao;

@Service
public class VilleService {

    private final VilleDao villeDao;

    public VilleService(VilleDao villeDao) {
        this.villeDao = villeDao;
    }

    public List<Ville> extractVilles() {
        return villeDao.findAll();
    }

    public Ville extractVille(int idVille) throws VilleException {
        Ville ville = villeDao.findById(idVille);
        if (ville == null) {
            throw new VilleException("Ville introuvable");
        }
        return ville;
    }

    public Ville extractVille(String nom) throws VilleException {
        Ville ville = villeDao.findByNom(nom);
        if (ville == null) {
            throw new VilleException("Ville introuvable");
        }
        return ville;
    }

    public List<Ville> insertVille(Ville ville) throws VilleException {
        validerVille(ville);
        if (villeDao.findByNom(ville.getNom()) != null) {
            throw new VilleException("La ville existe déjà");
        }
        ville.setId(0);
        villeDao.save(ville);
        return villeDao.findAll();
    }

    public List<Ville> modifierVille(int idVille, Ville villeModifiee) throws VilleException {
        validerVille(villeModifiee);
        Ville ville = extractVille(idVille);
        ville.setNom(villeModifiee.getNom());
        ville.setPopulation(villeModifiee.getPopulation());
        villeDao.save(ville);
        return villeDao.findAll();
    }

    public List<Ville> supprimerVille(int idVille) throws VilleException {
        if (!villeDao.deleteById(idVille)) {
            throw new VilleException("Ville introuvable");
        }
        return villeDao.findAll();
    }

    public List<Ville> getVillesParNom(String debut) throws VilleException {
        List<Ville> villes = villeDao.findByNomStartingWith(debut);
        if (villes.isEmpty()) {
            throw new VilleException("Aucune ville dont le nom commence par " + debut + " n'a été trouvée");
        }
        return villes;
    }

    public List<Ville> getVillesParPopulationMin(int min) throws VilleException {
        List<Ville> villes = villeDao.findByPopulationGreaterThan(min);
        if (villes.isEmpty()) {
            throw new VilleException("Aucune ville n'a une population supérieure à " + min);
        }
        return villes;
    }

    public List<Ville> getVillesParPopulationEntre(int min, int max) throws VilleException {
        List<Ville> villes = villeDao.findByPopulationBetween(min, max);
        if (villes.isEmpty()) {
            throw new VilleException("Aucune ville n'a une population comprise entre " + min + " et " + max);
        }
        return villes;
    }

    private void validerVille(Ville ville) throws VilleException {
        if (ville.getNom() == null || ville.getNom().length() < 2) {
            throw new VilleException("Le nom de la ville doit contenir au moins 2 lettres");
        }
        if (ville.getPopulation() < 10) {
            throw new VilleException("La ville doit avoir au moins 10 habitants");
        }
    }

}
