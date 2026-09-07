package fr.diginamic.hello.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fr.diginamic.hello.Departement;
import fr.diginamic.hello.Ville;
import fr.diginamic.hello.dto.VilleDto;
import fr.diginamic.hello.dto.VilleMapper;
import fr.diginamic.hello.exceptions.VilleException;
import fr.diginamic.hello.repositories.DepartementRepository;
import fr.diginamic.hello.repositories.VilleRepository;

@Service
public class VilleService {

    private final VilleRepository villeRepository;

    private final DepartementRepository departementRepository;

    public VilleService(VilleRepository villeRepository, DepartementRepository departementRepository) {
        this.villeRepository = villeRepository;
        this.departementRepository = departementRepository;
    }

    public Page<VilleDto> extractVilles(Pageable pageable) {
        return villeRepository.findAll(pageable).map(VilleMapper::toDto);
    }

    public VilleDto extractVille(int idVille) throws VilleException {
        return VilleMapper.toDto(trouverVilleParId(idVille));
    }

    public VilleDto extractVille(String nom) throws VilleException {
        Ville ville = villeRepository.findByNomIgnoreCase(nom)
                .orElseThrow(() -> new VilleException("Ville introuvable"));
        return VilleMapper.toDto(ville);
    }

    public List<VilleDto> insertVille(VilleDto villeDto) throws VilleException {
        validerVille(villeDto);
        if (villeRepository.findByNomIgnoreCase(villeDto.getNom()).isPresent()) {
            throw new VilleException("La ville existe déjà");
        }
        Departement departement = resoudreDepartement(villeDto);
        Ville ville = new Ville(villeDto.getNom(), villeDto.getPopulation());
        ville.setDepartement(departement);
        villeRepository.save(ville);
        return toDtoList(villeRepository.findAll());
    }

    public List<VilleDto> modifierVille(int idVille, VilleDto villeDto) throws VilleException {
        validerVille(villeDto);
        Departement departement = resoudreDepartement(villeDto);
        Ville ville = trouverVilleParId(idVille);
        ville.setNom(villeDto.getNom());
        ville.setPopulation(villeDto.getPopulation());
        ville.setDepartement(departement);
        villeRepository.save(ville);
        return toDtoList(villeRepository.findAll());
    }

    public List<VilleDto> supprimerVille(int idVille) throws VilleException {
        Ville ville = trouverVilleParId(idVille);
        villeRepository.delete(ville);
        return toDtoList(villeRepository.findAll());
    }

    public List<VilleDto> getVillesParNom(String debut) throws VilleException {
        List<Ville> villes = villeRepository.findByNomStartingWithIgnoreCase(debut);
        if (villes.isEmpty()) {
            throw new VilleException("Aucune ville dont le nom commence par " + debut + " n'a été trouvée");
        }
        return toDtoList(villes);
    }

    public List<VilleDto> getVillesParPopulationMin(int min) throws VilleException {
        List<Ville> villes = villeRepository.findByPopulationGreaterThanOrderByPopulationDesc(min);
        if (villes.isEmpty()) {
            throw new VilleException("Aucune ville n'a une population supérieure à " + min);
        }
        return toDtoList(villes);
    }

    public List<VilleDto> getVillesParPopulationEntre(int min, int max) throws VilleException {
        List<Ville> villes = villeRepository.findByPopulationGreaterThanAndPopulationLessThanOrderByPopulationDesc(min, max);
        if (villes.isEmpty()) {
            throw new VilleException("Aucune ville n'a une population comprise entre " + min + " et " + max);
        }
        return toDtoList(villes);
    }

    public List<VilleDto> getVillesParDepartementId(int idDepartement) {
        return toDtoList(villeRepository.findByDepartementId(idDepartement));
    }

    private Ville trouverVilleParId(int idVille) throws VilleException {
        return villeRepository.findById(idVille).orElseThrow(() -> new VilleException("Ville introuvable"));
    }

    private Departement resoudreDepartement(VilleDto villeDto) throws VilleException {
        Integer idDepartement = villeDto.getIdDepartement();
        String codeDepartement = villeDto.getCodeDepartement();
        if (idDepartement == null && (codeDepartement == null || codeDepartement.isBlank())) {
            throw new VilleException("Le département de la ville est obligatoire (code ou identifiant)");
        }
        Departement departement = null;
        if (idDepartement != null) {
            departement = departementRepository.findById(idDepartement).orElse(null);
        }
        if (departement == null && codeDepartement != null && !codeDepartement.isBlank()) {
            departement = departementRepository.findByCodeIgnoreCase(codeDepartement).orElse(null);
        }
        if (departement != null) {
            return departement;
        }
        if (codeDepartement != null && !codeDepartement.isBlank()) {
            return departementRepository.save(new Departement(codeDepartement, null));
        }
        throw new VilleException("Département inconnu");
    }

    private void validerVille(VilleDto villeDto) throws VilleException {
        if (villeDto.getNom() == null || villeDto.getNom().length() < 2) {
            throw new VilleException("Le nom de la ville doit contenir au moins 2 lettres");
        }
        if (villeDto.getPopulation() < 10) {
            throw new VilleException("La ville doit avoir au moins 10 habitants");
        }
    }

    private List<VilleDto> toDtoList(List<Ville> villes) {
        return villes.stream().map(VilleMapper::toDto).toList();
    }

}
