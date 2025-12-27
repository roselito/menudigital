/**
 *
 * @author Roselito
 */
package com.rfs.menudigital.controllers;

import com.rfs.menudigital.beans.UserSessionData;
import com.rfs.menudigital.models.CartItem;
import com.rfs.menudigital.models.Customer;
import com.rfs.menudigital.models.Endereco;
import com.rfs.menudigital.models.Item;
import com.rfs.menudigital.models.UserLogin;
import com.rfs.menudigital.repositories.CustomersRepository;
import com.rfs.menudigital.repositories.EnderecosRepository;
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
import com.rfs.menudigital.util.Crypt;
import com.rfs.menudigital.util.IterableToList;
import com.rfs.menudigital.util.NumberConverter;
import jakarta.validation.Valid;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    @Autowired
    private EnderecosRepository enderecosRepository;
    @Autowired
    private Crypt crypt;

    @GetMapping("/favicon.ico")
    @ResponseBody
    public void dummyFavicon() {
        // empty body to avoid get favicon.ico error in JS console
    }

    @RequestMapping(value = "/catalog")
    public String mostrarCatalogo(HttpServletRequest request, Model model) {
        atualizarModelCatalogo(model);
        Customer customerCadastro = userSessionData.getCustomer();
        if (model.getAttribute("customerCadastro") != null) {
            customerCadastro = (Customer) model.getAttribute("customerCadastro");
        }
        model.addAttribute("customerCadastro", customerCadastro);
        return "catalog";
    }

    @GetMapping("/editarCadastro")
    public String editarCadastro(Model model) {
        Customer customerCadastro = userSessionData.getCustomer();
        model.addAttribute("customerCadastro", customerCadastro);
        model.addAttribute("modais", Arrays.asList("#modalCadastro"));
        return "fragments/modals/cadastro :: cadastroContent";
    }

    @GetMapping("/recuperarCarrinho")
    public String recuperarCarrinho(Model model) {
        atualizarModelCatalogo(model);
        model.addAttribute("modais", Arrays.asList("#modalCart"));
        return "fragments/modals/carrinho :: carrinhoContentFragment";
    }

    @PostMapping("/gravarCadastro")
    public String gravarCadastro(@Valid @ModelAttribute("customerCadastro") Customer customerCadastro, BindingResult result, Model model) {
        Integer id = customerCadastro.getId();
        String email = customerCadastro.getEmail();
        String senha = customerCadastro.getSenha();
        String senhaConf = customerCadastro.getSenhaConf();
        senha = senha == null ? "" : senha;
        senhaConf = senhaConf == null ? "" : senhaConf;
        String retorno = "redirect:/catalog";
        List<Customer> emails = customersRepository.findByEmail(email);
        if ((id == null || (!senha.isEmpty() || !senhaConf.isEmpty()))) {
            if (!senha.equals(senhaConf)) {
                result.rejectValue("senha", "", "Senha e confirmação não estão iguais.");
                result.rejectValue("senhaConf", "", "");
            }
            if (!senhaForte(senha)) {
                result.rejectValue("senha", "", "Senha deve ter pelo menos 4 números e letras misturados.");
            }
        }
        for (Customer c : emails) {
            if (!c.getId().equals(id)) {
                result.rejectValue("email", "", "E-mail já cadastrado.");
            }
        }
        if (result.hasErrors()) {
            model.addAttribute("customerCadastro", customerCadastro);
            model.addAttribute("modais", Arrays.asList("#modalCadastro"));
            model.addAttribute("toasts", Arrays.asList("#toastErrosCadastro"));
            model.addAttribute("errors", result.getFieldErrors());
            retorno = "catalog";
        } else {
            if (!senha.isEmpty()) {
                customerCadastro.setSenha(crypt.SHA(senha, "SHA-256"));
            } else {
                Customer c = customersRepository.findById(id).get();
                customerCadastro.setSenha(c.getSenha());
            }
            customersRepository.save(customerCadastro);
            userSessionData.setCustomer(customerCadastro);
        }
        return retorno;
    }

    @GetMapping("/telaLogin")
    public String telaLogin(Model model) {
        UserLogin userLogin = new UserLogin();
        model.addAttribute("userLogin", userLogin);
        model.addAttribute("modais", Arrays.asList("#modalLogin"));
        return "fragments/modals/login :: loginContent";
    }

    @GetMapping("/enderecos")
    public String enderecos(Model model) {
//        IterableToList<Endereco> itl = new IterableToList<>();
//        List<Endereco> enderecos = itl.converter(enderecosRepository.findAll());
        List<Endereco> enderecos = enderecosRepository.findByIdCustomer(userSessionData.getCustomer().getId());
        model.addAttribute("enderecos", enderecos);
        model.addAttribute("modais", Arrays.asList("#modalEnderecos"));
        return "fragments/modals/enderecos :: enderecosFragment";
    }

    @PostMapping("/login")
    public String logar(@Valid UserLogin userLogin, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        String retorno = "catalog :: cabecalhoFragment";
        userSessionData.setCustomer(new Customer());
        List<Customer> customers = customersRepository.findByEmail(userLogin.getEmailLogin());
        if (!customers.isEmpty()) {
            Customer foundCustomer = customers.get(0);
            String senhaCrypt = crypt.SHA(userLogin.getSenhaLogin(), "SHA-256");
            if (foundCustomer.getSenha().equalsIgnoreCase(senhaCrypt)) {
                userSessionData.setCustomer(foundCustomer);
                redirectAttributes.addFlashAttribute("successMessage", "Bem-vindo(a)!");
            } else {
                userSessionData.setCustomer(null);
                result.rejectValue("senhaLogin", "", "Senha incorreta!.");
            }
        } else {
            userSessionData.setCustomer(null);
            result.rejectValue("emailLogin", "", "E-mail não cadastrado.");
        }
        if (result.hasErrors()) {
            model.addAttribute("modais", Arrays.asList("#modalLogin"));
            model.addAttribute("toasts", Arrays.asList("#toastErrosLogin"));
            model.addAttribute("errors", result.getFieldErrors());
            return "fragments/modals/login :: loginContent";
        }
        atualizarModelCatalogo(model);
        return retorno;
    }

    @PostMapping("/logout")
    public String sair(Model model) {
        userSessionData.setCustomer(null);
        atualizarModelCatalogo(model);
        return "catalog :: cabecalhoFragment";
    }

    @PostMapping("/addCartItem/{title}/{description}/{amount}/{unitprice}/{observations}/{itemid}")
    public String addCartItem(
            @PathVariable String title,
            @PathVariable String description,
            @PathVariable String amount,
            @PathVariable String unitprice,
            @PathVariable String observations,
            @PathVariable String itemid,
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
        cartItem.setUnitPrice(Double.valueOf(unitprice));
        cartItem.setObservations(observations);
        userSessionData.getCart().add(cartItem);
        atualizarModelCatalogo(model);
        return "catalog :: cabecalhoFragment";
//        return "redirect:/catalog";
    }

    @PostMapping("/removeCartItem/{cartItemId}")
    public String removeCartItem(
            @PathVariable String cartItemId,
            Model model) {
        String retorno = "redirect:/catalog";
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
        Double totalCarrinho = 0.0;
        if (!cart.isEmpty()) {
            totalCarrinho = cart.stream().mapToDouble(item
                    -> item.getUnitPrice() == null ? 0.0 : item.getAmount() * item.getUnitPrice()).sum();
            model.addAttribute("cart", cart);
            model.addAttribute("totalCarrinho", totalCarrinho);
            retorno = "fragments/modals/carrinho :: cartItems";
        } else {
            retorno = "catalog :: cabecalhoFragment";
        }
        return retorno;
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
            String array[] = json.split("\n");
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

    public boolean senhaForte(String senha) {
        // a expressão mais completa:
        // "^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,10}$"
        Pattern p = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)[A-Za-z\\d]{4,}$");
        Matcher m = p.matcher(senha);
        return m.matches();
    }

    public void atualizarModelCatalogo(Model model) {
        List<Item> itens = new ArrayList((Collection) itensRepository.findAll());
        if (itens.isEmpty()) {
            cadastroInicial.executar();
        }
        if (userSessionData.getCustomer() == null) {
            userSessionData.setCustomer(new Customer());
        }
        List<CartItem> cart = userSessionData.getCart();
        Double totalCarrinho = 0.0;
        if (!cart.isEmpty()) {
            totalCarrinho = cart.stream().mapToDouble(item
                    -> item.getUnitPrice() == null ? 0.0 : item.getAmount() * item.getUnitPrice()).sum();
        }
        String userName = userSessionData.getCustomer() != null ? (userSessionData.getCustomer().getNome() != null ? userSessionData.getCustomer().getNome() : "") : "";
        model.addAttribute("itens", itens);
        model.addAttribute("userName", userName);
        model.addAttribute("cart", cart);
        model.addAttribute("totalCarrinho", totalCarrinho);
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {

        if (!file.isEmpty()) {
            try {
                System.out.println("//////////////////////////////////");
                System.out.println("");
                System.out.println("");
                System.out.println("");
                System.out.println("");
                System.out.println("");
                System.out.println("");
                System.out.println("");
                System.out.println("");
                System.out.println("");
                byte[] bytes = file.getBytes();
//                bytes = matOfByte.toArray();
                try {
                    String arquivo = "temp";
                    File imageFile = new File(arquivo);
                    try (OutputStream os = new FileOutputStream(imageFile)) {
                        os.write(bytes);
                    }
                    Tesseract tess4j = new Tesseract();
                    tess4j.setDatapath("/usr/share/tesseract-ocr/5/tessdata");
//                tess4j.setDatapath("C:\\Users\\rosel\\tessdata");
                    tess4j.setLanguage("por");
                    try {
                        String result = tess4j.doOCR(imageFile);
                        byte[] isoBytes = result.getBytes(StandardCharsets.UTF_8);
                        result = new String(isoBytes, "windows-1252");
                        System.out.println(result);
                    } catch (TesseractException e) {
                        System.err.println(e.getMessage());
                    }
                } catch (IOException ex) {
                    System.getLogger(CatalogController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
            } catch (IOException ex) {
                System.getLogger(CatalogController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }
        System.out.println("Fim!");
        System.out.println("/////////////////////////////////////////");
        return "redirect:/catalog";
    }

}
