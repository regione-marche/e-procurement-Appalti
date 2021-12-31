<%
/*
 * Created on: 19-ott-2007
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

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
</gene:redefineInsert>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetTIPGENFunction" parametro='${gene:getValCampo(key, "TORN.CODGAR")}' />

<c:set var="esisteIntegrazioneLavori" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneLavoriFunction", pageContext)}' />

<c:set var="tipgarTornata" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTIPGARFunction", pageContext, gene:getValCampo(key, "TORN.CODGAR"))}'/>

<c:if test='${tipologiaGara == "3"}'>
	<c:set var="filtroGenere" value="AND (GARE.GENERE is null or GARE.GENERE <>3)"/>
	<c:set var="bloccoOliamm" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteGaraOLIAMMFunction",  pageContext,gene:getValCampo(key, "TORN.CODGAR"))}' />
	<c:set var="bustalotti" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetBustalottiFunction", pageContext, key)}' scope="request" />
	<c:set var="modcont" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetModcontFunction", pageContext, gene:getValCampo(key, "TORN.CODGAR"))}' />
</c:if>


<c:set var="bloccoPubblicazionePortaleBando" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,gene:getValCampo(key, "TORN.CODGAR"),"BANDO","false")}' />
<c:choose>
	<c:when test='${tipologiaGara == "3"}'>
		<c:set var="controlloTuttiLotti" value="false"/>
	</c:when>
	<c:otherwise>
		<c:set var="controlloTuttiLotti" value="true"/>
	</c:otherwise>
</c:choose>
<c:set var="bloccoPubblicazionePortaleEsito" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,gene:getValCampo(key, "TORN.CODGAR"),"ESITO",controlloTuttiLotti)}' />
<c:if test="${!isProceduraTelematica }">
	<c:set var="applicareBloccoPubblicazioneGareNonTelematiche" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext,"A1153","1","true")}' />
</c:if>

<c:set var="bloccoModificatiDati" value='${(isProceduraTelematica || applicareBloccoPubblicazioneGareNonTelematiche eq "1") && (bloccoPubblicazionePortaleEsito eq "TRUE" || bloccoPubblicazionePortaleBando eq "TRUE")}' />

<c:set var="isCodificaAutomatica" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.IsCodificaAutomaticaFunction", pageContext, "TORN", "CODGAR")}'/>

<%/*Imposto il menu nel titolo*/%>
<c:set var="visualizzaPopUp" value='${(gene:checkProt(pageContext, "MASC.VIS.GARE.GARE-scheda") ||  
				 gene:checkProtFunz(pageContext, "MOD","MOD") ||
				gene:checkProtFunz(pageContext, "DEL","DEL"))}'/>

		<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
		<gene:redefineInsert name="pulsanteListaEliminaSelezione"></gene:redefineInsert>
		<gene:redefineInsert name="listaNuovo">
		<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO") && bloccoOliamm ne "true" && !bloccoModificatiDati}'>
			<tr>
				<td class="vocemenulaterale">
					<c:if test='${isNavigazioneDisattiva ne "1"}'><a href="javascript:listaNuovaGaraLotto();" title="Inserisci" tabindex="1501"></c:if>
					${gene:resource("label.tags.template.lista.listaPageNuovo")}
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
			<c:if test='${autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.TORN-lista.LISTALOTTI.ExportXLSLotti") and bustalotti eq "1"}'>
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:esportaInExcel();" title='Esporta in Excel' tabindex="1503">
							Esporta in Excel
						</a>
					</td>
				</tr>
			</c:if>
			<c:if test='${autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.TORN-lista.LISTALOTTI.ImportXLSLotti") and bustalotti eq "1"}'>
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:importaDaExcel();" title='Importa da Excel' tabindex="1504">
							Importa da Excel
						</a>
					</td>
				</tr>
			</c:if>
		</c:if>
		
		<c:if test='${integrazioneWSERP eq "1"  && tipoWSERP eq "FNM" && tipologiaGara == "3" && gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GestioneUnicaERP")}'>
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:creaLotti();"
					title='Crea lotti da lista procedimenti' tabindex="1505">
						Crea lotti da lista procedimenti
					</a>
				</td>
			</tr>
		</c:if>
		<tr>
			<td class="vocemenulaterale">
				<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ImpostaFiltroLotti")}'>	
					<a href="javascript:impostaFiltroLotti();" title='filtra lotti' tabindex="1507">
						Imposta filtro
					</a>
				</c:if>
			</td>
		</tr>
		</gene:redefineInsert>

		<gene:redefineInsert name="pulsanteListaInserisci">
		<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO") && bloccoOliamm ne "true" && !bloccoModificatiDati}'>
			<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaPageNuovo")}' title='${gene:resource("label.tags.template.lista.listaPageNuovo")}' onclick="listaNuovaGaraLotto();">
		</c:if>
		</gene:redefineInsert>

<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp" >
	<jsp:param name="entita" value="V_GARE_TORN"/>
	<jsp:param name="inputFiltro" value="${key}"/>
	<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
