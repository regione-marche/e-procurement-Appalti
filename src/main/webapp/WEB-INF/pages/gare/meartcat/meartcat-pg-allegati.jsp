<%
/*
 * Created on: 12/06/2014
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




<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>


<c:set var="where" value="MEARTCAT.ID = #MEARTCAT.ID#"/>
<gene:formScheda entita="MEARTCAT" where="${where}" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreMEARTCAT" >
	<gene:redefineInsert name="schedaNuovo" />
	<gene:redefineInsert name="pulsanteNuovo" />
	<gene:campoScheda campo="ID" visibile="false"/>
	<gene:campoScheda campo="NGARA" visibile="false" defaultValue="${param.ngara}"/>
	<gene:campoScheda campo="COD" obbligatorio="true" visibile="false" />
	<gene:campoScheda title="Immagine ?" campo="OBBLIMG" visibile="false" obbligatorio="true" defaultValue="2"/>					
	<gene:campoScheda title="Descrizione aggiuntiva ?" campo="OBBLDESCAGG"  visibile="false" obbligatorio="true" defaultValue="2"/>
	<gene:campoScheda title="Dimensioni ?" campo="OBBLDIM"  visibile="false" obbligatorio="true" defaultValue="2"/>
	<gene:campoScheda title="Certificazioni ?" campo="OBBLCERTIF" visibile="false" obbligatorio="true" defaultValue="2"/>
	<gene:campoScheda campo="CERTIFRICH"  visibile="false" obbligatorio="true" />

	
	<c:set var="id" value='${gene:getValCampo(key,"ID")}' scope="request" />
	<c:set var="result" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetValoriMEALLARTCATFunction", pageContext, id, "2")}' />
	
	<c:choose>
	<c:when test="${modo eq 'VISUALIZZA' && !empty  datiMEALLARTCAT}">
		<gene:campoScheda addTr="false">
			<tbody id="sezioneMultiplaMEALLARTCAT">
		</gene:campoScheda>
		
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='MEALLARTCAT'/>
			<jsp:param name="chiave" value='${datiRiga.MEARTCAT_ID}'/>
			<jsp:param name="nomeAttributoLista" value='datiMEALLARTCAT' />
			<jsp:param name="idProtezioni" value="MEALLARTCAT" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/meallartcat/meallartcat-interno-t2-scheda.jsp"/>
			<jsp:param name="arrayCampi" value="'MEALLARTCAT_ID_', 'MEALLARTCAT_IDARTCAT_', 'MEALLARTCAT_TIPOALL_', 'MEALLARTCAT_IDPRG_','MEALLARTCAT_IDDOCDIG_', 'W_DOCDIG_IDPRG_', 'W_DOCDIG_IDDOCDIG_', 'W_DOCDIG_DIGNOMDOC_'"/>		
			<jsp:param name="titoloSezione" value="Facsimile certificato" />
			<jsp:param name="titoloNuovaSezione" value="Nuovo facsimile certificato" />
			<jsp:param name="descEntitaVociLink" value="facsimile certificato" />
			<jsp:param name="msgRaggiuntoMax" value="i facsimile"/>
			<jsp:param name="usaContatoreLista" value="true"/>
			<jsp:param name="sezioneListaVuota" value="false" />
		</jsp:include>

		<gene:campoScheda addTr="false">
			</tbody>
		</gene:campoScheda>
	</c:when>
	<c:otherwise>
		<gene:campoScheda addTr="false">
			<tr>
			<td colspan="8">Nessun elemento estratto</td>
			</tr>
		</gene:campoScheda>
	</c:otherwise>
	</c:choose>

	<gene:redefineInsert name="pulsanteModifica" />
	<gene:redefineInsert name="schedaModifica" />
	<gene:redefineInsert name="pulsanteNuovo" />
	<gene:redefineInsert name="schedaNuovo" />

	<gene:redefineInsert name="schedaConferma">
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:schedaConfermaUploadMultiplo();" title="Salva modifiche" tabindex="1501">
					${gene:resource("label.tags.template.dettaglio.schedaConferma")}</a></td>
		</tr>
	</gene:redefineInsert>
	
	<gene:redefineInsert name="schedaAnnulla">
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:schedaAnnullaUploadMultiplo();" title="Annulla modifiche" tabindex="1502">
				${gene:resource("label.tags.template.dettaglio.schedaAnnulla")}</a></td>
		</tr>
	</gene:redefineInsert>

	<gene:redefineInsert name="pulsanteSalva">
		<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConfermaUploadMultiplo();">
	</gene:redefineInsert>
	
	<gene:redefineInsert name="pulsanteAnnulla">
		<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnullaUploadMultiplo();">
	</gene:redefineInsert>

			

</gene:formScheda>		
		
<gene:javaScript>

			document.forms[0].encoding="multipart/form-data";
			
			function schedaConfermaUploadMultiplo(){
				document.forms[0].action = "${pageContext.request.contextPath}/pg/Scheda.do?"+csrfToken;
				schedaConferma();
			}
			
			function schedaAnnullaUploadMultiplo(){
				document.forms[0].action = "${pageContext.request.contextPath}/pg/Scheda.do?"+csrfToken;
				schedaAnnulla();
			}
			
            function scegliFile(indice) {
				var selezioneFile = document.getElementById("selFile[" + indice + "]").value;
				var lunghezza_stringa=selezioneFile.length;
				var posizione_barra=selezioneFile.lastIndexOf("\\");
				var nome=selezioneFile.substring(posizione_barra+1,lunghezza_stringa).toUpperCase();
				if(nome.length>100){
					alert("Il nome del file non può superare i 100 caratteri!");
					document.getElementById("selFile[" + indice + "]").value="";
					setValue("W_DOCDIG_DIGNOMDOC_" + indice,"");
				}else{
					setValue("W_DOCDIG_DIGNOMDOC_" + indice,nome);
				}
			}
			

			function visualizzaFileAllegato(idprg,iddocdig,dignomdoc) {
				var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
				document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
			}

</gene:javaScript>
	
