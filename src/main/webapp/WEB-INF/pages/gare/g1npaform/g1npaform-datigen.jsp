<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:choose>
	<c:when test="${not empty param.idForm}">
		<c:set var="idForm" value="${param.idForm}"/>
	</c:when>
	<c:otherwise>
		<c:set var="idForm" value="${idForm}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.modpres}">
		<c:set var="modpres" value="${param.modpres}"/>
	</c:when>
	<c:otherwise>
		<c:set var="modpres" value="${modpres}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.tipoDocumento}">
		<c:set var="tipoDocumento" value="${param.tipoDocumento}"/>
	</c:when>
	<c:otherwise>
		<c:set var="tipoDocumento" value="${tipoDocumento}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.numeroRettifiche}">
		<c:set var="numeroRettifiche" value="${param.numeroRettifiche}"/>
	</c:when>
	<c:otherwise>
		<c:set var="numeroRettifiche" value="${numeroRettifiche}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.uuid_eform_originale}">
		<c:set var="uuid_eform_originale" value="${param.uuid_eform_originale}"/>
	</c:when>
	<c:otherwise>
		<c:set var="uuid_eform_originale" value="${uuid_eform_originale}"/>
	</c:otherwise>
</c:choose>

<gene:formScheda entita="G1NPAFORM" gestisciProtezioni="true" 
	gestore="it.eldasoft.sil.pg.tags.gestori.submit.npa.GestoreG1NPAFORM" 
	plugin="it.eldasoft.sil.pg.tags.gestori.plugin.npa.GestoreInizializzazioniG1NPAFORM">

	<c:set var="sqlCheckExchange" value="SELECT CASE WHEN exchange_eform IS NOT NULL THEN 1 ELSE 0 END FROM g1npascheda WHERE id = ?"/>
	<c:set var="isExchangePresent" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, sqlCheckExchange, datiRiga.G1NPASCHEDA_ID)}'/>

	<c:if test="${modo ne 'NUOVO'}">
		<c:set var="sql" value="select codgar from g1npaform where id=?"/>
		<c:set var="codiceGara" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, sql, idForm)}'/>
	
		<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
			<jsp:param name="entita" value="V_GARE_TORN"/>
			<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
			<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
		</jsp:include>
	</c:if>

<c:set var="paginaModificabile" value='${statonpa eq 1 and autorizzatoModifiche ne "2"}'/>
<c:set var="isVisibileModifica" value='${paginaModificabile and inviabileMRP ne "1" and modo eq "VISUALIZZA" and (not empty numeroEFORM or isESPD eq "1") and gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD")}'/>
<c:set var="isVisibileElimina" value='${paginaModificabile and modo eq "VISUALIZZA" and gene:checkProtFunz(pageContext,"DEL","SCHEDAELIMINA")}'/>

<c:set var="urlFEUValorizzato" value='${not empty gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.bandoavvisosimap.ws.url")}'/>
<c:set var="urlMEFormsValorizzato" value='${not empty gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "integrazioneMEforms.url")}'/>
<c:set var="sseDisabled" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "integrazioneMEforms.sse.disable")}'/>

<gene:gruppoCampi idProtezioni="ANACFORM">
	<gene:campoScheda>
		<td colspan="2"><b>Tipologia scheda</b></td>
	</gene:campoScheda>
	<gene:campoScheda campo="ID" visibile="false"/>
	<gene:campoScheda campo="IDCFNPA" visibile="false" defaultValue="${param.formularioScelto}"/>
	<gene:campoScheda campo="CODGAR" visibile="false" defaultValue="${param.codiceGara}"/>

	<c:if test="${modo ne 'NUOVO'}">
		<gene:campoScheda campo="ID" entita="G1CF_NPA" where="G1CF_NPA.ID = G1NPAFORM.IDCFNPA" visibile="false"/>
		<gene:campoScheda campo="MODPRES" entita="G1CF_NPA" visibile="false"/>
		<gene:campoScheda campo="EFORM" entita="G1CF_NPA" visibile="false"/>
		<gene:campoScheda campo="ISESPD" entita="G1CF_NPA" visibile="false"/>
		<gene:campoScheda campo="ISNOFEU" entita="G1CF_NPA" visibile="false"/>
	</c:if>

	<gene:campoScheda campo="NOME_SCHEDA" entita="G1CF_NPA" modificabile="false" defaultValue="${initNOME_SCHEDA}"/>
	<gene:campoScheda campo="OGGETTO" entita="G1CF_NPA" modificabile="false" defaultValue="${initOGGETTO}"/>
	<gene:campoScheda campo="DESCRIZIONE" entita="G1CF_NPA" modificabile="false" defaultValue="${initDESCRIZIONE}"/>
	<gene:campoScheda campo="ISLOTTI" entita="G1CF_NPA" modificabile="false" visibile="false" defaultValue="${initISLOTTI}"/>
	<gene:campoScheda campo="ISESPDfitt" entita="G1CF_NPA" campoFittizio="true" modificabile="false" visibile="false" defaultValue="${isESPD}" definizione="T2;;;;" />
