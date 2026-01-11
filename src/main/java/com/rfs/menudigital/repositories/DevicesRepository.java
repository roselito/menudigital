package com.rfs.menudigital.repositories;

import com.rfs.menudigital.models.Device;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Roselito@RFS
 */
public interface DevicesRepository extends CrudRepository<Device, Integer> {
    
    List<Device> findByToken(String token);
}
