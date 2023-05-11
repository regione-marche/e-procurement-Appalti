<%
/*
 * Created on: 30-07-2015
 *
 * Copyright (c) Maggioli S.p.A. - Divisione EldaSoft
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<c:choose>
	<c:when test='${not empty param.integrazioneWSDM}'>
		<c:set var="integrazioneWSDM" value='${param.integrazioneWSDM}'/>
	</c:when>
	<c:when test='${not empty keyParent}'>
		<c:set var="integrazioneWSDM" value='${integrazioneWSDM}'/>
	</c:when>
</c:choose>

<gene:template file="popup-template.jsp" >
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:setString name="titoloMaschera" value="Imposta filtro"/>
	
	<c:set var="modo" value="MODIFICA" scope="request" />
	<gene:redefineInsert name="corpo">
		<%-- la condizione di filtro serva da discriminante per l'aggiornamento del form di ricerca in sessione --%>
  		<gene:formTrova entita="W_INVCOM" filtro="W_INVCOM.IDCOM=W_INVCOM.IDCOM AND 2=2">
			<gene:campoTrova campo="COMMSGOGG" />		
			<gene:campoTrova campo="COMPUB" />		
			<gene:campoTrova campo="${gene:getDBFunction(pageContext,'datetimetodate','COMDATINS')}" computed="true" title="Data inserimento" definizione="D;0;;DATA_ELDA;COMDATINS" />	
			<gene:campoTrova campo="COMSTATO" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoCOMSTATO"/>		
			<gene:campoTrova campo="DESINTEST" entita="W_INVCOMDES" where="W_INVCOMDES.IDPRG=W_INVCOM.IDPRG AND W_INVCOMDES.IDCOM=W_INVCOM.IDCOM" title="Destinatario"/>		
			<gene:campoTrova campo="DESMAIL" entita="W_INVCOMDES" where="W_INVCOMDES.IDPRG=W_INVCOM.IDPRG AND W_INVCOMDES.IDCOM=W_INVCOM.IDCOM" title="Mail destinatario" />
			<c:if test="${integrazioneWSDM =='1'}">
				<gene:campoTrova campo="COMNUMPROT" />
				<gene:campoTrova campo="${gene:getDBFunction(pageContext,'datetimetodate','COMDATPROT')}" computed="true" title="Data protocollo" definizione="D;0;;DATA_ELDA;COMDATPROT" />	
			</c:if>	
			<gene:campoTrova campo="COMMODELLO" />
			<input type="hidden" name="integrazioneWSDM" id="integrazioneWSDM" value="${integrazioneWSDM}" />	
		</gene:formTrova>
	</gene:redefineInsert>
	
	<gene:javaScript>	
	document.forms[0].jspPathTo.value="gare/commons/popup-filtro.jsp";
	document.forms[0].action+="?tipo=ComunicazioniOut&entFiltro=W_INVCOM";
	document.getElementById("risultatiPerPagina").disabled=true;
	</gene:javaScript>
	
</gene:template>
