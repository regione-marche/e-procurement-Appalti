<%
/*
 * Created on: 20/11/2008
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
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
</gene:redefineInsert>

<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>

<c:set var="ngara" value='${gene:getValCampo(key, "NGARA")}'/>
<c:set var="meruolo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMERUOLOFunction",  pageContext, ngara)}'/>

<table class="dettaglio-tab-lista">
	<tr>
		<td>
			<gene:formLista entita="V_GARE_PRODOTTI_VALUTATI" where='V_GARE_PRODOTTI_VALUTATI.NGARA = #GARE.NGARA#' tableclass="datilista" sortColumn="3;2"
						gestisciProtezioni="true" pagesize="25" >
				<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
					<jsp:param name="entita" value="V_GARE_TORN"/>
					<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
					<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
				</jsp:include>
					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
				
  					<gene:redefineInsert name="addToAzioni" >
						<c:if test='${ autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.AssegnaMigliorOfferente") and (meruolo eq "1" or meruolo eq "3") }'>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:affidaProdottiValutati('${datiRiga.V_GARE_PRODOTTI_VALUTATI_NGARA}');" title="Affida prodotti valutati" tabindex="1502">
										Affida prodotti valutati
									</a>
								</td>
							</tr>
						</c:if>
  					</gene:redefineInsert>
				
				<gene:campoLista campo="CODGAR" visibile="false" />
				<gene:campoLista campo="NGARA" visibile="false" />
				<c:set var="visualizzaLink" value='true'/>				
				<c:set var="link" value='javascript:archivioImpresa("${datiRiga.V_GARE_PRODOTTI_VALUTATI_DITTAO}");' />
				<gene:campoLista campo="DITTAO" visibile="false" />
				<gene:campoLista campo="NOMEST" headerClass="sortable" href="${gene:if(visualizzaLink, link, '')}" />
				<gene:campoLista campo="IMPTOT" headerClass="sortable" />
				<gene:campoLista campo="AFFIDAMENTO" />
				<gene:campoLista campo="CODCIG"  />
				<gene:campoLista campo="IMPAFFIDATO"  />
			
					<gene:campoLista title="" width="20" >
					<c:if test='${autorizzatoModifiche ne 2 and empty datiRiga.V_GARE_PRODOTTI_VALUTATI_AFFIDAMENTO and (meruolo eq "1" or meruolo eq "3")}'>
						<a href="javascript:creaAffidamento('${datiRiga.V_GARE_PRODOTTI_VALUTATI_DITTAO}');" title="Crea affidamento" >
							<img width="20" height="20" title="Crea affidamento" alt="Crea affidamento" src="${pageContext.request.contextPath}/img/attiva_valutazione.png"/>
						</a>
					</c:if>
					</gene:campoLista>
				
			</gene:formLista >
		</td>
	</tr>
</table>


	<form name="formCreaAffValutazioneProdotti" id="formCreaAffValutazioneProdotti" action="${pageContext.request.contextPath}/pg/CreaAffValutazioneProdotti.do" method="post">
		<input type="hidden" id="seguen" name="seguen" value="${ngara}" />
		<input type="hidden" id="dittao" name="dittao" value="" />
		<input type="hidden" id="uffint" name="uffint" value="${uffint}" />
	</form>

<gene:javaScript>
		
	function creaAffidamento(ditta){
		$("#dittao").val(ditta);
		
		$.ajax({
			type: "POST",
			dataType: "json",
			async: false,
			beforeSend: function(x) {
				if(x && x.overrideMimeType) {
					x.overrideMimeType("application/json;charset=UTF-8");
				}
			},
			url: "pg/GetRagioneSocialeDitta.do",
			data : {
				codiceDitta: ditta
			}, 
			success: function(data){
				if (data) {
					ragioneSoialecDitta = data.ragsocDitta;
	        	}
			},
			error: function(e) {
				alert("Errore durante la selezione della ragione sociale della ditta");
			}
		});
		
		
		var msg="Proseguendo verrà creato l'affidamento per la ditta "+ragioneSoialecDitta+".\nConfermi la creazione?";
		if(ditta!=null && confirm(msg)){
			bloccaRichiesteServer();
			document.formCreaAffValutazioneProdotti.submit();
		}
	}
	
	function affidaProdottiValutati(ngara){
		var comando = "href=gare/gare/gare-popup-prodotti-valutati.jsp&ngara=" + ngara;
	 	openPopUpCustom(comando, "affidaProdottiValutati", 900, 450, "yes", "yes");
		
	}
	
	
	
	
	

</gene:javaScript>