</gene:gruppoCampi>

<c:choose>
	<c:when test="${modpres eq 2}">
		<c:set var="titoloSchedaPCP" value="Riepilogo schede PCP"/>
	</c:when>
	<c:otherwise>
		<c:set var="titoloSchedaPCP" value="Interoperabilità PCP"/>
	</c:otherwise>
</c:choose>

<c:if test="${modo ne 'NUOVO'}">
	<gene:gruppoCampi idProtezioni="SCHEDAPCP">
		<gene:campoScheda>
			<td colspan="2"><b>${titoloSchedaPCP}</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="ID" entita="G1NPASCHEDA" where="G1NPASCHEDA.IDFORM = G1NPAFORM.ID" visibile="false"/>
		<gene:campoScheda campo="IDSCHEDANPARET" entita="G1NPASCHEDA" visibile="false"/>
		<gene:campoScheda campo="IDSCHEDANPA" entita="G1NPASCHEDA" modificabile="false"/>
		<gene:campoScheda campo="STATONPA" entita="G1NPASCHEDA" modificabile="false"/>
		<gene:campoScheda campo="ISNOCANC" entita="G1NPASCHEDA" modificabile="false" visibile="false"/>
		<gene:campoScheda campo="INVIABILE" entita="G1NPASCHEDA" visibile="false"/>
		<gene:campoScheda title="Gestione in M-RP" campo="INVIABILEfitt" campoFittizio="true" definizione="T2;;;;G1INVIAMRP;" modificabile="false" value="${datiRiga.G1NPASCHEDA_INVIABILE eq '1'?'Si':'No'}" visibile="${gene:callFunction('it.eldasoft.gene.tags.functions.GetPropertyFunction', 'mrp.abilita') eq '1'}"/>
		<gene:campoScheda campo="IDPRG_EFORM" entita="G1NPASCHEDA" modificabile="false" visibile="false"/>
		<gene:campoScheda campo="IDDOC_EFORM" entita="G1NPASCHEDA" modificabile="false" visibile="false"/>
		<gene:campoScheda campo="IDPRG_ESPD" entita="G1NPASCHEDA" modificabile="false" visibile="false"/>
		<gene:campoScheda campo="IDDOC_ESPD" entita="G1NPASCHEDA" modificabile="false" visibile="false"/>
		
	</gene:gruppoCampi>
	<c:if test="${not empty numeroEFORM}">

		<c:set var="idprgEFORM" value="${documentoEFORM[0]}"/>
		<c:set var="iddocEFORM" value="${documentoEFORM[1]}"/>
		<c:set var="nomeDocEFORM" value="${documentoEFORM[2]}"/>
		<c:set var="descrizioneDocEFORM" value="${documentoEFORM[3]}"/>
		<c:set var="isPresenteDocumentoEFORM" value="${not empty idprgEFORM and not empty iddocEFORM and not empty nomeDocEFORM}"/>

		<jsp:include page="/WEB-INF/pages/gare/g1npaform/modaleFEU.jsp">
			<jsp:param name="idForm" value="${idForm}"/>
			<jsp:param name="codgar" value="${codiceGara}"/>
			<jsp:param name="isPresenteDocumentoEFORM" value="${isPresenteDocumentoEFORM}"/>
			<jsp:param name="numeroRettifiche" value="${numeroRettifiche}"/>
		</jsp:include>

		<gene:gruppoCampi idProtezioni="EFORM">
		
			<gene:campoScheda>
				<td colspan="2">

				<b>eForm pubblicazione TED</b>

				<c:if test="${modo eq 'MODIFICA' and isPresenteDocumentoEFORM and empty datiRiga.G1NPASCHEDA_UUID_EFORM}">
				<a style="float:right;" id="eliminaFileEFORM" class="link-generico"
				href="javascript:eliminaFile('EFORM');" title="Elimina">
				Elimina
				<img src="${pageContext.request.contextPath}/img/opzioni_del.gif" height="16" width="16" alt="Elimina File EFORM">
				</a>
				</c:if>

				</td>

			</gene:campoScheda>
			<c:if test="${isPresenteDocumentoEFORM and modo eq 'VISUALIZZA'}">
				<gene:campoScheda title="Descrizione" modificabile="false">
					${descrizioneDocEFORM}
				</gene:campoScheda>
			</c:if>

			<gene:campoScheda campo="DIGNOMDOC" entita="W_DOCDIG" from="G1NPASCHEDA" id="nomeFileEFORM"
				href="javascript:visualizzaFileAllegato('${idprgEFORM}','${iddocEFORM}','${nomeDocEFORM}');" modificabile="false" value="${nomeDocEFORM }"
				where="G1NPASCHEDA.IDFORM = G1NPAFORM.ID and G1NPASCHEDA.IDPRG_EFORM = W_DOCDIG.IDPRG and G1NPASCHEDA.IDDOC_EFORM = W_DOCDIG.IDDOCDIG">
				<c:if test="${paginaModificabile and modo eq 'VISUALIZZA' and urlMEFormsValorizzato}">
					<c:if test="${isExchangePresent ne '1' && datiRiga.G1NPASCHEDA_STATONPA eq '1'}">
						<a style="float:right;" id="genera_Eform" href="javascript:generaEform('${datiRiga.G1NPASCHEDA_ID}');">
							Genera con M-EFORMS
						</a>
						<c:if test="${sseDisabled eq '1'}">
							<a style="float:right;margin-right: 15px;display:none" id="rileggi_Eform" href="javascript:visualizzaFormNPA('${idForm}','${param.islotti}','${modpres}');">
								Rileggi dati
							</a>
						</c:if>
					</c:if>
					<c:if test="${isExchangePresent eq '1' && datiRiga.G1NPASCHEDA_STATONPA eq '1'}">
						<a style="float:right;" id="aggiorna_Eform" href="javascript:generaEform('${datiRiga.G1NPASCHEDA_ID}');">
							Apri con M-EFORMS
						</a>
					</c:if>
				</c:if>
			</gene:campoScheda>
			<gene:campoScheda campo="DNOT_EFORM" entita="G1NPASCHEDA" modificabile="false" visibile="${not empty datiRiga.G1NPASCHEDA_DNOT_EFORM}"/>
			<c:if test='${modo eq "MODIFICA"}'>
				<gene:campoScheda id="campoUploadEFORM" title="Nome file" visibile="${empty datiRiga.G1NPASCHEDA_UUID_EFORM}">
					<input type="file" id="selFile[0]" name="selFile[0]" onchange="javascript:scegliFile('EFORM');" class="file"
					size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" />
				</gene:campoScheda>
				<gene:campoScheda id="campoUploadEFORM" title="Nome file" visibile="${not empty datiRiga.G1NPASCHEDA_UUID_EFORM}" modificabile="false"/>
			</c:if>

			<gene:campoScheda campo="UUID_EFORM" entita="G1NPASCHEDA" visibile="${not empty datiRiga.G1NPASCHEDA_UUID_EFORM}" modificabile="false">
				<c:if test="${paginaModificabile and modo ne 'VISUALIZZA' and urlMEFormsValorizzato}">
					<c:if test="${datiRiga.G1NPASCHEDA_STATONPA eq '1'}">
						<a style="float:right;" id="elimina_Eform" href="javascript:eliminaMEform('${datiRiga.G1NPASCHEDA_ID}');">
							Elimina da M-EFORMS
						</a>
					</c:if>
				</c:if>
			</gene:campoScheda>

			<gene:campoScheda campo="UUID_EFORM_ORIGINALE" entita="G1NPASCHEDA" campoFittizio="true" title="Identificativo avviso originale" visibile="${not empty datiRiga.G1NPASCHEDA_IDSCHEDANPARET}" modificabile="false"
				value="${(empty uuid_eform_originale) ? datiRiga.G1NPASCHEDA_UUID_EFORM : uuid_eform_originale}" definizione="T100;;;"/>
		</gene:gruppoCampi>
	</c:if>
	
	<c:if test="${isESPD eq '1' || isESPD eq '2'}"> 
		<c:set var="idprgESPD" value="${documentoESPD[0]}"/>
		<c:set var="iddocESPD" value="${documentoESPD[1]}"/>
		<c:set var="nomeDocESPD" value="${documentoESPD[2]}"/>
		<c:set var="descrizioneDocESPD" value="${documentoESPD[3]}"/>
		<c:set var="eliminabileDocESPD" value="${documentoESPD[4]}"/>
		<c:set var="isPresenteDocumentoESPD" value="${not empty idprgESPD and not empty iddocESPD and not empty nomeDocESPD}"/>
		<gene:gruppoCampi idProtezioni="ESPDDGUE">
		
			<gene:campoScheda>
				<td colspan="2">
				<b>ESPD / DGUE</b>

				<c:if test="${modo eq 'MODIFICA' and isPresenteDocumentoESPD and isESPD ne '2'}">
				<a style="float:right;" id="eliminaFileESPD" class="link-generico"
				href="javascript:eliminaFile('ESPD');" title="Elimina">
				Elimina
				<img src="${pageContext.request.contextPath}/img/opzioni_del.gif" height="16" width="16" alt="Elimina file ESPD/DGUE">
				</a>
				</c:if>

				</td>
			</gene:campoScheda>
			<c:if test="${isPresenteDocumentoESPD and modo eq 'VISUALIZZA'}">
				<gene:campoScheda title="Descrizione" modificabile="false">
					${descrizioneDocESPD}
				</gene:campoScheda>
			</c:if>
			<gene:campoScheda campo="DIGNOMDOC" entita="W_DOCDIG" from="G1NPASCHEDA" id="nomeFileESPD"
				href="javascript:visualizzaFileAllegato('${idprgESPD}','${iddocESPD}','${nomeDocESPD}');" modificabile="false" value="${nomeDocESPD }"
				where="G1NPASCHEDA.IDFORM = G1NPAFORM.ID and G1NPASCHEDA.IDPRG_ESPD = W_DOCDIG.IDPRG and G1NPASCHEDA.IDDOC_ESPD = W_DOCDIG.IDDOCDIG">
				<c:choose>
					<c:when test='${isPresenteDocumentoESPD and modo eq "VISUALIZZA"}'>
						<a style="float:right;" id="apri_MGUE_${param.contatore}" 
						href="javascript:apriConMDGUE('<c:out value="${datiRiga.G1NPAFORM_CODGAR}" />', '${datiRiga.G1NPASCHEDA_IDPRG_ESPD}', '${datiRiga.G1NPASCHEDA_IDDOC_ESPD}' );">
						Apri con M-DGUE
						</a>
					</c:when>
					<c:otherwise>
						<c:if test='${modo eq "VISUALIZZA" and paginaModificabile}'>
						<a style="float:right;" id="genera_MGUE_${param.contatore}" 
						href="javascript:generaConMDGUE('${datiRiga.G1NPAFORM_CODGAR}', 'PG', '');">
						Genera con M-DGUE
						</a>
						</c:if>
					</c:otherwise>
				</c:choose>
			</gene:campoScheda>
			<c:if test='${modo eq "MODIFICA" and isEspd ne "2"}'>
				<gene:campoScheda id="campoUploadESPD" title="Nome file">
					<input type="file" id="selFile[1]" name="selFile[1]" onchange="javascript:scegliFile('ESPD');" class="file"
						 size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" />
					<a style="float:right;" id="genera_MGUE_${param.contatore}" 
					href="javascript:generaConMDGUE('${datiRiga.G1NPAFORM_CODGAR}', 'PG', '');">
					Genera con M-DGUE
					</a>
				</gene:campoScheda>
			</c:if>
		</gene:gruppoCampi>
	</c:if>
