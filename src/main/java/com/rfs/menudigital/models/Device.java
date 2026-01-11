package com.rfs.menudigital.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import java.util.Date;
import lombok.Data;

/**
 *
 * @author Roselito@RFS
 */
@Data
@Entity
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    private String token;
    private Date created;
    @Transient
    private String fireBaseError;
}
