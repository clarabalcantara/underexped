package br.com.turmalina.dto;

import br.com.turmalina.model.enums.PapelParticipante;

public record ParticipanteResumo(
        String nome,
        PapelParticipante papel,
        boolean presencaConfirmada) {
}