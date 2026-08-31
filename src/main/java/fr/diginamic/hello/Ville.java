package fr.diginamic.hello;

public class Ville {

    private String nom;
    private String population;

    public Ville() {
    }

    public Ville(String nom, String population) {
        this.nom = nom;
        this.population = population;
    }

    public String getNom() {
        return nom;
    }

    public String getPopulation() {
        return population;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

}
