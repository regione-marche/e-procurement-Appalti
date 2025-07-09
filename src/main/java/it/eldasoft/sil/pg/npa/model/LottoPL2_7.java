package it.eldasoft.sil.pg.npa.model;

import it.eldasoft.sil.pg.bl.npa.NpaFunzioniManager;

import java.sql.Date;
import java.util.Map;

public class LottoPL2_7 extends LottoPL {

  private DatiBaseTerminiInvio datiBaseTerminiInvio;

  public DatiBaseTerminiInvio getDatiBaseTerminiInvio() {
    return datiBaseTerminiInvio;
  }

  public LottoPL2_7(Map parametriTornata, Map parametriLotti) throws Exception {
    super(parametriTornata, parametriLotti);

    NpaFunzioniManager npaFunzioniManager = (NpaFunzioniManager) parametriTornata.get("npaFunzioniManager");

    Long iterga = (Long) NpaFunzioniManager.getValoreParametro(parametriTornata, "ITERGA");
    String dteoff = npaFunzioniManager.getStringDateFormat((Date) NpaFunzioniManager.getValoreParametro(parametriLotti, "DTEOFF"));
    String oteoff = (String) NpaFunzioniManager.getValoreParametro(parametriLotti, "OTEOFF");
    String dtepar = npaFunzioniManager.getStringDateFormat((Date) NpaFunzioniManager.getValoreParametro(parametriLotti, "DTEPAR"));
    String otepar = (String) NpaFunzioniManager.getValoreParametro(parametriLotti, "OTEPAR");
    if(iterga != null) {
      if((!Long.valueOf(2).equals(iterga) && !Long.valueOf(4).equals(iterga)) || "PL2_8".equals(parametriTornata.get("nomeScheda")))
        this.datiBaseTerminiInvio = new DatiBaseTerminiInvio(null, npaFunzioniManager.unisciDataeOra(dteoff, oteoff));
      else if(Long.valueOf(2).equals(iterga) || Long.valueOf(4).equals(iterga))
        this.datiBaseTerminiInvio = new DatiBaseTerminiInvio(npaFunzioniManager.unisciDataeOra(dtepar, otepar), null);
    }
  }
}
