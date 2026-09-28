package br.com.turmalina.dto;

import br.com.turmalina.model.enums.SituacaoExpedicao;

import java.time.LocalDateTime;


public record ExpedicaoResumo(
        String codigo,
        String titulo,
        String caverna,
        LocalDateTime inicioPrevisto,
        LocalDateTime terminoPrevisto,
        SituacaoExpedicao situacao) {
}