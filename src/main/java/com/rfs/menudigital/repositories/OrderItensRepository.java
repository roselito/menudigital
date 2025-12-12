/**
 *
 * @author Roselito
 */
package com.rfs.menudigital.repositories;

import com.rfs.menudigital.models.OrderItem;
import org.springframework.data.repository.CrudRepository;


public interface OrderItensRepository extends CrudRepository<OrderItem, Integer> {    
}
