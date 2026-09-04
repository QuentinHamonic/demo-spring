package fr.diginamic.hello.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.diginamic.hello.Departement;

public interface DepartementRepository extends JpaRepository<Departement, Integer> {

    Optional<Departement> findByCodeIgnoreCase(String code);

}
