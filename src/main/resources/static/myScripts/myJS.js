var modal = null;
var enderecos = false;
var idImagem = "";

var formatter = new Intl.NumberFormat('en-US', {
    style: 'decimal', // or 'currency', 'percent'
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
});

var formatterBR = new Intl.NumberFormat('pt-BR', {
    style: 'decimal', // or 'currency', 'percent'
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
});

$(document).ready(function () {
    var SPMaskBehavior = function (val) {
        return val.replace(/\D/g, '').length === 11 ? '(00) 00000-0000' : '(00) 0000-00000';
    };
    var spOptions = {
        onKeyPress: function (val, e, field, options) {
            field.mask(SPMaskBehavior.apply({}, arguments), options);
        }
    };
    $('.cpfMask').mask('000.000.000-00');
    $('.phoneMask').mask(SPMaskBehavior, spOptions);
    $('.dateMask').mask('00/00/0000');

    // Eliminar warning js de aria-hidden
    document.querySelectorAll('.modal').forEach((modal) => {
        modal.addEventListener('hide.bs.modal', () => {
            // Blur the currently focused element before the modal is hidden
            if (document.activeElement instanceof HTMLElement) {
                document.activeElement.blur();
            }
        });
    });
});

$('#modalSelecionado').on('show.bs.modal', function (event) {
    modal = $(this);
});

function addCartItem(event) {
    event.preventDefault();
    var form = $('#formCartItem');
    $(":input:disabled").prop('disabled', false);
    $.ajax({
        type: 'POST',
        url: "/addCartItem",
        data: form.serialize(),
        success: function (htmlContent) {
            $('#modalSelecionado').modal('hide');
            $('#cabecalho').html(htmlContent);
        }
    });
}

function alterarSenha() {
    $('#divSenha').removeClass('d-none');
    $('#divGravar').removeClass('d-none');
    $('#divBarra1').addClass('d-none');
}

function atualizarCabecalho() {
    $.ajax({
        type: 'GET',
        url: "/atualizarCabecalho",
        success: function (htmlContent) {
            $('#cabecalho').html(htmlContent);
        }
    });
}

function buscarCEP() {
    var cepDigitado = document.getElementById('cep').value;
    $('.wait').val('Pesquisando...');
    $.ajax({
        type: 'GET',
        url: "/buscarCEP/" + cepDigitado,
        success: function (htmlContent) {
            // div #modalAddressContent engloba o div do fragmento que o backend vai retornar
            // o div #modalAddressContent é substituído pelo html que vem do return de /buscarCEP
            // e os dados são extraídos dos atributos adicionados ao model, também em  /buscarCEP
            $('#modalAddressContent').html(htmlContent);
        }
    });
}

function convertPtBrToEnUs(ptBrNumberString) {
    let cleanedString = String(ptBrNumberString);
    cleanedString = cleanedString.replace(/R/g, '');
    cleanedString = cleanedString.replace(/\$/g, '');
    cleanedString = cleanedString.replace(/\ /g, '');
    cleanedString = cleanedString.replace(/\./g, '');
    cleanedString = cleanedString.replace(/,/g, '.');
    const enUsNumber = Number(cleanedString);
    return enUsNumber;
}

function decreaseAmount() {
    var valorAmount = Number(document.getElementById('amount').value);
    var valor = document.getElementById('unitPrice').value;
    valor = convertPtBrToEnUs(valor);
    if (valorAmount > 1) {
        valorAmount = valorAmount - 1;
    }
    modal.find('#amount').val(valorAmount);
    modal.find('#calcPrice').val(formatterBR.format(valorAmount * valor));
}

function editarCustomer(edicao) {
    $.ajax({
        type: 'GET',
        url: "/editarCadastro/" + edicao,
        success: function (htmlContent) {
            $('#modalCadastroContent').html(htmlContent);
            $('#modalCadastro').modal('show');
        }
    });
}

function increaseAmount() {
    var valorAmount = Number(document.getElementById('amount').value);
    var valor = document.getElementById('unitPrice').value;
    valor = convertPtBrToEnUs(valor);

    valorAmount = valorAmount + 1;
    modal.find('#amount').val(valorAmount);
    modal.find('#calcPrice').val(formatterBR.format(valorAmount * valor));
}

function iniciarUpload(event) {
    if (event.target.files && event.target.files[0].name) {
        $('#formImagemButtonSubmit').click();
    }
}

function logout() {
    try {
        firebase.auth().signOut();
    } catch (error) {
        console.error("Erro ao deslogar:", error);
    }
    $.ajax({
        type: 'POST',
        url: '/logout',
        success: function (htmlContent) {
            $('#cabecalho').html(htmlContent);
        }
    });
}

