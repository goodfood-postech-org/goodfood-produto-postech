package com.goodfood.product.gateways.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.goodfood.product.domain.EProdutoCategoria;
import com.goodfood.product.domain.NotFoundException;
import com.goodfood.product.domain.Produto;
import com.goodfood.product.gateways.database.entities.ProdutoEntity;
import com.goodfood.product.gateways.database.repositories.ProdutoRepository;

class ProdutoDatabaseGatewayImplTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoDatabaseGatewayImpl produtoDatabaseGateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveShouldPersistProdutoAndReturnDomainObject() {
        Produto produto = Produto.builder()
                .id(UUID.randomUUID())
                .descricao("Test Product")
                .preco(BigDecimal.valueOf(10.99))
                .categoria("BEBIDA")
                .build();

        ProdutoEntity produtoEntity = new ProdutoEntity(produto);
        when(produtoRepository.save(any(ProdutoEntity.class))).thenReturn(produtoEntity);

        Produto savedProduto = produtoDatabaseGateway.save(produto);

        assertEquals(produto.getId(), savedProduto.getId());
        assertEquals(produto.getDescricao(), savedProduto.getDescricao());
        assertEquals(produto.getPreco(), savedProduto.getPreco());
        assertEquals(produto.getCategoria(), savedProduto.getCategoria());
    }

    @Test
    void deleteShouldRemoveProdutoById() {
        UUID id = UUID.randomUUID();
        doNothing().when(produtoRepository).deleteById(id);

        produtoDatabaseGateway.delete(id);

        verify(produtoRepository, times(1)).deleteById(id);
    }

    @Test
    void findByCategoryShouldReturnListOfProdutos() {
        ProdutoEntity produtoEntity = ProdutoEntity.builder()
                .id(UUID.randomUUID())
                .descricao("Test Product")
                .preco(BigDecimal.valueOf(10.99))
                .categoria(EProdutoCategoria.BEBIDA)
                .build();

        when(produtoRepository.findByCategoria(EProdutoCategoria.BEBIDA))
                .thenReturn(Collections.singletonList(produtoEntity));

        List<Produto> produtos = produtoDatabaseGateway.findByCategory("BEBIDA");

        assertEquals(1, produtos.size());
        assertEquals(produtoEntity.getId(), produtos.get(0).getId());
    }

    @Test
    void findByCategoryShouldReturnEmptyListWhenNoProdutosFound() {
        when(produtoRepository.findByCategoria(EProdutoCategoria.BEBIDA))
                .thenReturn(Collections.emptyList());

        List<Produto> produtos = produtoDatabaseGateway.findByCategory("BEBIDA");

        assertTrue(produtos.isEmpty());
    }

    @Test
    void findByIdShouldReturnProdutoWhenFound() {
        UUID id = UUID.randomUUID();
        ProdutoEntity produtoEntity = ProdutoEntity.builder()
                .id(id)
                .descricao("Test Product")
                .preco(BigDecimal.valueOf(10.99))
                .categoria(EProdutoCategoria.BEBIDA)
                .build();

        when(produtoRepository.findById(id)).thenReturn(Optional.of(produtoEntity));

        Produto produto = produtoDatabaseGateway.findById(id);

        assertEquals(id, produto.getId());
        assertEquals("Test Product", produto.getDescricao());
    }

    @Test
    void findByIdShouldThrowNotFoundExceptionWhenProdutoNotFound() {
        UUID id = UUID.randomUUID();
        when(produtoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> produtoDatabaseGateway.findById(id));
    }
}