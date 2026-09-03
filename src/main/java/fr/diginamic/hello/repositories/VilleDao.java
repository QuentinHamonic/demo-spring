package fr.diginamic.hello.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.diginamic.hello.Ville;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public class VilleDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Ville> findAll() {
        return entityManager.createQuery("SELECT v FROM Ville v", Ville.class).getResultList();
    }

    public Ville findById(int id) {
        return entityManager.find(Ville.class, id);
    }

    public Ville findByNom(String nom) {
        TypedQuery<Ville> query = entityManager.createQuery(
                "SELECT v FROM Ville v WHERE LOWER(v.nom) = LOWER(:nom)", Ville.class);
        query.setParameter("nom", nom);
        return query.getResultList().stream().findFirst().orElse(null);
    }

    public List<Ville> findByNomStartingWith(String debut) {
        TypedQuery<Ville> query = entityManager.createQuery(
                "SELECT v FROM Ville v WHERE LOWER(v.nom) LIKE LOWER(CONCAT(:debut, '%'))", Ville.class);
        query.setParameter("debut", debut);
        return query.getResultList();
    }

    public List<Ville> findByPopulationGreaterThan(int min) {
        TypedQuery<Ville> query = entityManager.createQuery(
                "SELECT v FROM Ville v WHERE v.population > :min", Ville.class);
        query.setParameter("min", min);
        return query.getResultList();
    }

    public List<Ville> findByPopulationBetween(int min, int max) {
        TypedQuery<Ville> query = entityManager.createQuery(
                "SELECT v FROM Ville v WHERE v.population > :min AND v.population < :max", Ville.class);
        query.setParameter("min", min);
        query.setParameter("max", max);
        return query.getResultList();
    }

    @Transactional
    public Ville save(Ville ville) {
        if (ville.getId() == 0) {
            entityManager.persist(ville);
            return ville;
        }
        return entityManager.merge(ville);
    }

    @Transactional
    public boolean deleteById(int id) {
        Ville ville = findById(id);
        if (ville == null) {
            return false;
        }
        entityManager.remove(ville);
        return true;
    }

}
