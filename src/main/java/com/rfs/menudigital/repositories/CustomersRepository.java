/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.rfs.menudigital.repositories;

import com.rfs.menudigital.models.Customer;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Roselito
 */
public interface CustomersRepository extends CrudRepository<Customer, Integer> {
    
    List<Customer> findByEmail(String email);
}
