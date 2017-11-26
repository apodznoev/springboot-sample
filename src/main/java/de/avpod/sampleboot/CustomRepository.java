package de.avpod.sampleboot;

import org.springframework.data.repository.CrudRepository;

/**
 * @author Andrei.Podznoev
 * Date    21.11.2017.
 */
public interface CustomRepository extends CrudRepository<Customer, Long> {
    Customer findByFullName(String fullName);
}
