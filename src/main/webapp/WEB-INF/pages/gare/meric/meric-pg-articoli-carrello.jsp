
<%
	/*
	 * Created on 04-06-2013
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

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<c:set var="id" value='${gene:getValCampo(key, "MERIC.ID")}' />

<c:choose>
	<c:when test="${param.updateLista eq 1}">
		<c:set var="gestorePagina" value="it.eldasoft.sil.pg.tags.gestori.submit.GestoreMERICARTLista"/>
	</c:when>
	<c:otherwise>
		<c:set var="gestorePagina" value="it.eldasoft.sil.pg.tags.gestori.submit.GestoreMERICART"/>
	</c:otherwise>
</c:choose>

<c:if test='${param.updateLista eq 1}' >
	<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />
</c:if>

<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetTabellatiMEARTCATFunction" />

<table class="dettaglio-tab-lista">
	<tr>
		<td>
			<table class="lista" style="border: 1px dotted #A0AABA; background-color: #EFEFEF">
				<tr id="rowfiltrotipo">
					<td width="110px">Tipo articolo:</td>
					<td>
						<select name="filtrotipo" id="filtrotipo" <c:if test="${param.updateLista eq '1'}"> disabled="disabled" </c:if>>
							<option value="">Visualizza tutti i tipi</option>
							<c:if test='${!empty listaTipo}'>
								<c:forEach items="${listaTipo}" var="valoreTipo">
									<option value="${valoreTipo[0]}">${valoreTipo[1]}</option>
								</c:forEach>
							</c:if>
						</select>
					</td>
					<td width="110px">Descrizione:</td>
					<td>
						<input class="testo" name="filtrodescr" id="filtrodescr" 
							<c:if test="${param.updateLista eq '1'}"> disabled="disabled" </c:if>
							title="Descrizione" type="text" size="40">
					</td>					
				</tr>
				<tr id="rowfiltrodescr">
					<td width="110px">Codice articolo:</td>
					<td>
						<input class="testo" name="filtrocod" id="filtrocod" 
							<c:if test="${param.updateLista eq '1'}"> disabled="disabled" </c:if>
							title="Codice articolo" type="text" size="40">
					</td>
					<td width="110px">Colore:</td>
					<td>
						<input class="testo" name="filtrocolore" id="filtrocolore" 
							<c:if test="${param.updateLista eq '1'}"> disabled="disabled" </c:if>
							title="Colore" type="text" size="40">
					</td>
				</tr>
				<tr id="rowfiltrounimisacq">
					<td width="110px">Unit&agrave; di misura:</td>
					<td>
						<select name="filtrounimisacq" id="filtrounimisacq" <c:if test="${param.updateLista eq '1'}"> disabled="disabled" </c:if>>
							<option value="">Visualizza tutte le tipologie</option>
							<c:if test='${!empty listaUnitaMisura}'>
								<c:forEach items="${listaUnitaMisura}" var="valoreUnitaMisura">
									<option value="${valoreUnitaMisura[0]}">${valoreUnitaMisura[1]}</option>
								</c:forEach>
							</c:if>
						</select>
					</td>
				</tr>
			</table>
		</td>
	</tr>

	<tr>
		<td>
			<gene:formLista entita="MERICART" pagesize="20" sortColumn="5;6" where="MERICART.IDRIC = #MERIC.ID#"
				tableclass="datilista" gestisciProtezioni="true" gestore="${gestorePagina}" pathScheda="gare/meartcat/meartcat-scheda.jsp">
				
				<input type="hidden" name="tipo" value="${param.tipo}" />
				<input type="hidden" name="descr" value="${param.descr}" />
				<input type="hidden" name="cod" value="${param.cod}" />
				<input type="hidden" name="colore" value="${param.colore}" />
				<input type="hidden" name="unimisacq" value="${param.unimisacq}" />
				<input type="hidden" name="listachiamante" value="meric-pg-articoli-carrello.jsp"/>
				
				<c:set var="link" value="javascript:chiaveRiga='MEARTCAT.ID=N:${datiRiga.MEARTCAT_ID}';listaVisualizza();"/>

				<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
					<jsp:param name="entita" value="MERIC"/>
					<jsp:param name="inputFiltro" value="ID=N:${id}"/>
					<jsp:param name="filtroCampoEntita" value="IDMERIC=${id}"/>
				</jsp:include>

				<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>"	width="50">
					<c:if test="${currentRow >= 0}">
						<c:if test="${param.updateLista ne '1'}">
							<gene:PopUp variableJs="rigaPopUpMenu${currentRow}"	onClick="chiaveRiga='${chiaveRigaJava}'">
								<gene:PopUpItem title="Visualizza articolo" href="${link}"/>
								<c:if test='${autorizzatoModifiche eq "1" && gene:checkProtFunz(pageContext, "DEL","DEL") && empty datiRiga.MERIC_DATVAL}'>
									<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina"	title="Elimina" />
								</c:if>
							</gene:PopUp>
							<c:if test='${autorizzatoModifiche eq "1" && gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") && empty datiRiga.MERIC_DATVAL}'>
								<input type="checkbox" name="keys" value="${chiaveRiga}" />
							</c:if>
						</c:if>
					</c:if>
				</gene:campoLista>
				
				<gene:campoLista campo="ID" visibile="false" edit="${param.updateLista eq '1'}"/>
				<gene:campoLista entita="MEARTCAT" campo="ID" visibile="false" where="MEARTCAT.ID=MERICART.IDARTCAT" edit="${param.updateLista eq '1'}"/>
				<gene:campoLista entita="MEARTCAT" campo="TIPO" where="MEARTCAT.ID=MERICART.IDARTCAT"/>
				<gene:campoLista entita="MEARTCAT" campo="DESCR" where="MEARTCAT.ID=MERICART.IDARTCAT" href="${gene:if(param.updateLista ne '1', link,'' )}" />
				<gene:campoLista entita="MEARTCAT" campo="COD" where="MEARTCAT.ID=MERICART.IDARTCAT"/>
				<gene:campoLista entita="MEARTCAT" campo="COLORE" where="MEARTCAT.ID=MERICART.IDARTCAT"/>
				<gene:campoLista entita="MEARTCAT" campo="UNIMISACQ" where="MEARTCAT.ID=MERICART.IDARTCAT"/>
				<gene:campoLista campo="QUANTI" edit="${param.updateLista eq '1'}" ordinabile="false"/>
				<gene:campoLista entita="MERIC" campo="DATVAL" where="MERIC.ID=MERICART.IDRIC" visibile="false"/>
				<gene:campoLista entita="MERIC" campo="ID" where="MERIC.ID=MERICART.IDRIC" visibile="false"/>
				<gene:campoLista entita="MEARTCAT" campo="PRZUNITPER" where="MEARTCAT.ID=MERICART.IDARTCAT" visibile="false" edit="${param.updateLista eq '1'}"/>
				<gene:campoLista campo="DESDET1" edit="${param.updateLista eq '1'}" visibile="false"/>
				<gene:campoLista campo="DESDET2" edit="${param.updateLista eq '1'}" visibile="false"/>
				<gene:campoLista campo="QUADET1" edit="${param.updateLista eq '1'}" visibile="false"/>
				<gene:campoLista campo="QUADET2" edit="${param.updateLista eq '1'}" visibile="false"/>
				<gene:campoLista title="&nbsp;" width="20" >
					<c:if test="${datiRiga.MEARTCAT_PRZUNITPER eq 4}" >
						<a href="javascript:chiaveRiga='${chiaveRigaJava}';impostaQuantita(${currentRow}+1,'${chiaveRigaJava}');" title="Dettaglio quantità richiesta" >
							<img width="16" height="16" title="Dettaglio quantità richiesta" alt="Dettaglio quantità richiesta" src="${pageContext.request.contextPath}/img/dettaglioQuantita.gif"/>
						</a>
					</c:if>
				</gene:campoLista>
			</gene:formLista>
		</td>
	</tr>

	<gene:redefineInsert name="listaNuovo" />

	<c:if test='${autorizzatoModifiche eq "1" && empty datiRiga.MERIC_DATVAL}'>
		<tr>
			<td class="comandi-dettaglio" colspan="2">
				<gene:insert name="addPulsanti"/>	
				<c:choose>
					<c:when test="${param.updateLista eq '1'}">
						<INPUT type="button"  class="bottone-azione" value='Conferma' title='Conferma' onclick="javascript:listaConfermaMessaggio();">
						<INPUT type="button"  class="bottone-azione" value='Annulla' title='Annulla' onclick="javascript:listaAnnullaModifica();">
					</c:when>
					<c:otherwise>
						<gene:insert name="pulsanteListaModifica">
							<c:if test='${gene:checkProtFunz(pageContext,"MOD","LISTAMODIFICA")}'>
								<INPUT type="button"  class="bottone-azione" value='Modifica' title='Modifica' onclick="javascript:listaApriInModifica();">
							</c:if>
						</gene:insert>
						<gene:insert name="pulsanteListaEliminaSelezione">
							<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
								<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione();">
							</c:if>
						</gene:insert>
						<gene:insert name="pulsanteValutazioneProdotti">
							<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.MERIC.valutazioneprodotti")}'>
								<INPUT type="button"  class="bottone-azione" value='Procedi alla valutazione prodotti' title='Procedi alla valutazione prodotti' onclick="javascript:valutazioneProdotti(${id},'VALUTAZIONE');">
							</c:if>
						</gene:insert>
					</c:otherwise>
				</c:choose>
				&nbsp;
			</td>
		</tr>
	</c:if>
	
</table>

<gene:redefineInsert name="addToAzioni" >
	<jsp:include page="/WEB-INF/pages/gare/meric/meric-addtoazioni.jsp" />
</gene:redefineInsert>

<gene:javaScript>

	$('#filtrotipo').val('${param.tipo}');
	$('#filtrodescr').val('${param.descr}');
	$('#filtrocod').val('${param.cod}');	
	$('#filtrocolore').val('${param.colore}');
	$('#filtrounimisacq').val('${param.unimisacq}');

	$('#filtrocod, #filtrodescr, #filtrocolore').keyup(function() {
		delay(function(){
			searchArticoli();
			}, 600);
	});

	$('#filtrotipo, #filtrounimisacq').change(function() {
		delay(function(){
			searchArticoli();
			}, 600);
	});

	function searchArticoli() {

		var filtrotipo = $("#filtrotipo").val();
		var filtrodescr = $("#filtrodescr").val();
		var filtrocod = $("#filtrocod").val();
		var filtrocolore = $("#filtrocolore").val();
		var filtrounimisacq = $("#filtrounimisacq").val();

		document.forms[0].tipo.value=filtrotipo;
		document.forms[0].descr.value=filtrodescr;
		document.forms[0].cod.value=filtrocod;
		document.forms[0].colore.value=filtrocolore;
		document.forms[0].unimisacq.value=filtrounimisacq;

		var addwhere = "";
		var parameter = "";

		if (filtrotipo != "") {
			addwhere += "MEARTCAT.TIPO = ?";
			parameter += "N:" + filtrotipo;
		}

		if (filtrodescr != "") {
			if (addwhere != "") {
				addwhere += " AND ";
			}
			addwhere += "UPPER(MEARTCAT.DESCR) like ?";
			if (parameter != "") {
				parameter += ";";
			}
			parameter += "T:%" + filtrodescr.toUpperCase() + "%";
		}

		if (filtrocod != "") {
			if (addwhere != "") {
				addwhere += " AND ";
			}
			addwhere += "UPPER(MEARTCAT.COD) like ?";
			if (parameter != "") {
				parameter += ";";
			}
			parameter += "T:%" + filtrocod.toUpperCase() + "%";
		}

		if (filtrocolore != "") {
			if (addwhere != "") {
				addwhere += " AND ";
			}
			addwhere += "UPPER(MEARTCAT.COLORE) like ?";
			if (parameter != "") {
				parameter += ";";
			}
			parameter += "T:%" + filtrocolore.toUpperCase() + "%";
		}

		if (filtrounimisacq != "") {
			if (addwhere != "") {
				addwhere += " AND ";
			}
			addwhere += "MEARTCAT.UNIMISACQ = ?";
			if (parameter != "") {
				parameter += ";";
			}
			parameter += "N:" + filtrounimisacq;
		}

		document.forms[0].trovaAddWhere.value = addwhere;
		document.forms[0].trovaParameter.value = parameter;
		bloccaRichiesteServer();
		listaVaiAPagina(0);
	}

	var delay = (function(){
		var timer = 0;
		return function(callback, ms){
			clearTimeout (timer);
			timer = setTimeout(callback, ms);
		};
	})();
		
		
	function listaConfermaMessaggio() {
		var visualizzamessaggio = false;
		var c_mericart_quanti = $('[id^="MERICART_QUANTI_"]');
		c_mericart_quanti.each(function(i) {
			var quantita = $(this).val();
			if (quantita == null || (quantita != null && quantita <=0)) {
				visualizzamessaggio = true;
			}
		});
		if (visualizzamessaggio) {
			alert("Tutte le quantita' devono essere valorizzate e maggiori di zero");
		} else {
		
			var numeroArticoli = ${currentRow}+1;
			
			for(var t=1; t <= numeroArticoli; t++){
				document.getElementById("MERICART_QUANTI_" + t).disabled = false;
			}
		
			listaConferma();
		}
	}	
	

	$('[id*="MERICART_QUANTI_"]:input').change(
		function() {
			var quantita = $(this).val();
			if (quantita == null || (quantita != null && quantita <=0)) {
				alert("Tutte le quantita' devono essere valorizzate e maggiori di zero");
			}
		}
	);	
	
	function impostaQuantita(indiceRiga,chiaveRiga){
		var href = "href=gare/meric/meric-popup-impostaQuantita.jsp";
		href += "&key=" + chiaveRiga;
		href += "&indiceRiga=" + indiceRiga;
		<c:if test='${param.updateLista eq "1"}' >
			href += "&modo=MODIFICA";
		</c:if>
		openPopUpCustom(href, "impostaQuantita", 550, 350, "yes", "yes");
	}
	
	<c:if test='${param.updateLista eq 1}'>
	function inizializzaLista(){
			var numeroArticoli = ${currentRow}+1;
			
			for(var t=1; t <= numeroArticoli; t++){
				if(getValue("MEARTCAT_PRZUNITPER_" + t) == "4"){ 
					document.getElementById("MERICART_QUANTI_" + t).disabled = true;
					document.getElementById("MERICART_QUANTI_" + t).style.backgroundColor="#ECECEC";
				}
			}
	}
	
	inizializzaLista();
	</c:if>
</gene:javaScript>