</jsp:include>



<table class="dettaglio-tab-lista">
	<c:if test="${!empty filtroLotti }">
		<tr>
			<td style="font: 11px Verdana, Arial, Helvetica, sans-serif;">
			 <br><img src="${pageContext.request.contextPath}/img/filtro.gif" alt="Filtro">&nbsp;<span style="color: #ff0028; font-weight: bold;">Lista filtrata</span>
			 <c:if test='${updateLista ne 1}'>
				 &nbsp;&nbsp;&nbsp;[ <a href="javascript:AnnullaFiltro(7);" ><IMG SRC="${pageContext.request.contextPath}/img/cancellaFiltro.gif" alt="Cancella filtro"></a>
				 <a class="link-generico" href="javascript:AnnullaFiltro(7);">Cancella filtro</a> ]
			 </c:if>
			</td>
		</tr>
	</c:if>
	<tr>
		<td>
			<gene:formLista entita="GARE" where="GARE.CODGAR1 = #TORN.CODGAR# ${filtroGenere} ${filtroLotti}" sortColumn="4" tableclass="datilista" pagesize="25" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreGARE">
				<c:if test='${isProceduraTelematica}'>
					<c:if test='${tipologiaGara ne "3"}'>
						<c:set var="bloccoPubblicazionePortaleEsito" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,datiRiga.GARE_NGARA,"ESITO","false")}' />
					</c:if>
				</c:if>
				<gene:campoLista title="Opzioni" width="50">
						
				<c:if test='${currentRow >= 0 && visualizzaPopUp}'>
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
						<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.GARE-scheda")}' >
								<gene:PopUpItem title="Visualizza lotto" href="javascript:listaVisualizza()" />
						</c:if>
						<c:if test='${(autorizzatoModifiche ne "2") && gene:checkProt(pageContext, "MASC.VIS.GARE.GARE-scheda") && gene:checkProtFunz(pageContext, "MOD","MOD") && !bloccoModificatiDati}' >
							<gene:PopUpItem title="Modifica lotto" href="javascript:listaModifica()" />
						</c:if>
						<c:if test='${(autorizzatoModifiche ne "2") && bloccoOliamm ne "true" && gene:checkProtFunz(pageContext, "DEL","DEL") && !bloccoModificatiDati}' >
							<gene:PopUpItem title="Elimina lotto" href="javascript:eliminaLotto()" />
						</c:if>
						<c:if test='${bloccoOliamm ne "true" && gene:checkProtFunz(pageContext, "ALT","Copia-lotto") && !(tipologiaGara == "3" && autorizzatoModifiche eq "2")  && !bloccoModificatiDati}'>
							<gene:PopUpItem title="Copia lotto" href="javascript:copiaLotto('${chiaveRigaJava}', '${datiRiga.GARE_CODGAR1}', ${datiRiga.V_GARE_TORN_TIPGEN},${tipologiaGara})"/>
						</c:if>
					</gene:PopUp>
				</c:if>
				</gene:campoLista>
				<gene:campoLista campo="TIPGEN" entita="V_GARE_TORN" headerClass="sortable" visibile="false" where="GARE.CODGAR1 = V_GARE_TORN.CODGAR" />
				<gene:campoLista campo="CODGAR1" headerClass="sortable" visibile="false" />
				<c:choose>
					<c:when test='${gene:checkProt(pageContext, "MASC.VIS.GARE.GARE-scheda")}' >
						<gene:campoLista campo="NGARA" title="Codice lotto" width="100" headerClass="sortable" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();"/>
					</c:when>
					<c:otherwise>
						<gene:campoLista campo="NGARA" title="Codice lotto" width="100" headerClass="sortable" />
					</c:otherwise>
				</c:choose>
				<gene:campoLista campo="CODIGA" title="Lotto" headerClass="sortable" width="50"/>
				<gene:campoLista campo="CODCIG" headerClass="sortable" />
				<gene:campoLista campo="NOT_GAR" headerClass="sortable" />
				<gene:campoLista campo="IMPAPP" headerClass="sortable" width="150" />
				<gene:campoLista campo="DITTA" visibile="false" />
				<c:if test='${tipologiaGara != "3"}'>
					<gene:campoLista campo="STATO" entita="V_GARE_STATOESITOLOTTI" where="V_GARE_STATOESITOLOTTI.CODICE = GARE.NGARA" visibile="false"/>
					<gene:campoLista campo="ESITO" entita="V_GARE_STATOESITOLOTTI" where="V_GARE_STATOESITOLOTTI.CODICE = GARE.NGARA" visibile="false"/>
					<gene:campoLista campo="ISAGGIU_FIT" title="Stato lotto" campoFittizio="true" definizione="T30" value="${datiRiga.V_GARE_STATOESITOLOTTI_STATO} ${datiRiga.V_GARE_STATOESITOLOTTI_ESITO}"/>
				</c:if>
			</gene:formLista>
		</td>
	</tr>
	<tr><jsp:include page="/WEB-INF/pages/commons/pulsantiListaPage.jsp" /></tr>
