package it.eldasoft.sil.pg.npa.model;

import it.eldasoft.sil.pg.bl.npa.NpaFunzioniManager;

import java.sql.Date;
import java.util.Arrays;
import java.util.Map;

public class LottoPL {
    private String lotIdentifier;
    private DatiBaseLottoOptional datiBase;
    private DatiBaseCPV datiBaseCPV;
    private DatiBaseAggiudicazioneOptional datiBaseAggiudicazione;
    private DatiBaseAggiuntivi datiBaseAggiuntivi;
    private DatiBaseContratto datiBaseContratto;
    private DatiBaseAccessibilita datiBaseAccessibilita;
    private DatiBaseImporto datiBaseImporto;
    private DatiBaseComunicazione datiBaseComunicazione;
    private DatiBaseDocumentiOptional datiBaseDocumenti;

    public String getLotIdentifier() {
        return lotIdentifier;
    }

    public DatiBaseLottoOptional getDatiBase() {
        return datiBase;
    }

    public DatiBaseCPV getDatiBaseCPV() {
        return datiBaseCPV;
    }

    public DatiBaseDocumentiOptional getDatiBaseDocumenti() {
        return datiBaseDocumenti;
    }

    public DatiBaseAggiudicazioneOptional getDatiBaseAggiudicazione() {
        return datiBaseAggiudicazione;
    }

    public DatiBaseAggiuntivi getDatiBaseAggiuntivi() {
        return datiBaseAggiuntivi;
    }

    public DatiBaseContratto getDatiBaseContratto() {
        return datiBaseContratto;
    }

    public DatiBaseAccessibilita getDatiBaseAccessibilita() {
        return datiBaseAccessibilita;
    }

    public DatiBaseImporto getDatiBaseImporto() {
        return datiBaseImporto;
    }

    public DatiBaseComunicazione getDatiBaseComunicazione() {
        return datiBaseComunicazione;
    }

    public LottoPL(Map parametriTornata, Map parametriLotti) throws Exception {
        NpaFunzioniManager npaFunzioniManager = (NpaFunzioniManager) parametriTornata.get("npaFunzioniManager");

        String codiga = (String) NpaFunzioniManager.getValoreParametro(parametriLotti, "CODIGA");
        this.lotIdentifier = npaFunzioniManager.getLotIdentByCodiga(codiga);

        Long tipoapp = (Long) NpaFunzioniManager.getValoreParametro(parametriTornata, "TIPOAPP");
        Long tipgen = (Long) NpaFunzioniManager.getValoreParametro(parametriTornata, "TIPGEN");

        EnumNPA oggettoContratto = null;
        if (tipgen != null) {
            oggettoContratto = npaFunzioniManager.getOggettoContrattoByTipgen(tipgen);
        } else if (tipoapp != null) {
            oggettoContratto = npaFunzioniManager.getOggettoContrattoByTipApp(tipoapp);
        }

        String oggetto = (String) NpaFunzioniManager.getValoreParametro(parametriLotti, "NOT_GAR");
        Double importo = null;
        if (NpaFunzioniManager.getValoreParametro(parametriLotti, "VALMAX") instanceof Long) {
            importo = ((Long) NpaFunzioniManager.getValoreParametro(parametriLotti, "VALMAX")).doubleValue();
        } else {
            importo = (Double) NpaFunzioniManager.getValoreParametro(parametriLotti, "VALMAX");
        }
        this.datiBase = new DatiBaseLottoOptional(oggettoContratto, oggetto, importo);

        this.datiBaseCPV = npaFunzioniManager.getDatiBaseCpvByNgara((String) NpaFunzioniManager.getValoreParametro(parametriLotti, "NGARA"));

        String codnuts = (String) NpaFunzioniManager.getValoreParametro(parametriLotti, "CODNUTS");
        String nomeScheda = (String) parametriTornata.get("nomeScheda");

        if (Arrays.asList("PL2_1", "PL2_2").contains(nomeScheda) && npaFunzioniManager.isApplicazioneVersioneAttiva("3")) {
            //APP-1932 struttura rimossa
        } else {
            this.datiBaseContratto = npaFunzioniManager.getDatiBaseContratto(codnuts);
        }

        String affiris = (String) NpaFunzioniManager.getValoreParametro(parametriLotti, "AFFIRIS");
        this.datiBaseAggiuntivi = npaFunzioniManager.getDatiBaseAggiuntivi(affiris);

        String accessi = (String) NpaFunzioniManager.getValoreParametro(parametriLotti, "ACCESSI");
        if(accessi != null) {
          EnumNPA accessibilitaEnum = new EnumNPA("accessibilita", npaFunzioniManager.getTab2d1ByTab2tip(accessi, "A1z23"));
          this.datiBaseAccessibilita = new DatiBaseAccessibilita(accessibilitaEnum, null);
        }

        this.datiBaseImporto = new DatiBaseImporto(false);

        Date dinvit = (Date) NpaFunzioniManager.getValoreParametro(parametriTornata, "DINVIT");
        if(dinvit != null){
          String dataInvito = npaFunzioniManager.getStringDateFormat(dinvit);
          this.datiBaseComunicazione = new DatiBaseComunicazione(dataInvito);
        }

        Long critlicg = (Long) NpaFunzioniManager.getValoreParametro(parametriLotti, "CRITLICG");
        Boolean costofisso = NpaFunzioniManager.getBooleanByString((String) NpaFunzioniManager.getValoreParametro(parametriLotti, "COSTOFISSO"));
        String gartel = (String) NpaFunzioniManager.getValoreParametro(parametriTornata, "GARTEL");
        Long genere = (Long) NpaFunzioniManager.getValoreParametro(parametriTornata,"GENERE");
        this.datiBaseAggiudicazione = new DatiBaseAggiudicazioneOptional(critlicg, costofisso, gartel, genere);

        String codgar = (String) NpaFunzioniManager.getValoreParametro(parametriTornata, "CODGAR");
        String ngara = (String) NpaFunzioniManager.getValoreParametro(parametriTornata, "NGARA");
        this.datiBaseDocumenti = npaFunzioniManager.getDatiBaseDocumentiSQL(codgar, ngara, "P");
    }
}
