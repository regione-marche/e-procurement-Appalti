
<%
	/*
	 * Created on 01-02-2012
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

<c:choose>
	<c:when test='${not empty RISULTATO and RISULTATO eq "OK"}' >
		<script type="text/javascript">
			window.opener.document.forms[0].pgSort.value = "";
			window.opener.document.forms[0].pgLastSort.value = "";
			window.opener.document.forms[0].pgLastValori.value = "";
			window.opener.bloccaRichiesteServer();
			window.opener.listaVaiAPagina(0);
			window.close();
		</script>
	</c:when>
	<c:otherwise>
	

<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.garaElenco}'>
		<c:set var="garaElenco" value="${param.garaElenco}"  />
	</c:when>
	<c:otherwise>
		<c:set var="garaElenco" value="${garaElenco}" />
	</c:otherwise>
</c:choose>

<c:choose>
     <c:when test='${not empty param.categoriaPrev}'>
             <c:set var="categoriaPrev" value="${param.categoriaPrev}"  />
     </c:when>
	<c:otherwise>
		<c:set var="categoriaPrev" value="${categoriaPrev}" />
	</c:otherwise>
</c:choose>

<c:choose>
     <c:when test='${not empty param.classifica}'>
             <c:set var="classifica" value="${param.classifica}"  />
     </c:when>
	<c:otherwise>
		<c:set var="classifica" value="${classifica}" />
	</c:otherwise>
</c:choose>

<c:choose>
     <c:when test='${not empty param.tipoGara}'>
             <c:set var="tipoGara" value="${param.tipoGara}"  />
     </c:when>
	<c:otherwise>
		<c:set var="tipoGara" value="${tipoGara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.inserimentoDitteIterSemplificato}'>
		<c:set var="inserimentoDitteIterSemplificato" value="${param.inserimentoDitteIterSemplificato}" />
	</c:when>
	<c:otherwise>
		<c:set var="inserimentoDitteIterSemplificato" value="${inserimentoDitteIterSemplificato}" />
	</c:otherwise>
</c:choose>

<c:set var="where" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetFiltroSelOpUltimaAggiudicatariaFunction", pageContext)}' scope="request"/>

<c:choose>
	<c:when test='${not empty param.isGaraLottiConOffertaUnica}'>
		<c:set var="isGaraLottiConOffertaUnica" value="${param.isGaraLottiConOffertaUnica}" />
	</c:when>
	<c:otherwise>
		<c:set var="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.WIZARD_PAGINA_ATTIVA}'>
		<c:set var="WIZARD_PAGINA_ATTIVA" value="${param.WIZARD_PAGINA_ATTIVA}" />
	</c:when>
	<c:otherwise>
		<c:set var="WIZARD_PAGINA_ATTIVA" value="${WIZARD_PAGINA_ATTIVA}" />
	</c:otherwise>
</c:choose>

<c:set var="modo" value="MODIFICA" scope="request" />

<gene:template file="popup-template.jsp" gestisciProtezioni="false">
	
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
	</gene:redefineInsert>
	
	<gene:setString name="titoloMaschera" value="Selezione ditta ultima aggiudicataria dall'elenco operatori economici ${garaElenco}" />
	<gene:setString name="entita" value="${entita}" />
	<gene:redefineInsert name="corpo">
	
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		
		<br>
		Nella lista sottostante è riportata la ditta aggiudicataria dell'ultima gara, collegata all'elenco, aggiudicata in via definitiva e  
		con uguale categoria e classifica della gara corrente:<br>
		${tableCategoria }
		<br>
		Selezionare le ditte che si intende inserire in gara.
		<br>
			
		<table class="lista">
			<tr>
				<td><gene:formLista entita="GARE" pagesize="20" tableclass="datilista" gestisciProtezioni="false" sortColumn="2;5;4" where="${where}" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAssociaOperatoriEconomici">
					<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>"	width="50">
						<c:if test="${currentRow >= 0}">
						<input type="checkbox" name="keys" value="${datiRiga.GARE_DITTA };${datiRiga.IMPR_NOMIMP}" />
						<input type="hidden" name="imprese" value="${datiRiga.GARE_DITTA }" />
						</c:if>
					</gene:campoLista>
					<gene:campoLista campo="DITTA"  visibile="false" edit="true"/>
					<gene:campoLista campo="NOMIMP"  entita="IMPR" where ="IMPR.CODIMP = GARE.DITTA"/>					
					<gene:campoLista campo="NGARA"  visibile="false"/>
					<gene:campoLista campo="CODGAR1" visibile="false" />
					<gene:campoLista campo="CODICEGARA" title="Gara aggiudicata" campoFittizio="true" definizione="T21" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoCodiceGara"/>
					<gene:campoLista campo="CODIGA" title="Lotto" ordinabile="false"/>
					<gene:campoLista campo="NOT_GAR"  visibile="false"/>
					<gene:campoLista campo="DESTOR"  entita="TORN" where="TORN.CODGAR = GARE.CODGAR1" visibile="false"/>
					<gene:campoLista campo="OGGETTO" title="Oggetto" campoFittizio="true" definizione="T4000;;;NOTE" value='${datiRiga.TORN_DESTOR}${gene:if(datiRiga.TORN_DESTOR !="" && datiRiga.GARE_NOT_GAR != "", " -", "")} ${datiRiga.GARE_NOT_GAR}'/>
					<gene:campoLista campo="DATTOA" ordinabile="false" width="100"/>
					<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.VerificaDocumentiSelezioneDitteElenco')}">
						<gene:campoLista title="&nbsp;" width="20" >
							<a href="javascript:chiaveRiga='${chiaveRigaJava}';consultaDocumentiRichiesti('${datiRiga.GARE_DITTA}');" title="'Consultazione documenti iscrizione a elenco" >
								<img id="img${currentRow }" width="16" height="16" title="Consultazione documenti iscrizione a elenco" alt="Consultazione documenti iscrizione a elenco" src="${pageContext.request.contextPath}/img/documentazione_elenco.png"/>
							</a>
						</gene:campoLista>
					</c:if>
					
					
					<input type="hidden" name="where" id="where" value="${where}" />
					<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
                    <input type="hidden" name="garaElenco" id="garaElenco" value="${garaElenco}" />
                    <input type="hidden" name="categoriaPrev" id="categoriaPrev" value="${categoriaPrev}" />
                    <input type="hidden" name="classifica" id="classifica" value="${classifica}" />
                    <input type="hidden" name="WIZARD_PAGINA_ATTIVA" id="WIZARD_PAGINA_ATTIVA" value="${WIZARD_PAGINA_ATTIVA}" />
					<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
					<input type="hidden" name="inserimentoDitteIterSemplificato" id="inserimentoDitteIterSemplificato" value="${inserimentoDitteIterSemplificato}" />
					<input type="hidden" name="tipoGara" id="tipoGara" value="${tipoGara}" />
					<input type="hidden" name="ultimaAggiudicataria" id="ultimaAggiudicataria" value="1" />
					
					
				</gene:formLista></td>
			</tr>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<INPUT type="button"  class="bottone-azione" value='Aggiungi ditte selezionate' title='Aggiungi ditte selezionate' onclick="javascript:aggiungi();">&nbsp;&nbsp;&nbsp;
					<INPUT type="button"  class="bottone-azione" value='Chiudi' title='Chiudi' onclick="javascript:chiudi();">&nbsp;
					
				</td>
			</tr>			
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
		
		//setValue("WIZARD_PAGINA_ATTIVA", window.opener.getValue("WIZARD_PAGINA_ATTIVA"));
		function aggiungi(){
			var numeroOggetti = contaCheckSelezionati(document.forms[0].keys);
	  		if (numeroOggetti == 0) {
	      		alert("Selezionare almeno una ditta dalla lista");
	      	} else {
	      		listaConferma();
 			}
		}
		
		//Viene ricaricata la pagina chiamante in modo da ripulire le 
		//variabili di sessioni
		function chiudi(){
			window.opener.document.forms[0].pgSort.value = "";
			window.opener.document.forms[0].pgLastSort.value = "";
			window.opener.document.forms[0].pgLastValori.value = "";
			window.opener.bloccaRichiesteServer();
			window.opener.listaVaiAPagina(0);
			window.close();
		}
						
						
		function inizializzaLista(){
           for(var i=1; i <= ${currentRow} + 1; i++){
				var dittaPrec =  getValue("GARE_DITTA_" + (i -1));
				var ditta =  getValue("GARE_DITTA_" + i );
				if(dittaPrec != "" &&  ditta!= "" && dittaPrec == ditta){
					document.forms[0].keys[i -1].style.display = "none";
					document.images["img"+(i-1)].style.visibility = "hidden"; 
				}
			}
		}
		
		inizializzaLista();
		
		function selezionaTutti(objArrayCheckBox) {
	    	for (i = 0; i < objArrayCheckBox.length; i++) {
	      		if(objArrayCheckBox[i].style.display!="none")
	      			objArrayCheckBox[i].checked = true;
	      		
	    	}
	    }
	    
	    function consultaDocumentiRichiesti(codiceDitta){
			/*
			href = "href=gare/imprdocg/imprdocg-listaPopup.jsp";
			href += "&tipo=CONSULTAZIONE";
			var codiceGara= "$" + "${garaElenco }";
			var ngara= "${garaElenco}";
			var chiave = "DITG.CODGAR5=T:" + codiceGara + ";DITG.DITTAO=T:" + codiceDitta + ";DITG.NGARA5=T:" + ngara;
			href += "&key="+chiave;
			//href += "&stepWizard=${varTmp}";
			href += "&comunicazioniVis=0";
			openPopUpCustom(href, "verificaDocumentiRichiesti", 1100, 550, "yes", "yes");
			*/
			var codiceGara= "$" + "${garaElenco }";
			var ngara= "${garaElenco}";
			var chiave = "DITG.CODGAR5=T:" + codiceGara + ";DITG.DITTAO=T:" + codiceDitta + ";DITG.NGARA5=T:" + ngara;
			setContextPath("${pageContext.request.contextPath}");
			verificaDocumentiRichiesti(chiave,"CONSULTAZIONE",0,"false","${autorizzatoModifiche }");
		}
	</gene:javaScript>
</gene:template>
</c:otherwise>
</c:choose>