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
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    private String senha;
    private String endereco;
    private String numero;
    private String bairro;
    private String estado;
    private String cidade;
    private String telefone;
    private String email;
}
