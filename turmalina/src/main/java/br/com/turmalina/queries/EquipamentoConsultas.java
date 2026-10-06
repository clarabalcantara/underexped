package br.com.turmalina.queries;

import jakarta.persistence.EntityManager;

import java.time.Instant;
import java.util.List;

import br.com.turmalina.model.Equipamento;


public class EquipamentoConsultas {

    private final EntityManager em;

    public EquipamentoConsultas(EntityManager em) {
        this.em = em;
    }

    public List<Equipamento> getEquipamentosDisponiveisEntreFaixas(Instant inicio, Instant fim) {
        return this.em.createQuery("""
            SELECT e FROM Equipamento e
            WHERE NOT EXISTS (
                SELECT m FROM MovimentacaoEquipamento m
                WHERE (
                    m.equipamento = e
                    AND 
                    m.retirada < :fim
                    AND
                    m.previsaoDevolucao > :inicio
                )
            )
        """, Equipamento.class)
        .setParameter("inicio", inicio)
        .setParameter("fim", fim)
        .getResultList();
    }
}
