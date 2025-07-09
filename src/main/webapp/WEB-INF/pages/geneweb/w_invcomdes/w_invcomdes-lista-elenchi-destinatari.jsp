<%
/*
 * Created on: 23/05/2018
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

/*
	Descrizione:
		Finestra che visualizza la conferma per l'inserimento delle pubblicazioni bando/esito predefinite
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>



<div style="width:97%;">

<gene:template file="popup-message-template.jsp">
	
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
	</gene:redefineInsert>
	
	<c:choose>
		<c:when test='${not empty dittao}'>
			<c:set var="dittao" value='${dittao}' />
		</c:when>
		<c:otherwise>
			<c:set var="dittao" value="${param.dittao}" />
		</c:otherwise>
	</c:choose>
	
	<gene:setString name="titoloMaschera" value='Dettaglio iscrizione agli elenchi' />
		
	<gene:redefineInsert name="corpo">
	
	<c:set var="where" value="(DITG.DITTAO= ? OR DITG.DITTAO in (SELECT CODIME9 FROM RAGIMP WHERE RAGIMP.CODDIC= ? AND EXISTS (SELECT 1 FROM IMPR WHERE TIPIMP in (3,10) AND CODIMP=CODIME9) )) AND EXISTS(SELECT 1 FROM GARE WHERE GARE.NGARA=DITG.NGARA5 AND GARE.GENERE=10)"/>
	<c:set var="parametri" value="T:${dittao};T:${dittao}"/>
	${gene:callFunction4("it.eldasoft.gene.tags.functions.ImpostazioneFiltroFunction", pageContext, "DITG", where, parametri)}
		
	
		<gene:formLista entita="DITG" pagesize="20" sortColumn="2" tableclass="datilista" gestisciProtezioni="true">
			<gene:campoLista campo="NOMIMO"  />
			<gene:campoLista campo="NGARA5" title="Codice elenco"/>
			<gene:campoLista campo="OGGETTO" entita="GAREALBO" where="GAREALBO.NGARA=DITG.NGARA5"  />
			<gene:campoLista campo="ABILITAZ"/>
			
			
			<gene:redefineInsert name="buttons">
				<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi()">&nbsp;
			</gene:redefineInsert>
			<input type="hidden" name="dittao" id="dittao" value="${dittao}"/>
		</gene:formLista>

  	</gene:redefineInsert>

	<gene:javaScript>

		
		function chiudi(){
			window.close();
		}
	</gene:javaScript>
	
	</gene:template>
</div>