</table>

<gene:javaScript>
	
	var idconfi = "${idconfi}";
		
	function eliminaLotto(){
		var href = "href=gare/commons/conferma-eliminazione.jsp&chiaveRiga=" + chiaveRiga + "&numeroPopUp=1";
		win = openPopUpCustom(href, "confermaEliminaGara", 500, 200, "no", "no");

		if(win!=null)
			win.focus();
	}

	function confermaDelete(){
		closePopUps();
		document.forms[0].key.value = chiaveRiga;
		document.forms[0].metodo.value = "elimina";
		document.forms[0].submit();
	}

	function copiaLotto(ngara, codgar, tipoGara, genereGara){
		var tipgarTornata = "${tipgarTornata }";
		href = "href=gare/v_gare_torn/copia-gare-torn.jsp&key="+chiaveRiga+"&codgar=" + codgar + "&tipoGara=" + tipoGara + "&numeroPopUp=1&garaSorgenteModificabile=${autorizzatoModifiche ne 2}"+"&genereGara="+genereGara;
		href+="&tipgarTornata=" + tipgarTornata;
		openPopUpCustom(href, "copiaGara", 600, 350, "no", "yes");
	}
	
	function listaNuovaGaraLotto() {
		<c:choose>
			<c:when test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.AssociaGaraAppalto") and (tipologiaGara eq "1" || (tipologiaGara eq "3" and modcont eq "1")) and esisteIntegrazioneLavori eq "TRUE"}' >
				var href="${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&"+csrfToken+"&href=gare/gare/trovaAppalto/associaAppalto.jsp&modo=NUOVO&tipoAppalto=${tipoAppalto}&chiavePadre='"+getValue("keyParent")+"'";
				if(idconfi){
					href = href + "&idconfi="+idconfi;
				}
				<c:choose>
					<c:when test="${tipologiaGara eq '1' }">
						href += "&lottoOfferteDistinte=1";
					</c:when>
					<c:otherwise>
						href += "&lottoOffertaUnica=SI";
					</c:otherwise>
				</c:choose>
				document.location.href = href;
			</c:when>
			<c:otherwise>
				document.forms[0].action += "&tipoAppalto=${tipoAppalto}";
				listaNuovo();
			</c:otherwise>
		</c:choose>
	}
	
	function esportaInExcel(){
   	  var codgar = document.forms[0].keyParent.value;
	  var act = "${pageContext.request.contextPath}/pg/InitExportLottiGara.do";
	  var par = "codgar=" + codgar.substring(codgar.indexOf(":")+1);
      par += "&garaLotti=1";
	  openPopUpActionCustom(act, par, 'exportLottiGara', 700, 500, "yes", "yes");
	}

	function importaDaExcel(){
   	  var codgar = document.forms[0].keyParent.value;
	  var act = "${pageContext.request.contextPath}/pg/InitImportLottiGara.do";
	  var par = "codgar=" + codgar.substring(codgar.indexOf(":")+1);
	  <c:choose>
  		<c:when test='${isCodificaAutomatica eq "true"}'>
  			par += "&isCodificaAutomatica=1";
  		</c:when>
  		<c:otherwise>
  			par += "&isCodificaAutomatica=2";
  		</c:otherwise>
	</c:choose>
      par += "&garaLottiOffDist=1";
	  openPopUpActionCustom(act, par, 'importLottiGara', 700, 500, "yes", "yes");
	}
	
	<c:if test='${integrazioneWSERP eq "1" && tipoWSERP eq "FNM"}'>
		function creaLotti(){
		 	var kp = document.forms[0].keyParent.value;
			var codgar = kp.substring(kp.indexOf(":")+1);
			bloccaRichiesteServer();
			formListaRda.href.value = "gare/commons/lista-rda-scheda.jsp";
			formListaRda.codgar.value = codgar;
			formListaRda.codice.value = "";
			formListaRda.genere.value = "3";
			formListaRda.tipoAppalto.value = "${tipoAppalto}";
			formListaRda.tipoProcedura.value = "${tipgarTornata}";
			formListaRda.bustalotti.value = "${bustalotti}";
			formListaRda.linkrda.value = "1";
			formListaRda.submit();
		}
	</c:if>

</gene:javaScript>


	 	<form name="formListaRda" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
			<input type="hidden" name="href" value="" /> 
			<input type="hidden" name="codgar" id="codgar" value="" />
			<input type="hidden" name="codice" id="codice" value="" />
			<input type="hidden" name="genere" id="genere" value="" />
			<input type="hidden" name="tipoAppalto" id="tipoAppalto" value="" />
			<input type="hidden" name="tipoProcedura" id="tipoProcedura" value="" />
			<input type="hidden" name="bustalotti" id="bustalotti" value="" />
			<input type="hidden" name="linkrda" id="linkrda" value="" />
			<input type="hidden" name="uffint" id="uffint" value="${sessionScope.uffint}" />
		</form> 
