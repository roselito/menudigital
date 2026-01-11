package com.rfs.menudigital.models;

import lombok.Data;

/**
 *
 * @author roselito
 */
@Data
public class UserLogin {
    private String emailLogin;
    private String senhaLogin;
    private String fireBaseError;
}
