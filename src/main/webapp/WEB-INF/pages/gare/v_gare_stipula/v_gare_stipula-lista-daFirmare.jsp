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

<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>

<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />
<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_STIPULA")}' />
<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.PulisciSessioneFiltriFunction" parametro=""/>

<c:set var="filtroUffint" value=""/> 
<c:if test="${! empty sessionScope.uffint}">
	<c:set var="filtroUffint" value=" CENINT = '${sessionScope.uffint}'"/>
</c:if>


<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="V_GARE_STIPULA-lista" >

	<gene:setString name="titoloMaschera" value="Lista stipule contratti per richiesta di approvazione/firma"/>
	<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GARE.G1STIPULA-scheda")}'/>
	<c:set var="idStipula" value=''/>
	<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
	<gene:redefineInsert name="pulsanteListaEliminaSelezione"></gene:redefineInsert>

	
	
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
	
			
  	<%// Creo la lista per gare e torn mediante la vista %>
		<table class="lista">
			<tr>
				<td>
  	<gene:formLista entita="V_GARE_STIPULA" gestisciProtezioni="true" pagesize="20" tableclass="datilista" sortColumn="-3" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreG1STIPULA">
		<gene:redefineInsert name="listaNuovo" />
		<gene:redefineInsert name="addToAzioni" >
			<c:if test='${datiRiga.rowCount > 0 }'>
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:checkNumeroStipule(0,0,'PG','','', '${pageContext.request.contextPath}','','');" title="Firma tutti i documenti con richiesta di firma" tabindex="1510">
							Firma documenti
						</a>
					</td>
				</tr>
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:inoltraEfirmaMassiva(0,0,'PG','','', '${pageContext.request.contextPath}','','');" title="Firma e Inoltra tutti i documenti con richiesta di firma" tabindex="1510">
							Firma e inoltra documenti
						</a>
					</td>
				</tr>
			</c:if>
		</gene:redefineInsert>
			<c:set var="numeroDocumentiDaFirmare" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNumDocumentiDaFirmareFunction", pageContext, datiRiga.V_GARE_STIPULA_ID)}'/>
			<gene:campoLista campo="ID" visibile="false" />

			<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
				<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
					<gene:PopUpItem title="Visualizza stipula" href="javascript:listaVisualizzaStipula('${chiaveRigaJava}')" />
					<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") and (numeroDocumentiDaFirmare ne "0")}'>
						<input type="checkbox" name="keys" value="${datiRiga.V_GARE_STIPULA_ID}"  />
						<input type="hidden" name="numDocs" value="${numeroDocumentiDaFirmare}"  />
						<input type="hidden" name="numDocsFormato" value="${conteggioFormato}"  />
					</c:if>
				</gene:PopUp>
			</gene:campoLista>
			<% // Campi veri e propri %>
			<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizzaStipula('${chiaveRigaJava}');" />
			
			<gene:campoLista campo="CODSTIPULA" title="Codice stipula" href="${gene:if(visualizzaLink, link, '')}" />
			<gene:campoLista campo="NGARA" title="Cod.gara o lotto" />
			<gene:campoLista campo="CODCIG" title="Codice CIG" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoCigListaGare"/>
			<c:choose>
				<c:when test="${!empty (filtroLivelloUtente)}">
					<gene:campoLista campo="AUTORI" entita="G_PERMESSI" where="G_PERMESSI.IDSTIPULA=V_GARE_STIPULA.ID AND G_PERMESSI.SYSCON=${profiloUtente.id}" visibile="false"/>
					<gene:campoLista campo="PROPRI" entita="G_PERMESSI" where="G_PERMESSI.IDSTIPULA=V_GARE_STIPULA.ID AND G_PERMESSI.SYSCON=${profiloUtente.id}" visibile="false"/>
				</c:when>
				<c:otherwise>
					<gene:campoLista campo="AUTORI" entita="G_PERMESSI" campoFittizio="true" value="1" visibile="false"/>
					<gene:campoLista campo="PROPRI" entita="G_PERMESSI" campoFittizio="true" value="1" visibile="false"/>
				</c:otherwise>
			</c:choose>
			<gene:campoLista campo="OGGETTO" />
			<gene:campoLista campo="DAATTO" visibile="false"/>
			<gene:campoLista campo="CODIMP"  visibile="false"/>
			<gene:campoLista campo="NOMEST" title="Ditta aggiudicataria"/>
			<gene:campoLista campo="FIRMA_FIT" campoFittizio="true"  title="Doc. da firmare" width="24" definizione="T2;;;;G1_DIGFIRMACHECK_DD" >			
			
			<c:set var="idStipula" value='${datiRiga.V_GARE_STIPULA_ID}'/>
			<c:set var="imgDocDigORFirmaCheck" value="firmaRemota.png" />
			
			<c:if test="${conteggioFormato ne '0'}">
				<c:set var="imgDocDigORFirmaCheck" value="firmaRemotaAlert.png" />
			</c:if>
			
			<c:if test="${numeroDocumentiDaFirmare ne '0' or conteggioFormato ne '0'}">
				<c:set var="titleDocDig" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNomiDocumentiDaFirmareFunction", pageContext, datiRiga.V_GARE_STIPULA_ID)}'/>
				<c:if test="${conteggioFormato ne '0'}">
					<c:set var="titleConteggioFormato" value="C'è ${conteggioFormato} documento da firmare senza allegato " />
					<c:if test="${conteggioFormato ne '1'}">
						<c:set var="titleConteggioFormato" value='Ci sono ${conteggioFormato} documenti da firmare senza allegato ' />
					</c:if>
					<c:set var="titleDocDig" value='${titleDocDig}${titleConteggioFormato}'/>
				</c:if>
				<a href="javascript:checkNumeroDocumenti('${numeroDocumentiDaFirmare}','${conteggioFormato}','PG','','', '${pageContext.request.contextPath}','','${datiRiga.V_GARE_STIPULA_ID}');" title="Firma tutti i documenti con richiesta di firma" tabindex="1510">
					<img src="${pageContext.request.contextPath}/img/${imgDocDigORFirmaCheck}" value="chiaveRiga='${datiRiga}'" title="${titleDocDig}" alt="Documenti Da Firmare" width="16" height="16">
				</a>
			</c:if>
			
			</gene:campoLista>
			<gene:campoLista campo="STATO" visibile="false"/>
			<gene:campoLista campo="NOME_CONTRACT" visibile="false"/>
			<gene:campoLista campo="CENINT" visibile="false" />
		</gene:formLista>
				</td>
			</tr>
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<gene:insert name="addPulsanti"/>
					<gene:insert name="pulsanteFirma">
						<c:if test='${datiRiga.rowCount > 0 }'>
							<INPUT type="button"  class="bottone-azione" value='Firma documenti' title='Firma tutti i documenti con richiesta di firma' onclick="javascript:checkNumeroStipule(0,0,'PG','','', '${pageContext.request.contextPath}','','');">
						</c:if>
					</gene:insert>
					<gene:insert name="pulsanteFirmaInoltra">
						<c:if test='${datiRiga.rowCount > 0 }'>
							<INPUT type="button"  class="bottone-azione" value='Firma e inoltra documenti' title='Firma e Inoltra tutti i documenti con richiesta di firma' onclick="javascript:inoltraEfirmaMassiva(0,0,'PG','','', '${pageContext.request.contextPath}','','');">
						</c:if>
					</gene:insert>
					&nbsp;
				</td>
			</tr>
		</table>
		
		<form name="formVisualizzaPermessiUtentiStandard" action="${pageContext.request.contextPath}/pg/VisualizzaPermessiUtentiStipula.do" method="post">
			<input type="hidden" name="metodo" id="metodo" value="apri" />
			<input type="hidden" name="id" id="id" value="" />
			<input type="hidden" name="codstipula" id="codstipula" value="" />
			<input type="hidden" name="permessimodificabili" id="permessimodificabili" value="" />
			<input type="hidden" name="codein" id="codein" value="${sessionScope.uffint}" />
		</form>		
		<jsp:include page="/WEB-INF/pages/gene/system/firmadigitale/modalPopupFirmaDigitaleRemota.jsp" >
			<jsp:param name="modalita" value="massiva"/>
			<jsp:param name="numeroDocumenti" value="${numeroDocumentiDaFirmare}"/>
			<jsp:param name="numeroDocumentiFormato" value="${conteggioFormato}"/>
			<jsp:param name="entitaFirma" value="DOCSTIPULA"/>
			<jsp:param name="chiaveFirma" value="${idStipula}"/>
		</jsp:include>
		<jsp:include page="/WEB-INF/pages/gare/g1stipula/g1stipula-FirmaInoltroMassivo.jsp" >
			<jsp:param name="numeroDocu" value="${numeroDocumentiDaFirmare}"/>
			<jsp:param name="numeroDocuFormato" value="${conteggioFormato}"/>
			<jsp:param name="entitaFirma" value="DOCSTIPULA"/>
			<jsp:param name="chiaviFirma" value="${idStipula}"/>
		</jsp:include>
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
	function listaVisualizzaStipula(chiaveRiga){
		document.forms[0].keyParent.value = chiaveRiga;
		document.forms[0].entita.value = "G1STIPULA";
		document.forms[0].pathScheda.value="/WEB-INF/pages/gare/g1stipula/g1stipula-scheda-approvazione-firma.jsp";
		listaVisualizza();
	}
	
	
	function listaNuovo(){
		document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?href=gare/g1stipula/g1stipula_nuovaStipula.jsp";
	}
	
	function checkNumeroDocumenti(numeroDocumenti,conteggioFormato,idprg,iddocdig,nome,contextPath,coacod,idStipula){
		/* caso particolare firma remota in cui devo passargli manualmente i dati perchè mi trovo in una lista di stipule,
			quindi in base alla stipula che clicco mando l'idStipula e il numeroDocumenti da firmare
		*/
		document.getElementById('chiaveFirma').value = idStipula;
		document.getElementById('numeroDocumentiHidden').value = numeroDocumenti;
		document.getElementById('numeroDocumentiFormatoHidden').value = conteggioFormato;
		//if(numeroDocumenti != null && numeroDocumenti > 0){
			openModal(idprg,iddocdig,nome,contextPath,coacod);
		/*} else {
			if(conteggioFormato != null && conteggioFormato > 0){
				if(conteggioFormato > 1){
					alert('Non ci sono documenti da firmare \nCi sono '+conteggioFormato+' documenti da firmare senza allegato');
				} else {
					alert("Non ci sono documenti da firmare \nC'è "+conteggioFormato+" documento da firmare senza allegato");
				}
			} else {
				alert('Non ci sono documenti da firmare');
			}
		}*/
	}
	
	function checkNumeroStipule(numeroDocumenti,numeroDocumentiFormato,idprg,iddocdig,nome,contextPath,coacod,idStipula){
		var numeroOggetti = contaCheckSelezionati(document.forms[0].keys);

		if (numeroOggetti == 0) {
			alert("Nessun elemento selezionato nella lista");
		} else {
			var arrayLen = "" + document.forms[0].keys.length;
			if (arrayLen != 'undefined') 
			{
				for (i = 0; i < document.forms[0].keys.length; i++) {
					if (document.forms[0].keys[i].checked){
						idStipula += document.forms[0].keys[i].value+",";
						numeroDocumenti += parseInt(document.forms[0].numDocs[i].value);
						numeroDocumentiFormato += parseInt(document.forms[0].numDocsFormato[i].value);
					}
				}
			} else {
				if (document.forms[0].keys.checked) {
					idStipula += document.forms[0].keys.value+",";
					numeroDocumenti += parseInt(document.forms[0].numDocs.value);
					numeroDocumentiFormato += parseInt(document.forms[0].numDocsFormato.value);
				}
			}
			document.getElementById('chiaveFirma').value = idStipula.substring(0,idStipula.length-1);
			document.getElementById('numeroDocumentiHidden').value = numeroDocumenti;
			document.getElementById('numeroDocumentiFormatoHidden').value = numeroDocumentiFormato;
		
		
			openModal(idprg,iddocdig,nome,contextPath,coacod);
		}
	}
	
	//Apertura popup per l'inoltro e la firma massiva delle stipule
	function inoltraEfirmaMassiva(numeroDocumenti,numeroDocumentiFormato,idprg,iddocdig,nome,contextPath,coacod,idStipula) {
		var numeroOggetti = contaCheckSelezionati(document.forms[0].keys);
		if (numeroOggetti == 0) {
			alert("Nessun elemento selezionato nella lista");
		} else {
			var arrayLen = "" + document.forms[0].keys.length;
			if (arrayLen != 'undefined') {
				for (i = 0; i < document.forms[0].keys.length; i++) {
					if (document.forms[0].keys[i].checked){
						idStipula += document.forms[0].keys[i].value+",";
						numeroDocumenti += parseInt(document.forms[0].numDocs[i].value);
						numeroDocumentiFormato += parseInt(document.forms[0].numDocsFormato[i].value);
					}
				}
			} else {
				if (document.forms[0].keys.checked) {
					idStipula += document.forms[0].keys.value+",";
					numeroDocumenti += parseInt(document.forms[0].numDocs.value);
					numeroDocumentiFormato += parseInt(document.forms[0].numDocsFormato.value);
				}
			}
		
		
			document.getElementById('chiaviFirma').value = idStipula.substring(0,idStipula.length-1);
			document.getElementById('numeroDocuHidden').value = numeroDocumenti;
			document.getElementById('numeroDocuFormatoHidden').value = numeroDocumentiFormato;
			openModalFirmaInoltra(idprg,iddocdig,nome,contextPath,coacod);
		}
		
	}
	
	
	</gene:javaScript>
</gene:template>