function mostrarEndereco(id) {
    $.ajax({
        type: 'GET',
        url: "/mostrarEndereco/" + id,
        success: function (htmlContent) {
            $('#modalCadastro').modal('hide');
            $('#modalEnderecos').modal('hide');
            $('#modalAddressContent').html(htmlContent);
            $('#modalAddress').modal('show');
        }
    });
}

function mostrarEnderecos() {
    // precisa salvar antes
    $.ajax({
        type: 'GET',
        url: "/enderecos",
        success: function (htmlContent) {
            $('#enderecosContent').html(htmlContent);
            $('#modalCadastro').modal('hide');
//            $('#enderecosContent').html(htmlContent);
            $('#modalEnderecos').modal('show');
        }
    });
}

function recuperarCarrinho() {
    $.ajax({
        type: 'GET',
        url: "/recuperarCarrinho",
        success: function (htmlContent) {
            $('#carrinhoContent').html(htmlContent);
            $('#modalCart').modal('show');
        }
    });
}

function removerCartItem(id) {
    $.ajax({
        type: 'POST',
        url: "/removeCartItem/" + id,
        success: function (htmlContent) {
            if (String(htmlContent).indexOf("<span >Excluir</span>") < 0) {
                $('#modalCart').modal('hide');
                $('#cabecalho').html(htmlContent);
            } else {
                $('#cartItemsContent').html(htmlContent);
            }
        }
    });
}

function removerEndereco(id) {
    $.ajax({
        method: 'post',
        url: '/removerEndereco/' + id,
        success: function (htmlContent) {
            if (String(htmlContent).indexOf("<span >Excluir</span>") < 0) {
                $('#modalEnderecos').modal('hide');
                $('#cabecalho').html(htmlContent);
            } else {
                $('#enderecosContent').html(htmlContent);
            }
        }
    });
}

function selecionar(id) {
    $.ajax({
        type: 'GET',
        url: '/selecionar/' + id,
        success: function (htmlContent) {
            $('#selecionadoContent').html(htmlContent);
            $('#modalSelecionado').modal('show');
        }
    });
}

function submitAddress(event) {
    event.preventDefault();
    var form = $('#formEndereco');
    var url = form.attr('action');
    var formData = form.serialize();
    var formMethod = form.attr('method');
    $.ajax({
        method: formMethod,
        url: url,
        data: formData,
        success: function (htmlContent) {
            if (String(htmlContent).indexOf("Erros encontrados") < 0) {
                $('#modalAddress').modal('hide');
                $('#cabecalho').html(htmlContent);
            } else {
                $('#modalAddressContent').html(htmlContent);
                $('#toastErrosAddress').toast('show');
            }
        }
    });
}

async function submitCustomer(event) {
    event.preventDefault();
    $(":input:disabled").prop('disabled', false);
    var form = $('#formCadastro');
    var url = form.attr('action');
    var formMethod = form.attr('method');
    var formData = form.serializeArray();
    var formArray = formData.map(input => [input.name, input.value]);
    var formMap = new Map(formArray);
    var email = formMap.get('email');
    var senha = formMap.get('senha');
    var senhaConf = formMap.get('senhaConf');
    var senhaFraca = false;
    var senhaNaoConfere = false;
    var emailEmUso = false;
    console.log(email, senha);
    const fetchSignIn = async (email, formData) => {
        try {
            const methods = await auth.fetchSignInMethodsForEmail(email);
            if (methods.length > 0) {
                // invalidar o form
                formData.push({name: 'fireBaseError', value: 'auth/email-already-in-use'});
                emailEmUso = true;
            }
        } catch (error) {
            console.log("erro do fetchsign:", error.code);
        }
    };
    await fetchSignIn(email, formData);
    if (!emailEmUso) {
        if (!senhaForte(senha)) {
            formData.push({name: 'fireBaseError', value: 'auth/weak-password'});
            senhaFraca = true;
        } else {
            if (senha !== senhaConf) {
                formData.push({name: 'fireBaseError', value: 'auth/not-equal-password'});
                senhaNaoConfere = true;
            }
        }
    }
    if (!emailEmUso && !senhaFraca && !senhaNaoConfere) {
        const invalida = async (email, senha) => {
            try {
                const userCredential = await auth.createUserWithEmailAndPassword(email, senha);
                console.log(userCredential);
            } catch (error) {
                console.log("erro do firebase:", error.code);
                // invalidar o form
                formData.push({name: 'fireBaseError', value: error.code});
            }
        };
        invalida(email, senha);
    }
    $.ajax({
        method: formMethod,
        url: url,
        data: formData,
        success: function (htmlContent) {
            if (String(htmlContent).indexOf("Erros encontrados") < 0) {
                document.body.focus();
                $('#modalCadastro').modal('hide');
                $('.modal-backdrop').remove();
                $('#cabecalho').html(htmlContent);
                if (enderecos) {
                    mostrarEnderecos();
                    enderecos = false;
                }
            } else {
                $('#modalCadastroContent').html(htmlContent);
                $('#toastErrosCadastro').toast('show');
            }
        }
    });
}

