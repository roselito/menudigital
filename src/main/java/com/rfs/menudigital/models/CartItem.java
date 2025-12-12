/**
 *
 * @author Roselito
 */
package com.rfs.menudigital.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private Integer itemId;
    private String title;
    private String description;
    private Integer amount;
    private Double unitPrice;
    private String observations;
}
