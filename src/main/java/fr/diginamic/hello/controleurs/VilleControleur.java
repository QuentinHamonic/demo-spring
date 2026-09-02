package fr.diginamic.hello.controleurs;

import java.util.List;

import org.springframework.http.HttpStatus;
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

import fr.diginamic.hello.Ville;
import fr.diginamic.hello.exceptions.VilleException;
import fr.diginamic.hello.services.VilleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/villes")
public class VilleControleur {

    private final VilleService villeService;

    public VilleControleur(VilleService villeService) {
        this.villeService = villeService;
    }

    @GetMapping
    public List<Ville> getVilles() {
        return villeService.getVilles();
    }

    @Operation(summary = "Retourne une ville à partir de son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Ville au format JSON",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Ville.class)) }),
            @ApiResponse(responseCode = "404", description = "Ville non trouvée", content = @Content())
    })
    @GetMapping("/{id}")
    public ResponseEntity<Ville> getVille(
            @Parameter(description = "Identifiant de la ville à récupérer", example = "1", required = true) @PathVariable int id) {
        Ville ville = villeService.getVilleById(id);
        if (ville == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ville);
    }

    @Operation(summary = "Crée une nouvelle ville")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ville insérée avec succès", content = @Content()),
            @ApiResponse(responseCode = "400", description = "Ville invalide ou déjà existante", content = @Content())
    })
    @PostMapping
    public ResponseEntity<String> insertVille(
            @Parameter(description = "Ville à créer", required = true) @RequestBody Ville nouvelleVille) throws VilleException {
        Ville villeInseree = villeService.insertVille(nouvelleVille);
        if (villeInseree == null) {
            return ResponseEntity.badRequest().body("La ville existe déjà");
        }
        return ResponseEntity.ok("Ville insérée avec succès");
    }

    @Operation(summary = "Modifie une ville existante à partir de son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ville modifiée avec succès", content = @Content()),
            @ApiResponse(responseCode = "400", description = "Ville invalide", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Ville non trouvée", content = @Content())
    })
    @PutMapping("/{id}")
    public ResponseEntity<String> updateVille(
            @Parameter(description = "Identifiant de la ville à modifier", example = "1", required = true) @PathVariable int id,
            @Parameter(description = "Nouvelles données de la ville", required = true) @RequestBody Ville villeModifiee)
            throws VilleException {
        if (!villeService.updateVille(id, villeModifiee)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ville introuvable");
        }
        return ResponseEntity.ok("Ville modifiée avec succès");
    }

    @Operation(summary = "Supprime une ville à partir de son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ville supprimée avec succès", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Ville non trouvée", content = @Content())
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVille(
            @Parameter(description = "Identifiant de la ville à supprimer", example = "1", required = true) @PathVariable int id) {
        if (!villeService.deleteVille(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ville introuvable");
        }
        return ResponseEntity.ok("Ville supprimée avec succès");
    }

    @GetMapping("/recherche/nom/{nom}")
    public List<Ville> getVillesParNom(@PathVariable String nom) throws VilleException {
        return villeService.getVillesParNom(nom);
    }

    @GetMapping("/recherche/population")
    public List<Ville> getVillesParPopulation(@RequestParam int min,
            @RequestParam(required = false) Integer max) throws VilleException {
        if (max == null) {
            return villeService.getVillesParPopulationMin(min);
        }
        return villeService.getVillesParPopulationEntre(min, max);
    }

}
