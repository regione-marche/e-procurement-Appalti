<%
/*
 * Created on: 01-apr-2020
 *
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A. - Divisione ELDASOFT
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 /* Dettaglio configurazione selezione da elenco operatori */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<fmt:setBundle basename="AliceResources" />

<c:set var="idOrdine" value='${gene:getValCampo(keyParent,"ID")}' />
<c:set var="idLineaOrdine" value='${gene:getValCampo(key,"ID")}' />
<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNsoDatiLineaOrdineFunction", pageContext, idOrdine,idLineaOrdine)}'/>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="NSO_LINEE_ORDINI-scheda">


<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:redefineInsert name="head">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.cpvvp.mod.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.cpvvsupp.mod.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/cpvvp/jquery.cpvvp.mod.css" >
</gene:redefineInsert>

	<%-- Settaggio delle stringhe utilizzate nel template --%>
	<gene:setString name="titoloMaschera" value="Dettaglio linea ordine" />
	
	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="NSO_LINEE_ORDINI" gestisciProtezioni="true"  gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreLineeOrdiniNso" >
		
			<gene:redefineInsert name="addToAzioni" />
			<gene:redefineInsert name="addToDocumenti" />
			<gene:redefineInsert name="schedaNuovo" />
			<gene:redefineInsert name="pulsanteNuovo" />
			<c:if test='${(requestScope.statoOrdine ne 1 && requestScope.statoOrdine ne 2)}'>
				<gene:redefineInsert name="schedaModifica" />
				<gene:redefineInsert name="pulsanteModifica" />
			</c:if>
			<%-- <c:if test="${requestScope.statoOrdine eq 8}">
				<%/*
					se l'ordine è revocato non devo permettere alcuna modifica
				*/ %>
				<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
				<gene:redefineInsert name="pulsanteModifica"></gene:redefineInsert>
			</c:if> --%>
			
			<gene:campoScheda campo="ID" visibile='false'/>
			<gene:campoScheda campo="NSO_ORDINI_ID" defaultValue="${idOrdine}" visibile='false'/>
			
			<gene:gruppoCampi idProtezioni="DATGEN">
				<gene:campoScheda>
					<td colspan="2"><b>Dati generali</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="ID_LINEA" modificabile="false" />
				<gene:campoScheda campo="CODICE" />
				<gene:campoScheda campo="DESCRIZIONE" />
			</gene:gruppoCampi>			
			
			<gene:gruppoCampi idProtezioni="PREZZI">
				<gene:campoScheda>
					<td colspan="2"><b>Prezzi</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="QUANTITA" visibile="${datiRiga.NSO_LINEE_ORDINI_UNIMIS ne 'ac'}"/>
				
				<gene:campoScheda title="Quantità disponibile" campo="QUANTITA_DISPONIBILE" value="${requestScope.qtaDisponibile}" visibile="${!empty requestScope.qtaDisponibile && datiRiga.NSO_LINEE_ORDINI_UNIMIS ne 'ac'}" modificabile="false" campoFittizio="true" definizione="F24.5;0;;;NSO_LO_QUANTI" />
				<gene:campoScheda campo="UNIMIS" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoUnitaMisura" />
				<gene:campoScheda campo="PREZZO_UNITARIO" modificabile="true" visibile="${datiRiga.NSO_LINEE_ORDINI_UNIMIS ne 'ac'}" />
				<gene:campoScheda visibile="${modo ne 'VISUALIZZA'}" >
					<div id="msgTotCalcolato" >
							<i><b>Attenzione: al salvataggio prezzo totale e prezzo totale disponibile verranno ricalcolati<br>
							in base percentuale rispetto all'importo di contratto </b></i><br>						
					</div>
				</gene:campoScheda>
				<gene:campoScheda campo="PREZZO_TOTALE_RIGA" title="Prezzo totale" campoFittizio="true" definizione="F24.5;0;;MONEY;NSO_LO_PU" value="${prezzoTotRiga}" modificabile="${datiRiga.NSO_LINEE_ORDINI_UNIMIS eq 'ac'}"/>
				<gene:campoScheda title="Prezzo totale disponibile" campo="PREZZO_TOTALE_DISPONIBILE" value="${requestScope.prezzoDisponibile}" modificabile="false" campoFittizio="true" definizione="F24.5;0;;MONEY;IAGGIU" visibile="${datiRiga.NSO_LINEE_ORDINI_UNIMIS eq 'ac'}"/>
				<gene:campoScheda campo="IMPORTO_DI_CONTRATTO" title="Importo di contratto" campoFittizio="true" definizione="F24.5;0;;MONEY;IAGGIU" modificabile="false" value="${requestScope.importoDiContratto}" visibile="${requestScope.isMonoRiga eq 1}"/>
				<gene:campoScheda campo="IVA" />
				<gene:campoScheda campo="CODICE_ESENZIONE" />
				<gene:campoScheda campo="CENTRO_COSTO" />
			</gene:gruppoCampi>			
			
			<gene:gruppoCampi idProtezioni="ICONS">
				<gene:campoScheda>
					<td colspan="2"><b>Informazioni di consegna</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="CONS_PARZIALE" />
				<gene:campoScheda campo="DATA_INIZIO_CONS" />
				<gene:campoScheda campo="DATA_FINE_CONS" />
				
				<gene:campoScheda title="Codice CPV" campo="CODCPV" href="#" speciale="true" >
						<gene:popupCampo titolo="Dettaglio CPV" href="" />
				</gene:campoScheda>

				
				<gene:campoScheda campo="NOTE" />
			</gene:gruppoCampi>
			
			
			<gene:gruppoCampi idProtezioni="URICH" >
			<gene:campoScheda>
				<td colspan="2"><b>Richiedente</b></td>
			</gene:campoScheda>
			<gene:archivio titolo="Uffici intestatari"
				 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CENINT"),"gene/uffint/uffint-lista-popup.jsp","")}'
				 scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda.jsp","")}'
				 schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda-popup.jsp","")}'
				 campi="UFFINT.CODEIN;UFFINT.NOMEIN"
				 chiave="NSO_LINEE_ORDINI_CODEIN_RICH" 
				 functionId="skip|abilitazione:1">
					<gene:campoScheda campo="CODEIN_RICH" title="Ufficio richiedente" />
					<gene:campoScheda campo="NOMEIN" title="Denominazione" entita="UFFINT" where="NSO_LINEE_ORDINI.CODEIN_RICH=UFFINT.CODEIN" 
						defaultValue="${requestScope.initNOMEIN}" />
			</gene:archivio>
			</gene:gruppoCampi>
			
			
			<gene:fnJavaScriptScheda funzione='calcolaTotaliP()' elencocampi='PREZZO_TOTALE_RIGA' esegui="false" />
			<gene:fnJavaScriptScheda funzione='calcolaTotaliQ()' elencocampi='NSO_LINEE_ORDINI_QUANTITA' esegui="false" />
			<gene:fnJavaScriptScheda funzione='calcolaTotaleRiga()' elencocampi='NSO_LINEE_ORDINI_QUANTITA;NSO_LINEE_ORDINI_PREZZO_UNITARIO' esegui="false" />
			

			
			<gene:campoScheda>	
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
			
			<input type="hidden" id="IS_MONO_RIGA" name="IS_MONO_RIGA" value="${requestScope.isMonoRiga}" />
			
		</gene:formScheda>

	</gene:redefineInsert>
		
