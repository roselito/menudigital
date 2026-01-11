/**
 *
 * @author Roselito
 */
package com.rfs.menudigital.models;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    private String senha;
    private String telefone;
    private String email;
    @Transient
    private String senhaConf;
    @Transient
    private String fireBaseError;
}
