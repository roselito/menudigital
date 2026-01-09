/**
 *
 * @author Roselito
 */
package com.rfs.menudigital.controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
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
import com.rfs.menudigital.util.NumberConverter;
import jakarta.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.upload.dir}")
    private String uploadDir;

    @GetMapping("/favicon.ico")
    @ResponseBody
    public void dummyFavicon() {
        // empty body to avoid get favicon.ico error in JS console
    }

    @RequestMapping("/cores")
    public String checkout(Model model) {
        model.addAttribute("mensagem", "Teste com thymeleaf");
        return "index";
    }

    @RequestMapping(value = {"/", "/catalog"})
    public String mostrarCatalogo(HttpServletRequest request, Model model) {
        atualizarModelCatalogo(model);
        Customer customerCadastro = userSessionData.getCustomer();
        if (model.getAttribute("customerCadastro") != null) {
            customerCadastro = (Customer) model.getAttribute("customerCadastro");
        }
        model.addAttribute("customerCadastro", customerCadastro);
        return "catalog";
    }

    @PostMapping("/addCartItem")
    public String addCartItem(
            @ModelAttribute("itemCartao") CartItem itemCartao,
            Model model) {
        if (userSessionData.getCart().isEmpty()) {
            userSessionData.setCart(new ArrayList<>());
        }
        if (userSessionData.getCustomer() != null) {
            itemCartao.setUserId(userSessionData.getCustomer().getId());
        }
        itemCartao.setId(userSessionData.getCart().size() + 1);
        userSessionData.getCart().add(itemCartao);
        atualizarModelCatalogo(model);
        return "catalog :: cabecalhoFragment";
    }

    @GetMapping("/atualizarCabecalho")
    public String atualizarCabecalho(Model model) {
        atualizarModelCatalogo(model);
        return "catalog :: cabecalhoFragment";
    }

    @GetMapping("/buscarCEP/{cep}")
    @SuppressWarnings("CallToPrintStackTrace")
    public String buscarCep(@PathVariable String cep, Model model) {
        String json;
        Endereco address = (Endereco) model.getAttribute("address");
        if (address == null) {
            address = new Endereco();
        }
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
                address.setCep(cep);
                address.setEndereco(array[7]);
                address.setBairro(array[19]);
                address.setCidade(array[23]);
                address.setEstado(array[27]);
                address.setIdCustomer(userSessionData.getCustomer().getId());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException ex) {
            System.getLogger(CatalogController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        model.addAttribute("address", address);
        return "fragments/modals/address :: addressContent";
    }

    @GetMapping("/editarCadastro/{edicao}")
    public String editarCadastro(@PathVariable String edicao, Model model) {
        edicao = edicao == null ? "0" : edicao;
        String alteraSenha = "0";
        Customer customerCadastro = userSessionData.getCustomer();
        model.addAttribute("customerCadastro", customerCadastro);
        if (edicao.equals("0")) {
            model.addAttribute("border", "border-0");
            model.addAttribute("readonly", "readonly");
            model.addAttribute("disabled", "disabled");
        }
        if (edicao.equals("2")) {
            alteraSenha = "1";
            edicao = "1";
        }
        model.addAttribute("edicao", edicao);
        model.addAttribute("alteraSenha", alteraSenha);
        model.addAttribute("modais", Arrays.asList("#modalCadastro"));
        return "fragments/modals/cadastro :: cadastroContent";
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

    @PostMapping("/gravarCadastro")
    public String gravarCadastro(@Valid @ModelAttribute("customerCadastro") Customer customerCadastro, BindingResult result, Model model) {
        Integer id = customerCadastro.getId();
        String email = customerCadastro.getEmail();
        String senha = customerCadastro.getSenha();
        String senhaConf = customerCadastro.getSenhaConf();
        senha = senha == null ? "" : senha;
        senhaConf = senhaConf == null ? "" : senhaConf;
        String retorno = "catalog :: cabecalhoFragment";
        List<Customer> emails = customersRepository.findByEmail(email);
        if ((id == null || (!senha.isEmpty() || !senhaConf.isEmpty()))) {
            if (!senha.equals(senhaConf)) {
                result.rejectValue("senha", "", "Senha e confirmação não estão iguais.");
                result.rejectValue("senhaConf", "", "");
            }
            if (!senhaForte(senha)) {
                result.rejectValue("senha", "", "Senha deve ter pelo menos 4 números e letras misturados.");
            }
            if (result.getFieldErrorCount("senha") + result.getFieldErrorCount("senhaConf") > 0) {
                model.addAttribute("alteraSenha", "1");
                model.addAttribute("edicao", "0");
                model.addAttribute("border", "border-0");
                model.addAttribute("readonly", "readonly");
                model.addAttribute("disabled", "disabled");
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
            retorno = "fragments/modals/cadastro :: cadastroContent";
        } else {
            if (!senha.isEmpty()) {
                customerCadastro.setSenha(crypt.SHA(senha, "SHA-256"));
            } else {
                Customer c = customersRepository.findById(id).get();
                customerCadastro.setSenha(c.getSenha());
            }
            customersRepository.save(customerCadastro);
            userSessionData.setCustomer(customerCadastro);
            atualizarModelCatalogo(model);
        }
        return retorno;
    }

    @PostMapping("/gravarEndereco")
    public String gravarEndereco(@Valid @ModelAttribute("address") Endereco address, BindingResult result, Model model) {
        enderecosRepository.save(address);
        atualizarModelCatalogo(model);
        return "catalog :: cabecalhoFragment";
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

    @GetMapping("/mostrarEndereco/{id}")
    public String mostrarEndereco(@PathVariable String id, Model model) {
        Endereco endereco = new Endereco();
        if (!id.equals("0")) {
            endereco = enderecosRepository.findById(Integer.valueOf(id)).get();
        } else {
            endereco.setIdCustomer(userSessionData.getCustomer().getId());
        }
        model.addAttribute("address", endereco);
//        model.addAttribute("modais", Arrays.asList("#modalCadastro"));
        return "fragments/modals/address :: addressContent";
    }

    @GetMapping("/recuperarCarrinho")
    public String recuperarCarrinho(Model model) {
        atualizarModelCatalogo(model);
        model.addAttribute("modais", Arrays.asList("#modalCart"));
        return "fragments/modals/carrinho :: carrinhoContentFragment";
    }

    @PostMapping("/removerEndereco/{id}")
    public String removerEndereco(@PathVariable String id, Model model) {
        Endereco address = enderecosRepository.findById(Integer.valueOf(id)).get();
        enderecosRepository.delete(address);
        List<Endereco> enderecos = enderecosRepository.findByIdCustomer(userSessionData.getCustomer().getId());
        if (enderecos.isEmpty()) {
            atualizarModelCatalogo(model);
            return "catalog :: cabecalhoFragment";
        } else {
            model.addAttribute("enderecos", enderecos);
            model.addAttribute("modais", Arrays.asList("#modalEnderecos"));
            return "fragments/modals/enderecos :: enderecosFragment";
        }
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

    @GetMapping("/selecionar/{id}")
    public String selecionar(@PathVariable String id, Model model) {
        Item item = itensRepository.findById(Integer.valueOf(id)).get();
        CartItem itemCartao = new CartItem();
        itemCartao.setAmount(1);
        itemCartao.setDescription(item.getDescription());
        itemCartao.setItemId(item.getId());
        itemCartao.setTitle(item.getTitle());
        itemCartao.setImagePath(item.getImagePath());
        itemCartao.setUnitPrice(item.getPrice());
        itemCartao.setCalcPrice(item.getPrice());
        model.addAttribute("itemCartao", itemCartao);
        return "fragments/modals/selecionado :: selecionadoFragment";
    }

    @GetMapping("/telaLogin")
    public String telaLogin(Model model) {
        UserLogin userLogin = new UserLogin();
        model.addAttribute("userLogin", userLogin);
        model.addAttribute("modais", Arrays.asList("#modalLogin"));
        return "fragments/modals/login :: loginContent";
    }

    @PostMapping("/upload")
    public String fileUpload(@RequestParam("imgFile") MultipartFile imgFile, @RequestParam("id") String id, Model model) {
        String fileName = UUID.randomUUID().toString() + "-" + imgFile.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path targetLocation = uploadPath.resolve(fileName);
            Files.copy(imgFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Item item = itensRepository.findById(Integer.valueOf(id)).get();
        item.setImagePath(fileName);
        itensRepository.save(item);
        model.addAttribute("itemCartao", item);
        return "fragments/components/icones :: itemImagem";
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

    @RequestMapping("/teste")
    public String teste(Model model) {
        return "teste";
    }

    @RequestMapping("/testea")
    public String testea(Model model) {
        return "testea";
    }

    @GetMapping("/token/{s}")
    public String token(@PathVariable String s, Model model) {
        System.out.println(s);
        return "catalog :: cabecalhoFragment";
    }

    @GetMapping("/mensagem")
    public String enviarMensagem() {

        try {
            Notification notification = Notification.builder()
                    .setTitle("Mensagem")
                    .setBody("Teste firebase com sucesso")
                    .setImage("/imagens/ic_launcher.png")
                    .build();
            Message msg = Message.builder()
                    .setToken("cDwu1FYUNiTe-3nlGpVDp4:APA91bH3CJfK_QeWueG6-qFdKOsjUbzIIsC3SDgEQDIeHxur0FGZAYHThKTpJXcRcc-yBGWtk6AbYSVyeedOspv6g0bqbVPy_P_ArLMgzgRln0E4InbMgF0")
                    .setNotification(notification)
                    .build();
            String idmsg = FirebaseMessaging.getInstance().send(msg);
            System.out.println(idmsg);
        } catch (FirebaseMessagingException ex) {
            System.getLogger(CatalogController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return "catalog :: cabecalhoFragment";
    }

}