async function submitLogin(event) {
    event.preventDefault();
    var form = $('#formLogin');
    var url = form.attr('action');
    var formMethod = form.attr('method');

    var formData = form.serializeArray();
    var formArray = formData.map(input => [input.name, input.value]);
    var formMap = new Map(formArray);
    var email = formMap.get('emailLogin');
    var senhaLogin = formMap.get('senhaLogin');

    const signIn = async (email, senhaLogin) => {
        try {
            userCredential = await auth.signInWithEmailAndPassword(email, senhaLogin);
            console.log(userCredential);
            console.log(userCredential.user._delegate.email);
            /*
             *  guardar na sessão: 
             *      user._delegate.accessToken
             *      user._delegate.displayName
             *      user._delegate.email
             *      user._delegate.uid
             */
        } catch (error) {
            console.log("erro signin:", error.code);
            // invalidar o form
            formData.push({name: 'fireBaseError', value: error.code});
        }
    };
    await signIn(email, senhaLogin);
    $.ajax({
        method: formMethod,
        url: url,
        data: formData,
        success: function (htmlContent) {
            if (String(htmlContent).indexOf("Erros encontrados") < 0) {
                $('#modalLogin').modal('hide');
                $('.modal-backdrop').remove();
                $('#cabecalho').html(htmlContent);
            } else {
                $('#modalLoginContent').html(htmlContent);
                $('#toastErrosLogin').toast('show');
            }
        }
    });
}

function submitLoginCustomer(event) {
    event.preventDefault();
    var form = $('#formLoginCustomer');
    var url = form.attr('action');
    var formData = form.serialize();
    var formMethod = form.attr('method');
    $.ajax({
        method: formMethod,
        url: url,
        data: formData,
        success: function (htmlContent) {
            if (String(htmlContent).indexOf("Erros encontrados") < 0) {
                $('#modalLogin').modal('hide');
                $('.modal-backdrop').remove();
                $('#modalCadastroContent').html(htmlContent);
                $('#modalCadastro').modal('show');
            } else {
                $('#modalLoginContent').html(htmlContent);
                $('#toastErrosLogin').toast('show');
            }
        }
    });
}

function telaLogin() {
    $.ajax({
        type: 'GET',
        url: "/telaLogin",
        success: function (htmlContent) {
            $('#modalLoginContent').html(htmlContent);
            $('#modalLogin').modal('show');
        }
    });
}

function toggleEnderecos() {
    enderecos = true;
}

function marcarImagem(id) {
    idImagem = id;
    $('#imgFile').click();
}


async function notificacoes() {
    if (await receberNotificacoes()) {
        console.log("device token: ", deviceToken);
        gravarToken();
    }
}

function gravarToken() {
    const payload = {
        token: deviceToken
    };
    $.ajax({
        url: "/gravarToken",
        contentType: "application/json",
        type: "POST",            
        data: JSON.stringify(payload),
        success: function (response) {
            console.log("Device salvo:", response);
            alert("Device criado com ID: " + response.id);
        },
        error: function (error) {
            console.error("Erro:", error);
            alert("Erro ao salvar device");
        }
    })
}

function senhaForte(senha) {
    const regex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[^A-Za-z\d]).{6,}$/;
    console.log(senha, "senhaforte:", regex.test(senha));
    return regex.test(senha);
}


function uploadImagem(event) {
    event.preventDefault();
    var formDOM = $('#formImagem')[0];
    var url = '/upload';
    var formData = new FormData(formDOM);
    formData.append("id", idImagem);
    var formMethod = 'POST';
    $.ajax({
        method: formMethod,
        url: url,
        data: formData,
        processData: false,
        contentType: false,
        success: function (htmlContent) {
            $('#imagemSelecionado' + idImagem).html(htmlContent);
            $('#imagemCartao' + idImagem).html(htmlContent);
        }
    });
}

function voltarTela(modal) {
    $(modal).modal('hide');
    $.ajax({
        type: 'GET',
        url: "/catalog",
        success: function (htmlContent) {
            window.location.href = 'catalog';
        }
    });
}

setTimeout(function () {
    var sucesso = document.querySelector('.alert-success');
    var erro = document.querySelector('.alert-danger');
    var mensagem = document.querySelector('.mensagem');
    if (sucesso) {
        sucesso.style.display = 'none';
    }
    if (erro) {
        erro.style.display = 'none';
    }
    if (mensagem) {
        mensagem.style.display = 'none';
    }
}
, 5000);


