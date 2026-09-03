package fr.diginamic.hello.controleurs;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
import jakarta.validation.Valid;

@RestController
@RequestMapping("/villes")
public class VilleControleur {

    private final VilleService villeService;

    public VilleControleur(VilleService villeService) {
        this.villeService = villeService;
    }

    @Operation(summary = "Retourne la liste de toutes les villes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Liste des villes au format JSON",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Ville.class)) })
    })
    @GetMapping
    public List<Ville> getVilles() {
        return villeService.extractVilles();
    }

    @Operation(summary = "Retourne une ville à partir de son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Ville au format JSON",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Ville.class)) }),
            @ApiResponse(responseCode = "400", description = "Ville non trouvée", content = @Content())
    })
    @GetMapping("/{id}")
    public Ville getVilleParId(
            @Parameter(description = "Identifiant de la ville à récupérer", example = "1", required = true) @PathVariable int id)
            throws VilleException {
        return villeService.extractVille(id);
    }

    @Operation(summary = "Retourne une ville à partir de son nom exact")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Ville au format JSON",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Ville.class)) }),
            @ApiResponse(responseCode = "400", description = "Ville non trouvée", content = @Content())
    })
    @GetMapping("/nom/{nom}")
    public Ville getVilleParNom(
            @Parameter(description = "Nom exact de la ville à récupérer", example = "Nice", required = true) @PathVariable String nom)
            throws VilleException {
        return villeService.extractVille(nom);
    }

    @Operation(summary = "Crée une nouvelle ville")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Liste des villes après insertion",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Ville.class)) }),
            @ApiResponse(responseCode = "400", description = "Ville invalide ou déjà existante", content = @Content())
    })
    @PostMapping
    public ResponseEntity<List<Ville>> insertVille(
            @Parameter(description = "Ville à créer", required = true) @Valid @RequestBody Ville nouvelleVille,
            BindingResult bindingResult) throws VilleException {
        if (bindingResult.hasErrors()) {
            throw new VilleException(construireMessageErreurs(bindingResult));
        }
        return ResponseEntity.ok(villeService.insertVille(nouvelleVille));
    }

    @Operation(summary = "Modifie une ville existante à partir de son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Liste des villes après modification",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Ville.class)) }),
            @ApiResponse(responseCode = "400", description = "Ville invalide ou non trouvée", content = @Content())
    })
    @PutMapping("/{id}")
    public ResponseEntity<List<Ville>> updateVille(
            @Parameter(description = "Identifiant de la ville à modifier", example = "1", required = true) @PathVariable int id,
            @Parameter(description = "Nouvelles données de la ville", required = true) @Valid @RequestBody Ville villeModifiee,
            BindingResult bindingResult) throws VilleException {
        if (bindingResult.hasErrors()) {
            throw new VilleException(construireMessageErreurs(bindingResult));
        }
        return ResponseEntity.ok(villeService.modifierVille(id, villeModifiee));
    }

    @Operation(summary = "Supprime une ville à partir de son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Liste des villes après suppression",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Ville.class)) }),
            @ApiResponse(responseCode = "400", description = "Ville non trouvée", content = @Content())
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<List<Ville>> deleteVille(
            @Parameter(description = "Identifiant de la ville à supprimer", example = "1", required = true) @PathVariable int id)
            throws VilleException {
        return ResponseEntity.ok(villeService.supprimerVille(id));
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

    private String construireMessageErreurs(BindingResult bindingResult) {
        StringBuilder message = new StringBuilder();
        for (FieldError erreur : bindingResult.getFieldErrors()) {
            message.append(erreur.getField()).append(" : ").append(erreur.getDefaultMessage()).append(". ");
        }
        return message.toString().trim();
    }

}
