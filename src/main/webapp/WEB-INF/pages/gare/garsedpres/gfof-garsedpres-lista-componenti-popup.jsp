<%
/*
 * Created on: 04/06/2013
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
		Finestra che visualizza la lista dei componenti della commissione da associare alla seduta di gara
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div style="width:97%;">

<gene:template file="popup-message-template.jsp">
	
	<c:choose>
		<c:when test='${not empty param.numeroGara}'>
			<c:set var="numeroGara" value="${param.numeroGara}" />
		</c:when>
		<c:otherwise>
			<c:set var="numeroGara" value="${numeroGara}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${not empty param.numeroSeduta}'>
			<c:set var="numeroSeduta" value="${param.numeroSeduta}" />
		</c:when>
		<c:otherwise>
			<c:set var="numeroSeduta" value="${numeroSeduta}" />
		</c:otherwise>
	</c:choose>
	
	${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ValidazioneParametroFunction", pageContext, numeroGara, "SC", "20")}
	${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ValidazioneParametroFunction", pageContext, numeroSeduta, "N","")}
	
	<c:set var="parametri" value="T:${numeroGara};T:${numeroGara};N:${numeroSeduta}"/>
	<c:set var="where" value="GFOF.NGARA2=? AND GFOF.CODFOF NOT IN (SELECT CODFOF FROM GARSEDPRES WHERE GARSEDPRES.NGARA = ? AND GARSEDPRES.NUMSED=?)"/>
	${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ImpostazioneFiltroFunction", pageContext, "GFOF", where, parametri)}
	
	<c:set var="where1" value="GFOF.NGARA2='${numeroGara}' AND GFOF.CODFOF NOT IN (SELECT CODFOF FROM GARSEDPRES WHERE GARSEDPRES.NGARA ='${numeroGara }' AND GARSEDPRES.NUMSED=${numeroSeduta})" />
		
	<gene:setString name="titoloMaschera" value='Lista dei componenti della commissione da associare alla seduta di gara' />
		
	<gene:redefineInsert name="corpo">
	
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>

		<br>
		<c:choose>
			<c:when test='${empty RISULTATO }'>
				Selezionare dalla lista sottostante i componenti della commissione che si intende associare alla seduta di gara
			</c:when>
			<c:when test='${RISULTATO eq "OK"}'>
				I componenti della commissione sono stati associati alla seduta di gara
			</c:when>
			<c:when test='${RISULTATO eq "ERRORI"}'>
				Non è possibile associare i componenti della commissione alla seduta di gara
			</c:when>
		</c:choose>
		
		<br>
		<br>	
		
			
		<c:choose>
			<c:when test='${empty RISULTATO}'>
				<c:set var="modo" value="MODIFICA" scope="request" />
			</c:when>
			<c:otherwise>
				<c:set var="modo" value="APRI" scope="request" />
			</c:otherwise>
		</c:choose>
		
		
		<gene:formLista entita="GFOF" sortColumn="3" tableclass="datilista" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAssociaComponentiGARSEDPRES"
		 	distinct="true" pagesize="0">
			<gene:campoLista title="Seleziona<br><center>${titoloMenu}</center>" width="50">
				<c:if test="${currentRow >= 0}">
					<input type="checkbox" name="keys" value="${datiRiga.GFOF_NGARA2};${datiRiga.GFOF_CODFOF};${numeroSeduta}" />
				</c:if>
			</gene:campoLista>
			<gene:campoLista campo="NGARA2" visibile="false"/>
			<gene:campoLista campo="CODFOF" title="Codice componente" width="100"/>
			<gene:campoLista campo="NOMFOF" title="Nome componente"/>
			<input type="hidden" name="numeroGara" id="numeroGara" value="${numeroGara}" />
			<input type="hidden" name="numeroSeduta" id="numeroSeduta" value="${numeroSeduta}" />			
						
			<gene:redefineInsert name="buttons">
				<c:if test='${empty RISULTATO and datiRiga.rowCount > 0 }'>
					<INPUT type="button" class="bottone-azione" value="Aggiungi componenti commissione selezionati" title="Aggiungi componenti commissione selezionati" onclick="javascript:aggiungi()">&nbsp;
				</c:if>
				<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi()">&nbsp;
			</gene:redefineInsert>
			
		</gene:formLista>

  	</gene:redefineInsert>

	<gene:javaScript>
		function aggiungi(){
			var numeroOggetti = contaCheckSelezionati(document.forms[0].keys);
	  		if (numeroOggetti == 0) {
	      		alert("Selezionare almeno un componente della commissione dalla lista");
	      	} else {
	      		listaConferma();
 			}
		}

		function chiudi(){
			<c:if test='${not empty RISULTATO}'>
				var paginalista = opener.document.forms[0].pgCorrente.value = 0;
				opener.listaVaiAPagina(paginalista);
			</c:if>
			window.close();
		}
	</gene:javaScript>
	
	</gene:template>
</div>
