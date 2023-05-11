<%/*
   * Created on 06-12-2013
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="codiceGara" value='${gene:getValCampo(key, "DITG.CODGAR5")}' />
<c:set var="numeroGara" value='${gene:getValCampo(key, "DITG.NGARA5")}' />
<c:set var="codiceDitta" value='${gene:getValCampo(key, "DITG.DITTAO")}' />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="where" value="V_CATAPROD.NGARA='${numeroGara }' and  V_CATAPROD.CODIMP='${codiceDitta }'" />

<c:set var="nomimo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNOMIMOFunction", pageContext, numeroGara, codiceGara, codiceDitta)}' />

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetTabellatiMEISCRIZPRODFunction" />

<gene:template file="popup-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="MEISCRIZPROD-lista">
	<gene:setString name="titoloMaschera" value="Elenco prodotti dell'operatore economico ${nomimo}"/>
	
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
	
		
	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
		<jsp:param name="entita" value="V_GARE_TORN"/>
		<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
		<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
	</jsp:include>
		
  	<%// Creo la lista per gcap e dpre mediante la vista v_gcap_dpre%>
		<table class="lista">
			
			<tr>
				<td>
					<table class="lista" style="border-bottom: 1px solid #A0AABA;background-color: #EFEFEF" >
						<tr id="rowfiltrodescr">
							<td width="90px">Descrizione articolo:</td>
							<td>
								<input class="testo" name="filtrodescr" id="filtrodescr" title="Descrizione articolo" type="text" size="40">
							</td>
							<td width="90px">Codice articolo:</td>
							<td>
								<input class="testo" name="filtrocod" id="filtrocod" title="Codice articolo" type="text" size="40">
							</td>
						</tr>
						<tr id="rowfiltronome">
							<td width="90px">Nome commerciale:</td>
							<td>
								<input class="testo" name="filtronome" id="filtronome" title="Nome commerciale prodotto" type="text" size="40">
							</td>
							<td width="90px">Descrizione aggiuntiva:</td>
							<td>
								<input class="testo" name="filtrodescagg" id="filtrodescagg" title="Descrizione aggiuntiva" type="text" size="40">
							</td>
						</tr>
						<tr id="rowfiltrstato">
							<td width="90px">Stato :</td>
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
							<td width="90px">Categoria:</td>
							<td>
								<input class="testo" name="filtrocategoria" id="filtrocategoria" title="Codice e descrizione categoria" type="text" size="40">
							</td>
						</tr>
					</table>
				</td>
			</tr>
			
								
			<tr>
				<td ${stileDati}>
  				<gene:formLista entita="V_CATAPROD" where='${where}' pagesize="200" tableclass="datilista" sortColumn="6;7;9" gestisciProtezioni="true" 
  						plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreSearchProdottiPopUp">
 					<gene:redefineInsert name="listaNuovo" />
														
					<gene:campoLista title="Opzioni" width="50">
						<c:if test="${currentRow >= 0}">
							<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
								<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.MEISCRIZPROD-scheda")}' >
									<gene:PopUpItem title="Visualizza prodotto" href="listaVisualizzaProdotto('${datiRiga.V_CATAPROD_IDPRODOTTO }')" />
								</c:if>
								<c:if test='${(autorizzatoModifiche ne "2") && gene:checkProt(pageContext, "MASC.VIS.GARE.MEISCRIZPROD-scheda") && gene:checkProtFunz(pageContext, "MOD","MOD")}' >
									<gene:PopUpItem title="Modifica prodotto" href="listaModificaProdotto('${datiRiga.V_CATAPROD_IDPRODOTTO }')" />
								</c:if>
							</gene:PopUp>
						</c:if>
					</gene:campoLista>
							
					<c:set var="link" value="javascript:listaVisualizzaProdotto(${datiRiga.V_CATAPROD_IDPRODOTTO });" />
					
					<gene:campoLista campo="IDPRODOTTO"  visibile="false"/>
					<gene:campoLista campo="CODIMP" visibile="false"/>
					<gene:campoLista campo="NGARA" visibile="false"/>	
					<gene:campoLista title="&nbsp;" width="20" >
						<c:if test="${datiRiga.V_CATAPROD_STATO eq 2}">
							<img width="16" height="16" title="Prodotto da verificare" alt="Prodotto da verificare" src="${pageContext.request.contextPath}/img/prod-oe-verif.png"/>
						</c:if>
					</gene:campoLista>
					<gene:campoLista campo="DESCR" title="Descrizione articolo" />
					<gene:campoLista campo="COD"   />
					<gene:campoLista campo="IDARTICOLO" visibile="false"/>
					<gene:campoLista campo="CODOE"  href="${link}" />
					<gene:campoLista campo="NOME" />
					<gene:campoLista computed="true" campo="${gene:getDBFunction(pageContext,'DATETIMETOSTRING','V_CATAPROD.DATMOD')}" definizione="T20;0;;;DATMODMEPROD" width="150"/>
					<gene:campoLista campo="STATO" />
													
										
					<input type="hidden" name="codiceDitta" id="codiceDitta" value="${codiceDitta }" />
					<input type="hidden" name="numeroGara" id="numeroGara" value="${numeroGara }" />
					<input type="hidden" name="codiceGara" id="codiceGara" value="${codiceGara }" />
					<input type="hidden" name="descr" value="${param.descr}" />
                    <input type="hidden" name="cod" value="${param.cod}" />
					<input type="hidden" name="nome" value="${param.nome}" />
					<input type="hidden" name="descagg" value="${param.descagg}" />
					<input type="hidden" name="stato" value="${param.stato}" />
					<input type="hidden" name="categoria" value="${param.categoria}" />
                                        															
				</gene:formLista>
				</td>
			</tr>
			<tr>
		<td class="comandi-dettaglio" colSpan="2">
			<gene:insert name="addPulsanti"/>
			<INPUT type="button"  class="bottone-azione" value='Chiudi' title='Chiudi' onclick="javascript:chiudi();">
			&nbsp;
		</td>
	</tr>			
				
		</table>
  </gene:redefineInsert>
	<gene:javaScript>
		document.forms[0].descr.value = '${param.descr}';
		document.forms[0].cod.value = '${param.cod}';
		document.forms[0].nome.value = '${param.nome}';
		document.forms[0].descagg.value = '${param.descagg}';
		document.forms[0].stato.value = '${param.stato}';
		document.forms[0].categoria.value = '${param.categoria}';
	
		$('#filtrodescr').val('${param.descr}');
		$('#filtrocod').val('${param.cod}');
		$('#filtronome').val('${param.nome}');
		$('#filtrodescagg').val('${param.descagg}');
		$('#filtrostato').val('${param.stato}');
		$('#filtrocategoria').val('${param.categoria}');
					
	    $('#filtrodescr, #filtrocod, #filtronome, #filtrodescagg, #filtrocategoria').keyup(function() {
		    	delay(function(){
		    		fillFilters();
		    		listaVaiAPagina(0);
		    	}, 600);
	    });
	    
	    $('#filtrostato').change(function() {
			delay(function(){
		    		fillFilters();
		    		listaVaiAPagina(0);
				}, 600);
		});
		
		function fillFilters() {
			document.forms[0].descr.value = $("#filtrodescr").val();
			document.forms[0].cod.value = $("#filtrocod").val();
			document.forms[0].nome.value = $("#filtronome").val();
			document.forms[0].descagg.value = $("#filtrodescagg").val();
			document.forms[0].stato.value = $("#filtrostato").val();
			document.forms[0].categoria.value = $("#filtrocategoria").val();
		}
	
		var delay = (function(){
			  var timer = 0;
			  return function(callback, ms){
			    clearTimeout (timer);
			    timer = setTimeout(callback, ms);
			  };
		})();
		
		
	function listaVisualizzaProdotto(id){
		document.forms[0].entita.value="MEISCRIZPROD";
		document.forms[0].key.value="MEISCRIZPROD.ID=N:"+id;
		document.forms[0].metodo.value="apri";
		document.forms[0].activePage.value="0";
		document.forms[0].submit();
	}
	
	function listaModificaProdotto(id){
		document.forms[0].entita.value="MEISCRIZPROD";
		document.forms[0].key.value="MEISCRIZPROD.ID=N:"+id;
		document.forms[0].metodo.value="modifica";
		document.forms[0].activePage.value="0";
		document.forms[0].submit();
	}
	
	function chiudi(){
			window.opener.document.forms[0].pgSort.value = "";
			window.opener.document.forms[0].pgLastSort.value = "";
			window.opener.document.forms[0].pgLastValori.value = "";
			window.opener.bloccaRichiesteServer();
			window.opener.listaVaiAPagina(0);
			window.close();
		}
	</gene:javaScript>
		
</gene:template>