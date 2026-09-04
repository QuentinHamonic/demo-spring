package fr.diginamic.hello.controleurs;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.diginamic.hello.Departement;
import fr.diginamic.hello.dto.VilleDto;
import fr.diginamic.hello.exceptions.DepartementException;
import fr.diginamic.hello.services.DepartementService;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/departements")
public class DepartementControleur {

    private final DepartementService departementService;

    public DepartementControleur(DepartementService departementService) {
        this.departementService = departementService;
    }

    @Operation(summary = "Retourne la liste de tous les départements")
    @GetMapping
    public List<Departement> getDepartements() {
        return departementService.extractDepartements();
    }

    @Operation(summary = "Retourne un département à partir de son identifiant")
    @GetMapping("/{id}")
    public Departement getDepartement(@PathVariable int id) throws DepartementException {
        return departementService.extractDepartement(id);
    }

    @Operation(summary = "Crée un nouveau département")
    @PostMapping
    public ResponseEntity<List<Departement>> insertDepartement(@RequestBody Departement departement) throws DepartementException {
        return ResponseEntity.ok(departementService.insertDepartement(departement));
    }

    @Operation(summary = "Modifie un département existant à partir de son identifiant")
    @PutMapping("/{id}")
    public ResponseEntity<List<Departement>> updateDepartement(@PathVariable int id, @RequestBody Departement departement)
            throws DepartementException {
        return ResponseEntity.ok(departementService.modifierDepartement(id, departement));
    }

    @Operation(summary = "Supprime un département à partir de son identifiant")
    @DeleteMapping("/{id}")
    public ResponseEntity<List<Departement>> deleteDepartement(@PathVariable int id) throws DepartementException {
        return ResponseEntity.ok(departementService.supprimerDepartement(id));
    }

    @Operation(summary = "Retourne les n plus grandes villes d'un département")
    @GetMapping("/{id}/villes/top/{n}")
    public List<VilleDto> getTopNVilles(@PathVariable int id, @PathVariable int n) throws DepartementException {
        return departementService.topNVilles(id, n);
    }

    @Operation(summary = "Retourne les villes d'un département dont la population est supérieure à min, ou comprise entre min et max si max est fourni")
    @GetMapping("/{id}/villes")
    public List<VilleDto> getVillesParPopulation(@PathVariable int id, @RequestParam int min,
            @RequestParam(required = false) Integer max) throws DepartementException {
        if (max == null) {
            return departementService.villesParPopulationMin(id, min);
        }
        return departementService.villesParPopulation(id, min, max);
    }

}