</c:if>

<input type="hidden" name="isInseritoEFORM" id="isInseritoEFORM" value=""/>
<input type="hidden" name="isCancellatoEFORM" id="isCancellatoEFORM" value=""/>
<input type="hidden" name="nomeDocumentoEFORM" id="nomeDocumentoEFORM" value=""/>
<input type="hidden" name="isInseritoESPD" id="isInseritoESPD" value=""/>
<input type="hidden" name="isCancellatoESPD" id="isCancellatoESPD" value=""/>
<input type="hidden" name="nomeDocumentoESPD" id="nomeDocumentoESPD" value=""/>
<input type="hidden" name="sbiancaUUIDEFORM" id="sbiancaUUIDEFORM" value=""/>

<gene:campoScheda>

<tr>
	<td class="comandi-dettaglio" colSpan="2">
		<gene:insert name="addPulsanti"/>
		<c:if test="${isVisibileModifica or isVisibileElimina}">
			<c:if test='${isVisibileModifica}'>
				<INPUT type="button"  class="bottone-azione" value='Modifica' title='Modifica' onclick="javascript:schedaModifica();">
			</c:if>
			<c:if test='${isVisibileElimina}'>
				<gene:insert name="pulsanteElimina">
					<INPUT type="button"  class="bottone-azione" value='Elimina' title='Elimina' onclick="javascript:schedaElimina();">
				</gene:insert>
			</c:if>
			&nbsp;<br><br>
		</c:if>
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
			<c:if test="${gene:checkProtFunz(pageContext, 'ALT', 'VISUALIZZACONTROLLA')}">
				<c:choose>
				<c:when test="${not paginaModificabile}">
					<INPUT type="button" class="bottone-azione" id="button_visualizzacontrolla" value="Visualizza" title="Visualizza" onclick="visualizzaControlla('${datiRiga.G1NPAFORM_CODGAR}','${datiRiga.G1CF_NPA_NOME_SCHEDA}','visualizza','${datiRiga.G1NPASCHEDA_ID}');">
				</c:when>
				<c:otherwise>
					<INPUT type="button" class="bottone-azione" id="button_visualizzacontrolla" value="Visualizza e controlla" title="Visualizza e controlla" onclick="visualizzaControlla('${datiRiga.G1NPAFORM_CODGAR}','${datiRiga.G1CF_NPA_NOME_SCHEDA}','check','${datiRiga.G1NPASCHEDA_ID}');">
				</c:otherwise>
				</c:choose>
			</c:if>
			<c:if test="${gene:checkProtFunz(pageContext, 'ALT', 'INVIAPCP')}">
				<INPUT type="button" class="bottone-azione" id="button_inviapcp" value="Invia ad ANAC" title="Invia ad ANAC" onclick="visualizzaControlla('${datiRiga.G1NPAFORM_CODGAR}','${datiRiga.G1CF_NPA_NOME_SCHEDA}','invio','${datiRiga.G1NPASCHEDA_ID}');"">
			</c:if>
		</c:otherwise>
		</c:choose>
		&nbsp;
	</td>
