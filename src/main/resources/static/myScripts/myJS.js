var modal = null;
var enderecos = false;

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
    spOptions = {
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
    var elemento = $(event.relatedTarget); // Elemento que disparou o modal
    var description = elemento.data('description'); // obtido de th:data-
    var title = elemento.data('title');
    var amount = elemento.data('amount');
    var unitprice = elemento.data('unitprice');
    var calcprice = elemento.data('calcprice');
    var observations = elemento.data('observations');
    var itemid = elemento.data('itemid');
    modal.find('#description').text(description);
    modal.find('#title').val(title);
    modal.find('#titlelabel').text(title);
    modal.find('#amountlabel').text(amount);
    modal.find('#amount').val(amount);
    modal.find('#unitprice').val(formatterBR.format(unitprice));
    modal.find('#calcprice').val(formatterBR.format(calcprice));
    modal.find('#observations').text(observations);
    modal.find('#itemid').val(itemid);
});

function addCartItem() {
    var title = document.getElementById('title').value;
    var description = document.getElementById('description').value;
    var amount = document.getElementById('amount').value;
    var unitprice = document.getElementById('unitprice').value;
    var observations = document.getElementById('observations').value + " ";
    var itemid = document.getElementById('itemid').value;
    $.ajax({
        type: 'POST',
        url: "/addCartItem/"
                + title + "/"
                + description + "/"
                + amount + "/"
                + convertPtBrToEnUs(unitprice) + "/"
                + observations + "/"
                + itemid,
        success: function (htmlContent) {
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
    cleanedString = cleanedString.replace(/\./g, '');
    cleanedString = cleanedString.replace(/,/g, '.');
    const enUsNumber = Number(cleanedString);
    return enUsNumber;
}

function decreaseAmount() {
    var valorAmount = parseInt(modal.find('#amount').val());
    if (valorAmount > 1) {
        valorAmount = valorAmount - 1;
    }
    modal.find('#amount').val(valorAmount);
    modal.find('#amountlabel').text(valorAmount);
    modal.find('#calcprice').val(formatterBR.format(valorAmount * parseFloat(convertPtBrToEnUs(modal.find('#unitprice').val()))));
}

function editarCustomer(edicao) {
    $.ajax({
        type: 'GET',
        url: "/editarCadastro/"+edicao,
        success: function (htmlContent) {
            $('#modalCadastroContent').html(htmlContent);
            $('#modalCadastro').modal('show');
        }
    });
}

function increaseAmount() {
    var valorAmount = parseInt(modal.find('#amount').val());
    valorAmount = valorAmount + 1;
    modal.find('#amount').val(valorAmount);
    modal.find('#amountlabel').text(valorAmount);
    modal.find('#calcprice').val(formatterBR.format(valorAmount * parseFloat(convertPtBrToEnUs(modal.find('#unitprice').val()))));
}

function logout() {
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
        url: '/removerEndereco/'+id,
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

function submitCustomer(event) {
    event.preventDefault();
    $(":input:disabled").prop('disabled',false);
    var form = $('#formCadastro');
    var url = form.attr('action');
    var formData = form.serialize();
    var formMethod = form.attr('method');
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

function submitLogin(event) {
    event.preventDefault();
    var form = $('#formLogin');
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
}, 5000);
