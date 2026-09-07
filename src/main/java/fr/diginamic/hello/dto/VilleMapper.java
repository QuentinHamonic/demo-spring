package fr.diginamic.hello.dto;

import fr.diginamic.hello.Departement;
import fr.diginamic.hello.Ville;

public class VilleMapper {

    private VilleMapper() {
    }

    public static VilleDto toDto(Ville ville) {
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
