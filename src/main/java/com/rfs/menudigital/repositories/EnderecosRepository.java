/**
 *
 * @author Roselito
 */
package com.rfs.menudigital.repositories;

import com.rfs.menudigital.models.Endereco;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface EnderecosRepository extends CrudRepository<Endereco, Integer> {

    List<Endereco> findByIdCustomer(Integer idCustomer);
}