</tr>
</gene:campoScheda>

<gene:redefineInsert name="schedaNuovo" />
<gene:redefineInsert name="schedaModifica" />

<gene:redefineInsert name="addToAzioni" >
<c:if test="${paginaModificabile and modo eq 'VISUALIZZA'}">

<c:if test='${isVisibileModifica}'>
	<tr>
		<td class="vocemenulaterale" >
			<a href="javascript:schedaModifica()" title="Modifica">
				Modifica
			</a>
		</td>
	</tr>
</c:if>
<c:if test='${isVisibileElimina}'>
	<tr>
		<td class="vocemenulaterale" >
			<a href="javascript:schedaElimina()" title="Elimina">
				Elimina
			</a>
		</td>
	</tr>
</c:if>
</c:if>
<c:if test="${modo eq 'VISUALIZZA' and gene:checkProtFunz(pageContext, 'ALT', 'VISUALIZZACONTROLLA')}">
<tr id="visualizzaControllaMenuLaterale">
	<td class="vocemenulaterale" >
		<c:choose>
			<c:when test="${not paginaModificabile}">
				<a href="javascript:visualizzaControlla('${datiRiga.G1NPAFORM_CODGAR}','${datiRiga.G1CF_NPA_NOME_SCHEDA}','visualizza','${datiRiga.G1NPASCHEDA_ID}');" title="Visualizza">
					Visualizza
				</a>
			</c:when>
			<c:otherwise>
				<a href="javascript:visualizzaControlla('${datiRiga.G1NPAFORM_CODGAR}','${datiRiga.G1CF_NPA_NOME_SCHEDA}','check','${datiRiga.G1NPASCHEDA_ID}');" title="Visualizza e controlla">
					Visualizza e controlla
				</a>
			</c:otherwise>
		</c:choose>	
	</td>
