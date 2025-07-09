
<%
	/*
	 * Created on 21-Ott-2008
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

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<c:set var="idprg" value='${gene:getValCampo(key,"IDPRG")}'/>
<c:set var="idcom" value='${gene:getValCampo(key,"IDCOM")}'/>
<c:set var="comkey1" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetCOMKEYGaraLottoFunction",pageContext,idprg,idcom)}' />

<c:set var="comstato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetCOMSTATOFunction",pageContext,idprg,idcom)}' />
<c:set var="autorizzatoModificaComunicazione" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.AutorizzatoModificaComunicazioneFunction",pageContext,idprg,idcom, "true")}' />	
<c:set var="comintest" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetCOMINTESTFunction",pageContext,idprg,idcom)}' />
<c:set var="codgar" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function", pageContext, comkey1)}'/>
<c:set var="statopec" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.MostraStatoPECFunction", pageContext, idprg, idcom)}' />
 
<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codgar, idconfi)}'/>
<c:set var="tipoGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction",  pageContext,codgar)}' />
<c:if test="${integrazioneWSDM eq 1 }" >
	<c:set var="tipoWSDM" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetTipoWSDMFunction", pageContext, "FASCICOLOPROTOCOLLO", "NO",idconfi)}' />
	<c:set var="abilitatoInvioMailDocumentale" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.AbilitatoInvioMailDocumentaleFunction", pageContext, idconfi)}'/>
</c:if>

<c:choose>
	<c:when test='${not empty param.keyAdd}'>
		<c:set var="keyAdd" value="${param.keyAdd}"  />
	</c:when>
	<c:otherwise>
		<c:set var="keyAdd" value="${keyAdd}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.chiaveWSDM}'>
		<c:set var="chiaveWSDM" value="${param.chiaveWSDM}"  />
	</c:when>
	<c:otherwise>
		<c:set var="chiaveWSDM" value="${chiaveWSDM}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.entitaWSDM}'>
		<c:set var="entitaWSDM" value="${param.entitaWSDM}"  />
	</c:when>
	<c:otherwise>
		<c:set var="entitaWSDM" value="${entitaWSDM}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.idconfi}'>
		<c:set var="idconfi" value="${param.idconfi}"  />
	</c:when>
	<c:otherwise>
		<c:set var="idconfi" value="${idconfi}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.comdatins}'>
		<c:set var="comdatins" value="${param.comdatins}"  />
	</c:when>
	<c:otherwise>
		<c:set var="comdatins" value="${comdatins}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${tipoGara eq '10'}">
		<c:set var="testoTipoGara" value="elenco" />
	</c:when>
	<c:when test="${tipoGara eq '20'}">
		<c:set var="testoTipoGara" value="catalogo" />
	</c:when>
	<c:otherwise>
		<c:set var="testoTipoGara" value="gara" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.cenint}'>
		<c:set var="cenint" value="${param.cenint}"  />
	</c:when>
	<c:otherwise>
		<c:set var="cenint" value="${cenint}" />
	</c:otherwise>
</c:choose>


<c:if test="${integrazioneWSDM eq '1'}">
	<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.EsistonoAllegatiComunicazioneFunction" parametro="${idprg};${idcom}"/>
	<c:set var="esitoControlli" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ControlliInvioComunicazioniFunction",pageContext,idprg,idcom,commodello)}' />
</c:if>
<c:set var="iterga" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetITERGAFunction", pageContext, codgar)}' />
<c:set var="isfineconc" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select isfineconc from torn where codgar=?", codgar)}'/>

<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>
<table class="dettaglio-tab-lista">
	<tr>
		<td><gene:formLista entita="W_INVCOMDES" pagesize="20" sortColumn="3"
			where="W_INVCOMDES.IDPRG = #W_INVCOM.IDPRG# AND W_INVCOMDES.IDCOM = #W_INVCOM.IDCOM#" tableclass="datilista"
			gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreW_INVCOMDES">

			<c:if test="${comstato eq '1' and autorizzatoModificaComunicazione eq 'true'}">
				<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>"
					width="50">
					<c:if test="${currentRow >= 0}">
						<gene:PopUp variableJs="rigaPopUpMenu${currentRow}"
							onClick="chiaveRiga='${chiaveRigaJava}'">
							<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL")}'>
								<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina"
									title="Elimina" />
							</c:if>
						</gene:PopUp>
						<c:if test='${gene:checkProtFunz(pageContext, "DEL","LISTADELSEL")}'>
							<input type="checkbox" name="keys" value="${chiaveRiga}" />
						</c:if>
					</c:if>
				</gene:campoLista>			
			</c:if>
			<gene:campoLista campo="IDPRG" visibile="false"/>
			<gene:campoLista campo="IDCOM" visibile="false"/>
			<gene:campoLista campo="IDCOMDES" width="40" title="N°" visibile="false"/>
			<gene:campoLista campo="DESCODSOG" visibile="false" />
			<gene:campoLista campo="DESCC" visibile="false" where="DESCC is null"/>
			<gene:campoLista campo="DESCODENT" visibile="false"/>
			
<%/*
// 			<c:if test="${datiRiga.IMPR_NOMEST ne ''}">
//			  <gene:campoLista entita="IMPR" campo="NOMEST" where="IMPR.CODIMP=W_INVCOMDES.DESCODSOG" visibile="false" />
//			  <gene:campoLista title="Intestazione" campo="DESINTEST" value="${datiRiga.IMPR_NOMEST}" />
//			</c:if>
*/%>
   		    <gene:campoLista campo="DESINTEST" visibile='false'/>
			<gene:campoLista campo="DESINTEST_fittizio" campoFittizio='true' definizione="T2000;;;;DESINTEST" visibile="true" value="${(iterga ne '9' || isfineconc eq '1' || (not empty datiRiga.W_INVCOMDES_DESINTEST and gene:matches(datiRiga.W_INVCOMDES_DESINTEST, '^operatore \\\d{5}$', false)))?datiRiga.W_INVCOMDES_DESINTEST:'Dato anonimizzato'}"/>
			<gene:campoLista campo="DESMAIL" visibile="${compub eq '2'}"/>
			<gene:campoLista campo="COMTIPMA" visibile="${compub eq '2'}" title="Tipo"/>
			<c:if test="${!empty comstato and comstato ne '1'}">
				<gene:campoLista campo="DESSTATO" />
				<c:if test='${statopec eq "true"}'>
					<gene:campoLista campo="DESESITOPEC" width="150" />
					<c:set var="tmp" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetDocumentiRiconciliazionePECFunction",pageContext,idprg,idcom,datiRiga.W_INVCOMDES_IDCOMDES)}' />
					<gene:campoLista title="Ricevute" width="20">
						<c:if test="${not empty idDocumentoAccettazionePEC or not empty idDocumentoAccettazionePEC}">
							<c:if test="${not empty idDocumentoAccettazionePEC}">
								<a href="javascript:downloadEML('${idprg}','${idDocumentoAccettazionePEC}','accettazione.eml')" title="Download accettazione" >
									<img width="24" height="24" title="Download accettazione" alt="Download accettazione" src="${pageContext.request.contextPath}/img/visualizzafile.gif">
								</a>
							</c:if>
							<c:if test="${not empty idDocumentoRicevutaPEC}">
								<a href="javascript:downloadEML('${idprg}','${idDocumentoRicevutaPEC}','consegna.eml')" title="Download ricevuta" >
									<img width="24" height="24" title="Download consegna" alt="Download consegna" src="${pageContext.request.contextPath}/img/visualizzafile.gif">
								</a>
							</c:if>
						</c:if>
					</gene:campoLista>
				</c:if>
				<c:if test="${tipoWSDM  eq 'PALEO' and (comstato eq '10' or comstato eq '11' or comstato eq '21')}">
					<gene:campoLista campo="DESESITODOCUMENTALE"/>
				</c:if>
				<gene:campoLista campo="DESDATINV" width="150" />
				<c:if test='${statopec eq "true"}'>
					<gene:campoLista campo="DESDATCONS" width="150" />
				</c:if>
				<c:if test="${fn:contains(listaOpzioniDisponibili, 'OP114#')}">
					<gene:campoLista campo="DESDATLET" width="150" title="Data e ora lettura su portale"/>
				</c:if>
				<gene:campoLista campo="DESERRORE" visibile="${compub eq '2'}"/>						
			</c:if>
			<c:if test="${coment eq 'HOMEPG' && gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare')}">
			<gene:campoLista title="&nbsp;" width="20">	
				<c:if test="${not empty datiRiga.W_INVCOMDES_DESCODSOG && datiRiga.W_INVCOMDES_DESCODENT eq 'IMPR'}">			
					<a href="javascript:visualizzaElenchiCollegati('${datiRiga.W_INVCOMDES_DESCODSOG}')" >
						<img width="16" height="16" title="Visualizza iscrizione elenchi" alt="Visualizza iscrizione elenchi" src="${pageContext.request.contextPath}/img/consultaGare.png" />
					</a>
				</c:if>
			</gene:campoLista>
			</c:if>

			<input type="hidden" name="keyAdd" value="${keyAdd}" />
			<input type="hidden" name="chiaveWSDM" id="chiaveWSDM" value="${chiaveWSDM}"/>
			<input type="hidden" name="entitaWSDM" id="entitaWSDM" value="${entitaWSDM}"/>
			<input type="hidden" name="idconfi" id="idconfi" value="${idconfi}"/>
			<input type="hidden" name="comdatins" id="comdatins" value="${comdatins}"/>
			<input type="hidden" name="cenint" id="cenint" value="${cenint}"/>
		</gene:formLista></td>
	</tr>
	<c:set var="bloccoDestinatariFS12Rettifica" value="${commodello eq '11' or commodello eq '12' or commodello eq '16' or commodello eq '17'}"/>
	<c:choose>
		<c:when test="${comstato eq '1' and autorizzatoModificaComunicazione eq 'true'}">
			<gene:redefineInsert name="listaNuovo">
				<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
					<c:if test='${tipoGara ne "4" and tipoGara ne "11" and entitaParent ne "NSO_ORDINI" and entitaParent ne "G1STIPULA" and coment ne "HOMEPG" and bloccoDestinatariFS12Rettifica ne "true"}' >
						<c:if test='${commodello ne "1"}' >
						<tr>
							<td class="vocemenulaterale">
								<a href="javascript:ricercaDitteConcorrenti('${idprg}','${idcom}');" title="Aggiungi destinatari da ditte in ${testoTipoGara}" tabindex="1501">
									Aggiungi destinatari da ditte in ${testoTipoGara}</a></td>
						</tr>
						</c:if>
						<tr>
							<td class="vocemenulaterale">
								<a href="javascript:aggiungiDestManRT('${idprg}','${idcom}');" title="Aggiungi destinatari da mandanti RT" tabindex="1502">
									Aggiungi destinatari da mandanti RT</a></td>
						</tr>
					</c:if>
					<c:if test='${entitaParent ne "NSO_ORDINI" and commodello ne "1" and compub ne "3" and bloccoDestinatariFS12Rettifica ne "true"}' >
						<tr>
							<td class="vocemenulaterale">
								<a href="javascript:ricercaDestinatariAnagrafica('${idprg}','${idcom}');" title="Aggiungi destinatari da anagrafica" tabindex="1503">
									Aggiungi destinatari da anagrafica</a></td>
						</tr>
					</c:if>
					<c:if test='${compub ne "3"}' >
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:aggiungiAltriIndirizzi('${idprg}','${idcom}');" title="Aggiungi altri destinatari" tabindex="1504">
								Aggiungi altri destinatari</a></td>
					</tr>
					</c:if>
				</c:if>
			</gene:redefineInsert>
			
			<gene:redefineInsert name="addToAzioni" >
				<c:choose>
					<c:when test='${integrazioneWSDM =="1" and !protpres and compub==2}'>
						<tr>
							<td class="vocemenulaterale" >
								<a href="javascript:protocollacomunicazione('${idprg}','${idcom}');" title="Protocolla ed invia comunicazione" tabindex="1505">Protocolla ed invia comunicazione</a>
							</td>
						</tr>
					</c:when>
					<c:otherwise>
						<tr>
							<td class="vocemenulaterale" >
								<a href="javascript:inviacomunicazione('${idprg}','${idcom}');" title="Invia comunicazione" tabindex="1506">Invia comunicazione</a>
							</td>
						</tr>
					</c:otherwise>
				</c:choose>
			</gene:redefineInsert>
			
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
						<c:if test='${tipoGara ne "4" and tipoGara ne "11" and entitaParent ne "NSO_ORDINI" and coment ne "HOMEPG" and bloccoDestinatariFS12Rettifica ne "true"}' >
							<c:if test='${commodello ne "1" and entitaParent ne "G1STIPULA"}' >
								<INPUT type="button"  class="bottone-azione" value='Aggiungi destinatari da ditte in ${testoTipoGara}' title='Aggiungi destinatari da ditte in ${testoTipoGara}' onclick="javascript:ricercaDitteConcorrenti('${idprg}','${idcom}');">&nbsp;
							</c:if>
							<INPUT type="button"  class="bottone-azione" value='Aggiungi destinatari da mandanti RT' title='Aggiungi destinatari da mandanti RT' onclick="javascript:aggiungiDestManRT('${idprg}','${idcom}');">&nbsp;
						</c:if>
						<c:if test='${entitaParent ne "NSO_ORDINI" and commodello ne "1" and compub ne "3" and bloccoDestinatariFS12Rettifica ne "true"}' >
							<INPUT type="button"  class="bottone-azione" value='Aggiungi destinatari da anagrafica' title='Aggiungi destinatari da anagrafica' onclick="javascript:ricercaDestinatariAnagrafica('${idprg}','${idcom}');">&nbsp;
						</c:if>
						<c:if test='${tipoGara ne "4" and tipoGara ne "11" and entitaParent ne "NSO_ORDINI" and coment ne "HOMEPG"}' >
							<br><br>
						</c:if>
						<c:if test='${!(integrazioneWSDM =="1" and !protpres and compub==2) and compub ne "3"}'>
							<INPUT type="button"  class="bottone-azione" value='Aggiungi altri destinatari' title='Aggiungi altri destinatari' onclick="javascript:aggiungiAltriIndirizzi('${idprg}','${idcom}');">&nbsp;&nbsp;
						</c:if>
					</c:if>
					<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione();">
					</c:if>
				</td>
			</tr>
		</c:when>
		<c:otherwise>
			<gene:redefineInsert name="listaNuovo" />
			<gene:redefineInsert name="listaEliminaSelezione" />
			
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					&nbsp;
				</td>
			</tr>
		</c:otherwise>
	</c:choose>
	
