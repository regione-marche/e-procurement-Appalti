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

<c:set var="ngara" value='${gene:getValCampo(key, "GARE.NGARA")}' />

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetTIPGENFunction" parametro='${codiceGara}' />

<c:set var="modelliPredispostiAttivoIncondizionato" value="1" scope="request" />

<c:set var="temp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GestioneStipulaAccordoQuadroFunction",  pageContext, ngara)}'/>

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
	
	<gene:redefineInsert name="addToAzioni" >
		<c:if test='${autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.EvadiIdsAssociati") and modo ne "MODIFICA"}' >
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:EvadiIdsAssociati();" title='Evadi ids associati' tabindex="1511">
						Evadi ids associati
					</a>
				</td>
			</tr>
		</c:if>
	</gene:redefineInsert>
		
	<jsp:include page="gare-interno-contratto.jsp">
		<jsp:param name="chiamante" value="stipula-accordo-quadro"/>
		<jsp:param name="ngara" value="${ngara }"/> 
		<jsp:param name="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica }"/> 
		<jsp:param name="tipoAppalto" value="${tipoAppalto }"/> 
		<jsp:param name="aqoper" value="${aqoper }"/>  
	</jsp:include>
		

	<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
	<input type="hidden" name="WIZARD_PAGINA_ATTIVA" value="${paginaAttivaWizard}" />
	<input type="hidden" name="DIREZIONE_WIZARD" id="DIREZIONE_WIZARD" value="" />

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
		
	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.PersonalizzazioneAutovie")}'>
		redefineLabels();
		redefineTooltips();
		redefineTitles();
	</c:if>
	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.AutovieArchiflow")}'>
		addHrefs();
	</c:if>
	
	<c:if test="${aqoper eq 2 }">
		$(document).ready(function (){
			ridisoPrec = [] ;
			var numeroDitteAggiudicatarie = ${numeroDitteAggiudicatarie};
			if(numeroDitteAggiudicatarie>0){
				for(var i=1; i <= numeroDitteAggiudicatarie; i++){
						ridisoPrec[i] = getValue("DITGAQ_RIDISO_" + i);
				}		
			}
		});
		
		function calcolaIMPGAR(indice){
			var newRidiso = getValue("DITGAQ_RIDISO_" + indice);
			var oldRidiso = ridisoPrec[indice];
			var importoCauzione = toVal(getValue("DITGAQ_IMPGAR_" + indice));
			
			if(importoCauzione == null ||importoCauzione=="") importoCauzione=0;
			importoCauzione=parseFloat(importoCauzione);
			
			if (newRidiso == null || newRidiso=="") newRidiso=2;
			if (oldRidiso == null || oldRidiso=="") oldRidiso=2;
			
			if (newRidiso != oldRidiso) {
				if (newRidiso==1) {
					importoCauzione = importoCauzione / 2;
				}else{ 
					importoCauzione = importoCauzione * 2;
				}
			}
			ridisoPrec[indice] = newRidiso;
			return toMoney(importoCauzione);
		}
		
	</c:if>
	
			
	</gene:javaScript>
</gene:formScheda>