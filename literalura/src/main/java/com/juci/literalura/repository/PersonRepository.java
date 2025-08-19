package com.juci.literalura.repository;

import com.juci.literalura.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    // Busca por nome exato (útil se quiser manter)
    List<Person> findByName(String name);

    // Busca autores vivos em um determinado ano
    @Query("""
           SELECT p FROM Person p 
           WHERE p.birthYear IS NOT NULL 
             AND (:year >= p.birthYear) 
             AND (p.deathYear IS NULL OR :year <= p.deathYear)
           """)
    List<Person> findLivingAuthorsInYear(@Param("year") int year);

    // Busca por nome parcial, ignorando caixa
    @Query("SELECT p FROM Person p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Person> findByNameContainingIgnoreCase(@Param("name") String name);
}
