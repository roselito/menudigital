/**
 *
 * @author Roselito
 */
package com.rfs.menudigital.controllers;

import com.rfs.menudigital.beans.UserSessionData;
import com.rfs.menudigital.models.CartItem;
import com.rfs.menudigital.models.Customer;
import com.rfs.menudigital.models.Item;
import com.rfs.menudigital.repositories.CustomersRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.rfs.menudigital.util.CadastroInicial;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import com.rfs.menudigital.repositories.ItensRepository;
import com.rfs.menudigital.util.NumberConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Roselito
 */
@Controller
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CatalogController {

    @Autowired
    UserSessionData userSessionData;

    @Autowired
    NumberConverter numberConverter;

    @Autowired
    CadastroInicial cadastroInicial;

    @Autowired
    private ItensRepository itensRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @RequestMapping(value = "/catalog")
    public String mostrarCatalogo(HttpServletRequest request, Model model) {
        List<Item> itens = new ArrayList((Collection) itensRepository.findAll());
        if (itens.isEmpty()) {
            cadastroInicial.executar();
        }
        Customer customer = userSessionData.getCustomer();
        List<CartItem> cart = userSessionData.getCart();
        Double totalCarrinho = 0.0;
        if (!cart.isEmpty()) {
            totalCarrinho = cart.stream().mapToDouble(item
                    -> item.getUnitPrice() == null ? 0.0 : item.getAmount() * item.getUnitPrice()).sum();
        }
        String userName = customer != null ? customer.getNome() : "";
        model.addAttribute("itens", itens);
        model.addAttribute("userName", userName);
        model.addAttribute("cart", cart);
        model.addAttribute("totalCarrinho", totalCarrinho);
        model.addAttribute("closeModalCart", userSessionData.getCloseModalCart());
//        System.out.println(cart);
        return "catalog";
    }

    @PostMapping("/login")
    public String logar(
            @RequestParam String email,
            @RequestParam String senha,
            Model model,
            RedirectAttributes redirectAttributes) {
        String retorno = "redirect:/catalog";
        userSessionData.setCustomer(new Customer());
        List<Customer> customers = customersRepository.findByEmail(email);
        if (!customers.isEmpty()) {
            try {
                Customer foundCustomer = customers.get(0);

                MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
                byte messageDigest[] = algorithm.digest(senha.getBytes("UTF-8"));
                StringBuilder hexString = new StringBuilder();
                for (byte b : messageDigest) {
                    hexString.append(String.format("%02X", 0xFF & b));
                }
                String senhaCrypt = hexString.toString();
                if (foundCustomer.getSenha().equals(senhaCrypt)) {
                    userSessionData.setCustomer(foundCustomer);
                    redirectAttributes.addFlashAttribute("successMessage", "Bem-vindo(a)!");
                } else {
                    userSessionData.setCustomer(null);
                    redirectAttributes.addFlashAttribute("errorMessage", "Senha incorreta!");
                }
            } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
                System.getLogger(CatalogController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        } else {
            userSessionData.setCustomer(null);
            redirectAttributes.addFlashAttribute("errorMessage", "Cliente não encontrado!");
        }
        return retorno;
    }

    @PostMapping("/logout")
    public String sair(Model model) {
        userSessionData.setCustomer(null);
        return "redirect:/catalog";
    }

    @PostMapping("/addCartItem")
    public String addCartItem(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String amount,
            @RequestParam String unitprice,
            @RequestParam String observations,
            @RequestParam String itemid,
            Model model) {
        if (userSessionData.getCart().isEmpty()) {
            userSessionData.setCart(new ArrayList<>());
        }
        CartItem cartItem = new CartItem();
        if (userSessionData.getCustomer() != null) {
            cartItem.setUserId(userSessionData.getCustomer().getId());
        }
        cartItem.setDescription(description);
        cartItem.setId(userSessionData.getCart().size() + 1);
        cartItem.setTitle(title);
        cartItem.setItemId(Integer.valueOf(itemid));
        cartItem.setAmount(Integer.valueOf(amount));
        cartItem.setUnitPrice(Double.valueOf(numberConverter.ptBrEnUs(unitprice)));
        cartItem.setObservations(observations);
        userSessionData.getCart().add(cartItem);
        userSessionData.setCloseModalCart("ocultar");
        return "redirect:/catalog";
    }

    @PostMapping("/removeCartItem/{cartItemId}")
    public String removeCartItem(
            @PathVariable String cartItemId,
            Model model) {
        Integer id = Integer.valueOf(cartItemId);
        if (userSessionData.getCart().isEmpty()) {
            userSessionData.setCart(new ArrayList<>());
        }
        List<CartItem> cart = userSessionData.getCart();
        List<CartItem> temp = new ArrayList<>(cart);
        cart.clear();
        for (CartItem c : temp) {
            if (!c.getId().equals(id)) {
                cart.add(c);
            }
        }
        userSessionData.setCart(new ArrayList<>(cart));
        if (!userSessionData.getCart().isEmpty()) {
            userSessionData.setCloseModalCart("mostrar");
        } else {
            userSessionData.setCloseModalCart("ocultar");
        }
        Double totalCarrinho = 0.0;
        if (!cart.isEmpty()) {
            totalCarrinho = cart.stream().mapToDouble(item
                    -> item.getUnitPrice() == null ? 0.0 : item.getAmount() * item.getUnitPrice()).sum();
        }
        model.addAttribute("cart", cart);
        model.addAttribute("totalCarrinho", totalCarrinho);
        return "fragments/modals/carrinho :: cartItems";
    }

    @RequestMapping("/closeModalCart")
    public String closeModalCart(Model model
    ) {
        userSessionData.setCloseModalCart("ocultar");
        return "redirect:/catalog";
    }

    @RequestMapping("/")
    public String checkout(Model model
    ) {
        model.addAttribute("mensagem", "Teste com thymeleaf");
        return "index";
    }

    @GetMapping("/buscarCEP/{cep}")
    @SuppressWarnings("CallToPrintStackTrace")
    public String buscarCep(@PathVariable String cep, Model model) {
        String json;
        try {
            URL url = (new URI("http://viacep.com.br/ws/" + cep + "/json")).toURL();
            URLConnection urlConnection = url.openConnection();
            InputStream is = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonSb = new StringBuilder();
            br.lines().forEach(l -> jsonSb.append(l.trim()));
            json = jsonSb.toString();
            json = json.replaceAll("[{},:]", "").replaceAll("\"", "\n");
            String array[] = new String[30];
            array = json.split("\n");
            /*
            for (int i = 0; i < array.length; i++) {
                System.out.println(i);
                System.out.println(array[i]);
            }
             */
            if (array.length > 27) {
                model.addAttribute("endereco", array[7]);
                model.addAttribute("bairro", array[19]);
                model.addAttribute("cidade", array[23]);
                model.addAttribute("estado", array[27]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException ex) {
            System.getLogger(CatalogController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return "fragments/modals/address :: addressContent";
    }
}
