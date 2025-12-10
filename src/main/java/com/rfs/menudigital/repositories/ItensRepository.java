/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.rfs.menudigital.repositories;

import com.rfs.menudigital.models.Item;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Roselito
 */
public interface ItensRepository extends CrudRepository<Item, Integer> {
}