</tr>
</c:if>
<c:if test="${modo eq 'VISUALIZZA' and gene:checkProtFunz(pageContext, 'ALT', 'INVIAPCP')}">
	<tr>
		<td class="vocemenulaterale" >
			<a href="javascript:visualizzaControlla('${datiRiga.G1NPAFORM_CODGAR}','${datiRiga.G1CF_NPA_NOME_SCHEDA}','invio','${datiRiga.G1NPASCHEDA_ID}');" title="Invia ad ANAC">
				Invia ad ANAC
			</a>
		</td>
	</tr>
</c:if>

</gene:redefineInsert>

</gene:formScheda>

<gene:javaScript>


<c:if test="${modo ne 'NUOVO'}">

function schedaElimina() {
	if (${datiRiga.G1NPASCHEDA_ISNOCANC eq 1}) {
		alert("Non è possibile procedere all'eliminazione della scheda in quanto è stato modificato l'importo di gara");
		return;
	}
	if(confirm("Procedere con l'eliminazione della scheda?")){
		$.ajax({
			type: "POST",
			dataType: "json",
			async: false,
			beforeSend: function(x) {
				if(x && x.overrideMimeType) {
					x.overrideMimeType("application/json;charset=UTF-8");
				}
			},
			url: "${contextPath}/pg/EliminaAnacFormAJAX.do?idForm=${idForm}",
			error: function(e){
				alert("Impossibile eliminare questo form, sono presenti schede in stato non conforme con l'eliminazione");
			},
			success: function(e) {
				historyVaiIndietroDi(1);
			}
		});
	}
}

