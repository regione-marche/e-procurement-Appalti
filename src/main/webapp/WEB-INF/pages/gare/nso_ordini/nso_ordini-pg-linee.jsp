<%/*
   * Created on 22-05-2012
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

<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "NSO_ORDINI")}' />

<c:set var="filtroUffint" value=""/> 
<c:if test="${! empty sessionScope.uffint}">
	<c:set var="filtroUffint" value=""/>
</c:if>

<c:set var="where" value="NSO_LINEE_ORDINI.NSO_ORDINI_ID = #NSO_ORDINI.ID#"/>
<c:set var="idOrdine" value='${gene:getValCampo(key,"ID")}' />

<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/nso-ordini.js"></script>
</gene:redefineInsert>

<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>

	<gene:setString name="titoloMaschera" value="Lista linee ordini"/>
	
	
	<gene:redefineInsert name="addToAzioni" >
		<c:if test="${(requestScope.statoOrdine eq 1) || (requestScope.statoOrdine eq 2)}">
			<tr>
				<td class="vocemenulaterale">
						<a href="javascript:validaOrdine();" id="menuValidaOrdine" title="Controlla Dati Inseriti" tabindex="1510">Controlla Dati Inseriti</a>
				</td>
			</tr>
		</c:if>
	</gene:redefineInsert>
	<gene:redefineInsert name="addToDocumenti" />
		<c:choose>
		<c:when test='${requestScope.statoOrdine eq 3 || requestScope.statoOrdine eq 4}'>
		<gene:redefineInsert name="listaNuovo" />
		<gene:redefineInsert name="listaEliminaSelezione"/>
		</c:when>
		<c:otherwise>
			<gene:redefineInsert name="listaNuovo" >
				<c:if test='${autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext, "INS","INS")}'>
				<c:if test='${requestScope.isMonoRiga eq 0}'>
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:visListaLavorazioni();" title="Aggiungi linea da gara collegata" tabindex="1501">
								Aggiungi linea da gara collegata</a></td>
					</tr>
				</c:if>
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:listaNuovo();" title="Inserisci voce libera" tabindex="1501">
								Inserisci voce libera</a></td>
					</tr>
				</c:if>
			</gene:redefineInsert>
			<gene:redefineInsert name="listaEliminaSelezione">
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:listaEliminaSelezione();" title="Elimina selezionati" tabindex="1503">
								${gene:resource("label.tags.template.lista.listaEliminaSelezione")}</a>
						</td>
					</tr>
			</gene:redefineInsert>									
		</c:otherwise>
		</c:choose>
 
		<table class="lista">
			<tr>
				<td>
  	<gene:formLista entita="NSO_LINEE_ORDINI" where='${where}' tableclass="datilista" sortColumn="4"
					gestisciProtezioni="true"  gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreLineeOrdiniNso" pagesize="25" >
  	
			<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
				<c:choose>
				<c:when test='${requestScope.statoOrdine eq 3 || requestScope.statoOrdine eq 4}'>
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
							<gene:PopUpItem title="Visualizza linea ordine" href="javascript:listaVisualizza()" />
					</gene:PopUp>
				</c:when>
				<c:otherwise>
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
							<gene:PopUpItem title="Visualizza linea ordine" href="javascript:listaVisualizza()" />
							
						<c:if test='${gene:checkProtFunz(pageContext, "MOD","MOD")}' >
							<gene:PopUpItem title="Modifica linea ordine" href="javascript:listaModifica()" />
						</c:if>
						<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL")}' >
							<gene:PopUpItem title="Elimina linea ordine" href="javascript:listaElimina()" />
						</c:if>
					</gene:PopUp>
					<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
							<input type="checkbox" name="keys" value="${chiaveRiga}"  />
					</c:if>	
				</c:otherwise>
				</c:choose>
			</gene:campoLista>
			<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
			<gene:campoLista campo="ID" visibile="false" />
			<gene:campoLista campo="NSO_ORDINI_ID" visibile="false"  />
			<gene:campoLista title="Id riga" campo="ID_LINEA" />
			<gene:campoLista campo="CODICE" headerClass="sortable" href="${gene:if(visualizzaLink, link, '')}" />
			<gene:campoLista campo="DESCRIZIONE" headerClass="sortable" />
			<gene:campoLista  campo="QUANTITA" />
			<gene:campoLista campo="UNIMIS" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoUnitaMisura" />
			<gene:campoLista campo="PREZZO_UNITARIO" />
			<c:set var="calcolo" value="${datiRiga.NSO_LINEE_ORDINI_QUANTITA*datiRiga.NSO_LINEE_ORDINI_PREZZO_UNITARIO}" />
			<gene:campoLista title="Totale calcolato" campo="TOTALE_CALCOLATO" visibile="true" campoFittizio="true" definizione="F24.5;0;;MONEY5;NSO_LO_PU" value="${calcolo}" computed="true" />
			<c:if test='${requestScope.versioneOrdine eq 0}'>
				<gene:campoLista title="Azioni" width="20" >
					<a href="javascript:chiaveRiga='${chiaveRigaJava}';apriRiepilogoLinea('${chiaveRigaJava}');" title="Utilizzo linea ordine" >
						<img width="16" height="16" title="Riepilogo ordini con consumo stesso codice linea" alt="Riepilogo ordini con consumo stesso codice linea" src="${pageContext.request.contextPath}/img/prod-oe.png"/>
					</a>
				</gene:campoLista>
			</c:if>	
			
			<input type="hidden" id="NSO_ORDINI_ID" name="NSO_ORDINI_ID" value="${idOrdine}"/>
			<input type="hidden" id="CODICE_GARA" name="CODICE_GARA" value="${codiceGara}"/>
			<input type="hidden" id="NUMERO_GARA" name="NUMERO_GARA" value="${numeroGara}"/>
			<input type="hidden" id="CODICE_DITTA" name="CODICE_DITTA" value="${codiceDitta}"/>
			<input type="hidden" id="UFFINT" name="UFFINT" value="${sessionScope.uffint}"/>
			
		</gene:formLista>
				</td>
			</tr>
			<tr>
				<c:choose>
				<c:when test='${requestScope.statoOrdine eq 3 || requestScope.statoOrdine eq 4}'>
				</c:when>
				<c:otherwise>
					<td class="comandi-dettaglio"  colSpan="2">
						<c:if test='${autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext, "INS","INS")}'>
						<c:if test='${requestScope.isMonoRiga eq 0}'>
							<INPUT type="button"  class="bottone-azione" value='Aggiungi linea da gara collegata' title='Aggiungi linea da gara collegata' onclick="javascript:variazioneLavorazioni();">&nbsp;&nbsp;&nbsp;
						</c:if>
							<INPUT type="button"  class="bottone-azione" value='Inserisci voce libera' title='Inserisci voce libera' onclick="javascript:listaNuovo();">&nbsp;&nbsp;&nbsp;
						</c:if>
						<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
							<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione()">
						</c:if>
					</td>
				</c:otherwise>
				</c:choose>
			</tr>
		</table>
		
	
<div id="nso-dialog-verification" title="Verifica Ordine NSO" style="display:none">
  <p id="nso-dialog-verification-content">
  	
  </p>
</div>		
  

	<gene:javaScript>
	
	
	function variazioneLavorazioni(){
		var codiceGara = getValue("CODICE_GARA");
		var numeroGara = getValue("NUMERO_GARA");
		var codiceDitta = getValue("CODICE_DITTA");
		var uffint = getValue("UFFINT");
		var chiave = "DITG.CODGAR5=T:" + codiceGara + ";";
		chiave += "DITG.DITTAO=T:" + codiceDitta + ";";
		chiave += "DITG.NGARA5=T:" + numeroGara;
		var href = contextPath + "/ApriPagina.do?href=gare/nso_ordini/nso_lavorazioni-lista.jsp";
		href += "&codiceGara="+codiceGara;
		href += "&numeroGara="+numeroGara;
		href += "&codiceDitta="+codiceDitta;
		href += "&idOrdine="+${idOrdine};
		href += "&operazione=" + "ADD";
		href += "&uffint=" + uffint;
		href += "&" + csrfToken;
		document.location.href = href;
	}
	
	
	function apriRiepilogoLinea(chiaveRiga){
		href = "href=gare/nso_linee_ordini/nso_riepilogo-linea-listaPopup.jsp";
		href += "&idOrdine="+${idOrdine};
		href += "&key="+chiaveRiga;
		openPopUpCustom(href, "apriElencoProdotti", 900, 600, "yes", "yes");
	}
	
	
	</gene:javaScript>
