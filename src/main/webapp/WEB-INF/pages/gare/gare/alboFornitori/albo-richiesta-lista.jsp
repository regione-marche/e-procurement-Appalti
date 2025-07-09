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


<c:set var="modo" value="NUOVO" scope="request" />

<c:set var="integrazioneWSERP" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSERPFunction", pageContext)}' />
<c:set var="tipoWSERP" value='${requestScope.tipoWSERP}' />


<c:choose>
	<c:when test='${!empty param.codgar}'>
		<c:set var="codgar" value="${param.codgar}" />
	</c:when>
	<c:otherwise>
		<c:set var="codgar" value="${codgar}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${!empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${!empty param.numeroFaseAttiva}'>
		<c:set var="numeroFaseAttiva" value="${param.numeroFaseAttiva}" />
	</c:when>
	<c:otherwise>
		<c:set var="numeroFaseAttiva" value="${numeroFaseAttiva}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${!empty param.garaLottiConOffertaUnica}'>
		<c:set var="garaLottiConOffertaUnica" value="${param.garaLottiConOffertaUnica}" />
	</c:when>
	<c:otherwise>
		<c:set var="garaLottiConOffertaUnica" value="${garaLottiConOffertaUnica}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${tipoWSERP eq "RAIWAY"}'>
		<c:set var="campi" value="V_CAIS_TIT.CAISIM;V_CAIS_TIT.TIPLAVG" />
		<c:set var="campiObbligatori" value="false" />
	</c:when>
	<c:otherwise>
		<c:set var="campi" value="V_CAIS_TIT.CAISIM;V_CAIS_TIT.DESCAT;V_CAIS_TIT.TIPLAVG" />
		<c:set var="campiObbligatori" value="true" />
	</c:otherwise>
</c:choose>


<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="ALBO-ricerca">

	<gene:setString name="titoloMaschera" value="Seleziona categoria per albo fornitori"/>
	<gene:redefineInsert name="addHistory">
		<gene:historyAdd titolo="Ricerca albo fornitori" id="Ricerca albo fornitori" />
	</gene:redefineInsert>
	
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
	
	
	<gene:redefineInsert name="addToAzioni">
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:conferma();" title="Cerca" tabindex="1500">Cerca
				</a>
			</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:annulla();" title="Cerca" tabindex="1500">Annulla
				</a>
			</td>
		</tr>
	</gene:redefineInsert>
	
	<gene:redefineInsert name="documentiAzioni"></gene:redefineInsert>
	
  	<%// Creo la lista per gare e torn mediante la vista %>
		<table class="lista">
			<tr>
				<td>
	<gene:formScheda entita="TORN" gestisciProtezioni="true" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInitAlboFornitori">
	
		<gene:campoScheda campo="TIPGEN" defaultValue="${requestScope.initTipgen}" visibile="false"  />
		<gene:gruppoCampi idProtezioni="CATG">
			<gene:campoScheda entita="CATG" campo="NGARA" visibile="false" where="CATG.NGARA=TORN.CODGAR" defaultValue='${ngara}' />
			<gene:campoScheda>
				<td colspan="2">
				<br>
				Cliccando sul pulsante <b>Conferma</b> verranno cercati sull'Albo fornitori SAP gli operatori economici corrispondenti 
