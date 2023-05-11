
<%
	/*
	 * Created on 10-02-2014
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:historyClear/>

<c:set var="documentiAssociatiDB" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.documentiAssociatiDB")}'/>	

<c:choose>
	<c:when test='${not empty param.tipgen}'>
		<c:set var="tipgen" value="${param.tipgen}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipgen" value="${tipgen}" />
	</c:otherwise>
</c:choose>

<c:set var="descTipgen" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDescrizioneTipgenFunction", pageContext, tipgen)}' />

<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:set var="obj" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetAssociazioneGaraLavoroFunction", pageContext, ngara)}' />

<c:set var="where" value="(C0OGGASS.C0AENT='APPA' AND C0OGGASS.C0AKEY1='${clavor}' AND C0OGGASS.C0AKEY2='${numera}') or (C0OGGASS.C0AENT='PERI' AND C0OGGASS.C0AKEY1='${clavor}')" />

<gene:template file="popup-template.jsp" gestisciProtezioni="false" >
	<gene:setString name="titoloMaschera" value="Lista dei documenti associati" />
	<gene:redefineInsert name="corpo">
			
		<table class="lista">
			<tr>
				<td><gene:formLista entita="C0OGGASS" pagesize="20" tableclass="datilista" gestisciProtezioni="true" sortColumn="-2;4" where="${where}" >
					<gene:campoLista campo="C0ACOD"  visibile="false"/>
					<gene:campoLista campo="C0AENT"  visibile="false"/>
					<gene:campoLista title="Documento per" campo="TIPO" campoFittizio="true" definizione="T10"  value='${gene:if(datiRiga.C0OGGASS_C0AENT eq "PERI", descTipgen, "Appalto")}' width="100"/>										
					<gene:campoLista campo="C0ADAT" width="100"/>
					<gene:campoLista campo="C0ATIT"   />
					<c:choose>
						<c:when test="${documentiAssociatiDB eq '1' }">
							<gene:campoLista campo="C0ANOMOGG" href="javascript:visualizzaFileDIGOGG('${datiRiga.C0OGGASS_C0ACOD}', '${datiRiga.C0OGGASS_C0ANOMOGG}');" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTitoloDocumentoDownload"/>
						</c:when>
						<c:otherwise>
							<gene:campoLista campo="C0ANOMOGG" href="javascript:mostraDocumento(${datiRiga.C0OGGASS_C0ACOD});" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTitoloDocumentoDownload"/>
						</c:otherwise>
					</c:choose>
					
					<input type="hidden" name="tipgen" id="tipgen" value="${tipgen}" />
					<input type="hidden" name="clavor" id="clavor" value="${clavor}" />
                    <input type="hidden" name="numera" id="numera" value="${numera}" />
               	
				</gene:formLista></td>
			</tr>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<INPUT type="button"  class="bottone-azione" value='Chiudi' title='Chiudi' onclick="javascript:window.close();">&nbsp;
					
				</td>
			</tr>			
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
			<c:choose>
				<c:when test="${documentiAssociatiDB eq '1' }">
					function visualizzaFileDIGOGG(c0acod, dignomdoc) {
						if (confirm("Si sta per scaricare (download) una copia del file in locale. Ogni modifica verrà apportata alla copia locale ma non all\'originale. Continuare?"))
						{
							var href = "${pageContext.request.contextPath}/VisualizzaFileDIGOGG.do";
							document.location.href = href+"?c0acod=" + c0acod + "&dignomdoc=" + dignomdoc + "&" + csrfToken;
						}
					}
				</c:when>
				<c:otherwise>
					function mostraDocumento(id){
						var tipgen="${tipgen }";
						var clavor="${clavor }";
						var numera="${numera }";
						document.location.href='${pageContext.request.contextPath}/pg/DocumentoAssociatoAppaltoDaGare.do?'+csrfToken+'&metodo=download&id=' + id + '&tipgen=' + tipgen + '&clavor=' + clavor + '&numera=' + numera;
					}
				</c:otherwise>
			</c:choose>		
			
	
	</gene:javaScript>
</gene:template>
