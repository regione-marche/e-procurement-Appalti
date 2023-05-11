
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
<c:set var="idGaraCatalogo" value='${gene:getValCampo(key, "GARE.NGARA")}' />

<c:set var="codiceGara" value="$ ${idGaraCatalogo}" />
<c:set var="codiceGara" value='${fn:replace(codiceGara," ", "")}' />

<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetTabellatiMEARTCATFunction" />

<table class="dettaglio-tab-lista">
	<tr>
		<td>
			<table class="lista" style="border: 1px dotted #A0AABA; background-color: #EFEFEF">
				<tr id="rowfiltrotipo">
					<td width="90px">Tipo articolo:</td>
					<td>
						<select name="filtrotipo" id="filtrotipo">
							<option value="">Visualizza tutti i tipi</option>
							<c:if test='${!empty listaTipo}'>
								<c:forEach items="${listaTipo}" var="valoreTipo">
									<option value="${valoreTipo[0]}">${valoreTipo[1]}</option>
								</c:forEach>
							</c:if>
						</select>
					</td>
					<td width="90px">Codice:</td>
					<td>
						<input class="testo" name="filtrocod" id="filtrocod" title="Codice articolo" type="text" size="40">
					</td>
				</tr>
				<tr id="rowfiltrodescr">
					<td width="90px">Descrizione:</td>
					<td>
						<input class="testo" name="filtrodescr" id="filtrodescr" title="Descrizione" type="text" size="40">
					</td>
					<td width="90px">Stato articolo:</td>
					<td>
						<select name="filtrostato" id="filtrostato">
							<option value="">Visualizza tutti gli stati</option>
							<c:if test='${!empty listaStato}'>
								<c:forEach items="${listaStato}" var="valoreStato">
									<option value="${valoreStato[0]}">${valoreStato[1]}</option>
								</c:forEach>
							</c:if>
						</select>
					</td>
				</tr>
				<tr id="rowfiltrocolore">
					<td width="90px">Colore:</td>
					<td>
						<input class="testo" name="filtrocolore" id="filtrocolore" title="Colore" type="text" size="40">
					</td>
				</tr>
			</table>
		</td>
	</tr>

	<tr>
		<td>
			<gene:formLista entita="MEARTCAT" pagesize="20" sortColumn="6" where="MEARTCAT.NGARA = #GARE.NGARA#" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreSearchArticoli" 
					tableclass="datilista" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreMEARTCAT">
				
				<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
					<jsp:param name="entita" value="V_GARE_TORN"/>
					<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
					<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
				</jsp:include>
				
				<input type="hidden" name="listachiamante" value="gare-pg-lista-articoli"/>
				<input type="hidden" name="tipo" value="${param.tipo}" />
				<input type="hidden" name="cod" value="${param.cod}" />
				<input type="hidden" name="descr" value="${param.descr}" />
				<input type="hidden" name="stato" value="${param.stato}" />
				<input type="hidden" name="colore" value="${param.colore}" />

				<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>"	width="50">
					<c:if test="${currentRow >= 0}">
						<gene:PopUp variableJs="rigaPopUpMenu${currentRow}"	onClick="chiaveRiga='${chiaveRigaJava}'">
							<gene:PopUpItemResource resource="popupmenu.tags.lista.visualizza"	title="Visualizza" />
							<c:if test='${autorizzatoModifiche ne "2" and gene:checkProtFunz(pageContext, "MOD","MOD")}'>
								<gene:PopUpItemResource resource="popupmenu.tags.lista.modifica" title="Modifica" />
							</c:if>
						</gene:PopUp>
					</c:if>
				</gene:campoLista>
				<gene:campoLista campo="ID" visibile="false"/>
				<gene:campoLista campo="NGARA" visibile="false" />
				<gene:campoLista campo="NOPEGA" visibile="false" />
				<gene:campoLista campo="TIPO" />
				<gene:campoLista campo="DESCR" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();"/>
				<gene:campoLista campo="COD" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
				<gene:campoLista campo="STATO"/>
				<gene:campoLista campo="COLORE"/>
			</gene:formLista>
		</td>
	</tr>

	<gene:redefineInsert name="pulsanteListaInserisci" />
	<gene:redefineInsert name="listaNuovo" />
	<gene:redefineInsert name="pulsanteListaEliminaSelezione" />
	<gene:redefineInsert name="listaEliminaSelezione" />

	<gene:redefineInsert name="addToAzioni">
			<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE-scheda.ARTICOLIGARA.scaricaModello")}'>
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:scaricaModello();" title='Scarica modello excel' tabindex="1500">
							Scarica modello
						</a>
					</td>
				</tr>
			</c:if>
			<c:if test='${autorizzatoModifiche ne "2" }'>
				<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE-scheda.ARTICOLIGARA.caricaArticoli")}'>
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:importaArticoli();" title='Importa dati da file' tabindex="1501">
								Carica articoli
							</a>
						</td>
					</tr>
				</c:if>
			</c:if>
	</gene:redefineInsert>

	<tr>
		<jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" />
	</tr>
</table>

<gene:javaScript>
	document.forms[0].tipo.value = '${param.tipo}';
	document.forms[0].cod.value = '${param.cod}';
	document.forms[0].descr.value = '${param.descr}';
	document.forms[0].stato.value = '${param.stato}';
	document.forms[0].colore.value = '${param.colore}';

	$('#filtrotipo').val('${param.tipo}');
	$('#filtrocod').val('${param.cod}');
	$('#filtrodescr').val('${param.descr}');
	$('#filtrostato').val('${param.stato}');
	$('#filtrocolore').val('${param.colore}');

	$('#filtrocod, #filtrodescr, #filtrocolore').keyup(function() {
		delay(function(){
				fillFilters();
				listaVaiAPagina(0);
			}, 600);
	});

	$('#filtrotipo, #filtrostato').change(function() {
		delay(function(){
				fillFilters();
				listaVaiAPagina(0);
			}, 600);
	});
	
	function fillFilters() {
		document.forms[0].tipo.value = $("#filtrotipo").val();
		document.forms[0].cod.value = $("#filtrocod").val();
		document.forms[0].descr.value = $("#filtrodescr").val();
		document.forms[0].stato.value = $("#filtrostato").val();
		document.forms[0].colore.value = $("#filtrocolore").val();
	}
	
	var delay = (function(){
		var timer = 0;
		return function(callback, ms){
			clearTimeout (timer);
			timer = setTimeout(callback, ms);
		};
	})();
	
	function scaricaModello(){
		var chiave = '${idGaraCatalogo}';
		var href="${contextPath}/pg/ExportModelloArticoli.do?"+csrfToken+"&chiave="+chiave+"&numeroPopUp=1";
		document.location.href=href;
	}
	
	function importaArticoli(){
		var chiave = "${idGaraCatalogo}";
		var href = "href=gare/gare/gare-pg-carica-articoli.jsp&chiave=" + chiave + "&numeroPopUp=1";
		openPopUpCustom(href, "caricaArticolo", "500", "340", "no", "no");
	}

</gene:javaScript>