alla categoria sotto specificata (o al raggruppamento merceologico, distinto sulla base della descrizione pi&ugrave; sotto riportata).
La categoria (o il raggruppamento merceologico) viene impostata sulla base di quella indicata nei dati generali della procedura.
E' possibile cambiarla (ad esempio per effettuare ricerche su altre categorie affini) e/o ripetere l'operazione di ricerca e selezione pi&ugrave; volte.
L'estrazione e la selezione degli operatori economici resta responsabilit&agrave; dell'utente che dovr&agrave; garantire il rispetto delle norme.
				<br>
				<br>
				</td>
			</gene:campoScheda>
			<c:choose>
				<c:when test="${modoAperturaScheda ne 'VISUALIZZA' and (empty datiRiga.CATG_CATIGA or empty datiRiga.CAIS_TIPLAVG)}">
					<c:set var="parametriWhere" value="N:${datiRiga.TORN_TIPGEN}" />
				</c:when>
				<c:otherwise>
					<c:set var="parametriWhere" value="N:${datiRiga.CAIS_TIPLAVG}" />
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test='${tipoWSERP eq "RAIWAY"}'>
					<table class="dettaglio-notab">
					<tr>
					<gene:campoScheda campo="CODCAT" title="Categorie" speciale="true" visibile='true' modificabile="true" campoFittizio="true" definizione="T2000;0;;;;" value="${categorie}">
						<gene:popupCampo titolo="Seleziona le categorie" href="javascript:getcategorie()" />
					</gene:campoScheda>
					</tr>
					<tr>
					<gene:campoScheda campo="RAGIONESOCIALE" title="Ragione sociale" visibile='true' modificabile="true" campoFittizio="true" definizione="T2000;0;;;;" value="">
					</gene:campoScheda>
					</tr>
				</table>
				
				</c:when>
				<c:otherwise>
					<gene:archivio titolo="Categorie d'iscrizione"
						lista="gene/cais/lista-categorie-iscrizione-popup.jsp"
						scheda=""
						schedaPopUp=""
						campi="${campi}"
						functionId="default"
						parametriWhere="${parametriWhere}"
						chiave=""
						formName="formCategoriaPrevalenteGare"
						inseribile="false">
						<c:set var="categoriaUtilizzata" value="${gene:callFunction3('it.eldasoft.sil.pg.tags.funzioni.CheckCategoriaPresenteLottiFunction', pageContext, datiRiga.CATG_CATIGA, codgar)}" />
						<gene:campoScheda campo="CATIGA" title="Codice categoria" entita="CATG" where="CATG.CATIGA=CAIS.CAISIM" obbligatorio="${campiObbligatori}" defaultValue="${requestScope.initCatiga}" />
						<gene:campoScheda campo="DESCAT" entita="CAIS" from="CATG" where="CATG.NGARA='${ngara}' and CATG.CATIGA=CAIS.CAISIM" modificabile="false" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.CATG.CATIGA")}' defaultValue="${requestScope.initDescat}" />
						<gene:campoScheda campo="TIPLAVG" entita="CAIS" visibile="false" defaultValue="${requestScope.initTiplavg}" />
					</gene:archivio>
				</c:otherwise>
			</c:choose>
		
		</gene:gruppoCampi>
		

		<gene:campoScheda addTr="false" visibile="false">
						<tr>
							<td class="etichetta-dato">Estrai:</td>
							<td class="valore-dato">
								<select id="qStatuslist" name="qStatuslist"></select>
							</td>						
						</tr>
		</gene:campoScheda>

		
		
		
		<gene:fnJavaScriptScheda funzione='setTipoCategorie("#TORN_TIPGEN#")' elencocampi='TORN_TIPGEN' esegui="false"/>
			
		
		<gene:campoScheda>
			<td colspan="2" class="comandi-dettaglio">
				<INPUT id="bottonecerca" type="button" class="bottone-azione" value="Cerca" title="Cerca" onclick="javascript:conferma();">
				<INPUT id="bottoneannulla" type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
			</td>
		</gene:campoScheda>
	</gene:formScheda>
		
  </gene:redefineInsert>

	<gene:javaScript>
	
	$(document).ready(function() {
		$('#menulaterale table tbody tr:nth-child(2) td, #menulaterale table tbody tr:nth-child(3) td').hide();
	});


	function annulla(){
		historyVaiIndietroDi(1);
	}

	function conferma() {
		var tipoWSERP="${tipoWSERP}";
		if(tipoWSERP == "RAIWAY"){
			var categorie = $('#CODCAT').val();
			var ragSociale = $('#RAGIONESOCIALE').val();
			document.formRichiestaAlboFornitori.categoria.value = categorie;
			document.formRichiestaAlboFornitori.ragionesociale.value = ragSociale;
			document.formRichiestaAlboFornitori.descat.value = descat;
			document.formRichiestaAlboFornitori.qsl.value = qualificationStatusList;
			document.formRichiestaAlboFornitori.codgar.value = "${codgar}";
			document.formRichiestaAlboFornitori.ngara.value = "${ngara}";
			
			document.formRichiestaAlboFornitori.numeroFaseAttiva.value = "${numeroFaseAttiva}";
			document.formRichiestaAlboFornitori.garaLottiConOffertaUnica.value = "${garaLottiConOffertaUnica}";
			
			document.formRichiestaAlboFornitori.tipoWSERP.value = tipoWSERP;
			
			document.formRichiestaAlboFornitori.submit();
			bloccaRichiesteServer();
		} else {
			var cat = getValue("CATG_CATIGA");
			var descat = getValue("CAIS_DESCAT");
			var qualificationStatusList = $("#qStatuslist").val();
			if(cat!=null && cat!=''){
				document.formRichiestaAlboFornitori.categoria.value=cat;
				document.formRichiestaAlboFornitori.descat.value=descat;
				document.formRichiestaAlboFornitori.qsl.value=qualificationStatusList;
				document.formRichiestaAlboFornitori.codgar.value="${codgar}";
				document.formRichiestaAlboFornitori.ngara.value="${ngara}";
				
				document.formRichiestaAlboFornitori.numeroFaseAttiva.value="${numeroFaseAttiva}";
				document.formRichiestaAlboFornitori.garaLottiConOffertaUnica.value="${garaLottiConOffertaUnica}";
				document.formRichiestaAlboFornitori.submit();
				bloccaRichiesteServer();
			}else{
				alert('Specificare la categoria!');
			}
		}
	}
	
	setTipoCategorie(getValue("TORN_TIPGEN"));
	
	function setTipoCategorie(tipoCategoria){
		if(tipoCategoria == "") tipoCategoria = "1";
		setTipoCategoriaPrevalentePerArchivio(tipoCategoria);
	}
	
	// Funzione per cambiare la condizione di where nell'apertura
	// dell'archivio delle categorie dell'appalto per la categoria prevalente
	function setTipoCategoriaPrevalentePerArchivio(tipoCategoria){
		var tipoWSERP="${tipoWSERP}";
		if(tipoWSERP != "RAIWAY"){
			if(getValue("CATG_CATIGA") == "" || getValue("CAIS_TIPLAVG") == ""){
				document.formCategoriaPrevalenteGare.archWhereLista.value = "V_CAIS_TIT.TIPLAVG=" + tipoCategoria + document.formCategoriaPrevalenteGare.archWhereLista.value;
				setValue("CAIS_TIPLAVG", "" + tipoCategoria);
			} else {
				document.formCategoriaPrevalenteGare.archWhereLista.value = "V_CAIS_TIT.TIPLAVG=" + getValue("CAIS_TIPLAVG") + document.formCategoriaPrevalenteGare.archWhereLista.value; 
			}
		}
	}
	
	function getcategorie(){
		var href = "href=gare/commons/popup-lista-categorie.jsp";
		openPopUpCustom(href, "getcategoria", 850, 600, "yes", "yes");
	}
	</gene:javaScript>
	
<form name="formRichiestaAlboFornitori" action="${pageContext.request.contextPath}/pg/GetWSERPAlboFornitori.do" method="post">
	<input type="hidden" name="categoria" id="categoria" value="" />
	<input type="hidden" name="descat" id="descat" value="" />
	<input type="hidden" name="codgar" id="codgar" value="" />
	<input type="hidden" name="ngara" id="ngara" value="" />
	<input type="hidden" name="qsl" id="qsl" value="" />
	<input type="hidden" name="numeroFaseAttiva" id="numeroFaseAttiva" value="" />
	<input type="hidden" name="garaLottiConOffertaUnica" id="garaLottiConOffertaUnica" value="" />
	<input type="hidden" name="tipoWSERP" id="tipoWSERP" value="" />
	<input type="hidden" name="ragionesociale" id="ragionesociale" value="" />
	<input type="hidden" name="inPopup" id="inPopup" value="0" />
</form> 

</gene:template>