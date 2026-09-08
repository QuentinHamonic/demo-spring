package fr.diginamic.hello.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import fr.diginamic.hello.Departement;
import fr.diginamic.hello.Ville;
import fr.diginamic.hello.dto.VilleDto;
import fr.diginamic.hello.exceptions.DepartementException;
import fr.diginamic.hello.repositories.DepartementRepository;
import fr.diginamic.hello.repositories.VilleRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DepartementServiceTest {

    @Autowired
    private DepartementService departementService;

    @Autowired
    private DepartementRepository departementRepository;

    @Autowired
    private VilleRepository villeRepository;

    @Test
    void extractDepartementsRetourneLesDepartementsEnBase() {
        creerDepartement("34", "Hérault");
        creerDepartement("30", "Gard");

        List<Departement> departements = departementService.extractDepartements();

        assertEquals(2, departements.size());
    }

    @Test
    void extractDepartementRetourneLeDepartementDemande() throws DepartementException {
        Departement herault = creerDepartement("34", "Hérault");

        Departement resultat = departementService.extractDepartement(herault.getId());

        assertEquals("Hérault", resultat.getNom());
    }

    @Test
    void extractDepartementLeveUneExceptionSiIdentifiantInconnu() {
        assertThrows(DepartementException.class, () -> departementService.extractDepartement(999));
    }

    @Test
    void insertDepartementAjouteLeDepartement() throws DepartementException {
        List<Departement> resultat = departementService.insertDepartement(new Departement("34", "Hérault"));

        assertEquals(1, resultat.size());
        assertEquals("Hérault", resultat.get(0).getNom());
    }

    @Test
    void insertDepartementRefuseUnCodeVide() {
        Departement departementInvalide = new Departement("", "Hérault");

        assertThrows(DepartementException.class,
                () -> departementService.insertDepartement(departementInvalide));
    }

    @Test
    void modifierDepartementMetAJourLeDepartement() throws DepartementException {
        Departement herault = creerDepartement("34", "Ancien nom");

        departementService.modifierDepartement(herault.getId(), new Departement("34", "Hérault"));

        Departement resultat = departementRepository.findById(herault.getId()).orElseThrow();
        assertEquals("Hérault", resultat.getNom());
    }

    @Test
    void supprimerDepartementRetireLeDepartement() throws DepartementException {
        Departement herault = creerDepartement("34", "Hérault");

        departementService.supprimerDepartement(herault.getId());

        assertFalse(departementRepository.existsById(herault.getId()));
    }

    @Test
    void topNVillesRetourneLesPlusPeupleesDansOrdreDecroissant() throws DepartementException {
        Departement herault = creerDepartement("34", "Hérault");
        creerVille("Montpellier", 302454, herault);
        creerVille("Béziers", 80000, herault);
        creerVille("Sète", 45000, herault);

        List<VilleDto> resultat = departementService.topNVilles(herault.getId(), 2);

        assertEquals(2, resultat.size());
        assertEquals("Montpellier", resultat.get(0).getNom());
        assertEquals("Béziers", resultat.get(1).getNom());
    }

    @Test
    void villesParPopulationMinRetourneLesVillesAuDessusDuMinimum() throws DepartementException {
        Departement herault = creerDepartement("34", "Hérault");
        creerVille("Montpellier", 302454, herault);
        creerVille("Béziers", 80000, herault);

        List<VilleDto> resultat = departementService.villesParPopulationMin(herault.getId(), 100000);

        assertEquals(1, resultat.size());
        assertEquals("Montpellier", resultat.get(0).getNom());
    }

    @Test
    void villesParPopulationRetourneLesVillesDansIntervalle() throws DepartementException {
        Departement herault = creerDepartement("34", "Hérault");
        creerVille("Montpellier", 302454, herault);
        creerVille("Béziers", 80000, herault);
        creerVille("Sète", 45000, herault);

        List<VilleDto> resultat = departementService.villesParPopulation(herault.getId(), 40000, 100000);

        assertEquals(2, resultat.size());
        assertEquals("Béziers", resultat.get(0).getNom());
        assertEquals("Sète", resultat.get(1).getNom());
    }

    @Test
    void extraireDepartementParCodeIgnoreLaCasse() throws DepartementException {
        creerDepartement("2A", "Corse-du-Sud");

        Departement resultat = departementService.extraireDepartementParCode("2a");

        assertEquals("Corse-du-Sud", resultat.getNom());
    }

    private Departement creerDepartement(String code, String nom) {
        return departementRepository.save(new Departement(code, nom));
    }

    private Ville creerVille(String nom, int population, Departement departement) {
        Ville ville = new Ville(nom, population);
        ville.setDepartement(departement);
        return villeRepository.save(ville);
    }
}
