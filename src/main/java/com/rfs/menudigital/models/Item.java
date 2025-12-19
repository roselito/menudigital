/**
 *
 * @author Roselito
 */
package com.rfs.menudigital.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import java.util.Base64;
import lombok.Data;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

@Data
@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String description;
    private String title;
    @NumberFormat(style = Style.CURRENCY)
    private Double price;
    @Lob
    @Column(name = "image", columnDefinition = "MEDIUMBLOB")
    private byte[] image;

    public String generateBase64Image() {
        // Use a utility like Base64 from java.util or Apache Commons Codec
        if (this.image != null) {
            return Base64.getEncoder().encodeToString(this.image);
        } else {
            return null;
        }
    }
}
