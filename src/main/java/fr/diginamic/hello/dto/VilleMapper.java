package fr.diginamic.hello.dto;

import org.springframework.stereotype.Component;

import fr.diginamic.hello.Departement;
import fr.diginamic.hello.Ville;

@Component
public class VilleMapper {

    public VilleDto toDto(Ville ville) {
        VilleDto dto = new VilleDto();
        dto.setId(ville.getId());
        dto.setNom(ville.getNom());
        dto.setPopulation(ville.getPopulation());
        Departement departement = ville.getDepartement();
        if (departement != null) {
            dto.setIdDepartement(departement.getId());
            dto.setCodeDepartement(departement.getCode());
            dto.setNomDepartement(departement.getNom());
        }
        return dto;
    }

}