function controlloTipoFile(nomeFile, estensioniConsentite){
	var estensioneValida=false;
	if(estensioniConsentite!=null && estensioniConsentite!=""){
		var estensioni=estensioniConsentite.split(";");
		nomeFile = nomeFile.toUpperCase();
		if(nomeFile==null || nomeFile==""){
			estensioneValida=true;
		}else{
			for(var i=0;i<estensioni.length;i++){
				if(estensioni[i]!=null && estensioni[i]!=""){
					if(nomeFile.lastIndexOf(estensioni[i].toUpperCase())>0){
						estensioneValida=true;
						break;
					}
				}
			}
		}
	}else{
		estensioneValida=true;
	}
	return estensioneValida;
}

function insDocumentoMDGUE(ngara,codgar,gruppo,isProceduraTelematica,tipologia){
	var href = "href=gare/documgara/popupCreaDocumentoMDGUE.jsp";
	href += "?ngara=" + ngara + "&codgar=" + codgar+  "&gruppo=" + gruppo + "&isProceduraTelematica=" + isProceduraTelematica + "&tipologia=" + tipologia;
	openPopUpCustom(href, "creaDocumentoMDGUE", 600, 300, "no", "no");
}

function generaConMDGUE(codiceGara,idprg,iddocdig){
	var getUrl = window.location;
	var baseUrl = getUrl .protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1]+'/';
	console.log('baseUrl: '+baseUrl);
	var a = document.createElement("a");
	a.target = 'blank';
	var href = baseUrl+'pg/DgueGenToken.do?codiceGara='+codiceGara+'&idprg='+idprg
	if(iddocdig)
		href += '&iddocdig='+iddocdig;
	a.href = href;
	document.body.appendChild(a);
	a.click();
	document.body.removeChild(a);
}

function apriConMDGUE(codiceGara,idprg,iddocdig){
	var getUrl = window.location;
	var baseUrl = getUrl .protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1]+'/';
	console.log('baseUrl: '+baseUrl);
	var a = document.createElement("a");
	a.target = 'blank';
	var href = baseUrl+'pg/DgueGenToken.do?codiceGara='+codiceGara+'&idprg='+idprg+'&apri=true';
	if(iddocdig)
		href += '&iddocdig='+iddocdig;
	a.href = href;
	document.body.appendChild(a);
	a.click();
	document.body.removeChild(a);
}

function isValidFilename(filename) {
	var reg = new RegExp("^[A-Za-z0-9-_()\\s\\.\\[\\]]+$", "g");
	return (reg.test(filename));
}

<c:if test="${modo eq 'MODIFICA'}">
	document.forms[0].encoding="multipart/form-data";
</c:if>

function scegliFile(sezione) {

	if (sezione == "EFORM"){
		var indiceDocumento = 0;
	} else if (sezione == "ESPD"){
		var indiceDocumento = 1;
	}

	selezioneFile = document.getElementById("selFile[" + indiceDocumento + "]" ).value;
	lunghezza_stringa=selezioneFile.length;
	posizione_barra=selezioneFile.lastIndexOf("\\");
	nome=selezioneFile.substring(posizione_barra+1,lunghezza_stringa).toUpperCase();

	var formatoAllegati = 'XML';
	if(!controlloTipoFile(nome,formatoAllegati)){
		alert("Il formato del file selezionato non è valido.\nI formati consentiti sono: " + formatoAllegati);
		document.getElementById("selFile[" + indiceDocumento + "]" ).value = "";
		return;
	}
	if( !isValidFilename(nome)) {
		alert("Il nome del file contiene caratteri speciali non consentiti!\nUnici caratteri speciali ammessi sono -_()[]");
		document.getElementById("selFile[" + indiceDocumento + "]" ).value = "";
		return;
	}
	if(nome.length>100){
		alert("Il nome del file non può superare i 100 caratteri!");
		document.getElementById("selFile[" + indiceDocumento + "]" ).value = "";
	}else{
		$('#isInserito' + sezione).val('1');
		$('#nomeDocumento' + sezione).val(nome);
	}
}

function eliminaFile(sezione) {
	<c:if test="${eliminabileDocESPD ne 'true'}">
		if(sezione==='ESPD'){
			alert("Impossibile eliminare il file in quanto risulta pubblicato sul Portale. Archiviare tale file prima di procedere con l'eliminazione");
			return;
		}
	</c:if>

	if (confirm("Procedere con l'eliminazione ?")){
		$('#isCancellato' + sezione).val('1');
		$('#rownomeFile' + sezione).hide();
		$('#eliminaFile' + sezione).hide();
		$('#rowcampoUpload' + sezione).show();
		if (sezione == "EFORM") {
			$('#rowG1NPASCHEDA_DNOT_EFORM').hide();
		}
	}
}

var schedaConfermaOld=schedaConferma;

