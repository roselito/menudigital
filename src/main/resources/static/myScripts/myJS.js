var modal = null;

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
//    document.getElementById('amountlabel').textContent = amount;
    modal.find('#amount').val(amount);
    modal.find('#unitprice').val(formatterBR.format(unitprice));
    modal.find('#calcprice').val(formatterBR.format(calcprice));
    modal.find('#observations').text(observations);
//    document.getElementById('itemid').value = itemid;
    modal.find('#itemid').val(itemid);
});

const formatter = new Intl.NumberFormat('en-US', {
    style: 'decimal', // or 'currency', 'percent'
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
});

const formatterBR = new Intl.NumberFormat('pt-BR', {
    style: 'decimal', // or 'currency', 'percent'
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
});

function decreaseAmount() {
    var valorAmount = parseInt(modal.find('#amount').val());
    if (valorAmount > 1) {
        valorAmount = valorAmount - 1;
    }
    modal.find('#amount').val(valorAmount);
//    document.getElementById('amountlabel').textContent = valorAmount;
    modal.find('#amountlabel').text(valorAmount);
    modal.find('#calcprice').val(formatterBR.format(valorAmount * parseFloat(convertPtBrToEnUs(modal.find('#unitprice').val()))));
}

function increaseAmount() {
    var valorAmount = parseInt(modal.find('#amount').val());
    valorAmount = valorAmount + 1;
    modal.find('#amount').val(valorAmount);
//    document.getElementById('amountlabel').textContent = valorAmount;
    modal.find('#amountlabel').text(valorAmount);
    modal.find('#calcprice').val(formatterBR.format(valorAmount * parseFloat(convertPtBrToEnUs(modal.find('#unitprice').val()))));
}

$(document).ready(function () {
    var closeModalCart = document.getElementById('closeModalCart').value;
    if (closeModalCart === "mostrar") {
        $('#modalCart').modal('show');
    }
});

function buscarCEP() {
    var cepDigitado = document.getElementById('cep').value;
    $.ajax({
        type: 'GET',
        url: "/buscarCEP/" + cepDigitado,
        success: function (htmlContent) {
            // o div #modalAddressContent é substituído pelo html que vem do return de /buscarCEP
            // e os dados são extraídos dos atributos adicionados ao model, também em  /buscarCEP
            $('#modalAddressContent').html(htmlContent);
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

function convertPtBrToEnUs(ptBrNumberString) {
    let cleanedString = String(ptBrNumberString);
    cleanedString = cleanedString.replace(/\./g, '');
    cleanedString = cleanedString.replace(/,/g, '.');
    const enUsNumber = Number(cleanedString);
    return enUsNumber;
}