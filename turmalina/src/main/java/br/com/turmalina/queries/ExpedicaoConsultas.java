package br.com.turmalina.queries;

import br.com.turmalina.dto.AmostraResumo;
import br.com.turmalina.dto.ExpedicaoResumo;
import br.com.turmalina.dto.ParticipanteResumo;
import br.com.turmalina.model.Coleta;
import br.com.turmalina.model.Expedicao;
import br.com.turmalina.model.enums.SituacaoExpedicao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


public class ExpedicaoConsultas {

    private final EntityManager em;

    public ExpedicaoConsultas(EntityManager em) {
        this.em = em;
    }

    public List<Expedicao> getAllExpedicoes() {
        try {
            return this.em.createQuery("""
                    SELECT e FROM Expedicao e
                    """, Expedicao.class)
                    .getResultList();

        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    public List<ExpedicaoResumo> listarPorPeriodoESituacao(LocalDateTime inicio, LocalDateTime fim,
                                                           SituacaoExpedicao situacao) {
        return em.createNamedQuery("Expedicao.listarPorPeriodoESituacao", ExpedicaoResumo.class)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .setParameter("situacao", situacao)
                .getResultList();
    }


    public Expedicao buscarDetalhes(Long expedicaoId) {
        Expedicao expedicao = em.createQuery("""
                        select distinct e from Expedicao e
                        join fetch e.caverna
                        left join fetch e.participacoes p
                        left join fetch p.pessoa
                        where e.id = :id
                        """, Expedicao.class)
                .setParameter("id", expedicaoId)
                .getSingleResult();

        // Reaproveita a mesma instância já gerenciada e só preenche os setores
        em.createQuery("""
                        select distinct e from Expedicao e
                        left join fetch e.setores
                        where e.id = :id
                        """, Expedicao.class)
                .setParameter("id", expedicaoId)
                .getSingleResult();

        return expedicao;
    }


    public List<ParticipanteResumo> listarParticipantes(Long expedicaoId) {
        return em.createQuery("""
                        select new br.com.turmalina.dto.ParticipanteResumo(
                            pe.nome, p.papel, p.presencaConfirmada)
                        from Participacao p
                        join p.pessoa pe
                        where p.expedicao.id = :id
                        order by pe.nome
                        """, ParticipanteResumo.class)
                .setParameter("id", expedicaoId)
                .getResultList();
    }


    public List<Coleta> listarColetas(Long expedicaoId) {
        return em.createNamedQuery("Coleta.listarPorExpedicao", Coleta.class)
                .setParameter("expedicaoId", expedicaoId)
                .getResultList();
    }


    public List<AmostraResumo> listarAmostras(Long coletaId) {
        return em.createQuery("""
                        select new br.com.turmalina.dto.AmostraResumo(
                            a.id, a.codigoCampo, a.categoria, a.quantidade,
                            a.unidadeMedida, a.condicaoConservacao, a.materialPerigoso)
                        from Amostra a
                        where a.coleta.id = :coletaId
                        order by a.codigoCampo
                        """, AmostraResumo.class)
                .setParameter("coletaId", coletaId)
                .getResultList();
    }
}