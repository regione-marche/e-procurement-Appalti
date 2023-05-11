<%
/*
 * Created on: 09/01/2013
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


<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.titoli.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.hrefProtocollo.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
</gene:redefineInsert>
	
<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
<c:set var="ncont" value="1"/>

<c:set var="ngara" value='${gene:getValCampo(key, "GARE.NGARA")}' />

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetTIPGENFunction" parametro='${codiceGara}' />

<c:set var="modelliPredispostiAttivoIncondizionato" value="1" scope="request" />

<gene:formScheda entita="GARE" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreContratto">
	
	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	
	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
		<jsp:param name="entita" value="V_GARE_TORN"/>
		<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
		<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
	</jsp:include>
	
	<gene:redefineInsert name="modelliPredisposti" >
		<tr>
			<c:choose>
		        <c:when test='${isNavigazioneDisabilitata ne "1"}'>
		        	<td class="vocemenulaterale">
						<a href="javascript:modelliPredispostiLocale();" title="Modelli predisposti" tabindex="1510">
							${gene:resource("label.tags.template.documenti.modelliPredisposti")}
					  	</a>
		   			</td>
		        </c:when>
		        <c:otherwise>
		          	<td>
						${gene:resource("label.tags.template.documenti.modelliPredisposti")}
					</td>
		        </c:otherwise>
			</c:choose>
		</tr>
	</gene:redefineInsert>
	
	<gene:redefineInsert name="documentiAssociati" >
	<c:choose>
		  <c:when test='${isNavigazioneDisabilitata ne "1"}'>
		  <c:set var="addWhere" value="COAKEY1=${datiRiga.GARECONT_NGARA};COAKEY2=${ncont}"/>
		  <c:set var="fictitiousVar" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNumDocAssociatiCustomFunction", pageContext, "GARECONT", addWhere)}' />
			<tr>
				<td class="vocemenulaterale">
					<a href='javascript:documentiAssociatiGarecont();' title="Documenti associati contratto" tabindex="1522">
						Documenti associati agg.efficace <c:if test="${not empty requestScope.numRecordDocAssociatiCustom}">(${requestScope.numRecordDocAssociatiCustom})</c:if>
					</a>
				</td>
			</tr>
		</c:when>
		        <c:otherwise>
		          	<td>
						Documenti associati agg.efficace
					</td>
		        </c:otherwise>
			</c:choose>
		</gene:redefineInsert>
				
	<jsp:include page="gare-interno-contratto.jsp">
		<jsp:param name="chiamante" value="aggiudicazione-efficace"/>
		<jsp:param name="ngara" value="${ngara }"/> 
		<jsp:param name="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica }"/> 
		<jsp:param name="tipoAppalto" value="${tipoAppalto }"/>  
	</jsp:include>

	<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
	<input type="hidden" name="WIZARD_PAGINA_ATTIVA" value="${paginaAttivaWizard}" />
	<input type="hidden" name="DIREZIONE_WIZARD" id="DIREZIONE_WIZARD" value="" />
	<input type="hidden" name="entitaPrincipaleModificabile" id="entitaPrincipaleModificabile" value="${sessionScope.entitaPrincipaleModificabile}" />
	<gene:campoScheda>
		<td class="comandi-dettaglio" colSpan="2">
			<c:choose>
			<c:when test='${modo eq "MODIFICA"}'>
				<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma()">
				<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
			</c:when>
			<c:otherwise>
																
				<c:choose>
					<c:when test='${(autorizzatoModifiche ne "2") and gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
					</c:when>
				</c:choose>
								
				
				
			</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</gene:campoScheda>

	<gene:javaScript>
	
	setIsGaraLottiConOffertaUnica("${isGaraLottiConOffertaUnica }");		
	
	var ridisoPrec = getValue("GARE_RIDISO");
			
		
	function modelliPredispostiLocale(){
	/***********************************************************
		Copia della funzione modelliPredisposti dal file jsAzioniTags.jsp
		e addattamento al caso specifico
	 ***********************************************************/
		var entita,valori;

		try {
			entita = "GARE";
			if(document.forms[0].key.value!=''){
				valori=document.forms[0].name+".key";
			} else if(document.forms[0].keyParent.value!=''){
				valori=document.forms[0].name+".keyParent";
			} else if(document.forms[0].keys.value!=''){
				valori=document.forms[0].name+".keys";
			}
			compositoreModelli('${pageContext.request.contextPath}',entita,'',valori);
		}catch(e){
		}
	}
	
	function archivioImpresaAggDef(){
		var codiceImpresa = getValue("GARE_DITTA");
		var href = ("href=gene/impr/impr-scheda.jsp&key=IMPR.CODIMP=T:" + codiceImpresa);
		document.location.href = contextPath + "/ApriPagina.do?"+csrfToken+"&" + href;
	}
	
	$('#rowLinkAddGARCOMPREQ td').parent().prepend($("<td>"));
	$('#rowLinkAddGARCOMPREQ td:eq(1)').attr("colspan","4");
	$('#rowMsgLastGARCOMPREQ td').parent().prepend($("<td>"));
	$('#rowMsgLastGARCOMPREQ td:eq(1)').attr("colspan","4");
	
	function documentiAssociatiGarecont(){
			var keys = "GARECONT.NGARA=T:"+getValue("GARECONT_NGARA")+";GARECONT.NCONT=N:"+${ncont};
			var href = contextPath+'/ListaDocumentiAssociati.do?'+csrfToken+'&metodo=visualizza&entita=GARECONT&valori='+keys;
		document.location.href = href;
	}
		
	
	</gene:javaScript>
</gene:formScheda>