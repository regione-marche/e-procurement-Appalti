<%
/*
 * Created on: 21/02/2014
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

<c:if test="${! empty sessionScope.uffint}">
	<c:set var="codfiscUffint" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetCodfiscUffintFunction", pageContext, sessionScope.uffint)}/'/>
</c:if>

<gene:formScheda entita="ANTICOR" gestisciProtezioni="true" >
	
	<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
	<gene:redefineInsert name="pulsanteModifica"></gene:redefineInsert>
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>

<c:if test='${sessionScope.profiloUtente.abilitazioneGare eq "A" }'>
	<gene:redefineInsert name="addToAzioni" >
		<c:if test='${datiRiga.ANTICOR_COMPLETATO eq 2 and datiRiga.ANTICOR_ESPORTATO ne 1 and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ANTICORLOTTI.approvaDati") and sessionScope.profiloUtente.abilitazioneGare eq "A"}'>
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:controllaDati('${key }');" title='Approva dati' tabindex="1501">
						Approva dati
					</a>
				</td>
			</tr>
		</c:if>
		<c:if test='${datiRiga.ANTICOR_COMPLETATO eq "1" and datiRiga.ANTICOR_ESPORTATO ne 1 and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ANTICOR-scheda.DATIGEN.esportaDati") and sessionScope.profiloUtente.abilitazioneGare eq "A"}'>
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:esportaDati('${key }');" title='Genera XML per ANAC' tabindex="1502">
						Genera XML per ANAC
					</a>
				</td>
			</tr>
		</c:if>
		<c:if test='${datiRiga.ANTICOR_ESPORTATO eq "1" && datiRiga.ANTICOR_PUBBLICATO ne 1 && isIntegrazionePortaleAlice eq "true" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ANTICOR-scheda.DATIGEN.pubblicaXML") and sessionScope.profiloUtente.abilitazioneGare eq "A"}'>
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:pubblicaXmlPortale('${key }');" title='Pubblica XML sul Portale' tabindex="1503">
						Pubblica XML sul Portale
					</a>
				</td>
			</tr>
		</c:if>
	</gene:redefineInsert>
</c:if>

	<c:set var="msgChiaveErrore">	
		<fmt:message key="match.tags.template.dettaglio.campoChiave.messaggio">
			<fmt:param value="$"/>
		</fmt:message>
	</c:set>

	<c:set var="msgChiaveErrore" value="${fn:replace(msgChiaveErrore, '\\\\', '')}" />	
	
	<gene:campoScheda campo="ID" visibile="false"/>
	<gene:campoScheda campo="ANNORIF" visibile="false"/>
	
	<gene:campoScheda campo="COMPLETATO" />
	<gene:campoScheda campo="DATAPPR" modificabile="false"/>
	<gene:campoScheda campo="ESPORTATO" />
	<gene:campoScheda campo="DATAPUBBL"/>
	<gene:campoScheda campo="DATAAGG" />
	<gene:campoScheda campo="URLSITO" />
	
	<c:set var="lunghezzaUrlsito" value="${fn:length(datiRiga.ANTICOR_URLSITO) }"/>
	<c:if test="${lunghezzaUrlsito > 0 }">
		<c:set var="ultimoCarattereUrlsito" value="${fn:substring(datiRiga.ANTICOR_URLSITO,lunghezzaUrlsito-1,lunghezzaUrlsito) }"/>
	</c:if>
	<c:choose>
		<c:when test="${ultimoCarattereUrlsito eq '/' }">
			<c:set var="urlsito" value="${datiRiga.ANTICOR_URLSITO}"/>
		</c:when>
		<c:otherwise>
			<c:set var="urlsito" value="${datiRiga.ANTICOR_URLSITO}/"/>
		</c:otherwise>
	</c:choose>
		
	<gene:campoScheda campo="URLCOMUNICATA" title="URL da comunicare ad ANAC" campoFittizio="true" definizione="T100" value="${urlsito }indice_dataset.xml" visibile="${datiRiga.ANTICOR_ESPORTATO eq '1' and  modo eq 'VISUALIZZA'}"/>
	<gene:campoScheda campo="NOMEFILE" modificabile="false" href='javascript:download("${datiRiga.ANTICOR_NOMEFILE }")'/>
		
	<gene:campoScheda campo="PUBBLICATO" visibile='${fn:contains(listaOpzioniDisponibili, "OP114#")}' />
	<gene:campoScheda campo="CODEIN" visibile='false' />
	
	<gene:campoScheda>
		<td class="comandi-dettaglio" colSpan="2">
			<gene:insert name="addPulsanti"/>
			<c:if test='${datiRiga.ANTICOR_COMPLETATO eq 2 and datiRiga.ANTICOR_ESPORTATO ne 1 and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ANTICORLOTTI.approvaDati") and sessionScope.profiloUtente.abilitazioneGare eq "A"}'>
				<INPUT type="button"  class="bottone-azione" value='Approva dati' title='Approva dati' onclick="javascript:controllaDati('${key }');" >
				&nbsp;
			</c:if>
			<c:if test='${datiRiga.ANTICOR_COMPLETATO eq "1" and datiRiga.ANTICOR_ESPORTATO ne 1 and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ANTICOR-scheda.DATIGEN.esportaDati") and sessionScope.profiloUtente.abilitazioneGare eq "A"}'>
				<INPUT type="button"  class="bottone-azione" value='Genera XML per ANAC' title='Genera XML per ANAC' onclick="javascript:esportaDati('${key }');" >
				&nbsp;
			</c:if>
			<c:if test='${datiRiga.ANTICOR_ESPORTATO eq "1" && datiRiga.ANTICOR_PUBBLICATO ne 1 && isIntegrazionePortaleAlice eq "true" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ANTICOR-scheda.DATIGEN.pubblicaXML") and sessionScope.profiloUtente.abilitazioneGare eq "A"}'>
				<INPUT type="button"  class="bottone-azione" value='Pubblica XML sul Portale' title='Pubblica XML sul Portale' onclick="javascript:pubblicaXmlPortale('${key }');" >
				&nbsp;
			</c:if>
		</td>
	</gene:campoScheda>
</gene:formScheda>

<gene:javaScript>
	
	function controllaDati(chiave){
		var id= chiave.substring(chiave.indexOf(":") + 1);
		var href = "href=gare/anticor/popup-controlloDati.jsp&id=" + id + "&tipo=" + 1 + "&entitaAdempimenti=ANTICOR&numeroPopUp=1";
		openPopUpCustom(href, "controllaDati", "500", "250", "no", "no");
	}
	
	
	function visualizzaFileAllegato(nomFile) {
		var url="${pageContext.request.contextPath}/" + nomFile ; 
		window.open(url,'Download');
	}
 	
 	function download(nomeFile){
 		var chiave= '${key }';
 		if(confirm("Si sta per scaricare (download) una copia del file in locale. Ogni modifica\nverrà apportata alla copia locale ma non all'originale.\nContinuare?")){
  			var href="${contextPath}/pg/DownloadFile.do?"+csrfToken+"&nomeFile="+nomeFile+"&chiave="+chiave+"&paginaRicaricata=gare/anticor/anticor-scheda.jsp";
  			<c:if test="${!empty codfiscUffint }">
  				var codfisc="${codfiscUffint }";
  				href+="&codfisc=" + codfisc;
  			</c:if>
  			href+="&property=si&paginaAttiva=0";
  			document.location.href=href;
  		}
  			
 	}
 	
 	function esportaDati(chiave){
		var id= chiave.substring(chiave.indexOf(":") + 1);
		var annorif = "${datiRiga.ANTICOR_ANNORIF}";
		var urlsito = escape(getValue("ANTICOR_URLSITO"));
		var isIntegrazionePortale = '${fn:contains(listaOpzioniDisponibili, "OP114#")}';
		var href = "href=gare/anticor/popup-esportaDati.jsp&id=" + id + "&anno=" + annorif + "&urlsito=" + urlsito + "&isIntegrazionePortale=" + isIntegrazionePortale + "&numeroPopUp=1";
		openPopUpCustom(href, "esportaDati", "850", "300", "no", "no");
	}
	
	function pubblicaXmlPortale(chiave){
		var id= chiave.substring(chiave.indexOf(":") + 1);
		var annorif = "${datiRiga.ANTICOR_ANNORIF}";
		var href = "href=gare/anticor/popup-pubblicaXmlPortale.jsp&id=" + id + "&anno=" + annorif + "&numeroPopUp=1";
		openPopUpCustom(href, "pubblicaXmlPortale", "450", "250", "no", "no");
	}
</gene:javaScript>