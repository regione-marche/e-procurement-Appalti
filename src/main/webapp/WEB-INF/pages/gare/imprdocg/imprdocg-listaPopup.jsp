<%/*
   * Created on 17-ott-2007
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

<c:set var="codiceGara" value='${gene:getValCampo(key, "DITG.CODGAR5")}' />
<c:set var="numeroGara" value='${gene:getValCampo(key, "DITG.NGARA5")}' />
<c:set var="codiceDitta" value='${gene:getValCampo(key, "DITG.DITTAO")}' />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<c:set var="prefissoFileDownloadComBari" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", prefissoFileDownloadComuneBari)}'/>

<c:choose>
	<c:when test='${not empty param.genereGara}'>
		<c:set var="genereGara" value="${param.genereGara}" scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="genereGara" value="${genereGara}" scope="request"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.tipo}'>
		<c:set var="tipo" value="${param.tipo}" scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="tipo" value="${tipo}" scope="request"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.stepWizard}'>
		<c:set var="stepWizard" value="${param.stepWizard}" />
	</c:when>
	<c:otherwise>
		<c:set var="stepWizard" value="${stepWizard}" />
	</c:otherwise>
</c:choose>

<c:if test="${stepWizard eq '6.5'}" >
	<c:set var="stepWizard" value="6" />
</c:if>

<c:set var="tipoImpresa" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaImpresaFunction",  pageContext,key)}'/>

<c:set var="nomimo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNOMIMOFunction", pageContext, numeroGara, codiceGara, codiceDitta)}' />

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.InizializzaUpdateListaFunction" parametro="${key}" />

<c:set var="ordinamento" value="-13;-14;5;8"/>

<gene:template file="popup-template.jsp" gestisciProtezioni="false">
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
	</gene:redefineInsert>
	<c:choose>
		<c:when test='${genereGara eq 20}'>
			<gene:setString name="titoloMaschera" value="Consultazione documenti richiesti per iscrizione al catalogo per la ditta ${nomimo}" />
		</c:when>
		<c:otherwise>
			<gene:setString name="titoloMaschera" value="Consultazione documenti richiesti per iscrizione a elenco per la ditta ${nomimo}" />
		</c:otherwise>
	</c:choose>
	
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	
	<c:set var="whereV_GARE_DOCDITTA"
				value="V_GARE_DOCDITTA.CODGAR='${codiceGara }' and (V_GARE_DOCDITTA.NGARA is null or V_GARE_DOCDITTA.NGARA='${numeroGara }') and V_GARE_DOCDITTA.CODIMP='${codiceDitta}' " />

	<c:set var="indiceRiga" value="-1"/>
	<c:set var="numCambi" value="0"/>
	
	<c:set var="whereImprdocg" value="IMPRDOCG.CODGAR = V_GARE_DOCDITTA.CODGAR and IMPRDOCG.NGARA = V_GARE_DOCDITTA.NGARA and IMPRDOCG.CODIMP = V_GARE_DOCDITTA.CODIMP and IMPRDOCG.NORDDOCI = V_GARE_DOCDITTA.NORDDOCI and IMPRDOCG.PROVENI = V_GARE_DOCDITTA.PROVENI"/>
	
	<c:set var="whereV_GARE_DOCDITTA" value="${whereV_GARE_DOCDITTA} and (V_GARE_DOCDITTA.ISARCHI='2' or  V_GARE_DOCDITTA.ISARCHI is null or (V_GARE_DOCDITTA.ISARCHI='1' and (V_GARE_DOCDITTA.DATARILASCIO is not null or V_GARE_DOCDITTA.ORARILASCIO is not null or V_GARE_DOCDITTA.IDDOCDG is not null)))"/>
	
	<c:choose>
		<c:when test="${tipoImpresa eq '' or empty tipoImpresa }">
			<c:set var="whereV_GARE_DOCDITTA" value="${whereV_GARE_DOCDITTA} and V_GARE_DOCDITTA.CONTESTOVAL is null"/>
		</c:when>
		<c:otherwise>
			<c:set var="whereV_GARE_DOCDITTA" value="${whereV_GARE_DOCDITTA} and (V_GARE_DOCDITTA.CONTESTOVAL is null or V_GARE_DOCDITTA.CONTESTOVAL = ${tipoImpresa })"/>
		</c:otherwise>
	</c:choose>
	
	<gene:redefineInsert name="corpo">
				
  	<%// Creo la lista per i documenti%>
		<table class="lista">
					
			<tr>
				<td ${stileDati}>
  				<gene:formLista entita="V_GARE_DOCDITTA" where='${whereV_GARE_DOCDITTA}' pagesize="20" tableclass="datilista" sortColumn="${ordinamento }" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreVerificaDocumenti">
  					
					
					<c:set var="oldTab1desc" value="${newTab1desc}"/>
					<c:set var="newTab1desc" value="${datiRiga.V_GARE_DOCDITTA_BUSTADESC}"/>
															
					<gene:campoLista campoFittizio="true" visibile="false">
						<%/* Nel caso in cui siano diversi inframezzo il titolo */%>
						<c:if test="${newTab1desc != oldTab1desc}">
							<td colspan="9">
								<b>${newTab1desc }</b> 
							</td>
						</tr>
											
						<tr class="odd">
						<c:set var="numCambi" value="${numCambi + 1}"/>
						</c:if>
						
					</gene:campoLista>
										
					<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" visibile="false" />
					
					<jsp:include page="imprdocg-documenti-interno.jsp" >
						<jsp:param name="whereImprdocg" value="${whereImprdocg}"/>
					</jsp:include>
					
										
					<input type="hidden" name="numeroDocumenti" id="numeroDocumenti" value="" />
					<input type="hidden" name="codiceDitta" id="codiceDitta" value="${codiceDitta }" />
					<input type="hidden" name="stepWizard" id="stepWizard" value="${stepWizard }" />
					<input type="hidden" name="tipo" id="tipo" value="${tipo }" />
					<input type="hidden" name="genereGara" id="genereGara" value="${genereGara }" />
				</gene:formLista>
				</td>
			</tr>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<INPUT type="button"  class="bottone-azione" value='Chiudi' title='Chiudi' onclick="javascript:chiudi();">
					&nbsp;
				</td>
			</tr>
			
		</table>
		
		<form name="formVisFirmaDigitale" id="formVisFirmaDigitale" action="${pageContext.request.contextPath}/ApriPagina.do" method="post" target="popUpFirma">
			<input type="hidden" name="href" value="gene/system/firmadigitale/verifica-firmadigitale-popUp.jsp" />
			<input type="hidden" name="idprg" id="idprg" value="" />
			<input type="hidden" name="iddocdig" id="iddocdig" value="" />
		</form>
		
  </gene:redefineInsert>
  
	<gene:javaScript>
	document.getElementById("numeroDocumenti").value = ${currentRow}+1;
				
	function chiudi(){
		window.close();
	}
	
	function ulterioriCampi(indiceRiga, chiaveRiga){
			var href = "href=gare/imprdocg/imprdocg-schedaPopup-ulterioriCampi.jsp";
			href += "&indiceRiga=" + indiceRiga;
			href += "&key=" + chiaveRiga;
			openPopUpCustom(href, "ulterioriCampi", 800, 550, "yes", "yes");
		}
	
	function visualizzaFileAllegato(idprg,iddocdig,dignomdoc,dataril,oraril,doctel,indiceRiga,norddoci,proveni) {
		var numeroGara = "${numeroGara}";
		var codiceDitta = "${codiceDitta}";
		var vet = dignomdoc.split(".");
		var ext = vet[vet.length-1];
		ext = ext.toUpperCase(); 
		if(ext=='P7M' || ext=='TSD'){
			if(dataril!=null && dataril!=''){
				var res1 = dataril.substring(0,2);
				var res2 = dataril.substring(3,5);
				var res3 = dataril.substring(6,10);
				var fdataril = res3+res2+res1;
				if(oraril!=null && oraril!="")
					fdataril+=" " + oraril +":00";
				if($("#ckdate").size() == 0){
					var _input = $("<input/>", {"type": "hidden","id": "ckdate", "name": "ckdate", value:""});
					$("#formVisFirmaDigitale").append(_input);
				}
				document.formVisFirmaDigitale.ckdate.value = fdataril;
			}else{
			 	if($("#ckdate").size() > 0){
					document.formVisFirmaDigitale.ckdate.remove();
				}
			}
		 	document.formVisFirmaDigitale.idprg.value = idprg;
			document.formVisFirmaDigitale.iddocdig.value = iddocdig;
			var l = Math.floor((screen.width-800)/2);
			var t = Math.floor((screen.height-550)/2);
			var numOpener = getNumeroPopUp()+1;
			l = l - 30 + (numOpener * 30);
			t = t - 50 + (numOpener * 50);
			tracciamentoDownloadDocimpresa(idprg, iddocdig,numeroGara,codiceDitta,doctel);
			window.open("","popUpFirma","toolbar=no,menubar=no,width=800,height=550,top="+t+",left="+l+",resizable=yes,scrollbars=yes");
			document.formVisFirmaDigitale.submit();
		}else{
			tracciamentoDownloadDocimpresa(idprg, iddocdig,numeroGara,codiceDitta,doctel);
			var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
			var nomeCodificato = encodeURIComponent(dignomdoc);
			document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + nomeCodificato;
		}
	}
	
	</gene:javaScript>


</gene:template>	

