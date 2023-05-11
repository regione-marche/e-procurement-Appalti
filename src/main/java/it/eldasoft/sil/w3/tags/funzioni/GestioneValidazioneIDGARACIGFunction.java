package it.eldasoft.sil.w3.tags.funzioni;


import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.w3.bl.ValidazioneIDGARACIGManager;
import it.eldasoft.utils.spring.UtilitySpring;
import java.util.HashMap;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import org.apache.log4j.Logger;

public class GestioneValidazioneIDGARACIGFunction extends AbstractFunzioneTag {

  static Logger logger = Logger.getLogger(GestioneValidazioneIDGARACIGFunction.class);

  public GestioneValidazioneIDGARACIGFunction() {
    super(5, new Class[] { PageContext.class, String.class,Integer.class, String.class, String.class });
  }

  public String function(PageContext pageContext, Object params[])
      throws JspException {
    
    ValidazioneIDGARACIGManager validazioneIDGARACIGManager = (ValidazioneIDGARACIGManager) UtilitySpring.getBean(
        "validazioneIDGARACIGManager", pageContext, ValidazioneIDGARACIGManager.class);

    String uffint = (String) pageContext.getSession().getAttribute("uffint");
    HashMap infoValidazione = validazioneIDGARACIGManager.validate(params,uffint);
    pageContext.setAttribute("titolo", infoValidazione.get("titolo"));
    pageContext.setAttribute("listaControlli", infoValidazione.get("listaControlli"));
    pageContext.setAttribute("numeroWarning", infoValidazione.get("numeroWarning"));
    pageContext.setAttribute("numeroErrori", infoValidazione.get("numeroErrori"));

    return null;
    
  }

}