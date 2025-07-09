package it.eldasoft.sil.pg.npa.model;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.sil.pg.bl.npa.NpaFunzioniManager;

import java.util.*;

public class AnacFormID {

    private String cig;
    private ComunicazioneID comunicazione;

    public String getCig() {
        return cig;
    }
    public ComunicazioneID getComunicazione() {
        return comunicazione;
    }

    public AnacFormID(HashMap<String, Object> parametri) throws Exception {

        Long idScheda = (Long) ((JdbcParametro)parametri.get("IDSCHEDA")).getValue();

        NpaFunzioniManager npaFunzioniManager = (NpaFunzioniManager) parametri.get("npaFunzioniManager");
        SqlManager sqlManager = (SqlManager) parametri.get("sqlManager");

        Vector<JdbcParametro> datiGara = sqlManager.getVector("select ngara,cupprg,codcig from gare where ngara=(select ngara from g1npascheda where id=?)", new Object[]{idScheda});
        String ngara = (String) datiGara.get(0).getValue();
        String cupprg = (String) datiGara.get(1).getValue();
        String codcig = (String) datiGara.get(2).getValue();

        this.cig = codcig;
        this.comunicazione = new ComunicazioneID<List>();

        ComunicazioneID comunicazione = new ComunicazioneID<List<String>>();
        Map<Boolean, List<String>> risultato = npaFunzioniManager.getCupLottoAcquisizioneCup(cupprg, ngara);
        boolean acquisizioneCup = risultato.keySet().iterator().next();
        comunicazione.setCup(risultato.get(acquisizioneCup));
        comunicazione.setMotivoRichiestaModifica(new EnumNPA("motivoRichiestaModifica","1"));
        this.comunicazione = comunicazione;

    }
}
