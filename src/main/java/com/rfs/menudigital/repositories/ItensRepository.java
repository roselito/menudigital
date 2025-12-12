/**
 *
 * @author Roselito
 */
package com.rfs.menudigital.repositories;

import com.rfs.menudigital.models.Item;
import org.springframework.data.repository.CrudRepository;

public interface ItensRepository extends CrudRepository<Item, Integer> {
}
