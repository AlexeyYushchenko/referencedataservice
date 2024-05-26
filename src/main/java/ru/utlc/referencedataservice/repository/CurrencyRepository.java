package ru.utlc.referencedataservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.utlc.referencedataservice.model.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {

}