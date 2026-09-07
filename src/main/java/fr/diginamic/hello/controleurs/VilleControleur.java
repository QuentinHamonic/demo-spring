package fr.diginamic.hello.controleurs;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import fr.diginamic.hello.dto.VilleDto;
import fr.diginamic.hello.exceptions.VilleException;
import fr.diginamic.hello.services.VilleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;

@RestController
@RequestMapping("/villes")
public class VilleControleur {

    private final VilleService villeService;

    public VilleControleur(VilleService villeService) {
        this.villeService = villeService;
    }

    @Operation(summary = "Retourne la liste des villes, paginée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Page de villes au format JSON",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VilleDto.class)) })
    })
    @GetMapping
    public Page<VilleDto> getVilles(
            @Parameter(description = "Numéro de page (0-indexé)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return villeService.extractVilles(pageable);
    }

    @Operation(summary = "Retourne une ville à partir de son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Ville au format JSON",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VilleDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Ville non trouvée", content = @Content())
    })
    @GetMapping("/{id}")
    public VilleDto getVilleParId(
            @Parameter(description = "Identifiant de la ville à récupérer", example = "1", required = true) @PathVariable int id)
            throws VilleException {
        return villeService.extractVille(id);
    }

    @Operation(summary = "Retourne une ville à partir de son nom exact")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Ville au format JSON",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VilleDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Ville non trouvée", content = @Content())
    })
    @GetMapping("/nom/{nom}")
    public VilleDto getVilleParNom(
            @Parameter(description = "Nom exact de la ville à récupérer", example = "Nice", required = true) @PathVariable String nom)
            throws VilleException {
        return villeService.extractVille(nom);
    }

    @Operation(summary = "Crée une nouvelle ville")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Liste des villes après insertion",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VilleDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Ville invalide, déjà existante ou département inconnu", content = @Content())
    })
    @PostMapping
    public ResponseEntity<List<VilleDto>> insertVille(
            @Parameter(description = "Ville à créer", required = true) @Valid @RequestBody VilleDto nouvelleVille,
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
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VilleDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Ville invalide, non trouvée ou département inconnu", content = @Content())
    })
    @PutMapping("/{id}")
    public ResponseEntity<List<VilleDto>> updateVille(
            @Parameter(description = "Identifiant de la ville à modifier", example = "1", required = true) @PathVariable int id,
            @Parameter(description = "Nouvelles données de la ville", required = true) @Valid @RequestBody VilleDto villeModifiee,
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
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VilleDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Ville non trouvée", content = @Content())
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<List<VilleDto>> deleteVille(
            @Parameter(description = "Identifiant de la ville à supprimer", example = "1", required = true) @PathVariable int id)
            throws VilleException {
        return ResponseEntity.ok(villeService.supprimerVille(id));
    }

    @GetMapping("/recherche/nom/{nom}")
    public List<VilleDto> getVillesParNom(@PathVariable String nom) throws VilleException {
        return villeService.getVillesParNom(nom);
    }

    @GetMapping("/recherche/population")
    public List<VilleDto> getVillesParPopulation(@RequestParam int min,
            @RequestParam(required = false) Integer max) throws VilleException {
        if (max == null) {
            return villeService.getVillesParPopulationMin(min);
        }
        return villeService.getVillesParPopulationEntre(min, max);
    }

    @Operation(summary = "Exporte au format CSV les villes dont la population est supérieure à min")
    @GetMapping("/export/csv")
    public void exportCsv(@RequestParam int min, HttpServletResponse response) throws VilleException, IOException {
        List<VilleDto> villes = villeService.getVillesParPopulationMin(min);
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"villes.csv\"");
        PrintWriter writer = response.getWriter();
        for (VilleDto ville : villes) {
            writer.append(ville.getNom()).append(";")
                    .append(String.valueOf(ville.getPopulation())).append(";")
                    .append(ville.getCodeDepartement() == null ? "" : ville.getCodeDepartement()).append(";")
                    .append(ville.getNomDepartement() == null ? "" : ville.getNomDepartement())
                    .append("\n");
        }
        response.flushBuffer();
    }

    private String construireMessageErreurs(BindingResult bindingResult) {
        StringBuilder message = new StringBuilder();
        for (FieldError erreur : bindingResult.getFieldErrors()) {
            message.append(erreur.getField()).append(" : ").append(erreur.getDefaultMessage()).append(". ");
        }
        return message.toString().trim();
    }

}
