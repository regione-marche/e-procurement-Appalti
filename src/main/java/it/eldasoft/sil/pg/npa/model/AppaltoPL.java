package it.eldasoft.sil.pg.npa.model;

import it.eldasoft.sil.pg.bl.npa.NpaFunzioniManager;

import java.util.Map;

public class AppaltoPL {
    private DatiBaseAppaltoOptional datiBase;
    private DatiBaseStrumentiProceduraCompleto datiBaseStrumentiProcedura;

    public DatiBaseAppaltoOptional getDatiBase() {
        return datiBase;
    }

    public DatiBaseStrumentiProceduraCompleto getDatiBaseStrumentiProcedura() {
        return datiBaseStrumentiProcedura;
    }

    public AppaltoPL(Map parameters) throws Exception{
        NpaFunzioniManager npaFunzioniManager = (NpaFunzioniManager) parameters.get("npaFunzioniManager");
        this.datiBase = new DatiBaseAppaltoOptional(
                (String)  NpaFunzioniManager.getValoreParametro(parameters, "OGGETTO"),
                null, (Double) parameters.get("importo")
        );
        this.datiBaseStrumentiProcedura = new DatiBaseStrumentiProceduraCompleto(parameters);
    }

}
