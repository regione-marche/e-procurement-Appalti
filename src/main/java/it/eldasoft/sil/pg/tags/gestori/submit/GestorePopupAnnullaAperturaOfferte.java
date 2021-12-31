
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore della popup di annullamento dell'apertura delle offerte
 *
 * @author Marcello Caminiti
 */

public class GestorePopupAnnullaAperturaOfferte extends
		AbstractGestoreEntita {

  /** Logger */
  static Logger logger = Logger.getLogger(GestorePopupAnnullaAperturaOfferte.class);




	@Override
  public String getEntita() {
		return "GARE";
	}

  public GestorePopupAnnullaAperturaOfferte() {
    super(false);
  }

	@Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {
	}

	@Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {
	}

	@Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {

	}

	@Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl)
			throws GestoreException {
	}

	@Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
			throws GestoreException {
	}

	@Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
			throws GestoreException {

	  TabellatiManager tabellatiManager=null;

		String oggEvento = "";

		String tmp = this.getRequest().getParameter("bustalotti");
        if(tmp == null)
          tmp = (String) this.getRequest().getAttribute("bustalotti");
        Long bustalotti=null;
        if(tmp!=null && !"".equals(tmp))
          bustalotti = new Long(tmp);

	    //variabili per tracciatura eventi
	    String messageKey = null;
	    int livEvento = 3;
	    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");


	    try{
	        try {
	            Long fasgar = new Long(2);
	            Long stepgar = new Long(35);
	            String ngara = impl.getString("NGARA");
	            String codgar = impl.getString("CODGAR1");
	            oggEvento = ngara;
	            this.sqlManager.update("update gare set fasgar=?, stepgar=? where ngara=?", new Object[]{fasgar,stepgar,ngara});

	            if(bustalotti!=null){
	              List listaLotti = this.sqlManager.getListVector("select ngara,modlicg from gare where codgar1=? and ngara <> codgar1", new Object[]{codgar});
	              if(listaLotti!=null && listaLotti.size()>0){
	                String lotto=null;
	                Long modlicg = null;
	                for(int i=0;i < listaLotti.size(); i++){
	                  lotto = SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).stringValue();
	                  modlicg = SqlManager.getValueFromVectorParam(listaLotti.get(i), 1).longValue();
	                  this.sqlManager.update("update gare set fasgar=null, stepgar=null where ngara=?", new Object[]{lotto});
	                  if(modlicg!=null && modlicg.longValue()==6)
	                    this.annullaPunteggiLotto( lotto, codgar, impl, status);
	                }
	              }
	            }else{
	              tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
	                  this.getServletContext(), TabellatiManager.class);

	              Long modlicg= impl.getLong("MODLICG");
    	            if(modlicg!=null && modlicg.longValue()==6)
    	              this.annullaPunteggiLotto( ngara, codgar, impl, status);
    	        }

	            //best case
	            livEvento = 1;
	            errMsgEvento = "";
	            this.getRequest().setAttribute("RISULTATO", "CALCOLOESEGUITO");
	        } catch (Throwable e) {
	            this.getRequest().setAttribute("RISULTATO", "ERRORI");
	            livEvento = 3;
	            messageKey = "errors.gestoreException.*.annullaAperturaOfferte";
	            errMsgEvento = this.resBundleGenerale.getString(messageKey);
	            throw new GestoreException(
	                    "Errore durante l'annullamento dell'apertura delle offerte",
	                    "annullaAperturaOfferte", e);
	        }

	    }finally{
	        //Tracciatura eventi
	        try {
	          LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
	          logEvento.setLivEvento(livEvento);

	          logEvento.setOggEvento(oggEvento);
	          logEvento.setCodEvento("GA_ANNULLA_FASE_OFFERTE");
	          String descr="Annullamento fase apertura offerte";
	          if(bustalotti==null){
	            Long faseAttuale = impl.getLong("FASGAR");
	            if(faseAttuale!=null){
    	            String descFase = tabellatiManager.getDescrTabellato("A1011",  faseAttuale.toString());
    	            descr+=" (attivata dalla fase '" + descFase + "')";
	            }
	          }

	          logEvento.setDescr(descr);
	          logEvento.setErrmsg(errMsgEvento);
	          LogEventiUtils.insertLogEventi(logEvento);
	        } catch (Exception le) {
	          messageKey = "errors.logEventi.inaspettataException";
	          logger.error(this.resBundleGenerale.getString(messageKey), le);
	        }
	    }


	}


	/**
	 * viene Effettuato l'annullamento dei punteggi tecnici ed economici del lotto, richiamando il GestorePopupAnnullaCalcoloPunteggi
	 * passandogli nel request il tipo (1 per punteggi tecnici, 2 per punteggi economici), ngara e codgar.
	 *
	 * @param ngara
	 * @param codgar
	 * @param impl
	 * @param status
	 * @throws GestoreException
	 */
	private void annullaPunteggiLotto(String ngara, String codgar,DataColumnContainer impl,TransactionStatus status) throws GestoreException{

        GestorePopupAnnullaCalcoloPunteggi gestore= new GestorePopupAnnullaCalcoloPunteggi();
        this.getRequest().setAttribute("ngara", ngara);
        this.getRequest().setAttribute("codgar", codgar);

        //Annullamento punteggi tecnici
        this.getRequest().setAttribute("tipo", "1");
        gestore.setRequest(this.getRequest());
        gestore.preInsert(status, impl);

        //Annullamento punteggi economici
        this.getRequest().setAttribute("tipo", "2");
        gestore.setRequest(this.getRequest());
        gestore.preInsert(status, impl);

	}
}