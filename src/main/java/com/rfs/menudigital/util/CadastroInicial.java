/**
 *
 * @author Roselito
 */
package com.rfs.menudigital.util;
import com.rfs.menudigital.models.Item;
import org.springframework.beans.factory.annotation.Autowired;
import com.rfs.menudigital.repositories.ItensRepository;
import org.springframework.stereotype.Component;

@Component
public class CadastroInicial {

    @Autowired
    private ItensRepository itensRepository;

    public void executar() {
        Item item = new Item();
//            item.setId(1);
        item.setDescription("Teste campo string com texto longo, exemplo de descrição que atrapalha se houver mais de três linhas.");
        item.setTitle("Título 1");
        item.setPrice(23.56);
        itensRepository.save(item);
        item = new Item();
//            item.setId(2);
        item.setDescription("Outro teste campo string com texto longo, exemplo de descrição que atrapalha se houver mais de três linhas.");
        item.setTitle("Teste título");
        item.setPrice(12.34);
        itensRepository.save(item);
        item = new Item();
//            item.setId(3);
        item.setDescription("Mais um teste campo string com texto longo, exemplo de descrição que atrapalha se houver mais de três linhas.");
        item.setTitle("Nome do item");
        item.setPrice(67.89);
        itensRepository.save(item);

    }
}
