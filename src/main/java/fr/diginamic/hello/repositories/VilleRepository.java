package fr.diginamic.hello.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import fr.diginamic.hello.Ville;

public interface VilleRepository extends JpaRepository<Ville, Integer> {

    Optional<Ville> findByNomIgnoreCase(String nom);

    List<Ville> findByNomStartingWithIgnoreCase(String debut);

    List<Ville> findByPopulationGreaterThanOrderByPopulationDesc(int min);

    List<Ville> findByPopulationGreaterThanAndPopulationLessThanOrderByPopulationDesc(int min, int max);

    List<Ville> findByDepartementIdAndPopulationGreaterThanOrderByPopulationDesc(int idDepartement, int min);

    List<Ville> findByDepartementIdAndPopulationGreaterThanAndPopulationLessThanOrderByPopulationDesc(int idDepartement, int min,
            int max);

    List<Ville> findByDepartementIdOrderByPopulationDesc(int idDepartement, Pageable pageable);

    List<Ville> findByDepartementId(int idDepartement);

}
