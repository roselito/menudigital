/**
 *
 * @author Roselito
 */
package com.rfs.menudigital.beans;

import com.rfs.menudigital.models.CartItem;
import com.rfs.menudigital.models.Customer;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION)
@Data
public class UserSessionData {
    private Customer customer;
    private List<CartItem> cart = new ArrayList();
    private String closeModalCart = "ocultar";
}
