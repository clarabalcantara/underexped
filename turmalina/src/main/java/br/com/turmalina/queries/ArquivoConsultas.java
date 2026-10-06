package br.com.turmalina.queries;

import br.com.turmalina.model.Expedicao;
import br.com.turmalina.model.AutorizacaoAmbiental;
import br.com.turmalina.model.RelatorioFinal;

import jakarta.persistence.EntityManager;


public class ArquivoConsultas {
    private final EntityManager em;

    public ArquivoConsultas(EntityManager em) {
        this.em = em;
    }

    public byte[] getMapaSegurancaByExpedicao(Expedicao expedicao) {
        return expedicao.getPlanoSeguranca().getMapaRota();
    }

    public AutorizacaoAmbiental getAutorizacaoAmbientalByExpedicao(Expedicao expedicao) {
        return this.em.createQuery("""
            SELECT aa FROM AutorizacaoAmbiental aa
            WHERE aa.expedicao.id = :idExpedicao
            """, AutorizacaoAmbiental.class)
            .setParameter("idExpedicao", expedicao.getId())
            .getSingleResult();
    }

    public RelatorioFinal getRelatorioFinalByExpedicao(Expedicao expedicao) {
        return this.em.createQuery("""
                    SELECT rf from RelatorioFinal rf
                    WHERE rf.expedicao.id = :idExpedicao
                """, RelatorioFinal.class)
                .setParameter("idExpedicao", expedicao.getId())
                .getSingleResult();
    }
}
