/**
 *
 * @author Roselito
 */
package com.rfs.menudigital.models;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer orderId;
    private Integer userId;
    private Integer itemId;
    private Integer amount;
    private Integer unitPrice;
    private String observations;
}
