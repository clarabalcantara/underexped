package br.com.turmalina.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "plano_seguranca")
@AllArgsConstructor 
@Builder 
public class PlanoSeguranca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "procedimentos_evacuacao", nullable = false, columnDefinition = "text")
    private String procedimentosEvacuacao;

    @Column(name = "ponto_encontro", nullable = false, length = 200)
    private String pontoEncontro;

    @Column(name = "tempo_max_sem_comunicacao_min", nullable = false)
    private int tempoMaxSemComunicacaoMin;

    @Column(name = "telefone_emergencia", nullable = false, length = 20)
    private String telefoneEmergencia;

    @Column(name = "exige_equipe_medica", nullable = false)
    private boolean exigeEquipeMedica;

    // Binário como objeto grande (oid no PostgreSQL), sem Base64. Lido só pela consulta de download.
    @Lob
    @Column(name = "mapa_rota")
    private byte[] mapaRota;


    @OneToOne(mappedBy = "planoSeguranca")
    private Expedicao expedicao;

    protected PlanoSeguranca() {
    }

    public PlanoSeguranca(String procedimentosEvacuacao, String pontoEncontro,
                          int tempoMaxSemComunicacaoMin, String telefoneEmergencia,
                          boolean exigeEquipeMedica) {
        this.procedimentosEvacuacao = procedimentosEvacuacao;
        this.pontoEncontro = pontoEncontro;
        this.tempoMaxSemComunicacaoMin = tempoMaxSemComunicacaoMin;
        this.telefoneEmergencia = telefoneEmergencia;
        this.exigeEquipeMedica = exigeEquipeMedica;
    }

    public Long getId() { return id; }

    public String getProcedimentosEvacuacao() { return procedimentosEvacuacao; }
    public void setProcedimentosEvacuacao(String procedimentosEvacuacao) { this.procedimentosEvacuacao = procedimentosEvacuacao; }

    public String getPontoEncontro() { return pontoEncontro; }
    public void setPontoEncontro(String pontoEncontro) { this.pontoEncontro = pontoEncontro; }

    public int getTempoMaxSemComunicacaoMin() { return tempoMaxSemComunicacaoMin; }
    public void setTempoMaxSemComunicacaoMin(int tempoMaxSemComunicacaoMin) { this.tempoMaxSemComunicacaoMin = tempoMaxSemComunicacaoMin; }

    public String getTelefoneEmergencia() { return telefoneEmergencia; }
    public void setTelefoneEmergencia(String telefoneEmergencia) { this.telefoneEmergencia = telefoneEmergencia; }

    public boolean isExigeEquipeMedica() { return exigeEquipeMedica; }
    public void setExigeEquipeMedica(boolean exigeEquipeMedica) { this.exigeEquipeMedica = exigeEquipeMedica; }

    public byte[] getMapaRota() { return mapaRota; }
    public void setMapaRota(byte[] mapaRota) { this.mapaRota = mapaRota; }

    public Expedicao getExpedicao() { return expedicao; }

    void setExpedicao(Expedicao expedicao) { this.expedicao = expedicao; }
}