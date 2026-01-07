/**
 *
 * @author Roselito
 */
package com.rfs.menudigital.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.format.annotation.NumberFormat;

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
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private Double unitPrice;
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private Double calcPrice;
    private String observations;
    private String imagePath;

}
