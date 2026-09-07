package fr.diginamic.hello.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fr.diginamic.hello.Departement;
import fr.diginamic.hello.dto.DepartementDto;
import fr.diginamic.hello.dto.VilleDto;
import fr.diginamic.hello.dto.VilleMapper;
import fr.diginamic.hello.exceptions.DepartementException;
import fr.diginamic.hello.repositories.DepartementRepository;
import fr.diginamic.hello.repositories.VilleRepository;
import jakarta.annotation.PostConstruct;

@Service
public class DepartementService {

    private final DepartementRepository departementRepository;

    private final VilleRepository villeRepository;

    private final VilleMapper villeMapper;

    private final boolean init;

    @PostConstruct
    public void initData() {
        if (!init) {
            return;
        }
        RestTemplate restTemplate = new RestTemplate();
        DepartementDto[] departementsDto = restTemplate.getForObject("https://geo.api.gouv.fr/departements",
                DepartementDto[].class);
        for (DepartementDto departementDto : departementsDto) {
            departementRepository.findByCodeIgnoreCase(departementDto.getCode())
                    .ifPresent(departement -> {
                        departement.setNom(departementDto.getNom());
                        departementRepository.save(departement);
                    });
        }

    }

    public DepartementService(DepartementRepository departementRepository, VilleRepository villeRepository,
            VilleMapper villeMapper, @Value("${application.init}") boolean init) {
        this.departementRepository = departementRepository;
        this.villeRepository = villeRepository;
        this.villeMapper = villeMapper;
        this.init = init;
    }

    public List<Departement> extractDepartements() {
        return departementRepository.findAll();
    }

    public Departement extractDepartement(int idDepartement) throws DepartementException {
        return trouverDepartementParId(idDepartement);
    }

    public List<Departement> insertDepartement(Departement departement) throws DepartementException {
        validerDepartement(departement);
        departement.setId(0);
        departementRepository.save(departement);
        return departementRepository.findAll();
    }

    public List<Departement> modifierDepartement(int idDepartement, Departement departementModifie)
            throws DepartementException {
        validerDepartement(departementModifie);
        Departement departement = trouverDepartementParId(idDepartement);
        departement.setCode(departementModifie.getCode());
        departement.setNom(departementModifie.getNom());
        departementRepository.save(departement);
        return departementRepository.findAll();
    }

    public List<Departement> supprimerDepartement(int idDepartement) throws DepartementException {
        Departement departement = trouverDepartementParId(idDepartement);
        departementRepository.delete(departement);
        return departementRepository.findAll();
    }

    public List<VilleDto> topNVilles(int idDepartement, int n) throws DepartementException {
        trouverDepartementParId(idDepartement);
        return villeRepository.findByDepartementIdOrderByPopulationDesc(idDepartement, PageRequest.of(0, n))
                .stream().map(villeMapper::toDto).toList();
    }

    public List<VilleDto> villesParPopulationMin(int idDepartement, int min) throws DepartementException {
        trouverDepartementParId(idDepartement);
        return villeRepository.findByDepartementIdAndPopulationGreaterThanOrderByPopulationDesc(idDepartement, min)
                .stream().map(villeMapper::toDto).toList();
    }

    public List<VilleDto> villesParPopulation(int idDepartement, int min, int max) throws DepartementException {
        trouverDepartementParId(idDepartement);
        return villeRepository
                .findByDepartementIdAndPopulationGreaterThanAndPopulationLessThanOrderByPopulationDesc(idDepartement,
                        min,
                        max)
                .stream().map(villeMapper::toDto).toList();
    }

    public Departement extraireDepartementParCode(String code) throws DepartementException {
        return departementRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new DepartementException("Département introuvable"));
    }

    private Departement trouverDepartementParId(int idDepartement) throws DepartementException {
        return departementRepository.findById(idDepartement)
                .orElseThrow(() -> new DepartementException("Département introuvable"));
    }

    private void validerDepartement(Departement departement) throws DepartementException {
        if (departement.getCode() == null || departement.getCode().isBlank()) {
            throw new DepartementException("Le code du département est obligatoire");
        }
        if (departement.getNom() == null || departement.getNom().isBlank()) {
            throw new DepartementException("Le nom du département est obligatoire");
        }
    }

}
