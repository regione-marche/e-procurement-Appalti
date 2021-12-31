<%/*
   * Created on 21-lug-2010
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

<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />
<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_ELEDITTE")}' />
<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.PulisciSessioneFiltriFunction" parametro="listaElenchi"/>

<c:set var="filtroUffint" value=""/> 
<c:if test="${! empty sessionScope.uffint}">
	<c:set var="filtroUffint" value=" exists (select codgar from torn where codgar = V_GARE_ELEDITTE.CODGAR and CENINT = '${sessionScope.uffint}')"/>
</c:if>

<c:set var="isPersonalizzazioneGenovaAttiva" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsPersonalizzazioneGenovaAttivaFunction",pageContext)}' scope="request" />

<c:set var="esistonoGarePubblicate"
	value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsistonoGarePubblicateFunction", pageContext,"V_GARE_ELEDITTE")}' />

<c:choose>
	<c:when test='${not empty param.tipoRicerca}'>
		<c:set var="tipoRicerca" value="${param.tipoRicerca}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipoRicerca" value="${tipoRicerca}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.findstr}'>
		<c:set var="findstr" value="${param.findstr}" />
	</c:when>
	<c:otherwise>
		<c:set var="findstr" value="${findstr}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.valoreCodimp}'>
		<c:set var="valoreCodimp" value="${param.valoreCodimp}" />
	</c:when>
	<c:otherwise>
		<c:set var="valoreCodimp" value="${valoreCodimp}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.valoreNomimo}'>
		<c:set var="valoreNomimo" value="${param.valoreNomimo}" />
	</c:when>
	<c:otherwise>
		<c:set var="valoreNomimo" value="${valoreNomimo}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.valoreCf}'>
		<c:set var="valoreCf" value="${param.valoreCf}" />
	</c:when>
	<c:otherwise>
		<c:set var="valoreCf" value="${valoreCf}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.valorePiva}'>
		<c:set var="valorePiva" value="${param.valorePiva}" />
	</c:when>
	<c:otherwise>
		<c:set var="valorePiva" value="${valorePiva}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.valoreTipimp}'>
		<c:set var="valoreTipimp" value="${param.valoreTipimp}" />
	</c:when>
	<c:otherwise>
		<c:set var="valoreTipimp" value="${valoreTipimp}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.valoreIsmpmi}'>
		<c:set var="valoreIsmpmi" value="${param.valoreIsmpmi}" />
	</c:when>
	<c:otherwise>
		<c:set var="valoreIsmpmi" value="${valoreIsmpmi}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.valoreEmail}'>
		<c:set var="valoreEmail" value="${param.valoreEmail}" />
	</c:when>
	<c:otherwise>
		<c:set var="valoreEmail" value="${valoreEmail}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.valorePec}'>
		<c:set var="valorePec" value="${param.valorePec}" />
	</c:when>
	<c:otherwise>
		<c:set var="valorePec" value="${valorePec}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.valoreCodCat}'>
		<c:set var="valoreCodCat" value="${param.valoreCodCat}" />
	</c:when>
	<c:otherwise>
		<c:set var="valoreCodCat" value="${valoreCodCat}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.valoreDescCat}'>
		<c:set var="valoreDescCat" value="${param.valoreDescCat}" />
	</c:when>
	<c:otherwise>
		<c:set var="valoreDescCat" value="${valoreDescCat}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.valoreTipCat}'>
		<c:set var="valoreTipCat" value="${param.valoreTipCat}" />
	</c:when>
	<c:otherwise>
		<c:set var="valoreTipCat" value="${valoreTipCat}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.valoreNumclass}'>
		<c:set var="valoreNumclass" value="${param.valoreNumclass}" />
	</c:when>
	<c:otherwise>
		<c:set var="valoreNumclass" value="${valoreNumclass}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.valoreDricind}'>
		<c:set var="valoreDricind" value="${param.valoreDricind}" />
	</c:when>
	<c:otherwise>
		<c:set var="valoreDricind" value="${valoreDricind}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.valoreAbilitaz}'>
		<c:set var="valoreAbilitaz" value="${param.valoreAbilitaz}" />
	</c:when>
	<c:otherwise>
		<c:set var="valoreAbilitaz" value="${valoreAbilitaz}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.valoreDscad}'>
		<c:set var="valoreDscad" value="${param.valoreDscad}" />
	</c:when>
	<c:otherwise>
		<c:set var="valoreDscad" value="${valoreDscad}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.valoreAltnot}'>
		<c:set var="valoreAltnot" value="${param.valoreAltnot}" />
	</c:when>
	<c:otherwise>
		<c:set var="valoreAltnot" value="${valoreAltnot}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.valoreCoordsic}'>
		<c:set var="valoreCoordsic" value="${param.valoreCoordsic}" />
	</c:when>
	<c:otherwise>
		<c:set var="valoreCoordsic" value="${valoreCoordsic}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.valoreStrin}'>
		<c:set var="valoreStrin" value="${param.valoreStrin}" />
	</c:when>
	<c:otherwise>
		<c:set var="valoreStrin" value="${valoreStrin}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.ignoraCaseSensitive}'>
		<c:set var="ignoraCaseSensitive" value="${param.ignoraCaseSensitive}" />
	</c:when>
	<c:otherwise>
		<c:set var="ignoraCaseSensitive" value="${ignoraCaseSensitive}" />
	</c:otherwise>
</c:choose>


<c:if test="${tipoRicerca eq 1}">
	<c:set var="filtroAusilio" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.FiltroHomeRicercaElenchiFunction", pageContext, "paginaHome",findstr)}' />
</c:if>

<c:if test="${!empty trovaAddWhere && tipoRicerca ne 1}">
	<c:set var="filtroAusilio" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.FiltroHomeRicercaElenchiFunction", pageContext, "paginaRicerca",findstr)}' />
</c:if>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="V_GARE_ELEDITTE-lista" >
	<gene:setString name="titoloMaschera" value="Lista elenchi"/>
	
	<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
	<gene:redefineInsert name="pulsanteListaEliminaSelezione"></gene:redefineInsert>
	<gene:redefineInsert name="listaNuovo">
		<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:listaNuovaGara();" title="Inserisci" tabindex="1501">
					${gene:resource("label.tags.template.lista.listaNuovo")}</a>
			</td>
		</tr>
		</c:if>
	</gene:redefineInsert>
	<gene:redefineInsert name="pulsanteListaInserisci">
		<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
			<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:listaNuovaGara();">
		</c:if>
	</gene:redefineInsert>

	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
		
  	<%// Creo la lista per gare e torn mediante la vista %>
		<table class="lista">
			<tr>
				<td>
  	<gene:formLista entita="V_GARE_ELEDITTE" gestisciProtezioni="true" pagesize="20" tableclass="datilista" sortColumn="3" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreElencoDitte"
  		where="${filtroUffint }">
  	<c:set var="garaLottoUnico" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GaraLottoUnicoFunction",  pageContext,chiaveRigaJava)}' />
  	<c:set var="tipGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction",  pageContext,chiaveRigaJava)}'/>
  	<c:set var="visualizzaLink" value='${(garaLottoUnico && 
									gene:checkProt(pageContext, "MASC.VIS.GARE.GARE-scheda")) ||
									(gene:checkProt(pageContext, "MASC.VIS.GARE.TORN-scheda") && 
									tipGara == "1") || (tipGara == "3" && 
									gene:checkProt(pageContext, "MASC.VIS.GARE.TORN-OFFUNICA-scheda") )}'/>  <%/*Inserita gestione gare a lotti con offerta unica*/ %>

  	<c:set var='visualizzaPopUp' value='${visualizzaLink || gene:checkProtFunz(pageContext, "MOD","MOD") || gene:checkProtFunz(pageContext, "DEL","DEL") || gene:checkProtFunz(pageContext, "ALT","Copia-gara")}'/>
  	
			<gene:campoLista title="Opzioni" width="50">
				<c:if test='${visualizzaPopUp}'>
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
						<c:choose>
							<c:when test='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GaraLottoUnicoFunction", pageContext, chiaveRigaJava)}'>
								<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.GARE-scheda")}' >
									<gene:PopUpItem title="Visualizza elenco" href="visualizzaGara('${chiaveRigaJava}')" />
								</c:if>
								<c:if test='${(datiRiga.G_PERMESSI_AUTORI ne "2") && gene:checkProt(pageContext, "MASC.VIS.GARE.GARE-scheda") && gene:checkProtFunz(pageContext, "MOD","MOD")}' >
									<gene:PopUpItem title="Modifica elenco" href="modificaGara('${chiaveRigaJava}')" />
								</c:if>
							</c:when>
							<c:otherwise>
								<c:if test='${(gene:checkProt(pageContext, "MASC.VIS.GARE.TORN-scheda") and tipGara== "1") or (gene:checkProt(pageContext, "MASC.VIS.GARE.TORN-OFFUNICA-scheda") and tipGara== "3")}' >
									<gene:PopUpItem title="Visualizza elenco" href="visualizzaGara('${chiaveRigaJava}')" />
								</c:if>
								<c:if test='${(datiRiga.G_PERMESSI_AUTORI ne "2") && ((gene:checkProt(pageContext, "MASC.VIS.GARE.TORN-scheda") and tipGara== "1") or (gene:checkProt(pageContext, "MASC.VIS.GARE.TORN-OFFUNICA-scheda") and tipGara== "3")) && gene:checkProtFunz(pageContext, "MOD","MOD")}' >
									<gene:PopUpItem title="Modifica elenco" href="modificaGara('${chiaveRigaJava}')" />
								</c:if>
							</c:otherwise>
						</c:choose>
						<c:if test='${(datiRiga.G_PERMESSI_AUTORI ne "2") && gene:checkProtFunz(pageContext, "DEL","DEL")}' >
							<gene:PopUpItem title="Elimina elenco" href="eliminaGara()" />
						</c:if>
						<c:if test='${gene:checkProtFunz(pageContext, "ALT","Condividi-gara")}'> 
							<c:choose>
								<c:when test='${isPersonalizzazioneGenovaAttiva eq "1"}'>
									<gene:PopUpItem title="Condividi e proteggi elenco" href="javascript:apriGestionePermessi('${datiRiga.V_GARE_ELEDITTE_CODGAR}',10,${datiRiga.G_PERMESSI_PROPRI eq '1' or abilitazioneGare eq 'A'})" />
								</c:when>
								<c:otherwise>
									<gene:PopUpItem title="Condividi e proteggi elenco" href="javascript:apriGestionePermessiStandard('${datiRiga.V_GARE_ELEDITTE_CODGAR}',10,${datiRiga.G_PERMESSI_PROPRI eq '1' or abilitazioneGare eq 'A'})" />		
								</c:otherwise>
							</c:choose>
						</c:if>
						<c:if test='${gene:checkProtFunz(pageContext, "ALT","Archivia-gara")}'>
							<c:if test='${(datiRiga.G_PERMESSI_AUTORI ne "2") && (datiRiga.V_GARE_ELEDDITTE_ISARCHI ne "1") && gene:checkProtFunz(pageContext, "MOD","MOD")}' >
								<gene:PopUpItem title="Archivia elenco" href="archiviaGara()" />
							</c:if>
						</c:if>
						<c:if test='${datiRiga.G_PERMESSI_AUTORI ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.V_GARE_ELEDITTE-lista.ElencoOpSorteggio")}'>
							<gene:PopUpItem title="Sorteggio pubblico operatori" href="elencoOperatori('${chiaveRigaJava}')" />
						</c:if>
					</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<% // Campi veri e propri %>
			
			<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';visualizzaGara('${chiaveRigaJava}');" />
			
			<gene:campoLista campo="CODGAR" visibile="false"/>
			<gene:campoLista campo="CODICE"  headerClass="sortable" href="${gene:if(visualizzaLink, link, '')}" />
			<gene:campoLista campo="TIPOELE" headerClass="sortable" />
			<gene:campoLista campo="OGGETTO" headerClass="sortable" />
			<gene:campoLista campo="PERIODO" />
			
			<c:choose>
				<c:when test="${!empty (filtroLivelloUtente)}">
					<gene:campoLista campo="AUTORI" entita="G_PERMESSI" where="G_PERMESSI.CODGAR=V_GARE_ELEDITTE.CODGAR AND G_PERMESSI.SYSCON=${profiloUtente.id}" visibile="false"/>
					<gene:campoLista campo="PROPRI" entita="G_PERMESSI" where="G_PERMESSI.CODGAR=V_GARE_ELEDITTE.CODGAR AND G_PERMESSI.SYSCON=${profiloUtente.id}" visibile="false"/>
				</c:when>
				<c:otherwise>
					<gene:campoLista campo="AUTORI" entita="G_PERMESSI" campoFittizio="true" value="1" visibile="false"/>
					<gene:campoLista campo="PROPRI" entita="G_PERMESSI" campoFittizio="true" value="1" visibile="false"/>
				</c:otherwise>
			</c:choose>
			
			<c:if test="${esistonoGarePubblicate == 'SI'}">
				<c:set var="garaPubblicata" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GaraPubblicataSuPortaleFunction",  pageContext, datiRiga.V_GARE_ELEDITTE_CODGAR )}'/>
				<gene:campoLista title="&nbsp;" width="20" >
					<c:if test="${garaPubblicata == 'SI'}">
						<img width="16" height="16" title="Elenco pubblicato su portale" alt="Elenco pubblicato su portale" src="${pageContext.request.contextPath}/img/ditta_acquisita.png"/>
					</c:if>
					
				</gene:campoLista>
			</c:if>
			
			<input type="hidden" name="tipoRicerca" value="${tipoRicerca }" />
			<input type="hidden" name="findstr" value="${findstr}" />
			<input type="hidden" name="valoreCodimp" value="${valoreCodimp }" />
			<input type="hidden" name="valoreNomimo" value="${valoreNomimo }" />
			<input type="hidden" name="valoreCf" value="${valoreCf }" />
			<input type="hidden" name="valorePiva" value="${valorePiva }" />
			<input type="hidden" name="valoreTipimp" value="${valoreTipimp }" />
			<input type="hidden" name="valoreIsmpmi" value="${valoreIsmpmi }" />
			<input type="hidden" name="valoreEmail" value="${valoreEmail }" />
			<input type="hidden" name="valorePec" value="${valorePec }" />
			<input type="hidden" name="valoreCodCat" value="${valoreCodCat }" />
			<input type="hidden" name="valoreDescCat" value="${valoreDescCat }" />
			<input type="hidden" name="valoreTipCat" value="${valoreTipCat }" />
			<input type="hidden" name="valoreNumclass" value="${valoreNumclass }" />
			<input type="hidden" name="valoreAbilitaz" value="${valoreAbilitaz }" />
			<input type="hidden" name="valoreDricind" value="${valoreDricind }" />
			<input type="hidden" name="valoreDscad" value="${valoreDricind }" />
			<input type="hidden" name="valoreAltnot" value="${valoreAltnot }" />
			<input type="hidden" name="valoreCoordsic" value="${valoreCoordsic }" />
			<input type="hidden" name="valoreStrin" value="${valoreStrin }" />
			<input type="hidden" name="ignoraCaseSensitive" value="${ignoraCaseSensitive }" />
			<input type="hidden" name="genere" value="10" />
			<input name="log" type="hidden" value="true"/>
		</gene:formLista>
				</td>
			</tr>
			<tr>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" />
			</tr>
		</table>
		
		<form name="formVisualizzaPermessiUtenti" action="${pageContext.request.contextPath}/pg/VisualizzaPermessiUtenti.do" method="post">
			<input type="hidden" name="metodo" id="metodo" value="apri" />
			<input type="hidden" name="codgar" id="codgar" value="" />
			<input type="hidden" name="genereGara" id="genereGara" value="" />
			<input type="hidden" name="permessimodificabili" id="permessimodificabili" value="" />
			<input type="hidden" name="codein" id="codein" value="${sessionScope.uffint}" />
		</form> 
		
		<form name="formVisualizzaPermessiUtentiStandard" action="${pageContext.request.contextPath}/pg/VisualizzaPermessiUtentiStandard.do" method="post">
			<input type="hidden" name="metodo" id="metodo" value="apri" />
			<input type="hidden" name="codgar" id="codgar" value="" />
			<input type="hidden" name="genereGara" id="genereGara" value="" />
			<input type="hidden" name="permessimodificabili" id="permessimodificabili" value="" />
			<input type="hidden" name="codein" id="codein" value="${sessionScope.uffint}" />
		</form>
		
  </gene:redefineInsert>

	<gene:javaScript>
	// Visualizzazione del dettaglio
	function visualizzaGara(chiaveRiga){
		//document.forms[0].jspPath.value = "/WEB-INF/pages/gare/gare/gare-pg-datigen-elencoditte.jsp";
		//document.forms[0].jspPathTo.value = "/WEB-INF/pages/gare/gare/gare-pg-datigen-elencoditte.jsp";
		//gara a lotto unico si va su gare
		//document.forms[0].action += "&tipoGara=garaLottoUnico&garaPerElenco=1";
		document.forms[0].keyParent.value = chiaveRiga;
		document.forms[0].entita.value = "GARE";
		var input = document.createElement("input");
	    input.setAttribute("type", "hidden");
        input.setAttribute("name", "log");
        input.setAttribute("value", true);
	    document.forms[0].appendChild(input);
	    console.log(document.forms[0]);
		listaVisualizza();
	}

	function modificaGara(chiaveRiga){
		document.forms[0].keyParent.value=chiaveRiga;
		document.forms[0].entita.value = "GARE";

		listaModifica();
	}

	function eliminaGara(){
		chiaveRiga = chiaveRiga.replace("V_GARE_ELEDITTE", "TORN");
		var href = "href=gare/commons/conferma-eliminazione.jsp&chiaveRiga=" + chiaveRiga + "&numeroPopUp=1&genere=10";
		win = openPopUpCustom(href, "confermaEliminaGara", 500, 200, "no", "no");

		if(win!=null)
			win.focus();
	}

	function confermaDelete(){
		closePopUps();
		document.forms[0].entita.value = "TORN";
		document.forms[0].key.value = chiaveRiga;
		document.forms[0].metodo.value = "elimina";
		document.forms[0].submit();
	}
	
	function apriGestionePermessi(codgar, genereGara, permessoModifica) {
		bloccaRichiesteServer();
		formVisualizzaPermessiUtenti.codgar.value = codgar;
		formVisualizzaPermessiUtenti.genereGara.value = genereGara;
		formVisualizzaPermessiUtenti.permessimodificabili.value = permessoModifica;
		formVisualizzaPermessiUtenti.submit();
	}

	function apriGestionePermessiStandard(codgar, genereGara, permessoModifica) {
		bloccaRichiesteServer();
		formVisualizzaPermessiUtentiStandard.codgar.value = codgar;
		formVisualizzaPermessiUtentiStandard.genereGara.value = genereGara;
		formVisualizzaPermessiUtentiStandard.permessimodificabili.value = permessoModifica;
		formVisualizzaPermessiUtentiStandard.submit();
	}
	
	function copiaGara(codGar, tipoGara, isGaraLottoUnico,genereGara){
		href = "href=gare/v_gare_eleditte/copia-gare-eleditte.jsp&key="+chiaveRiga+"&numeroPopUp=1&tipoGara="+tipoGara+"&genereGara="+genereGara;
		openPopUpCustom(href, "copiaGara", 600, 350, "no", "yes");
	}


	function archiviaGara(){
		chiaveRiga = chiaveRiga.replace("V_GARE_ELEDITTE", "GAREALBO");
		href = "href=gare/v_gare_torn/v_gare_torn-popup-archivia-gara.jsp&key="+chiaveRiga;
		openPopUpCustom(href, "archiviaGara", 500, 350, "no", "yes");
	}


	function listaNuovaGara(){
		document.forms[0].jspPathTo.value = "/WEB-INF/gare/gare/gare-scheda.jsp"
		document.forms[0].entita.value = "GARE";
		document.forms[0].metodo.value = "nuovo";
		document.forms[0].action+="&tipoGara=garaLottoUnico&garaPerElenco=1";
		document.forms[0].submit();
	}
	
	function elencoOperatori(chiaveRiga){
		var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/ditg/lista-operatoriAbilitati.jsp";
		href += "&key=" + chiaveRiga;
		document.location.href = href;
	}
	
	</gene:javaScript>
</gene:template>