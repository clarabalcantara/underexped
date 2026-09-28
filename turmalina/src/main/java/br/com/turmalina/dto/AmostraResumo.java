package br.com.turmalina.dto;

import br.com.turmalina.model.enums.CategoriaAmostra;
import br.com.turmalina.model.enums.CondicaoConservacao;
import br.com.turmalina.model.enums.UnidadeMedida;

import java.math.BigDecimal;

public record AmostraResumo(
        Long id,
        String codigoCampo,
        CategoriaAmostra categoria,
        BigDecimal quantidade,
        UnidadeMedida unidadeMedida,
        CondicaoConservacao condicaoConservacao,
        boolean materialPerigoso) {
}