<%
/*
 * Created on: 08/02/2021
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

<c:set var="idprg" value='${gene:getValCampo(key,"IDPRG")}' />
<c:set var="idcom" value='${gene:getValCampo(key,"IDCOM")}' />

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

<c:set var="comkey1" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetCOMKEYGaraLottoFunction",pageContext,idprg,idcom)}' />
<c:set var="codgar" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function", pageContext, comkey1)}'/>
<c:set var="comstato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetCOMSTATOFunction",pageContext,idprg,idcom)}' />
<c:set var="autorizzatoModificaComunicazione" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.AutorizzatoModificaComunicazioneFunction",pageContext,idprg,idcom, "true")}' />
<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codgar, idconfi)}'/>
<c:set var="tipoGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction",  pageContext,codgar)}' />

<gene:formScheda entita="W_INVCOM" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreW_INVCOM">
	<gene:campoScheda campo="IDPRG" visibile="false"/>
	<gene:campoScheda campo="IDCOM" visibile="false"/>
	<gene:campoScheda campo="COMPUB" visibile="false"/>
			
	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	
	${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GestioneDocumentiSoccorsoIstruttorioFunction", pageContext, idprg, idcom)}
	<gene:campoScheda campo="MAX_NUMORD" campoFittizio="true" definizione="N12" value='${maxNumord}' visibile="false" />
	<gene:campoScheda campo="IDPRG" visibile="false"/>
	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='G1DOCSOC'/>
		<jsp:param name="chiave1" value='${idprg}'/>
		<jsp:param name="chiave2" value='${idcom}'/>
		<jsp:param name="nomeAttributoLista" value='documentiSoccorsoIstruttorio' />
		<jsp:param name="idProtezioni" value="DOCSOCISTR" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/g1docsoc/documento.jsp"/>
		<jsp:param name="arrayCampi" value="'G1DOCSOC_ID_', 'G1DOCSOC_IDPRG_', 'G1DOCSOC_IDCOM_', 'G1DOCSOC_NUMORD_', 'G1DOCSOC_DESCRIZIONE_', 'G1DOCSOC_OBBLIGATORIO_', 'G1DOCSOC_FORMATO_' "/>		
		<jsp:param name="titoloSezione" value="Documento" />
		<jsp:param name="titoloNuovaSezione" value="Nuovo Documento" />
		<jsp:param name="descEntitaVociLink" value="documento" />
		<jsp:param name="msgRaggiuntoMax" value="i documenti"/>
		<jsp:param name="usaContatoreLista" value="true" />
	</jsp:include>
	
	<gene:campoScheda>
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
					<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") && (comstato eq "1" and autorizzatoModificaComunicazione eq "true")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
					</c:if>
				</gene:insert>
				
			</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</gene:campoScheda>
	
	
	
	<input type="hidden" name="keyAdd" value="${keyAdd}" />
	<input type="hidden" name="chiaveWSDM" id="chiaveWSDM" value="${chiaveWSDM}"/>
	<input type="hidden" name="entitaWSDM" id="entitaWSDM" value="${entitaWSDM}"/>
	<input type="hidden" name="idconfi" id="idconfi" value="${idconfi}"/>
	<input type="hidden" name="comdatins" id="comdatins" value="${comdatins}"/>
	<input type="hidden" name="cenint" id="cenint" value="${cenint}"/>
	<c:choose>
		<c:when test="${comstato eq '1' and autorizzatoModificaComunicazione eq 'true'}">
			<c:if test='${ modo ne "MODIFICA" and modo ne "NUOVO"}'>			
				<gene:redefineInsert name="addToAzioni" >
					<c:choose>
						<c:when test='${integrazioneWSDM =="1" and !protpres and compub==2 }'>
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
			</c:if>
		</c:when>
		<c:otherwise>
			<gene:redefineInsert name="pulsanteModifica" />
			<gene:redefineInsert name="schedaModifica" />			
			
		</c:otherwise>
	</c:choose>
</gene:formScheda>	



<gene:javaScript>
	var autorizzatoTelematiche ="${autorizzatoTelematiche }";
	var cenint = "${cenint}";
	
	function inviacomunicazione(idprg,idcom){
		if(autorizzatoTelematiche == "false"){
			alert("Non e' possibile procedere.\nLa funzione e' disponibile solo al Punto ordinante");
			return;
		}else{
			if(cenint == ""){
				alert("Non e' possibile procedere.\nDeve essere specificata la stazione appaltante");
				return;
			}else{
				href="href=geneweb/w_invcom/w_invcom-invia-popup.jsp&idprg=" + idprg + "&idcom=" + idcom + "&compub=${compub}" + "&cenint=" + cenint + "&commodello=1";
				openPopUpCustom(href, "inviacomunicazione", 550, 350, "no", "no");
			}
		}
	}
	
	function protocollacomunicazione(idprg,idcom) {
		if(autorizzatoTelematiche == "false"){
			alert("Non e' possibile procedere.\nLa funzione e' disponibile solo al Punto ordinante");
			return;
		}else{
			if(cenint == ""){
				alert("Non e' possibile procedere.\nDeve essere specificata la stazione appaltante");
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
		document.pagineForm.action += "&keyAdd=" + keyAdd + "&chiaveWSDM=" + chiaveWSDM + "&entitaWSDM=" + entitaWSDM + "&comdatins=" + comdatins + "&cenint=" + cenint + "&idconfi=" + idconfi;
		selezionaPaginaDefault(pageNumber);
	}
	
	//si elimina l'orario dal valore
	var comdatins = "${comdatins }";
	if(comdatins!=null){
		var tmp = comdatins.split(" ");
		comdatins = tmp[0];
		formprotocollacomunicazione.comdatins.value=comdatins;
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