schedaConferma=function(){
	if (${modo eq "MODIFICA"}){
		if ($('#isInseritoEFORM').val() == '1' || $('#isCancellatoEFORM').val() == 1 || $('#isInseritoESPD').val() == '1' || $('#isCancellatoESPD').val() == 1) {
			document.forms[0].action = "${pageContext.request.contextPath}/pg/Scheda.do";
			schedaConfermaOld();
		} else {
			schedaConfermaOld();
		}
	} else {
		schedaConfermaOld();
	}
}

function visualizzaFileAllegato(idprg,iddocdig,dignomdoc) {
	var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
	document.location.href=href+"?idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + dignomdoc;
}

$(document).ready(function() {
<c:if test="${modo eq 'MODIFICA'}">
	<c:set var="isDocumentoGiaAggiunto" value="${idprgEFORM ne '' and not empty idprgEFORM and iddocEFORM ne '' and not empty iddocEFORM}"/>
	<c:choose>
		<c:when test="${isDocumentoGiaAggiunto}">
			$('#rowcampoUploadEFORM').hide();
			$('#rownomeFileEFORM').show();
		</c:when>
		<c:otherwise>
			$('#rowcampoUploadEFORM').show();
			$('#rownomeFileEFORM').hide();
		</c:otherwise>
	</c:choose>
	<c:set var="isDocumentoGiaAggiunto" value="${idprgESPD ne '' and not empty idprgESPD and iddocESPD ne '' and not empty iddocESPD}"/>
	<c:choose>
		<c:when test="${isDocumentoGiaAggiunto}">
			$('#rowcampoUploadESPD').hide();
			$('#rownomeFileESPD').show();
		</c:when>
		<c:otherwise>
			$('#rowcampoUploadESPD').show();
			$('#rownomeFileESPD').hide();
		</c:otherwise>
	</c:choose>
</c:if>
	<c:if test="${statonpa eq '1' && inviabileMRP eq '1'}">
		$('#G1NPASCHEDA_STATONPAview').text($('#G1NPASCHEDA_STATONPAview').text()+" (passata in gestione in M-RP)");
	</c:if>
});

function initInvioPCP(modalita){
	formInitInvioPCP.key.value="G1NPASCHEDA.ID=N:"+$('#G1NPASCHEDA_ID').val()
	formInitInvioPCP.modalita.value=modalita;
	formInitInvioPCP.submit();
}

function aggiungiLottiScheda() {
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "${contextPath}/pg/AggiungiLottiSchedaAJAX.do?idForm=${idForm}",
		error: function(e){
			alert("Errore durante la creazione dei lotti collegati alla scheda PCP");
		}
	});
}

function inserisciNPA(codgar, scheda, mode, idscheda) {
	_wait();
	$.ajax({
		url: '${contextPath}/pg/InserisciJsonNpa.do',
		type: 'POST',
		async: true,
		dataType: 'json',
		data: {
				codgar: codgar,
				scheda: scheda,
				idscheda: idscheda
			},
		success: function(data) {
			var response = data.message;	
			if(response != null){			
				alert('Errore riscontrato in fase di generazione della scheda Anac');
				console.log(response);
			} else {
				initInvioPCP(mode)
			}
		
		},
		error: function(e){
			alert('Errore riscontrato in fase di generazione della scheda Anac');
			console.log(e);
		},
		complete: function () {
			_nowait();
		}
		
	});
}

function eliminaMEform(idscheda) {
	console.log('${contextPath}');
	if(confirm("Procedere con l'eliminazione del M-eform?")){
		_wait();
		$.ajax({
			url: '${contextPath}/pg/EliminaBozzaMEForm.do',
			type: 'POST',
			async: true,
			dataType: 'json',
			data: {
					idscheda: idscheda
				},
			success: function(data) {
				bloccaRichiesteServer();
				window.location.reload();
			},
			error: function(e){
				alert('Errore riscontrato in fase di eliminazione bozza');
				console.log(e);
			},
			complete: function () {
				_nowait();
			}
		
		});
	}
}




function _wait() {
	document.getElementById('bloccaScreen').style.visibility = 'visible';
	$('#bloccaScreen').css("width", $(document).width());
	$('#bloccaScreen').css("height", $(document).height());
	document.getElementById('wait').style.visibility = 'visible';
	$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200 });
}

function _nowait() {
	document.getElementById('bloccaScreen').style.visibility = 'hidden';
	document.getElementById('wait').style.visibility = 'hidden';
}

function visualizzaControlla(codgar, scheda, mode, idscheda) {
	bloccaRichiesteServer();
	if(${datiRiga.G1NPASCHEDA_STATONPA eq '1'}) {
		aggiungiLottiScheda();
		inserisciNPA(codgar, scheda, mode, idscheda);
	}else{
		initInvioPCP(mode)
	}
}

