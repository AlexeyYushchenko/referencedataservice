package ru.utlc.referencedataservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.utlc.referencedataservice.model.Country;

public interface CountryRepository extends JpaRepository<Country, Integer> {

}
