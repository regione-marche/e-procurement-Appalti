<%/*
       * Created on 08-ott-2008
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
<% // i filtri applicati sono il filtro sul livello utente ed il filtro sul codice profilo %>
<c:set var="filtroLivelloUtente"
	value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_PROFILO")}' />
<c:set var="filtro" value="${filtroLivelloUtente}" />
<c:set var="IsProfiloRDOFunction" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsProfiloRDOFunction", pageContext)}' />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" /> 
<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou89#")}' >
	<c:set var="amministratore" value="true"/>
</c:if>
<c:set var="uffintAbilitata" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.associazioneUffintAbilitata")}'/>



<gene:template file="ricerca-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="V_GARE_PROFILO-trova">
	
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.titoli.js"></script>
	</gene:redefineInsert>
	
	<c:set var="idUtente" value="${sessionScope.profiloUtente.id}" />
	
	<c:if test="${uffintAbilitata eq '1' and !amministratore}">
		<c:if test="${!empty filtro}">
			<c:set var="filtro" value="${filtro } AND "/>
		</c:if>
		<c:set var="filtro" value="${filtro} CENINT IN (select codein from usr_ein where syscon = ${idUtente} ) "/>
		
	</c:if>
	
	<c:if test="${!empty filtro}">
		<c:set var="filtro" value="${filtro } AND "/>
	</c:if>
	<c:set var="filtro" value="${filtro} V_GARE_PROFILO.CODPROFILO IN (SELECT COD_PROFILO FROM W_ACCPRO WHERE ID_ACCOUNT = ${idUtente} )" />
	
	<gene:redefineInsert name="trovaCreaNuovo"></gene:redefineInsert>
	<gene:setString name="titoloMaschera" value="Ricerca gare e avvisi"/>
	

	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
  	<% // Creo la form di trova con i campi dell'entità peri %>
  	<gene:formTrova entita="V_GARE_PROFILO" filtro="${filtro}" gestisciProtezioni="true" >
		
		<%-- Variabile che serve da indice per definire l'id dei campi fittizi, visto che nella pagina di trova tutti i campi in automatico hanno un nome del tipo Campo + progressivo 
			IMPORTANTE: aggiungendo campi nella maschera, incrementare la variabile 
		--%>
		<c:set var="numCampi" value="19"/>
		
		<gene:gruppoCampi idProtezioni="GEN">
			<tr><td colspan="3"><b>Dati generali</b></td></tr>
			<gene:campoTrova campo="CODICE" title="Codice gara o avviso"/> 
			<gene:campoTrova campo="OGGETTO" title="Oggetto gara o avviso"/> 
			<gene:campoTrova campo="NOMTEC" entita="TECNI"  from="TORN" where="torn.codgar = v_gare_profilo.codgar and tecni.codtec=torn.codrup" title="Resonsabile unico di procedimento"/> 
			<gene:campoTrova campo="GENERE"/> 
			<gene:campoTrova campo="NUMAVCP" entita="TORN" where="torn.codgar = v_gare_profilo.codgar"/>
			<gene:campoTrova campo="TIPGEN" entita="TORN" where="torn.codgar = v_gare_profilo.codgar"/> 
			<gene:campoTrova campo="TIPGAR"/> 
			<gene:campoTrova entita="TORN" campo="ACCQUA" where="torn.codgar = v_gare_profilo.codgar" title="Accordo quadro?"/> 
			<c:if test='${fn:contains(listaOpzioniDisponibili, "OP114#") && fn:contains(listaOpzioniDisponibili, "OP132#")}'>			
				<gene:campoTrova campo="GARTEL" entita="TORN" where="torn.codgar = v_gare_profilo.codgar" /> 
				<c:set var="numCampi" value="${numCampi + 1}"/>
			</c:if>
			<gene:campoTrova campo="STATO" entita="V_GARE_STATOESITO" where="V_GARE_STATOESITO.CODICE=v_gare_profilo.CODICE" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoStato"/> 
			<gene:campoTrova campo="ESITO" entita="V_GARE_STATOESITO" where="V_GARE_STATOESITO.CODICE=v_gare_profilo.CODICE" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoEsito"/> 
			<c:set var="idCampoFittizioAvv" value="${numCampi + 1}" />
				
			<tr id="rowCampo${idCampoFittizioAvv}">
				<td class="etichetta-dato">Avviso?</td>
				<td class="operatore-trova">
					<input type="hidden" name="Campo${idCampoFittizioAvv}_where" value="" />
					<input type="hidden" name="Campo${idCampoFittizioAvv}_computed" value="false" />
					<input type="hidden" name="Campo${idCampoFittizioAvv}_from" value="" />
					<input type="hidden" name="Campo${idCampoFittizioAvv}_conf" value="=" />
					<input type="hidden" name="defCampo${idCampoFittizioAvv}" value="" />
					&nbsp;
				</td>
				<td class="valore-dato-trova">
					<select id="Campo${idCampoFittizioAvv}" name="Campo${idCampoFittizioAvv}" title="Avviso?" onchange="javascript:valorizzaFiltro(this.value);"> 
						<option value=""></option>
						<option value="1" >Si</option>
						<option value="2" >No</option>
						
					</select>
					
				</td>
			</tr>
			<gene:campoTrova campo="ISARCHI" title="Gara o avviso archiviato?" defaultValue="2"/>
		</gene:gruppoCampi>
		<gene:gruppoCampi idProtezioni="GARERDA" >
			<tr><td colspan="3"><b>Dati della richiesta di acquisto </b></td></tr>
			<gene:campoTrova campo="NUMRDA" entita="GARERDA" where="garerda.codgar = v_gare_profilo.codgar" title="Numero richiesta di acquisto" />
			<gene:campoTrova campo="DATRIL" entita="GARERDA" where="garerda.codgar = v_gare_profilo.codgar" title="Data rilascio richiesta di acquisto"/> 
		</gene:gruppoCampi>
		<gene:gruppoCampi idProtezioni="GARE" >
			<tr><td colspan="3"><b>Dati della gara a lotto unico o del lotto di gara</b></td></tr>
			<gene:campoTrova campo="CODCIG" entita="GARE" where="gare.codgar1 = v_gare_profilo.codgar" />
			<gene:campoTrova campo="NOT_GAR" entita="GARE" where="gare.codgar1 = v_gare_profilo.codgar" />
			<gene:campoTrova campo="IMPAPP" entita="GARE" where="gare.codgar1 = v_gare_profilo.codgar" />
			<gene:campoTrova campo="CUPPRG" entita="GARE" where="gare.codgar1 = v_gare_profilo.codgar" />
			<gene:campoTrova entita="GARE" campo="CRITLICG" where="gare.codgar1 = v_gare_profilo.codgar" />
			<gene:campoTrova campo="NREPAT" entita="GARE" where="gare.codgar1 = v_gare_profilo.codgar" title="Num.repertorio atto contrattuale"/>
			<gene:campoTrova campo="DATTOA" entita="GARE" where="gare.codgar1 = v_gare_profilo.codgar" title="Data atto contrattuale"/>
		</gene:gruppoCampi>

    </gene:formTrova>    
  </gene:redefineInsert>
  
  <gene:javaScript>
  
  	//Importante, incrementare la variabile che contiene il numero di campi fittizzi inseriti nella pagina, per portere avere i valori in sessione
  	var numCampiFittizi = 1;
  	var numCampiReali = parseInt(document.forms[0].campiCount.value);
  	document.forms[0].campiCount.value = numCampiReali +  numCampiFittizi + 2;
  	function trovaCreaNuovaGara(){
			document.location.href = contextPath + "/pg/InitNuovaGara.do?" + csrfToken;
  	}
	
	var trovaEsegui_Default = trovaEsegui;
	
	function trovaEsegui_Custom(){
		document.forms[0].jspPathTo.value="gare/v_gare_profilo/v_gare_profilo-lista.jsp";
		trovaEsegui_Default();
	}
	
	trovaEsegui = trovaEsegui_Custom;
	
	function valorizzaFiltro(valore) {
		var msgSi = "v_gare_profilo.genere = 11";
		var msgNo = "v_gare_profilo.genere != 11";
		costruzioneFiltro(msgSi,msgNo, valore);
						
	}
			
	function costruzioneFiltro(msgExists, msgNotExists,valore){
		var filtroOld = document.forms[0].filtro.value;
					
		if(filtroOld!=null && filtroOld!="" && (filtroOld.indexOf(msgNotExists)>-1 || filtroOld.indexOf(msgExists)>-1)){
			if (valore == 1) {
				filtroOld = filtroOld.replace(msgNotExists,msgExists);
			}else if(valore == 2){
				filtroOld = filtroOld.replace(msgExists,msgNotExists);
			}else{
				filtroOld = filtroOld.replace(" and " + msgNotExists,"");
				filtroOld = filtroOld.replace(msgNotExists,"");
				filtroOld = filtroOld.replace(" and " + msgExists,"");
				filtroOld = filtroOld.replace(msgExists,"");
			}
			document.forms[0].filtro.value = filtroOld;
		}else{
			if(filtroOld!=null && filtroOld!="")
				filtroOld += " and ";
			else
				filtroOld += "";
				
			if (valore == 1) {
				document.forms[0].filtro.value = filtroOld + msgExists;	
			} else if(valore == 2) {
				document.forms[0].filtro.value = filtroOld + msgNotExists;	
			}		
		}
		document.forms[0].filtro.value = document.forms[0].filtro.value.trim();
              if(document.forms[0].filtro.value.startsWith("and")){
              	document.forms[0].filtro.value = document.forms[0].filtro.value.substring(4);
              }
	}
	
	/* Funzione che ricava il valore in sessione per i campi fittizi a partire dall'indice del campo.
			*  In sessione i campi sono del tipo Campo + progressivo.
			*  Se l'indice del campo fittizio dovessere essere maggiore di 27 aggiornare la funzione!!! 
			*/
			function getValoreDaIndiceCampo(indice){
				var valore;
				switch (parseInt(indice)) {
					case 10:
					    valore ="${sessionScope.trovaV_GARE_PROFILO.Campo10}";
					    break;
					case 11:
					    valore ="${sessionScope.trovaV_GARE_PROFILO.Campo11}";
					    break;
					case 12:
					    valore ="${sessionScope.trovaV_GARE_PROFILO.Campo12}";
					    break;
					case 13:
					    valore ="${sessionScope.trovaV_GARE_PROFILO.Campo13}";
					    break;
					case 14:
					    valore ="${sessionScope.trovaV_GARE_PROFILO.Campo14}";
					    break;
					case 15:
					    valore ="${sessionScope.trovaV_GARE_PROFILO.Campo15}";
					    break;
					case 16:
					    valore ="${sessionScope.trovaV_GARE_PROFILO.Campo16}";
					    break;
					case 17:
					    valore ="${sessionScope.trovaV_GARE_PROFILO.Campo17}";
					    break;
					case 18:
					    valore ="${sessionScope.trovaV_GARE_PROFILO.Campo18}";
					    break;
					case 19:
					    valore ="${sessionScope.trovaV_GARE_PROFILO.Campo19}";
					    break;
					case 20:
					    valore ="${sessionScope.trovaV_GARE_PROFILO.Campo20}";
					    break;
					case 21:
					    valore ="${sessionScope.trovaV_GARE_PROFILO.Campo21}";
					    break;
					case 22:
					    valore ="${sessionScope.trovaV_GARE_PROFILO.Campo22}";
					    break;
					case 23:
					    valore ="${sessionScope.trovaV_GARE_PROFILO.Campo23}";
					    break;
					case 24:
					    valore ="${sessionScope.trovaV_GARE_PROFILO.Campo24}";
					    break;
					case 25:
					    valore ="${sessionScope.trovaV_GARE_PROFILO.Campo25}";
					    break;
					case 26:
					    valore ="${sessionScope.trovaV_GARE_PROFILO.Campo26}";
					    break;
					case 27:
					    valore ="${sessionScope.trovaV_GARE_PROFILO.Campo27}";
					    break;
				}
				return valore;
			}
			
			
	initVisualizzaCampoFittizioAvviso();
		
	function initVisualizzaCampoFittizioAvviso(){
		var valore = getValoreDaIndiceCampo(${idCampoFittizioAvv});
						
		
		var filtroOld = document.forms[0].filtro.value;
		if(filtroOld!=null && filtroOld!="")
			filtroOld += " and ";
		else
			filtroOld += "";
		
		if (valore == 1) {
			document.getElementById('Campo${idCampoFittizioAvv}').selectedIndex = 1;
			document.forms[0].filtro.value = filtroOld + "v_gare_profilo.genere = 11";	
		} else if(valore == 2) {
			document.getElementById('Campo${idCampoFittizioAvv}').selectedIndex = 2;
			document.forms[0].filtro.value = filtroOld + "v_gare_profilo.genere != 11";
		}
		
	}

  </gene:javaScript>
</gene:template>