function scollegaFEU() {
	if (confirm("Sbiancando questo identificativo viene annullato il collegamento con l'eForm in FEU.\r\n"
	+ "Procedendo con il salvataggio della pagina, non sarà più possibile ripristinare il collegamento.\r\n\r\n"
	+ "Confermi l'operazione?")) {
		$('#sbiancaUUIDEFORM').val('1');
		$('#rowG1NPASCHEDA_UUID_EFORM').hide();
	}
}

$(document).ready(function() {
	document.forms[0].action = "${contextPath}/Scheda.do";
	if (${datiRiga.G1CF_NPA_NOME_SCHEDA eq 'P1_15_1'}) {
		$('#visualizzaControllaMenuLaterale').hide();
		$('#button_visualizzacontrolla').hide();
	}
	if (${empty datiRiga.G1NPASCHEDA_UUID_EFORM and empty datiRiga.G1NPASCHEDA_IDSCHEDANPARET}) {
		$('#radioLeggiXML').prop('disabled','true');
		$('#radioAccediFEU').prop('disabled','true');
		$('#radioInviaBandoAvviso').click();
	} else {
		$('#radioInviaBandoAvviso').prop('disabled','true');
		$('#radioLeggiXML').click();
	}

	<c:if test="${not empty datiRiga.G1NPASCHEDA_UUID_EFORM}">
		$('#rileggi_Eform').show();
	</c:if>
});

function generaEform(idscheda){
    aggiungiLottiScheda();

    var getUrl = window.location;
    var baseUrl = getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1]+'/';
    
    var a = document.createElement("a");
    a.target = 'blank';
    var href = baseUrl+'pg/GetTokenEForms.do?idscheda='+idscheda+'&urlChiamante='+baseUrl;
    a.href = href;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
	$('#rileggi_Eform').show();
    

}

var idschedaGlobal = $('#G1NPASCHEDA_ID').val();
<c:if test="${sseDisabled ne '1'}">
if (typeof(EventSource) !== "undefined") {
	var source = new EventSource("sse-stream");
		source.onmessage = function(event) {
		var data = JSON.parse(event.data);
		if (data.codiceEform === idschedaGlobal) {
			<c:if test="${modo ne 'MODIFICA'}">
				bloccaRichiesteServer();
				setTimeout(function(){historyVaiIndietroDi(0);}, 1000);
			</c:if>
		}
	};
} else {
	console.log('Il tuo browser non supporta SSE.');
}
</c:if>

</c:if>

function visualizzaFormNPA(idForm,islotti,modpres) {
	formVisualizzaFormNPA.key.value = "G1NPAFORM.ID=N:" + idForm;
	formVisualizzaFormNPA.idForm.value = idForm;
	formVisualizzaFormNPA.islotti.value = islotti;
	formVisualizzaFormNPA.modpres.value = modpres;
	formVisualizzaFormNPA.submit();
}


</gene:javaScript>

<form name="formModificaDocumento"  action="${contextPath}/Scheda.do" method="post">
<input type="hidden" name="jspPath" value="/WEB-INF/pages/gare/g1npaform/g1npaform-scheda.jsp" /> 
<input type="hidden" name="jspPathTo" value="/WEB-INF/pages/gare/g1npaform/g1npaform-scheda.jsp" /> 
<input type="hidden" name="activePage" value="0" /> 
<input type="hidden" name="isPopUp" value="0" /> 
<input type="hidden" name="numeroPopUp" value="0" /> 
<input type="hidden" name="metodo" value="modifica" /> 
<input type="hidden" name="entita" value="G1NPAFORM" /> 
<input type="hidden" name="gestisciProtezioni" value="1" />
<input type="hidden" name="key" value="G1NPAFORM.ID=N:${gene:getValCampo(key, 'G1NPAFORM.ID')}" />
<input type="hidden" name="tipoDocumento" value="" />
<input type="hidden" name="entitaPrincipaleModificabile" value="1" />
</form>

<form name="formInitInvioPCP"  action="${contextPath}/pg/InitInvioPCP.do" method="post">
	<input type="hidden" name="key" value="" />
	<input type="hidden" name="modalita" value="" />
</form>

<form name="formLoginFEU" target="_blank" action="${contextPath}/pg/LoginFEU.do" method="post">
	<input type="hidden" name="credenziali" value="">
	<input type="hidden" name="username" value="">
	<input type="hidden" name="password" value="">
</form>

<form name="formVisualizzaFormNPA" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/g1npaform/g1npaform-scheda.jsp" />
	<input type="hidden" name="key" value="" />
	<input type="hidden" name="codiceGara" value="${codiceGara}" />
	<input type="hidden" name="idForm" value="" />
	<input type="hidden" name="islotti" value="" />
	<input type="hidden" name="modpres" value="" />
</form>

