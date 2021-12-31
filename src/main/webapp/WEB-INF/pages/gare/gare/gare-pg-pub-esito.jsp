<%
/*
 * Created on: 28/10/2008
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

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />
<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.hrefProtocollo.js"></script>
</gene:redefineInsert>

<c:set var="tipologiaGara" value='${param.tipologiaGara}' />

<%/*La pagina può essere richiamata anche da TORN per le gare divise a lotti con offerta unica*/ %>
<c:choose>
	<c:when test='${tipologiaGara eq "3"}'>
		<%/*gara divisa a lotti con offerta unica*/ %>
		<c:set var="whereGare" value='TORN.CODGAR = GARE.NGARA'/>
		<c:set var="entita" value='TORN'/>
		<c:set var="numeroGara" value='${gene:getValCampo(key, "CODGAR")}'/>
		<c:set var="chiaveGara" value='GARE.NGARA=T:${gene:getValCampo(key, "CODGAR")}'/>
		<c:set var="condizioneModificaSezioneProfilo" value='${gene:checkProt(pageContext,"SEZ.MOD.GARE.TORN-OFFUNICA-scheda.PUBBLICITA.PUBESITO") }'/>
	</c:when>
	<c:when test='${tipologiaGara eq "1"}'>
		<%/*gara divisa a lotti con offerta unica*/ %>
		<c:set var="whereGare" value='TORN.CODGAR = GARE.CODGAR1'/>
		<c:set var="entita" value='TORN'/>
		<c:set var="numeroGara" value='${gene:getValCampo(key, "CODGAR")}'/>
		<c:set var="chiaveGara" value='GARE.NGARA=T:${gene:getValCampo(key, "CODGAR")}'/>
		<c:set var="condizioneModificaSezioneProfilo" value='${gene:checkProt(pageContext,"SEZ.MOD.GARE.TORN-scheda.PUBBLICITA.PUBESITO") }'/>
	</c:when>
	<c:otherwise>
		<c:set var="whereGare" value=''/>
		<c:set var="entita" value='GARE'/>
		<c:set var="numeroGara" value='${gene:getValCampo(key, "NGARA")}'/>
		<c:set var="chiaveGara" value='${key}'/>
		<c:set var="condizioneModificaSezioneProfilo" value='${gene:checkProt(pageContext,"SEZ.MOD.GARE.GARE-scheda.PUBBLICITA.PUBESITO") }'/>
	</c:otherwise>
