<%
/*
 * Created on: 14-11-2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Form per impostare un filtro sulla lista delle categorie */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<gene:template file="popup-template.jsp" >
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:setString name="titoloMaschera" value="Imposta filtro"/>
	
	<c:set var="modo" value="MODIFICA" scope="request" />
	
	<gene:redefineInsert name="corpo">
  		<gene:formTrova entita="GARE">
  			<gene:campoTrova campo="NGARA" entita="GARE" where="GARE.NGARA=GARE.CODGAR1" title="Codice lotto"/>
  			<gene:campoTrova campo="CODIGA" entita="GARE" where="GARE.NGARA=GARE.CODGAR1" />
  			<gene:campoTrova campo="CODCIG" entita="GARE" where="GARE.NGARA=GARE.CODGAR1" />
			<gene:campoTrova campo="NOT_GAR" entita="GARE" where="GARE.NGARA=GARE.CODGAR1" />
			<gene:campoTrova campo="IMPAPP" entita="GARE" where="GARE.NGARA=GARE.CODGAR1" title="Imp. a base di gara"/>
  			<gene:campoTrova campo="CRITLICG" entita="GARE" where="GARE.NGARA=GARE.CODGAR1"  />
			<gene:campoTrova campo="NOMIMA" entita="GARE" where="GARE.NGARA=GARE.CODGAR1" title="Ragione sociale ditta aggiudicataria"/>
			<input type="hidden" name="lottiPerPagina" value="${param.lottiPerPagina}"/>
		</gene:formTrova>
		
		<gene:javaScript>	
			document.forms[0].jspPathTo.value="gare/commons/popup-filtro.jsp";
			document.forms[0].action+= "?tipo=Lotti&entFiltro=GARE";
			
			var lottiPerPagina="${param.lottiPerPagina}";
			var indiceSelezionato =  document.getElementById('risultatiPerPagina').selectedIndex;
            document.getElementById("risultatiPerPagina").options[indiceSelezionato].value = lottiPerPagina;
            document.getElementById("risultatiPerPagina").options[indiceSelezionato].innerHTML = lottiPerPagina;
            if(lottiPerPagina == 0){$("#risultatiPerPagina").parent().hide();}
			document.getElementById("risultatiPerPagina").disabled=true;
			
		</gene:javaScript>
		
	</gene:redefineInsert>
</gene:template>
