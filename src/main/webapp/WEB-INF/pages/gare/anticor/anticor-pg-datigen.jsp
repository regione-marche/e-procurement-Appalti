<%
/*
 * Created on: 25/07/2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<fmt:setBundle basename="AliceResources" />
<c:set var="propertyUrlsito" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction",  "it.eldasoft.sil.pg.avcp.urlPortaleAlice")}' scope="request"/>

<gene:formScheda entita="ANTICOR" gestisciProtezioni="true"  gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreANTICOR">
	
	<c:if test="${datiRiga.ANTICOR_COMPLETATO eq '1'}">
		<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
		<gene:redefineInsert name="pulsanteModifica"></gene:redefineInsert>
	</c:if>

	<c:if test="${sessionScope.profiloUtente.abilitazioneGare ne 'A'}" >
		<gene:redefineInsert name="schedaNuovo" />
		<gene:redefineInsert name="schedaModifica" />
		<gene:redefineInsert name="pulsanteNuovo" />
		<gene:redefineInsert name="pulsanteModifica" />
	</c:if>

	<gene:redefineInsert name="addToAzioni" >	
	<c:if test='${datiRiga.ANTICOR_COMPLETATO eq "1" and modo eq "VISUALIZZA" and (gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ANTICOR-scheda.DATIGEN.rettifica"))}'>
		 <tr>   
		      <td class="vocemenulaterale">
			      	<a href="javascript:popupRettifica('${datiRiga.ANTICOR_ID}');" title="Rettifica/aggiorna pubblicazione" tabindex="1501">
					 Rettifica/aggiorna pubblicazione
					</a>		  
				</td>
		  </tr>
	</c:if>
	</gene:redefineInsert>
		
	<c:set var="msgChiaveErrore">	
		<fmt:message key="match.tags.template.dettaglio.campoChiave.messaggio">
			<fmt:param value="$"/>
		</fmt:message>
	</c:set>

	<c:set var="msgChiaveErrore" value="${fn:replace(msgChiaveErrore, '\\\\', '')}" />	

	
		
	<gene:campoScheda>
		<td colspan="2"><b>Dati generali</b></td>
	</gene:campoScheda>
	<gene:campoScheda campo="ID" visibile="false"/>
	<gene:campoScheda campo="ANNORIF" modificabile='${modo eq "NUOVO" }' obbligatorio="true">
		<gene:checkCampoScheda funzione='controlloAnno("##")' obbligatorio="true" messaggio="Il valore dell'anno di riferimento deve essere >=2013." onsubmit="false"/>
	</gene:campoScheda>
	<gene:campoScheda campo="TITOLO" obbligatorio="true"/>
	<gene:campoScheda campo="ESTRATTO" />
	<gene:campoScheda campo="ENTEPUBBL" obbligatorio="true"/>
	<gene:campoScheda campo="URLSITO" visibile="false"/>
	<gene:campoScheda campo="COMPLETATO" modificabile="false" defaultValue="2"/>
	<gene:campoScheda campo="ESPORTATO" modificabile="false"/>
	<gene:campoScheda campo="PUBBLICATO" visibile='${fn:contains(listaOpzioniDisponibili, "OP114#")}' modificabile="false"/>
	<gene:campoScheda campo="CODEIN" visibile='false' />
	<gene:campoScheda campo="LICENZA" obbligatorio="true" defaultValue="IODL" visibile="false"/>
	
	<gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
	</gene:campoScheda>
	
</gene:formScheda>

<gene:javaScript>
	function popupRettifica(id){
		var href = "href=gare/anticor/popupRettifica.jsp&id="+id+"&numeroPopUp=1";
		openPopUpCustom(href, "Rettifica", "480", "250", "no", "no");
	}
	 	
	
	function controlloAnno(anno){
		if(anno<2013)
		 return false;
		else
		 return true;
	}
	
	var schedaConfermaDefault = schedaConferma;

	function schedaConfermaCustom() {
		var protocollo = '${propertyUrlsito}';
		var isIntegrazionePortale = '${fn:contains(listaOpzioniDisponibili, "OP114#")}';
		protocollo = protocollo.substring(0, 5);
		if(isIntegrazionePortale == 'true' ){
			if ("http:".indexOf(protocollo) == -1 ){
				if (confirm("L'URL del sito di pubblicazione dei dati non contiene il protocollo http e pertanto non verr\u00E0 ritenuto corretto.\nL'indirizzo deve contenere obbligatoriamente il protocollo http e non https per essere ritenuto corretto.\n\nConfermi l'operazione?")) {
					schedaConfermaDefault();
				}
			}else{
				schedaConfermaDefault();
			}
		}
		else{
			schedaConfermaDefault();
		}
	}
	
	var schedaConferma = schedaConfermaCustom;
	
</gene:javaScript>