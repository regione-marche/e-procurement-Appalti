<%
/*
 * Created on: 14/07/2010
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

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/jquery.documenti.gara.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>


<c:set var="ngara" value='${gene:getValCampo(key, "GARE.NGARA")}' />
<c:set var="genereGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction", pageContext, key)}' scope="request"/>

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" /> 
<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou231#") || fn:contains(listaOpzioniUtenteAbilitate, "ou232#")}' >
	<c:set var="abilitatoQeditor" value="true"/>
</c:if>


<c:set var="key" value='${param.key}'/>
<c:set var="gruppo" value='${param.gruppo}'/>
<c:set var="lottoFaseInvito" value='${param.lottoFaseInvito}'/>
<c:set var="isProceduraTelematica" value='${param.isProceduraTelematica}'/>


<c:set var="busta" value='${param.busta}'/>
<c:set var="titoloBusta" value='${param.titoloBusta}'/>



<c:choose> 
	<c:when test="${RISULTATO eq 'OK' }" >
		<c:set var="modoQform" value='VISQFORM_RETT' scope="request"/>
	</c:when>
	<c:when test="${!empty param.modoQform}" >
		<c:set var="modoQform" value='${param.modoQform}' scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="modoQform" value='${modoQform}' scope="request"/>
	</c:otherwise>
</c:choose>

<c:choose> 
	<c:when test="${!empty param.idQformRett}" >
		<c:set var="idQformRett" value='${param.idQformRett}' scope="request"/>
	</c:when>
	<c:when test="${!empty requestScope.idQformRett}" >
		<c:set var="idQformRett" value='${requestScope.idQformRett}' scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="idQformRett" value='${idQformRett}' scope="request"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${!empty param.autorizzatoModifiche}">
		<c:set var="autorizzatoModifiche" value='${param.autorizzatoModifiche}' />
	</c:when>
	<c:otherwise>
		<c:set var="autorizzatoModifiche" value='${autorizzatoModifiche}' />
	</c:otherwise>
</c:choose>

<c:if test="${modoQform eq 'INSQFORM_RETT'}">
	<c:set var="modo" value="MODIFICA" scope="request" />
</c:if>


<c:choose>
	<c:when test="${not empty busta}">
		${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ValidazioneParametroFunction", pageContext, ngara, "SC", "20")}
		${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ValidazioneParametroFunction", pageContext, busta, "N","")}
		
		<c:set var="whereQFORM" value="QFORM.ENTITA='GARE' AND QFORM.KEY1='${ngara}' AND QFORM.BUSTA=${busta}" />
	</c:when>
	<c:otherwise>
		${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ValidazioneParametroFunction", pageContext, ngara, "SC", "20")}
		
		<c:set var="whereQFORM" value="QFORM.ENTITA='GARE' AND QFORM.KEY1='${ngara}'" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${modoQform eq 'INSQFORM_RETT'}">
		<c:set var="whereQFORM" value="${whereQFORM } AND QFORM.STATO!=7 AND QFORM.STATO!=8"/>
	</c:when>
	<c:otherwise>
		<c:set var="whereQFORM" value="${whereQFORM } AND QFORM.STATO=7"/>
	</c:otherwise>
</c:choose>



<gene:template file="scheda-template.jsp" gestisciProtezioni="false" >
	
	<gene:redefineInsert name="head" >
		<script type="text/javascript" src="${contextPath}/js/common-gare.js"></script>
	</gene:redefineInsert>
	
	<gene:redefineInsert name="corpo">
	
	
	<gene:formScheda entita="GARE" gestisciProtezioni="true"  plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreQform" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreDocumentazioneGara">
		<c:if test="${autorizzatoModifiche eq '2' or garaPub}">
			<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
		</c:if>
				
		<gene:redefineInsert name="addToAzioni" >
			<c:if test='${modoQform eq "VISQFORM_RETT" and autorizzatoModifiche ne "2" and not garaPub and modo eq "VISUALIZZA"}' >
			  <tr>
				  <td class="vocemenulaterale" >
						  <a href="javascript:elimina();" title="Elimina">Elimina</a>
				  </td>
			  </tr>
			</c:if>
			<c:if test='${abilitatoQeditor eq "true" and modoQform eq "VISQFORM_RETT" and modo eq "VISUALIZZA" and autorizzatoModifiche ne "2"}'>
				<tr>
				  <td class="vocemenulaterale" >
						  <a href="javascript:qformEditor(${datiRiga.QFORM_ID },'QFORM','false');" title="Modifica Q-form" >Modifica q-form</a>
				  </td>
			  </tr>
			</c:if>	
			<c:if test='${ modoQform eq "VISQFORM_RETT" and modo eq "VISUALIZZA" and (abilitatoQeditor ne "true" or autorizzatoModifiche eq "2")}'>
				<tr>
				  <td class="vocemenulaterale" >
						  <a href="javascript:qformEditor(${datiRiga.QFORM_ID },'QFORM','true');" title="Visualizza Q-form" >Visualizza q-form</a>
				  </td>
			  </tr>
			</c:if>	
			<c:if test='${modoQform eq "VISQFORM_RETT" and modo eq "VISUALIZZA" }'>
				<tr>
				  <td class="vocemenulaterale" >
						  <a href="javascript:qformAnteprima(${datiRiga.QFORM_ID },'QFORM');" title="Anteprima Q-form" >Anteprima Q-form</a>
			  </tr>
			</c:if>	
		</gene:redefineInsert>
	<c:set var="ngara" value='${gene:getValCampo(key, "GARE.NGARA")}' />
	
	
	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>

	<gene:redefineInsert name="addHistory">
		<c:if test="${modo eq 'VISUALIZZA' or modoQform eq 'VISQFORM_RETT'}" > 
			<gene:historyAdd titolo="${titoloBusta} - Rettifica in corso" id="rqform ${titoloBusta}" replaceParam='metodo;apri;modo;VISUALIZZA;modoQform;VISQFORM_RETT' />
		</c:if>
	</gene:redefineInsert>	
	
	<gene:setString name="titoloMaschera" value="${titoloBusta} - Rettifica in corso"/>
	
	<gene:campoScheda campo="CODGAR1"  value="${codiceGara}" visibile="false" />
	
	<gene:campoScheda campo="NGARA"  visibile="false" />
	
	<gene:campoScheda campo="ENTITA"  visibile="false" value="GARE" entita="QFORM" where="${whereQFORM}"/>
	<gene:campoScheda campo="KEY1"  visibile="false" value="${ngara}" entita="QFORM" where="${whereQFORM}"/>
	<gene:campoScheda>
		<td colspan="2"><b>Modello di riferimento</b></td>
	</gene:campoScheda>
	
	<c:choose>
		<c:when test="${genereGara ne 10 and genereGara ne 20}">
			<c:set var="functionId" value="default"/>
			<c:set var="parametriWhere" value="N:${busta}"/>
		</c:when>
		<c:otherwise>
			<c:set var="functionId" value="elenco"/>
			<c:set var="parametriWhere" value=""/>
		</c:otherwise>
	</c:choose>
	
	<gene:archivio titolo="modelli qform"
		obbligatorio="true" 
		scollegabile="false"
		lista='geneweb/qformlib/qformlib-lista-popup.jsp' 
		scheda="" 
		schedaPopUp="" 
		campi="QFORMLIB.CODMODELLO;QFORMLIB.TITOLO;QFORMLIB.ID;QFORMLIB.DESCRIZIONE;QFORMLIB.TIPOLOGIA;QFORMLIB.DULTAGG" 
		chiave="QFORM.IDMODELLO"
		functionId = "${functionId}"
		parametriWhere = "${parametriWhere}"
		formName="formArchivioModelliQformlib">
		<gene:campoScheda campo="CODICE" title="Codice modello" campoFittizio="true" definizione="T20;;;;W_QFCODMOD" obbligatorio="true" value="${requestScope.initCodiceModello}" modificabile="${modoQform eq 'INSQFORM_RETT'}"/>
		<gene:campoScheda campo="TITOLO" title="Titolo modello" campoFittizio="true" definizione="T500;;;;W_QFTITOLO" value="${requestScope.initTitoloModello}" modificabile="${modoQform eq 'INSQFORM_RETT'}"/>
		<gene:campoScheda campo="IDMODELLO"  entita="QFORM" where="${whereQFORM}" visibile="false"/>
		<gene:campoScheda campo="DESC" title="desc modello" campoFittizio="true" definizione="T2000" visibile="false"/>
		<gene:campoScheda campo="TIPOLOGIA"  visibile="false" entita="QFORM" where="${whereQFORM}"/>
		<c:choose>
			<c:when test="${modoQform eq 'INSQFORM_RETT'}">
				<gene:campoScheda campo="DULTAGGMOD_FIT" title="Data ultima attivazione" campoFittizio="true" definizione="T20;;;;G1QFDATAT" value="${requestScope.initDultaggmod}" />
			</c:when>
			<c:otherwise>
				<gene:campoScheda campo="DULTAGGMOD"  entita="QFORM" where="${whereQFORM}" modificabile="false"/>
			</c:otherwise>
		</c:choose>
		
	</gene:archivio>
	
	
	<c:if test="${not empty requestScope.initDultagg and modo ne 'MODIFICA'}">
		<gene:campoScheda>
			<td class="etichetta-dato">Data ultima attivazione modello effettiva</td>
			<td class="valore-dato">
				<span id="DULTAGGview" title="Data ultima attivazione modello effettiva" >${requestScope.initDultagg}</span>
			</td>
		</gene:campoScheda>
		<gene:campoScheda>
			<td class="etichetta-dato"></td>
			<td class="valore-dato">
				<span id="msgview" style="color:#0000FF"><b>ATTENZIONE:</b> Il modello da  cui deriva il Q-form è stato aggiornato successivamente. Per allineare il Q-form al modello aggiornato è necessario eliminare tale Q-form e crearne uno nuovo. Se già pubblicato, va rettificato.</span>
			</td>
		</gene:campoScheda>
	</c:if>
	
	<gene:campoScheda>
		<td colspan="2"><b>Dati generali</b></td>
	</gene:campoScheda>
	<gene:campoScheda campo="BUSTA"  modificabile="false" value="${busta}" entita="QFORM" where="${whereQFORM}" visibile="${genereGara ne 10 and genereGara ne 20 }"/>
	<gene:campoScheda campo="TITOLO" entita="QFORM" where="${whereQFORM}"/>
	<gene:campoScheda campo="DESCRIZIONE"  entita="QFORM" where="${whereQFORM}"/>
	<c:choose>
		<c:when test="${modoQform eq 'INSQFORM_RETT' }">
			<gene:campoScheda campo="STATO"  modificabile="false" value="7" entita="QFORM" where="${whereQFORM}"/>
			<gene:campoScheda campo="DATPUB"  modificabile="false" value="" entita="QFORM" where="${whereQFORM}"/>
		</c:when>
		<c:otherwise>
			<gene:campoScheda campo="STATO"  modificabile="false" entita="QFORM" where="${whereQFORM}"/>
			<gene:campoScheda campo="DATPUB"  modificabile="false" entita="QFORM" where="${whereQFORM}"/>
		</c:otherwise>
	</c:choose>
	
	<gene:campoScheda campo="ID"  visibile="false" entita="QFORM" where="${whereQFORM}"/>
	<gene:campoScheda campo="OGGETTO"  visibile="false" entita="QFORM" where="${whereQFORM}"/>
	
	<gene:campoScheda>
		<td class="comandi-dettaglio" colSpan="2">
			<gene:insert name="addPulsanti"/>
			<c:choose>
			<c:when test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
				<gene:insert name="pulsanteSalva">
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma();">
				</gene:insert>
				<gene:insert name="pulsanteAnnulla">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla();">
				</gene:insert>
		
			</c:when>
			<c:otherwise>
				<gene:insert name="pulsanteModifica">
					<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") and autorizzatoModifiche ne "2" and not garaPub}'>
						<INPUT type="button"  class="bottone-azione" value='Modifica' title='Modifica' onclick="javascript:schedaModifica()">
					</c:if>
				</gene:insert>
				<c:if test='${modoQform eq "VISQFORM_RETT" and autorizzatoModifiche ne "2" and not garaPub}' >
					<INPUT type="button"  class="bottone-azione" value='Elimina' title='Elimina' onclick="javascript:elimina();">
				</c:if>
				<c:if test='${abilitatoQeditor eq "true" and autorizzatoModifiche ne "2"}'>
					<INPUT type="button"  class="bottone-azione" value='Modifica Q-form' title='Modifica Q-form' onclick="javascript:qformEditor(${datiRiga.QFORM_ID },'QFORM','false');">
				</c:if>
				<c:if test='${abilitatoQeditor ne "true" or autorizzatoModifiche eq "2"}'>
					<INPUT type="button"  class="bottone-azione" value='Visualizza Q-form' title='Visualizza Q-form' onclick="javascript:qformEditor(${datiRiga.QFORM_ID },'QFORM','true');">
				</c:if>
				<INPUT type="button"  class="bottone-azione" value='Anteprima Q-form' title='Anteprima Q-form' onclick="javascript:qformAnteprima(${datiRiga.QFORM_ID },'QFORM');">	
			</c:otherwise>
			</c:choose>	
			&nbsp;
		</td>
	</gene:campoScheda>
	
	<input type="hidden"  name="busta" value="${busta}">
	<input type="hidden"  name="titoloBusta" value="${titoloBusta}">
	<input type="hidden"  name="tipologiaDoc" value="${tipologiaDoc}">
	<input type="hidden"  name="gruppo" value="${gruppo}">
	<input type="hidden"  name="tipoDoc" value="${tipoDoc}">
	<input type="hidden"  name="codiceGara" value="${codiceGara}">
	<input type="hidden"  name="lottoFaseInvito" value="${lottoFaseInvito}">
	<input type="hidden"  name="isProceduraTelematica" value="${isProceduraTelematica}">
	<input type="hidden"  name="idconfi" value="${idconfi}">
	<input type="hidden"  name="idconfi" value="${idconfi}">
	<input type="hidden"  name="modoQform" value="${modoQform}">
	<input type="hidden"  name="autorizzatoModifiche" value="${autorizzatoModifiche}">
	<input type="hidden"  id="datiArchivioImpostati" name="datiArchivioImpostati" value="">
	<input type="hidden"  id="modoQform" name="modoQform" value="${modoQform}">
	<input type="hidden"  id="idQformRett" name="idQformRett" value="${idQformRett}">
	<input type="hidden"  id="archivioMod" name="archivioMod" value="NO">
	
		
</gene:formScheda>
		
		
</gene:redefineInsert>
<gene:javaScript>
	<c:if test="${modoQform eq 'INSQFORM_RETT' }" >
		function schedaAnnullaCustom(){
			//historyVaiIndietroDi(1);
			document.forms[0].modo.value="NUOVO";
			schedaAnnullaDefault();
		}
		var schedaAnnullaDefault = schedaAnnulla;
		var schedaAnnulla = schedaAnnullaCustom;	
	</c:if>
	
	$("#DULTAGGMOD_FIT").prop( "disabled", true );
	
	function aggiornaCampiNacostiDaArchivio(){
		var titolo=getValue("TITOLO");
		var desc=getValue("DESC");
		//var data=getValue("DULTAGG_FIT");
		setValue("QFORM_TITOLO",titolo);
		setValue("QFORM_DESCRIZIONE",desc);
		
		setTimeout(function(){
			var codicePadre="${requestScope.initCodiceModello}";
			var nuovoCodice = getValue("CODICE");
			var dataPadreString = "${requestScope.initDultaggmod}";
			var dataString=getValue("DULTAGGMOD_FIT");
			if(codicePadre!=nuovoCodice)
				$("#archivioMod").val("SI");
			else{
				var dataPadre = daStringaAData(dataPadreString);
				var data = daStringaAData(dataString);
				if(data.getTime()>dataPadre.getTime()){
					var msg="Il modello selezionato è lo stesso associato di default alla rettifica.\nConfermando la selezione, il Q-form della rettifica viene allineato a tale modello sovrascrivendo quello di origine.\nConfermi la selezione?";
					if(confirm(msg))
						$("#archivioMod").val("SI");
					else{
						$("#archivioMod").val("NO");
						setValue("DULTAGGMOD_FIT",dataPadreString);
					}
				}else{
					$("#archivioMod").val("NO");
				}
			}
		}, 150);
	}
	
			
	function schedaConfermaCustom(){
		$("#DULTAGGMOD_FIT").prop( "disabled", false );
		document.forms[0].jspPathTo.value="gare/documgara/qform-rettifica.jsp";
		schedaConfermaDefault();
	}		
	
	var schedaConfermaDefault = schedaConferma;
	var schedaConferma = schedaConfermaCustom;		
			
	function elimina(){
		if(confirm("Procedere con l'eliminazione del Q-form di rettifica?")){
			_wait();
			id = $('#QFORM_ID').val();
			$.ajax({
				type: "GET",
				dataType: "text",
				async: false,
				beforeSend: function (x) {
					if (x && x.overrideMimeType) {
						x.overrideMimeType("application/text");
					}
				},
				url: contextPath + "/pg/EliminaQform.do",
				data: {
					id: id
				},
				success: function (data) {
					if (data && data.length > 0) {
						historyVaiIndietroDi(1);
					}
				},
				error: function (e) {
					alert("Errore durante l'eliminazione del Q-form");
				},
				complete: function () {
					_nowait();
				}
			});
		}
		
	}
	
	
	
</gene:javaScript>
</gene:template>
