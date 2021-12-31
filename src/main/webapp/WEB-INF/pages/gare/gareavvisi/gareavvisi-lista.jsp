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

<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />
<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "GAREAVVISI")}' />

<c:set var="filtroUffint" value=""/> 
<c:if test="${! empty sessionScope.uffint}">
	<c:set var="filtroUffint" value=" exists (select codgar from torn where codgar = gareavvisi.codgar and CENINT = '${sessionScope.uffint}')"/>
</c:if>

<c:set var="isPersonalizzazioneGenovaAttiva" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsPersonalizzazioneGenovaAttivaFunction",pageContext)}' scope="request" />

<c:set var="esistonoGarePubblicate"
	value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsistonoGarePubblicateFunction", pageContext,"GAREAVVISI")}' />

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="GAREAVVISI-lista" >
	<gene:setString name="titoloMaschera" value="Lista avvisi"/>
	<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GARE.GAREAVVISI-scheda")}'/>
	
	<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
	<gene:redefineInsert name="pulsanteListaEliminaSelezione"></gene:redefineInsert>
	
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
		
  	<%// Creo la lista per gare e torn mediante la vista %>
		<table class="lista">
			<tr>
				<td>
  	<gene:formLista entita="GAREAVVISI" gestisciProtezioni="true" pagesize="20" tableclass="datilista" sortColumn="-2" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreAvvisi"
  		where="${filtroUffint}">
  	

  	<c:set var='visualizzaPopUp' value='${visualizzaLink || gene:checkProtFunz(pageContext, "MOD","MOD") || gene:checkProtFunz(pageContext, "DEL","DEL")}'/>
  	
			<gene:campoLista title="Opzioni" width="50">
				<c:if test='${visualizzaPopUp}'>
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
						<c:if test='${gene:checkProtObj(pageContext, "MASC.VIS", "GARE.GAREAVVISI-scheda")}' >
							<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.visualizza" title="Visualizza avviso"/>
						</c:if>
						<c:if test='${(datiRiga.G_PERMESSI_AUTORI ne "2") && gene:checkProtObj(pageContext, "MASC.VIS", "GARE.GAREAVVISI-scheda") and gene:checkProtFunz(pageContext, "MOD", "MOD")}' >
							<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.modifica" title="Modifica avviso" />
						</c:if>
						<c:if test='${(datiRiga.G_PERMESSI_AUTORI ne "2") && gene:checkProtFunz(pageContext, "DEL", "DEL")}' >
							<gene:PopUpItem title="Elimina avviso" href="eliminaAvviso()" />
						</c:if>
						<c:if test='${gene:checkProtFunz(pageContext, "ALT","GAREAVVISI.Condividi-avviso")}'> 
							<c:choose>
								<c:when test='${isPersonalizzazioneGenovaAttiva eq "1"}'>
									<gene:PopUpItem title="Condividi e proteggi avviso" href="javascript:apriGestionePermessi('${datiRiga.GAREAVVISI_CODGAR}',11,${datiRiga.G_PERMESSI_PROPRI eq '1' or abilitazioneGare eq 'A'})"/>
								</c:when>
								<c:otherwise>
									<gene:PopUpItem title="Condividi e proteggi avviso" href="javascript:apriGestionePermessiStandard('${datiRiga.GAREAVVISI_CODGAR}',11,${datiRiga.G_PERMESSI_PROPRI eq '1' or abilitazioneGare eq 'A'})"/>
								</c:otherwise>
							</c:choose>
						</c:if>
						<c:if test='${gene:checkProtFunz(pageContext, "ALT","Archivia-avviso")}'>
							<c:if test='${(datiRiga.G_PERMESSI_AUTORI ne "2") && (datiRiga.GAREAVVISI_ISARCHI ne "1") && gene:checkProtFunz(pageContext, "MOD","MOD")}' >
								<gene:PopUpItem title="Archivia avviso" href="archiviaGara()" />
							</c:if>
						</c:if>
					</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<% // Campi veri e propri %>
			
			<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
			
			<gene:campoLista campo="NGARA" headerClass="sortable" href="${gene:if(visualizzaLink, link, '')}"/>
			<gene:campoLista campo="TIPOAVV"  headerClass="sortable"  />
			<gene:campoLista campo="TIPOAPP" headerClass="sortable" />
			<gene:campoLista campo="OGGETTO" headerClass="sortable" />
			<gene:campoLista campo="DATSCA" />
			<gene:campoLista campo="CODGAR" visibile="false"/>
			
			<c:choose>
				<c:when test="${!empty (filtroLivelloUtente)}">
					<gene:campoLista campo="AUTORI" entita="G_PERMESSI" where="G_PERMESSI.CODGAR=GAREAVVISI.CODGAR AND G_PERMESSI.SYSCON=${profiloUtente.id}" visibile="false"/>
					<gene:campoLista campo="PROPRI" entita="G_PERMESSI" where="G_PERMESSI.CODGAR=GAREAVVISI.CODGAR AND G_PERMESSI.SYSCON=${profiloUtente.id}" visibile="false"/>
				</c:when>
				<c:otherwise>
					<gene:campoLista campo="AUTORI" entita="G_PERMESSI" campoFittizio="true" value="1" visibile="false"/>
					<gene:campoLista campo="PROPRI" entita="G_PERMESSI" campoFittizio="true" value="1" visibile="false"/>
				</c:otherwise>
			</c:choose>
			
			<c:if test="${esistonoGarePubblicate == 'SI'}">
				<c:set var="garaPubblicata" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GaraPubblicataSuPortaleFunction",  pageContext, datiRiga.GAREAVVISI_CODGAR )}'/>
				<gene:campoLista title="&nbsp;" width="20" >
					<c:if test="${garaPubblicata == 'SI'}">
						<img width="16" height="16" title="Avviso pubblicato su portale" alt="Avviso pubblicato su portale" src="${pageContext.request.contextPath}/img/ditta_acquisita.png"/>
					</c:if>
					
				</gene:campoLista>
			</c:if>
		</gene:formLista>
				</td>
			</tr>
			<tr>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" />
			</tr>
		</table>
		
		<form name="formVisualizzaPermessiUtenti" action="${pageContext.request.contextPath}/pg/VisualizzaPermessiUtentiAvviso.do" method="post">
			<input type="hidden" name="metodo" id="metodo" value="apri" />
			<input type="hidden" name="codgar" id="codgar" value="" />
			<input type="hidden" name="genereGara" id="genereGara" value="" />
			<input type="hidden" name="permessimodificabili" id="permessimodificabili" value="" />
			<input type="hidden" name="codein" id="codein" value="${sessionScope.uffint}" />
		</form>
		
		<form name="formVisualizzaPermessiUtentiStandard" action="${pageContext.request.contextPath}/pg/VisualizzaPermessiUtentiAvvisoStandard.do" method="post">
			<input type="hidden" name="metodo" id="metodo" value="apri" />
			<input type="hidden" name="codgar" id="codgar" value="" />
			<input type="hidden" name="genereGara" id="genereGara" value="" />
			<input type="hidden" name="permessimodificabili" id="permessimodificabili" value="" />
			<input type="hidden" name="codein" id="codein" value="${sessionScope.uffint}" />
		</form>
		
  </gene:redefineInsert>

	<gene:javaScript>

	function apriGestionePermessi(codgar, genereGara, permessoModifica) {
		bloccaRichiesteServer();
		formVisualizzaPermessiUtenti.codgar.value = codgar;
		formVisualizzaPermessiUtentiStandard.genereGara.value = genereGara;
		formVisualizzaPermessiUtenti.permessimodificabili.value = permessoModifica;
		formVisualizzaPermessiUtenti.submit();
	}
	
	var listaVisualizzaOld = listaVisualizza;
	function listaVisualizzaCustom(){
		var input = document.createElement("input");
		input.setAttribute("type", "hidden");
		input.setAttribute("name", "log");
		input.setAttribute("value", true);
		document.forms[0].appendChild(input);
		console.log(document.forms[0]);
		listaVisualizzaOld();
	}
	listaVisualizza = listaVisualizzaCustom;	

	function apriGestionePermessiStandard(codgar, genereGara, permessoModifica) {
		bloccaRichiesteServer();
		formVisualizzaPermessiUtentiStandard.codgar.value = codgar;
		formVisualizzaPermessiUtentiStandard.genereGara.value = genereGara;
		formVisualizzaPermessiUtentiStandard.permessimodificabili.value = permessoModifica;
		formVisualizzaPermessiUtentiStandard.submit();
	}

	function archiviaGara(){
		href = "href=gare/v_gare_torn/v_gare_torn-popup-archivia-gara.jsp&key="+chiaveRiga;
		openPopUpCustom(href, "archiviaAvviso", 500, 350, "no", "yes");
	}


	function eliminaAvviso(){
		var href = "href=gare/commons/conferma-eliminazione.jsp&chiaveRiga=" + chiaveRiga + "&genere=11&numeroPopUp=1";
		win = openPopUpCustom(href, "confermaEliminaAvviso", 500, 200, "no", "no");

		if(win!=null)
			win.focus();
	}
	
	function confermaDelete(){
		closePopUps();
		document.forms[0].key.value = chiaveRiga;
		document.forms[0].metodo.value = "elimina";
		document.forms[0].submit();
	}
	
	</gene:javaScript>
</gene:template>