<gene:javaScript>

	$(window).ready(function (){
		_creaFinestraAlberoCpvVP();
	
		_creaLinkAlberoCpvVP($("#NSO_LINEE_ORDINI_CODCPV").parent(), "${modo}", $("#NSO_LINEE_ORDINI_CODCPV"), $("#NSO_LINEE_ORDINI_CODCPVview") );
		
		$("input[name*='CODCPV']").attr('readonly','readonly');
		$("input[name*='CODCPV']").attr('tabindex','-1');
		$("input[name*='CODCPV']").css('border-color','#A3A6FF');
		$("input[name*='CODCPV']").css('border-width','1px');
		$("input[name*='CODCPV']").css('background-color','#E0E0E0');
		
		<c:if test="${modo ne 'VISUALIZZA'}">
			$("#msgTotCalcolato").prependTo($("#PREZZO_TOTALE_RIGA").parent());
			$("#msgTotCalcolato").show();
		</c:if>

		
	});
	
	
				function calcolaTotaliP(){
				<c:if test="${datiRiga.NSO_LINEE_ORDINI_UNIMIS eq 'ac'}">

					var quantitaOriginale = getOriginalValue("NSO_LINEE_ORDINI_QUANTITA");
					var quantita = getValue("NSO_LINEE_ORDINI_QUANTITA");
					var quantitaDisponibile = getOriginalValue("QUANTITA_DISPONIBILE");
					var totaleDisponibile = getOriginalValue("PREZZO_TOTALE_DISPONIBILE");
					var prezun = getValue("NSO_LINEE_ORDINI_PREZZO_UNITARIO");
					var prezzoTotRigaOriginale = getOriginalValue("PREZZO_TOTALE_RIGA");
					var prezzoTotRiga = getValue("PREZZO_TOTALE_RIGA");
					var isMonoRiga=getValue("IS_MONO_RIGA");
					if (quantita == null || quantita == "" || prezun == null || prezun == ""){
						setValue("NSO_LINEE_ORDINI_QUANTITA",  "");
					}else {
							var importoDiContratto = getValue("IMPORTO_DI_CONTRATTO");
							importoDiContratto =  parseFloat(importoDiContratto);
							prezun = parseFloat(prezun);
							prezzoTotRiga = parseFloat(prezzoTotRiga);
							quantitaOriginale = parseFloat(quantitaOriginale);
							var qtaNew = prezzoTotRiga/prezun;
							setValue("NSO_LINEE_ORDINI_QUANTITA", round(eval(qtaNew), 5));
							quantitaDisponibile = parseFloat(quantitaDisponibile);
							var qtaDispNew = quantitaDisponibile+quantitaOriginale-qtaNew;
							prezzoTotRigaOriginale = parseFloat(prezzoTotRigaOriginale);
							totaleDisponibile = parseFloat(totaleDisponibile);
							prezzoTotRiga = parseFloat(prezzoTotRiga);
							var totaleDispNew =totaleDisponibile+prezzoTotRigaOriginale-prezzoTotRiga;
							setValue("QUANTITA_DISPONIBILE", round(eval(qtaDispNew), 5));
							setValue("PREZZO_TOTALE_DISPONIBILE", round(eval(totaleDispNew), 5));
					}
				</c:if>
				
				return true;
			}

			function calcolaTotaliQ(){
				<c:if test="${datiRiga.NSO_LINEE_ORDINI_UNIMIS ne 'ac'}">
					var quantitaOriginale = getOriginalValue("NSO_LINEE_ORDINI_QUANTITA");
					var quantita = getValue("NSO_LINEE_ORDINI_QUANTITA");
					var quantitaDisponibile = getOriginalValue("QUANTITA_DISPONIBILE");
					var totaleDisponibile = getValue("PREZZO_TOTALE_DISPONIBILE");
					var prezun = getValue("NSO_LINEE_ORDINI_PREZZO_UNITARIO");
					var prezzoTotRigaOriginale = getOriginalValue("PREZZO_TOTALE_RIGA");
					var prezzoTotRiga = getValue("PREZZO_TOTALE_RIGA");
					var isMonoRiga=getValue("IS_MONO_RIGA");
					
					if (quantita == null || quantita == "" || prezun == null || prezun == ""){
						setValue("PREZZO_TOTALE_RIGA",  "");
					}else {
						if(isMonoRiga==1){
							var importoDiContratto = getValue("IMPORTO_DI_CONTRATTO");
							importoDiContratto =  parseFloat(importoDiContratto);
							quantita = parseFloat(quantita);
							prezun = parseFloat(prezun);
							var prezzoTotRigaNew = quantita * prezun;
							setValue("PREZZO_TOTALE_RIGA", round(eval(prezzoTotRigaNew), 5));
							quantitaOriginale = parseFloat(quantitaOriginale);
							quantitaDisponibile = parseFloat(quantitaDisponibile);
							var qtaDispNew = quantitaDisponibile+quantitaOriginale-quantita;
							prezzoTotRigaOriginale = parseFloat(prezzoTotRigaOriginale);
							totaleDisponibile = parseFloat(totaleDisponibile);
							prezzoTotRiga = parseFloat(prezzoTotRiga);
							var totaleDispNew =totaleDisponibile+prezzoTotRigaOriginale-prezzoTotRiga;
							setValue("QUANTITA_DISPONIBILE", round(eval(qtaDispNew), 5));
							setValue("PREZZO_TOTALE_DISPONIBILE", round(eval(totaleDispNew), 5));
						}else{
							quantita = parseFloat(quantita);
							quantitaOriginale = parseFloat(quantitaOriginale);
							prezun = parseFloat(prezun);
							var prezzoTotRigaNew = quantita * prezun;
							setValue("PREZZO_TOTALE_RIGA", round(eval(prezzoTotRigaNew), 5));
							quantitaDisponibile = parseFloat(quantitaDisponibile);
							var qtaDispNew = quantitaDisponibile+quantitaOriginale-quantita;
							setValue("QUANTITA_DISPONIBILE", round(eval(qtaDispNew), 5));
							
						}
					}
				
				</c:if>
				return true;
			}
			
			function calcolaTotaleRiga(){

					var quantita = getValue("NSO_LINEE_ORDINI_QUANTITA");
					var prezun = getValue("NSO_LINEE_ORDINI_PREZZO_UNITARIO");
					if (quantita == null || quantita == "" || prezun == null || prezun == ""){
						setValue("PREZZO_TOTALE_RIGA",  "");
					}else {
						quantita = parseFloat(quantita);
						prezun = parseFloat(prezun);
						var prezzoTotRiga = quantita*prezun;
						prezzoTotRiga = parseFloat(prezzoTotRiga);
						setValue("PREZZO_TOTALE_RIGA", round(eval(prezzoTotRiga), 5));
					}
				
				return true;
			}

	
			
</gene:javaScript>
		
	
</gene:template>