</c:choose>

	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>

	<c:choose>
		<c:when test='${tipologiaGara eq "1"}'>
			<gene:campoScheda campo="NGARA" entita="GARE" campoFittizio="true" value="" visibile="false" />
		</c:when>
		<c:otherwise>
			<gene:campoScheda campo="NGARA" entita="GARE" where="${whereGare}" visibile="false" />
		</c:otherwise>
	</c:choose>
	<gene:campoScheda campo="CODGAR1" entita="GARE" where="${whereGare}" visibile="false" />
	<c:if test='${tipologiaGara eq "3" or tipologiaGara eq "1"}'>
		<gene:campoScheda campo="CODGAR" entita="TORN"  visibile="false" />
		<input type="hidden" name="LOTTO_OFFERTAUNICA" id="LOTTO_OFFERTAUNICA" value="SI"/>
		<gene:campoScheda campo="VALTEC" visibile="false" />
	</c:if>
	<c:if test='${tipologiaGara ne "3" and tipologiaGara ne "1"}'>
		<gene:campoScheda campo="VALTEC" entita="GARE1" where="GARE1.NGARA = GARE.NGARA" visibile="false" />
	</c:if>
	
	
	<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GestionePubblicazioniEsitoFunction", pageContext,numeroGara,tipologiaGara)}' />
	
	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='PUBG'/>
		<jsp:param name="chiave" value='${numeroGara}'/>
		<jsp:param name="nomeAttributoLista" value='pubblicazioniEsito' />
		<jsp:param name="idProtezioni" value="PUBESITO" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/pubg/pubblicazione-esito.jsp"/>
		<jsp:param name="arrayCampi" value="'PUBG_NGARA_', 'PUBG_NPUBG_', 'PUBG_TIPPUBG_', 'PUBG_TESPUBG_', 'PUBG_DINPUBG_', 'PUBG_DFIPUBG_', 'PUBG_IMPPUB_', 'PUBG_DINVPUBG_', 'PUBG_NPRPUB_'"/>		
		<jsp:param name="titoloSezione" value="Pubblicazione" />
		<jsp:param name="titoloNuovaSezione" value="Nuova pubblicazione" />
		<jsp:param name="descEntitaVociLink" value="pubblicazione" />
		<jsp:param name="msgRaggiuntoMax" value="e pubblicazioni"/>
		<jsp:param name="funzEliminazione" value="delPubblicazioniEsito"/>
		<jsp:param name="tipologiaGara" value="tipologiaGara"/>
		<jsp:param name="usaContatoreLista" value="true" />
	</jsp:include>

	

	<gene:campoScheda>	
		<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
			<jsp:param name="entita" value="V_GARE_TORN"/>
			<jsp:param name="inputFiltro" value="CODGAR=T:${datiRiga.GARE_CODGAR1}"/>
			<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
		</jsp:include>
		<td class="comandi-dettaglio" colSpan="2">
			<gene:insert name="addPulsanti"/>
			<c:choose>
			<c:when test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
				<gene:insert name="pulsanteSalva">
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma()">
				</gene:insert>
				<gene:insert name="pulsanteAnnulla">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
				</gene:insert>
		
			</c:when>
			<c:otherwise>
				<gene:insert name="pulsanteModifica">
					<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") and condizioneModificaSezioneProfilo}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
					</c:if>
				</gene:insert>
				<gene:insert name="pulsanteNuovo">
					<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:schedaNuovo()" id="btnNuovo">
					</c:if>
				</gene:insert>
			</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</gene:campoScheda>

	<c:if test='${!condizioneModificaSezioneProfilo}'>
		<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
	</c:if>
	
	<gene:redefineInsert name="addToAzioni" >
		<c:if test='${autorizzatoModifiche ne "2" and modoAperturaScheda eq "VISUALIZZA" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.PUBBLICITA.InsertPredefiniti")
					and (tipologiaGara ne "10" and tipologiaGara ne "20")}'>
			<tr>
				<td class="vocemenulaterale" >
					<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:apriPopupInsertPredefiniti()" title="Inserisci pubblicazioni predefinite" tabindex="1505">
					</c:if>
						Inserisci pubblicaz. predefinite
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
		</c:if>			

	</gene:redefineInsert>

<gene:javaScript>
function apriPopupInsertPredefiniti() {
	var href = "href=gare/commons/conferma-ins-pubbli-predefinite.jsp?codgar="+getValue("GARE_CODGAR1")+"&ngara="+getValue("GARE_NGARA")+"&bando=0&genere=${tipologiaGara}";
	openPopUpCustom(href, "insPubblicazioniPredefinite", 600, 350, "no", "yes");
}


<c:if test='${!(modo eq "VISUALIZZA")}'>
	var schedaConferma_Default = schedaConferma;
 	
 	function schedaConferma_Custom(){
 		var continua = true;
		
		//Controllo sull'unicità del tipo esito = 14
		for(var i=1; i < maxIdPUBESITOVisualizzabile ; i++){
			var tipo = getValue("PUBG_TIPPUBG_" + i);
			if(document.getElementById("rowtitoloPUBESITO_" + i) && (document.getElementById("rowtitoloPUBESITO_" + i).style.display != "none")){		
				if(tipo != null && tipo != "" && tipo==14){
				
					for(var jo=(i+1); jo <= maxIdPUBESITOVisualizzabile; jo++){
						if(document.getElementById("rowtitoloPUBESITO_" + jo).style.display != "none" && tipo == getValue("PUBG_TIPPUBG_" + jo)){
							continua = false;
							outMsg("Sono state definite pi&ugrave; pubblicazioni di tipo 'Amministrazione trasparente'", "ERR");
							onOffMsg();
					  	}
				  	}
			  	}
		   }
	  	}
		if(continua){
		  schedaConferma_Default();
		}
 	
 	}
 	
 	schedaConferma =   schedaConferma_Custom;
</c:if>

	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.AutovieArchiflow")}'>
		addHrefs();
	</c:if>

</gene:javaScript>