</table>

<gene:javaScript>
	
	var autorizzatoTelematiche ="${autorizzatoTelematiche }";
	var bloccoInvioComunicazioniGeneriche ="${bloccoInvioComunicazioniGeneriche}";
	var cenint = "${cenint}";
	var coment ="${coment}";
	
	function ricercaDitteConcorrenti(idprg,idcom){
		openPopUpCustom("href=geneweb/w_invcomdes/w_invcomdes-ricerca-ditte-popup.jsp&idprg=" + idprg + "&idcom=" + idcom + "&idconfi=${idconfi}", "aggiungiDitteConcorrenti", 950, 500, "yes", "yes");
	}
	
	function aggiungiAltriIndirizzi(idprg,idcom){
		openPopUpCustom("href=geneweb/w_invcomdes/w_invcomdes-schedaPopup-insert.jsp&modo=NUOVO"+"&idprg=" + idprg + "&idcom=" + idcom + "&idconfi=${idconfi}", "aggiungiAltriIndirizzi", 850, 500, "yes", "yes");
	}
	
	function ricercaDestinatariAnagrafica(idprg,idcom){
		openPopUpCustom("href=geneweb/w_invcomdes/w_invcomdes-ricerca-destinatariAnagrafica-popup.jsp&idprg=" + idprg + "&idcom=" + idcom + "&filtroRadio=1&idconfi=${idconfi}"+ "&coment=" +coment, "aggiundiDestinatariAnagrafica", 950, 500, "yes", "yes");
	}
	
	function inviacomunicazione(idprg,idcom){
		if(autorizzatoTelematiche == "false"){
			alert("Non e' possibile procedere.\nLa funzione e' disponibile solo al Punto ordinante");
			return;
		}
		if(bloccoInvioComunicazioniGeneriche == "true"){
			alert("Non e' possibile procedere.\nL'utente non risulta abilitato all'invio di comunicazioni generiche");
				return;
		}else{
			if(cenint == "" && coment != "HOMEPG"){
				alert("Non e' possibile procedere.\nDeve essere specificata la stazione appaltante");
				return;
			}else{
				var commodello = "${commodello}";
				href="href=geneweb/w_invcom/w_invcom-invia-popup.jsp&idprg=" + idprg + "&idcom=" + idcom + "&compub=${compub}" + "&cenint=" + cenint + "&commodello=" + commodello;
				openPopUpCustom(href, "inviacomunicazione", 550, 350, "no", "no");
			}
		}
	}
	
	function aggiungiDestManRT(idprg,idcom){
		var integrazioneWSDM="${integrazioneWSDM }"; 
		openPopUpCustom("href=geneweb/w_invcomdes/w_invcomdes-aggiungi-destinatariMandantiRT-popup.jsp&idprg=" + idprg + "&idcom=" + idcom + "&integrazioneWSDM=" + integrazioneWSDM, "aggiungiDestManRT", 750, 500, "yes", "yes");
	}
	
	function protocollacomunicazione(idprg,idcom) {
		var esistonoAllegatiDaFirmare = "${esistonoAllegatiDaFirmare}";
		var esitoControlli = "${esitoControlli}";
		if(autorizzatoTelematiche == "false"){
			alert("Non e' possibile procedere.\nLa funzione e' disponibile solo al Punto ordinante");
			return;
		}else{
			if(cenint == ""){
				alert("Non e' possibile procedere.\nDeve essere specificata la stazione appaltante");
				return;
			}else if(esistonoAllegatiDaFirmare == 'TRUE'){
				alert("Non e' possibile procedere.\nVi sono degli allegati in attesa di firma");
				return;
			}else if(esitoControlli == 'NO-TERMINISCA'){
				alert("Non è possibile inviare la comunicazione in quanto la data termine presentazione documentazione è precedente alla data corrente");
				return;
			}else{
				document.formprotocollacomunicazione.idprg.value = idprg;
				document.formprotocollacomunicazione.idcom.value = idcom;
				var chiaveOriginale;
				var keyAdd="${keyAdd }";
				var vet = keyAdd.split(";");
				var dimVet = vet.length;
				if(dimVet==1){
					chiaveOriginale = keyAdd.substring(keyAdd.indexOf(":")+1);
				}else if(dimVet>1){
					chiaveOriginale = keyAdd.substring(keyAdd.indexOf(":")+1,vet[0].length);
				}
				
				document.formprotocollacomunicazione.chiaveOriginale.value = chiaveOriginale;
				document.formprotocollacomunicazione.submit();
			}
		}
	}
	
	var selezionaPaginaDefault = selezionaPagina;
	var selezionaPagina = selezionaPaginaCustom;
	function selezionaPaginaCustom(pageNumber){
		var keyAdd="${keyAdd }"
		var chiaveWSDM="${chiaveWSDM }";
		var entitaWSDM="${entitaWSDM }";
		var idconfi="${idconfi }";
		var comdatins ="${comdatins }";
		document.pagineForm.action += "?keyAdd=" + keyAdd + "&chiaveWSDM=" + chiaveWSDM + "&entitaWSDM=" + entitaWSDM + "&comdatins=" + comdatins + "&cenint=" + cenint + "&idconfi=" + idconfi;
		selezionaPaginaDefault(pageNumber);
	}
	
	//si elimina l'orario dal valore
	var comdatins = "${comdatins }";
	if(comdatins!=null){
		var tmp = comdatins.split(" ");
		comdatins = tmp[0];
		formprotocollacomunicazione.comdatins.value=comdatins;
	}
	
	function aggiorna(){
			window.location=contextPath+'/History.do?metodo=reload&numeroPopUp='+getNumeroPopUp();
		}
		
		
	function visualizzaElenchiCollegati(codimp){ 
		openPopUpCustom("href=geneweb/w_invcomdes/w_invcomdes-lista-elenchi-destinatari.jsp&dittao="+codimp, "visualizzaElenchiCollegati", 750, 500, "yes", "yes");
	}

	function downloadEML(idprg,iddocdig,dignomdoc) {
		var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
		document.location.href=href+"?idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
	}
</gene:javaScript>

<form name="formprotocollacomunicazione" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/wsdm/wsdm-comunicazione.jsp" /> 
	<input type="hidden" name="entita" value="${entitaWSDM}" />
	<input type="hidden" name="key1" value='${chiaveWSDM}' />
	<input type="hidden" name="idconfi" value='${idconfi}' />
	<input type="hidden" name="key2" value='' /> 
	<input type="hidden" name="key3" value='' />
	<input type="hidden" name="key4" value='' />
	<input type="hidden" name="idprg" value="" />
	<input type="hidden" name="idcom" value="" />
	<input type="hidden" name="metodo" value="apri" />
	<input type="hidden" name="idcfg" value="${cenint}" />
	<input type="hidden" name="activePage" value="0" />
	<input type="hidden" name="chiaveOriginale" value="" />
	<input type="hidden" name="comdatins" value="" />
</form>
