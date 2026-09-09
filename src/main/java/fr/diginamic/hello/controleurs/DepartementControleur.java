package fr.diginamic.hello.controleurs;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
import fr.diginamic.hello.services.VilleService;
import io.swagger.v3.oas.annotations.Operation;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/departements")
public class DepartementControleur {

    private final DepartementService departementService;

    private final VilleService villeService;

    public DepartementControleur(DepartementService departementService, VilleService villeService) {
        this.departementService = departementService;
        this.villeService = villeService;
    }

    @Operation(summary = "Retourne la liste de tous les départements")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @GetMapping
    public List<Departement> getDepartements() {
        return departementService.extractDepartements();
    }

    @Operation(summary = "Retourne un département à partir de son identifiant")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @GetMapping("/{id}")
    public Departement getDepartement(@PathVariable int id) throws DepartementException {
        return departementService.extractDepartement(id);
    }

    @Operation(summary = "Crée un nouveau département")
    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<List<Departement>> insertDepartement(@RequestBody Departement departement) throws DepartementException {
        return ResponseEntity.ok(departementService.insertDepartement(departement));
    }

    @Operation(summary = "Modifie un département existant à partir de son identifiant")
    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    public ResponseEntity<List<Departement>> updateDepartement(@PathVariable int id, @RequestBody Departement departement)
            throws DepartementException {
        return ResponseEntity.ok(departementService.modifierDepartement(id, departement));
    }

    @Operation(summary = "Supprime un département à partir de son identifiant")
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<List<Departement>> deleteDepartement(@PathVariable int id) throws DepartementException {
        return ResponseEntity.ok(departementService.supprimerDepartement(id));
    }

    @Operation(summary = "Retourne les n plus grandes villes d'un département")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @GetMapping("/{id}/villes/top/{n}")
    public List<VilleDto> getTopNVilles(@PathVariable int id, @PathVariable int n) throws DepartementException {
        return departementService.topNVilles(id, n);
    }

    @Operation(summary = "Retourne les villes d'un département dont la population est supérieure à min, ou comprise entre min et max si max est fourni")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @GetMapping("/{id}/villes")
    public List<VilleDto> getVillesParPopulation(@PathVariable int id, @RequestParam int min,
            @RequestParam(required = false) Integer max) throws DepartementException {
        if (max == null) {
            return departementService.villesParPopulationMin(id, min);
        }
        return departementService.villesParPopulation(id, min, max);
    }

    @Operation(summary = "Exporte au format PDF la fiche d'un département à partir de son code")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @GetMapping("/{code}/export/pdf")
    public void exportPdf(@PathVariable String code, HttpServletResponse response) throws Exception {
        Departement departement = departementService.extraireDepartementParCode(code);
        List<VilleDto> villes = villeService.getVillesParDepartementId(departement.getId());
        villes = villes.stream()
                .sorted((v1, v2) -> Integer.compare(v2.getPopulation(), v1.getPopulation()))
                .toList();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"departement-" + code + ".pdf\"");

        String nomDepartement = departement.getNom() != null ? departement.getNom() : "Département " + departement.getCode();
        int populationTotale = villes.stream().mapToInt(VilleDto::getPopulation).sum();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        BaseColor headerColor = new BaseColor(30, 60, 100);

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        document.addTitle(nomDepartement);

        document.add(new Paragraph(nomDepartement, titleFont));
        document.add(new Paragraph("Code : " + departement.getCode(), labelFont));
        document.add(new Paragraph("Nombre de villes : " + villes.size(), labelFont));
        document.add(new Paragraph("Population totale : " + populationTotale + " habitants", labelFont));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 3, 1 });

        PdfPCell headerVille = new PdfPCell(new Phrase("Ville", headerFont));
        headerVille.setBackgroundColor(headerColor);
        headerVille.setPadding(6);
        table.addCell(headerVille);

        PdfPCell headerPopulation = new PdfPCell(new Phrase("Population", headerFont));
        headerPopulation.setBackgroundColor(headerColor);
        headerPopulation.setPadding(6);
        headerPopulation.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(headerPopulation);

        for (VilleDto ville : villes) {
            PdfPCell nomCell = new PdfPCell(new Phrase(ville.getNom(), cellFont));
            nomCell.setPadding(5);
            table.addCell(nomCell);

            PdfPCell populationCell = new PdfPCell(new Phrase(String.valueOf(ville.getPopulation()), cellFont));
            populationCell.setPadding(5);
            populationCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(populationCell);
        }

        document.add(table);
        document.close();
        response.flushBuffer();
    }

}
