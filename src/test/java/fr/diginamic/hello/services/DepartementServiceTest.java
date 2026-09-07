package fr.diginamic.hello.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import fr.diginamic.hello.Departement;
import fr.diginamic.hello.repositories.DepartementRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DepartementServiceTest {

    @Autowired
    private DepartementService departementService;

    @Autowired
    private DepartementRepository departementRepository;

    @Test
    void extractDepartementsRetourneLesDepartementsEnBase() {
        departementRepository.save(new Departement("34", "Hérault"));
        departementRepository.save(new Departement("30", "Gard"));

        List<Departement> departements = departementService.extractDepartements();

        assertEquals(2, departements.size());
    }
}
