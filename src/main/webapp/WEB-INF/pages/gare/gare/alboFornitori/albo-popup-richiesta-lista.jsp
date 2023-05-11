<%/*
   * Created on 02-10-2018
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>


<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<c:set var="modo" value="NUOVO" scope="request" />

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

<gene:template file="popup-message-template.jsp">
<gene:setString name="titoloMaschera" value="Seleziona categoria per albo fornitori"/>
	<gene:redefineInsert name="documentiAzioni" />
	<gene:redefineInsert name="corpo">
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
			<gene:archivio titolo="Categorie d'iscrizione"
			lista="gene/cais/lista-categorie-iscrizione-popup.jsp"
			scheda=""
			schedaPopUp=""
			campi="V_CAIS_TIT.CAISIM;V_CAIS_TIT.DESCAT;V_CAIS_TIT.TIPLAVG"
			functionId="default"
			parametriWhere="${parametriWhere}"
			chiave=""
			formName="formCategoriaPrevalenteGare"
			inseribile="false">
			<c:set var="categoriaUtilizzata" value="${gene:callFunction3('it.eldasoft.sil.pg.tags.funzioni.CheckCategoriaPresenteLottiFunction', pageContext, datiRiga.CATG_CATIGA, codgar)}" />
			<gene:campoScheda campo="CATIGA" title="Codice categoria" entita="CATG" where="CATG.CATIGA=CAIS.CAISIM" obbligatorio="true" defaultValue="${requestScope.initCatiga}" />		
			<gene:campoScheda campo="DESCAT" entita="CAIS" from="CATG" where="CATG.NGARA='${ngara}' and CATG.CATIGA=CAIS.CAISIM" modificabile="false" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.CATG.CATIGA")}' defaultValue="${requestScope.initDescat}" />
			<gene:campoScheda campo="TIPLAVG" entita="CAIS" visibile="false" defaultValue="${requestScope.initTiplavg}" />
		</gene:archivio>
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
			
		</gene:formScheda>
		
		
		
	</gene:redefineInsert>
	

		<gene:redefineInsert name="buttons">
			<input type="button" class="bottone-azione" id="confirm" value="Conferma" title="Conferma"	onclick="conferma();"/>
			<input type="button" class="bottone-azione"  id="cancel" value="Annulla"  title="Annulla" onclick="annulla();"/>&nbsp;&nbsp;
		</gene:redefineInsert>

<gene:javaScript>

	$("#qStatuslist").append($("<option/>", {value: "1", text: "solo fornitori qualificati" }));
	$("#qStatuslist").append($("<option/>", {value: "2", text: "fornitori con documenti scaduti" }));
	$('#qStatuslist option[value="1"').attr('selected','selected');
	

	function annulla(){
		window.close();
	}

	function conferma() {
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
	
	setTipoCategorie(getValue("TORN_TIPGEN"));
	
	function setTipoCategorie(tipoCategoria){
		if(tipoCategoria == "") tipoCategoria = "1";
		setTipoCategoriaPrevalentePerArchivio(tipoCategoria);
	}
	
	// Funzione per cambiare la condizione di where nell'apertura
	// dell'archivio delle categorie dell'appalto per la categoria prevalente
	function setTipoCategoriaPrevalentePerArchivio(tipoCategoria){
			if(getValue("CATG_CATIGA") == "" || getValue("CAIS_TIPLAVG") == ""){
				document.formCategoriaPrevalenteGare.archWhereLista.value = "V_CAIS_TIT.TIPLAVG=" + tipoCategoria + document.formCategoriaPrevalenteGare.archWhereLista.value;
				setValue("CAIS_TIPLAVG", "" + tipoCategoria);
			} else {
				document.formCategoriaPrevalenteGare.archWhereLista.value = "V_CAIS_TIT.TIPLAVG=" + getValue("CAIS_TIPLAVG") + document.formCategoriaPrevalenteGare.archWhereLista.value; 
			}
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
</form> 



</gene:template>