/**
 *
 * @author Roselito
 */
package com.rfs.menudigital.repositories;

import com.rfs.menudigital.models.Customer;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface CustomersRepository extends CrudRepository<Customer, Integer> {
    
    List<Customer> findByEmail(String email);
}