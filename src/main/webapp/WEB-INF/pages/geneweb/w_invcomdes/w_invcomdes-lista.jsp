<%
/*
 * Created on: 04/06/2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

/*
	Descrizione:
		Finestra che visualizza la conferma per l'inserimento delle pubblicazioni bando/esito predefinite
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<div style="width:97%;">

<gene:template file="popup-message-template.jsp">
	
	<gene:redefineInsert name="head">
		<script type="text/javascript">
		  var _contextPath="${pageContext.request.contextPath}";
		  var _entita="${entita}";
		  var _idprg="${param.idprg}";
		  var _idcom="${param.idcom}";
		  var _numeroPopUp="${param.numeroPopUp}";
		  var _genereGara="${param.genereGara}";
		  var _compub="${param.compub}";
		</script>
		<script type="text/javascript" src="${contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.validate.min.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.easytabs.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.selezionadestinatari.js"></script>
<style type="text/css">
		
		.div_importo{
			float: left;
			width: 48%;
			font: 11px Verdana, Arial, Helvetica, sans-serif;
			margin: 8px 1%;
		}
		.importo_collegato div b{
			margin-right: 10px;
		}
		TABLE.dettaglio-notab TD.etichetta-search {
			width: 300px;
			PADDING-RIGHT: 10px;
			TEXT-ALIGN: right;
		}
		
		TABLE.dettaglio-notab INPUT.search-input {
			margin-left: 10px;
		}
		
      TABLE.schedagperm {
        margin-top: 5px;
        margin-bottom: 5px;
        padding: 0px;
        font-size: 11px;
        border-collapse: collapse;
        border-left: 1px solid #A0AABA;
        border-top: 1px solid #A0AABA;
        border-right: 1px solid #A0AABA;
      }

      TABLE.schedagperm TR.intestazione {
        background-color: #CCE0FF;
        border-bottom: 1px solid #A0AABA;
      }

      TABLE.schedagperm TR.intestazione TD, TABLE.schedagperm TR.intestazione TH {
        padding: 5 2 5 2;
        text-align: center;
        font-weight: bold;
        border-left: 1px solid #A0AABA;
        border-right: 1px solid #A0AABA;
        border-top: 1px solid #A0AABA;
        border-bottom: 1px solid #A0AABA;
        height: 30px;
      }

      TABLE.schedagperm TR.sezione {
        background-color: #EFEFEF;
        border-bottom: 1px solid #A0AABA;
      }

      TABLE.schedagperm TR.sezione TD, TABLE.schedagperm TR.sezione TH {
        padding: 5 2 5 2;
        text-align: left;
        font-weight: bold;
        height: 25px;
      }

      TABLE.schedagperm TR {
        background-color: #FFFFFF;
      }

      TABLE.schedagperm TR TD {
        padding-left: 3px;
        padding-top: 1px;
        padding-bottom: 1px;
        padding-right: 3px;
        text-align: left;
        border-left: 1px solid #A0AABA;
        border-right: 1px solid #A0AABA;
        border-top: 1px solid #A0AABA;
        border-bottom: 1px solid #A0AABA;
        height: 25px;
        font: 11px Verdana, Arial, Helvetica, sans-serif;
      }

      TABLE.schedagperm TR.intestazione TH.ck, TABLE.schedagperm TR TD.ck {
        min-width: 50px;
        text-align: center;
      }


      img.img_titolo {
        padding-left: 8px;
        padding-right: 8px;
        width: 24px;
        height: 24px;
        vertical-align: middle;
      }

      .dataTables_length, .dataTables_filter {
        padding-bottom: 5px;
      }

      .dataTables_empty {
        padding-top: 6px;
      }

      div.tooltip {
        width: 300px;
        margin-top: 3px;
        margin-bottom:3px;
        border: 1px solid #A0AABA;
        padding: 10px;
        display: none;
        position: absolute;
        z-index: 1000;
        background-color: #F4F4F4;
      }


      input.search {
        height: 16px;
        font: 11px Verdana, Arial, Helvetica, sans-serif;
        background-color: #FFFFFF;
        color: #000000;
        vertical-align: middle;
        border: 1px #366A9B solid;
        width: 98%;
        font-style: italic;
      }

    </style>

	</gene:redefineInsert>
	
	
	<c:set var="idprg" value="${param.idprg}" />
	<c:set var="idcom" value="${param.idcom}" />
	<c:set var="comkey1" value="${param.comkey1}" />
	<c:set var="genereGara" value="${param.genereGara}" />
	<c:set var="idconfi" value="${param.idconfi}" />
	<c:set var="isInvioFax" value='${gene:callFunction("it.eldasoft.gene.tags.functions.IsInvioFaxFunction",pageContext)}' />
	<c:set var="codgar" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function", pageContext, comkey1)}'/>
	<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codgar,idconfi)}'/>
	<c:set var="descc" value="${param.descc}" />
	<c:set var="coment" value="${param.coment}" />
	<c:set var="CampoFittIscriz" value="${param.CampoFittIscriz}"/>
	<c:set var="CampoFittNgara" value="${param.CampoFittNgara}"/>
	<c:set var="CampoFittAbilitaz" value="${param.CampoFittAbilitaz}"/>
	<c:set var="CampoFittRT" value="${param.CampoFittRT}"/>
	<c:set var="CampoFitt" value="${param.CampoFitt}"/>
	
	<c:if test="${integrazioneWSDM eq 1}" >
		<c:set var="abilitatoInvioMailDocumentale" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.AbilitatoInvioMailDocumentaleFunction", pageContext, idconfi)}'/>	
	</c:if>

	<c:choose>
		<c:when test="${entita eq 'DITG'}" >
			<c:choose>
				<c:when test="${genereGara eq '10'}">
					<c:set var="testoTipoGara" value=" in elenco" />
				</c:when>
				<c:when test="${genereGara eq '20'}">
					<c:set var="testoTipoGara" value="in catalogo" />
					</c:when>
				<c:otherwise>
					<c:set var="testoTipoGara" value="in gara" />
				</c:otherwise>
			</c:choose>
			<gene:setString name="titoloMaschera" value='Selezione destinatari da ditte ${testoTipoGara}' />
		</c:when>
		<c:otherwise>
			<gene:setString name="titoloMaschera" value='Selezione destinatari da anagrafica' />
		</c:otherwise>
	</c:choose>
	<gene:redefineInsert name="corpo">
		<br>
		Selezionare dalla lista sottostante le ditte cui si intende inviare la comunicazione. Si sottolinea che nel caso di raggruppamento temporaneo viene considerata la ditta mandataria.
		<br>
			<gene:redefineInsert name="buttons">
				<INPUT type="button" class="bottone-azione" value="Aggiungi destinatari selezionati" title="Aggiungi destinatari selezionati" onclick="javascript:aggiungi()">&nbsp;
				<INPUT type="button" class="bottone-azione" value="Torna alla ricerca" title="Torna alla ricerca" onclick="javascript:torna(0);">&nbsp;
				<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi()">&nbsp;
			</gene:redefineInsert>
			
		
		<form name="formDestinatari" action="${pageContext.request.contextPath}/pg/AggiungiDitteConcorrenti.do" method="post">
			<input type="hidden" name="idprg" value="${idprg}" />
			<input type="hidden" name="idcom" value="${idcom}" />
			<input type="hidden" name="comkey1" value="${comkey1}" />
			<input type="hidden" name="genereGara" value="${genereGara}" />
			<input type="hidden" name="numeroPopUp" value="${param.numeroPopUp}">
			<input type="hidden" id="_abilitatoInvioMailDocumentale" value="${abilitatoInvioMailDocumentale}"/>
			<input type="hidden" id="_isInvioFax" value="${isInvioFax}"/>
			<input type="hidden" name="entitaRicerca" value="${entita}"/>
			<input type="hidden" id="_descc" name="descc" value="${descc}"/>
			<c:if test='${integrazioneWSDM =="1"}'>
				<input id="servizio" type="hidden" value="FASCICOLOPROTOCOLLO" />
				<input id="idprg" type="hidden" value="${sessionScope.moduloAttivo}" />
			</c:if>
			
			<table class="dettaglio-notab">
			 
			  <tr>
				<td>
				  <br>
					<div id="destinatariContainer" style="margin-left:8px; width: 98%"></div>
				  <br>
				</td>
			  </tr>
			 
			  
			</table>	

		</form>


  	</gene:redefineInsert>


	<gene:javaScript>

	function aggiungi(){
		var tableResultXpage= $('#destinatari').DataTable().page.info()?.length;
		var page= $('#destinatari').DataTable().page();
		$('#destinatari').DataTable().page.len(-1).draw();
		
		var numeroOggettiPec = contaCheckSelezionati(formDestinatari.keysPec);
		var numeroOggettiEmail = contaCheckSelezionati(formDestinatari.keysEmail);
		var numeroOggettiFax = contaCheckSelezionati(formDestinatari.keysFax);
		var numeroOggettiOperatore = contaCheckSelezionati(formDestinatari.keysOperatore);
		if (numeroOggettiPec == 0 && numeroOggettiEmail == 0 && numeroOggettiFax == 0 && numeroOggettiOperatore == 0) {
			alert("Selezionare almeno un destinatario dalla lista");
		} else {								
			formDestinatari.action="${pageContext.request.contextPath}/pg/AggiungiDestinatariAnagrafica.do";
			bloccaRichiesteServer();
			formDestinatari.submit();
		}
		$('#destinatari').DataTable().page.len(tableResultXpage)?.draw();
		$('#destinatari').DataTable().page(page).draw(false);
	}

		
		function chiudi(){
			window.close();
		}
		
	function torna(num){
		var radioValue= '${param.filtroRadio}';
		var coment= '${coment}';
		if(radioValue !='1' || coment != 'HOMEPG')
			historyVaiA(num);
		else{
			bloccaRichiesteServer();
			window.location=contextPath+"/ApriPopup.do?href=geneweb/w_invcomdes/w_invcomdes-ricerca-destinatariAnagrafica-popup.jsp&idprg=${idprg}&idcom=${idcom}&filtroRadio=1&idconfi=${idconfi}&coment=HOMEPG&numeroPopUp="+getNumeroPopUp()
			+"&campoFittIscriz=${CampoFittIscriz}"
			+"&campoFittNgara=${CampoFittNgara}"
			+"&campoFittAbilitaz=${CampoFittAbilitaz}"
			+"&campoFittRT=${CampoFittRT}"
			+"&campoFitt=${CampoFitt}"
		}
	}
	</gene:javaScript>
	</gene:template>
	
</div>
