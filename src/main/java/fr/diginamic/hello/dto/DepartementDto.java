package fr.diginamic.hello.dto;

public class DepartementDto {

    private String nom;

    private String code;

    private String codeRegion;

    public DepartementDto() {

    }

    public String getNom() {
        return nom;
    }

    public String getCode() {
        return code;
    }

    public String getCodeRegion() {
        return codeRegion;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setCodeRegion(String codeRegion) {
        this.codeRegion = codeRegion;
    }

}
