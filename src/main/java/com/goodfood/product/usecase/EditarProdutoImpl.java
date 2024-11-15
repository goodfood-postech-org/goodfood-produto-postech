package com.goodfood.product.usecase;

import java.util.UUID;
import org.springframework.stereotype.Component;
import com.goodfood.product.domain.Produto;
import com.goodfood.product.gateways.ProdutoDatabaseGateway;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EditarProdutoImpl implements EditarProduto {

  private final ProdutoDatabaseGateway produtoDatabaseGateway;

  @Override
  public void executar(UUID id, Produto produtoEdited) {
    Produto produto = produtoDatabaseGateway.obterPorId(id);
    
    produto.setPreco(produtoEdited.getPreco());
    produto.setDescricao(produtoEdited.getDescricao());
    produto.setCategoria(produtoEdited.getCategoria());
    
    produtoDatabaseGateway.salvar(produto);
  }
  
}
