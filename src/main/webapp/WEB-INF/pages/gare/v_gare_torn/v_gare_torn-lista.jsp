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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />
<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_TORN")}' />
<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.PulisciSessioneFiltriFunction" parametro=""/>

<c:set var="esistonoGarePubblicate"
	value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsistonoGarePubblicateFunction", pageContext,"V_GARE_TORN")}' />

<c:set var="isPersonalizzazioneGenovaAttiva" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsPersonalizzazioneGenovaAttivaFunction",pageContext)}' scope="request" />

<c:set var="filtroUffint" value=""/> 
<c:if test="${! empty sessionScope.uffint}">
	<c:set var="filtroUffint" value=" exists (select codgar from torn where codgar = V_GARE_TORN.CODGAR and CENINT = '${sessionScope.uffint}')"/>
</c:if>

<% //Si cancella dalla sessione il valore delle password per determinare le chiavi di cifratura delle buste %>
<c:set var="passBusteA" value='' scope="session"/>
<c:set var="passBusteB" value='' scope="session"/>
<c:set var="passBusteC" value='' scope="session"/>
<c:set var="passBustePreq" value='' scope="session"/>

<c:set var="applicareBloccoPubblicazioneGareNonTelematiche" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext,"A1153","1","true")}' />

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="V_GARE_TORN-lista">
	<gene:setString name="titoloMaschera" value="Lista gare"/>
	
	<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
	<gene:redefineInsert name="pulsanteListaEliminaSelezione"></gene:redefineInsert>
	<gene:redefineInsert name="listaNuovo">
		<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:listaNuovaGara();" title="Inserisci" tabindex="1501">
					${gene:resource("label.tags.template.lista.listaNuovo")}</a></td>
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
  	<gene:formLista entita="V_GARE_TORN" gestisciProtezioni="true" pagesize="20" tableclass="datilista" sortColumn="-2" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreTORN"
  		where="${filtroUffint }">
  	<c:set var="garaLottoUnico" value='${datiRiga.V_GARE_TORN_GENERE eq 2}' />
  	<c:set var="tipGara" value='${datiRiga.V_GARE_TORN_GENERE}'/>
  	<c:set var="visualizzaLink" value='${(garaLottoUnico && 
									gene:checkProt(pageContext, "MASC.VIS.GARE.GARE-scheda")) ||
									(gene:checkProt(pageContext, "MASC.VIS.GARE.TORN-scheda") && 
									tipGara == "1") || (tipGara == "3" && 
									gene:checkProt(pageContext, "MASC.VIS.GARE.TORN-OFFUNICA-scheda") )}'/>  <%/*Inserita gestione gare a lotti con offerta unica*/ %>

  	<c:set var='visualizzaPopUp' value='${visualizzaLink || gene:checkProtFunz(pageContext, "MOD","MOD") || gene:checkProtFunz(pageContext, "DEL","DEL") || gene:checkProtFunz(pageContext, "ALT","Copia-gara")}'/>
  	<c:set var="garaPubblicata" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GaraPubblicataSuPortaleFunction",  pageContext, datiRiga.V_GARE_TORN_CODGAR )}'/> 	
  	<c:set var="garaEsito" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GaraEsitoFunction",  pageContext, datiRiga.V_GARE_TORN_CODICE )}'/>
	<c:set var="bloccoPubblicazione" value='false' />		
			<gene:campoLista title="Opzioni" width="50">
				<c:if test='${visualizzaPopUp}'>
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
						<c:if test="${(datiRiga.V_GARE_TORN_GARTEL eq '1' || (datiRiga.V_GARE_TORN_GARTEL ne '1' && applicareBloccoPubblicazioneGareNonTelematiche eq '1')) and garaPubblicata eq 'SI'}">
								<c:set var="bloccoPubblicazione" value='true' />
						</c:if>
						
						<c:choose>
							<c:when test='${garaLottoUnico}'>
								<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.GARE-scheda")}' >
									<gene:PopUpItem title="Visualizza gara" href="visualizzaGara('${chiaveRigaJava}')" />
								</c:if>
								<c:if test='${(datiRiga.G_PERMESSI_AUTORI ne "2") && gene:checkProt(pageContext, "MASC.VIS.GARE.GARE-scheda") && gene:checkProtFunz(pageContext, "MOD","MOD") && bloccoPubblicazione ne "true"}' >
									<gene:PopUpItem title="Modifica gara" href="modificaGara('${chiaveRigaJava}')" />
								</c:if>
							</c:when>
							<c:otherwise>
								
								<c:if test='${(gene:checkProt(pageContext, "MASC.VIS.GARE.TORN-scheda") and tipGara== "1") or (gene:checkProt(pageContext, "MASC.VIS.GARE.TORN-OFFUNICA-scheda") and tipGara== "3")}' >
									<gene:PopUpItem title="Visualizza gara" href="visualizzaGara('${chiaveRigaJava}')" />
								</c:if>
								<c:if test='${(datiRiga.G_PERMESSI_AUTORI ne "2") && ((gene:checkProt(pageContext, "MASC.VIS.GARE.TORN-scheda") and tipGara== "1") or (gene:checkProt(pageContext, "MASC.VIS.GARE.TORN-OFFUNICA-scheda") and tipGara== "3")) && gene:checkProtFunz(pageContext, "MOD","MOD") && bloccoPubblicazione ne "true"}' >
									<gene:PopUpItem title="Modifica gara" href="modificaGara('${chiaveRigaJava}')" />
								</c:if>
							</c:otherwise>
						</c:choose>
						<c:if test='${(datiRiga.G_PERMESSI_AUTORI ne "2") && gene:checkProtFunz(pageContext, "DEL","DEL") && bloccoPubblicazione ne "true"}' >
							<gene:PopUpItem title="Elimina gara" href="eliminaGara()" />
						</c:if>
						<c:if test='${gene:checkProtFunz(pageContext, "ALT","Copia-gara")}'>
							<gene:PopUpItem title="Copia gara" href="copiaGara('${chiaveRigaJava}', '${datiRiga.V_GARE_TORN_TIPGEN}', ${garaLottoUnico},${tipGara },'${datiRiga.GARE_CLIV1}')"/>
						</c:if>
						<c:if test='${gene:checkProtFunz(pageContext, "ALT","Condividi-gara") && datiRiga.V_GARE_TORN_GARTEL != "1"}'> 
							<c:choose>
								<c:when test='${isPersonalizzazioneGenovaAttiva eq "1"}'>
									<gene:PopUpItem title="Condividi e proteggi gara" href="javascript:apriGestionePermessi('${datiRiga.V_GARE_TORN_CODGAR}',${tipGara},${datiRiga.G_PERMESSI_PROPRI eq '1' or abilitazioneGare eq 'A'})"/>
								</c:when>
								<c:otherwise>
									<gene:PopUpItem title="Condividi e proteggi gara" href="javascript:apriGestionePermessiStandard('${datiRiga.V_GARE_TORN_CODGAR}',${tipGara},${datiRiga.G_PERMESSI_PROPRI eq '1' or abilitazioneGare eq 'A'})"/>
								</c:otherwise>
							</c:choose>
						</c:if>

						<c:if test='${gene:checkProtFunz(pageContext, "ALT","Condividi-gara") && datiRiga.V_GARE_TORN_GARTEL == "1"}'> 
							<c:choose>
								<c:when test='${isPersonalizzazioneGenovaAttiva eq "1"}'>
									<gene:PopUpItem title="Punto ordinante e istruttore" href="javascript:apriGestionePermessiGaraTelematica('${datiRiga.V_GARE_TORN_CODGAR}',${tipGara},${datiRiga.G_PERMESSI_PROPRI eq '1' or abilitazioneGare eq 'A'})"/>
								</c:when>
								<c:otherwise>
									<gene:PopUpItem title="Punto ordinante e istruttore" href="javascript:apriGestionePermessiGaraTelematicaStandard('${datiRiga.V_GARE_TORN_CODGAR}',${tipGara},${datiRiga.G_PERMESSI_PROPRI eq '1' or abilitazioneGare eq 'A'})"/>
								</c:otherwise>
							</c:choose>
						</c:if>

						<c:if test='${gene:checkProtFunz(pageContext, "ALT","Archivia-gara")}'>
							<c:if test='${(datiRiga.G_PERMESSI_AUTORI ne "2") && (datiRiga.V_GARE_TORN_ISARCHI ne "1") && gene:checkProtFunz(pageContext, "MOD","MOD")}' >
								<gene:PopUpItem title="Archivia gara" href="archiviaGara()" />
							</c:if>
						</c:if>
					</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<% // Campi veri e propri %>
			
			
			<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';visualizzaGara('${chiaveRigaJava}');" />
			
			<gene:campoLista campo="CODICE" headerClass="sortable" href="${gene:if(visualizzaLink, link, '')}"/>
			<gene:campoLista campo="CODCIG" headerClass="sortable" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoCigListaGare"/>
			<gene:campoLista campo="TIPGEN" headerClass="sortable" />
			<gene:campoLista campo="OGGETTO" headerClass="sortable"/>
			<gene:campoLista campo="IMPORTO" headerClass="sortable"/>
			<gene:campoLista campo="ISLOTTI" headerClass="sortable" width="60"/>
			<gene:campoLista campo="GENERE" headerClass="sortable" />
			<gene:campoLista campo="ISAGGIU_FIT" title="Stato gara" campoFittizio="true" definizione="T30" width="100" value="${garaEsito}"/>
			<gene:campoLista title="Referente" entita="USRSYS" campo="SYSUTE" where="USRSYS.SYSCON=V_GARE_TORN.CLIV2" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.CLIV2")}' ordinabile="false"/>
			
			<gene:campoLista campo="GARTEL" visibile="false" />
			<c:if test='${fn:contains(listaOpzioniDisponibili, "OP114#") && fn:contains(listaOpzioniDisponibili, "OP132#")}'>
				<gene:campoLista title="&nbsp;" width="20" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.GARTEL")}'>
						<c:if test="${datiRiga.V_GARE_TORN_GARTEL eq '1'}">
							<img width="16" height="16" title="Procedura telematica nella piattaforma" alt="Procedura telematica nella piattaforma" src="${pageContext.request.contextPath}/img/Hardware-1.png"/>
						</c:if>
				</gene:campoLista>
			</c:if>
			
			<c:choose>
				<c:when test="${!empty (filtroLivelloUtente)}">
					<gene:campoLista campo="AUTORI" entita="G_PERMESSI" where="G_PERMESSI.CODGAR = V_GARE_TORN.CODGAR AND G_PERMESSI.SYSCON = ${profiloUtente.id}" visibile="false"/>
					<gene:campoLista campo="PROPRI" entita="G_PERMESSI" where="G_PERMESSI.CODGAR = V_GARE_TORN.CODGAR AND G_PERMESSI.SYSCON = ${profiloUtente.id}" visibile="false"/>
				</c:when>
				<c:otherwise>
					<gene:campoLista campo="AUTORI" entita="G_PERMESSI" campoFittizio="true" value="1" visibile="false"/>
					<gene:campoLista campo="PROPRI" entita="G_PERMESSI" campoFittizio="true" value="1" visibile="false"/>
				</c:otherwise>
			</c:choose>
			<gene:campoLista campo="ISARCHI" visibile="false"/>
			<!--gene:campoLista campo="CLIV1" entita="GARE" where="GARE.NGARA=V_GARE_TORN.CODICE" visibile="false"/-->
			<gene:campoLista campo="CODGAR" visibile="false"/>
			<c:if test="${esistonoGarePubblicate == 'SI'}">
				<gene:campoLista title="&nbsp;" width="20" >
					<c:if test="${garaPubblicata == 'SI'}">
						<c:choose>
							<c:when test="${tipoPubblicazione eq '1' }">
								<c:set var="titoloPubblicazione" value="Procedura pubblicata su portale"/>
								<c:set var="imgPubblicazione" value="ditta_acquisita.png"/>
							</c:when>
							<c:when test="${tipoPubblicazione eq '1.1' }">
								<c:set var="titoloPubblicazione" value="Procedura con pubblicazione invito in corso"/>
								<c:set var="imgPubblicazione" value="protocollazione_in_corso.png"/>
							</c:when>
							<c:otherwise>
								<c:set var="titoloPubblicazione" value="Esito procedura pubblicato su portale"/>
								<c:set var="imgPubblicazione" value="esito_pubblicato.png"/>
							</c:otherwise>
						</c:choose>
						<img width="16" height="16" title="${titoloPubblicazione}" alt="${titoloPubblicazione}" src="${pageContext.request.contextPath}/img/${imgPubblicazione}"/>
					</c:if>
					
				</gene:campoLista>
			</c:if>
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
	
	//Abilitazione alla gestione del codice html nei tooltip solo per il campo CIG e solo quando vi è
	//un elenco di codici CIG
	$(function() {
	  $('.tooltipCig').tooltip({
	    content: function(){
	      var element = $( this );
	      return element.attr('title')
	    }
	  });
	});
	
	
	// Visualizzazione del dettaglio
	function visualizzaGara(chiaveRiga){
		if (chiaveRiga.indexOf('$')>0){
			//gara a lotto unico si va su gare
			document.forms[0].keyParent.value=chiaveRiga;
			document.forms[0].entita.value = "GARE";
		} else {
			document.forms[0].entita.value = "TORN";
		}
		listaVisualizza();
	}

	function modificaGara(chiaveRiga){
		if (chiaveRiga.indexOf('$')>0){
			//gara a lotto unico si va su gare
			document.forms[0].keyParent.value=chiaveRiga;
			document.forms[0].entita.value = "GARE";
		} else {
			document.forms[0].entita.value = "TORN";
		}
		listaModifica();
	}
		
	function eliminaGara(){
		chiaveRiga = chiaveRiga.replace("V_GARE_", "");
		var href = "href=gare/commons/conferma-eliminazione.jsp&chiaveRiga=" + chiaveRiga + "&numeroPopUp=1";
		win = openPopUpCustom(href, "confermaEliminaGara", 500, 250, "no", "no");

		if(win!=null)
			win.focus();
	}
	
	function confermaDelete() {
		closePopUps();
		document.forms[0].entita.value = "TORN";
		document.forms[0].key.value = chiaveRiga;
		document.forms[0].metodo.value = "elimina";
		document.forms[0].submit();
	}

	function apriGestionePermessiGaraTelematica(codgar, genereGara, permessoModifica) {
		bloccaRichiesteServer();
		formVisualizzaPermessiUtentiStandard.permessimodificabili.value = permessoModifica;
		formVisualizzaPermessiUtentiStandard.action = "${pageContext.request.contextPath}/pg/VisualizzaPermessiUtentiGaraTelematica.do";
		formVisualizzaPermessiUtentiStandard.codgar.value = codgar;
		formVisualizzaPermessiUtentiStandard.genereGara.value = genereGara;
		formVisualizzaPermessiUtentiStandard.submit();
	}

	function apriGestionePermessiGaraTelematicaStandard(codgar, genereGara, permessoModifica) {
		bloccaRichiesteServer();
		formVisualizzaPermessiUtentiStandard.action = "${pageContext.request.contextPath}/pg/VisualizzaPermessiUtentiGaraTelematicaStandard.do";
		formVisualizzaPermessiUtentiStandard.permessimodificabili.value = permessoModifica;
		formVisualizzaPermessiUtentiStandard.codgar.value = codgar;
		formVisualizzaPermessiUtentiStandard.genereGara.value = genereGara;
		formVisualizzaPermessiUtentiStandard.submit();
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

	function copiaGara(codGar, tipoGara, isGaraLottoUnico,genereGara,cliv1){
		href = "href=gare/v_gare_torn/copia-gare-torn.jsp&key="+chiaveRiga+"&numeroPopUp=1&tipoGara="+tipoGara+"&genereGara="+genereGara;
		openPopUpCustom(href, "copiaGara", 600, 350, "no", "yes");
	}


	function archiviaGara(){
		chiaveRiga = chiaveRiga.replace("V_GARE_", "");
		href = "href=gare/v_gare_torn/v_gare_torn-popup-archivia-gara.jsp&key="+chiaveRiga;
		openPopUpCustom(href, "archiviaGara", 500, 350, "no", "yes");
	}

	
	function listaNuovaGara(){
		document.location.href = contextPath + "/pg/InitNuovaGara.do?" + csrfToken;
	}

	</gene:javaScript>
</gene:template>