<%--
/*
 * Created on: 10-apr-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
Scheda categorie d'iscrizione

--%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="opzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" scope="request"/>
<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="ARCHDOCG-scheda">

	<c:choose>
		<c:when test="${param.gruppoSelezionato eq '1'}">
			<gene:setString name="titoloMaschera" value="Dettaglio documento del bando" />
		</c:when>
		<c:when test="${param.gruppoSelezionato eq '6'}">
			<gene:setString name="titoloMaschera" value="Dettaglio documento dell'invito" />
		</c:when>
		<c:when test="${param.gruppoSelezionato eq '2'}">
			<gene:setString name="titoloMaschera" value="Dettaglio requisito dei concorrenti" />
		</c:when>
		<c:when test="${param.gruppoSelezionato eq '3'}">
			<gene:setString name="titoloMaschera" value="Dettaglio documento richiesto ai concorrenti" />
		</c:when>
		<c:when test="${param.gruppoSelezionato eq '4'}">
			<gene:setString name="titoloMaschera" value="Dettaglio documento dell'esito" />
		</c:when>
		<c:otherwise>
			<gene:setString name="titoloMaschera" value="Dettaglio documentazione di gara" />
		</c:otherwise>
	</c:choose>

	<gene:redefineInsert name="schedaNuovo">
		<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
			<tr>
				<td class="vocemenulaterale">
					<c:if test='${isNavigazioneDisattiva ne "1"}'><a href="javascript:schedaNuovoDocArchivio();" title='Nuovo' tabindex="1501"></c:if>
					Nuovo
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
		</c:if>
	</gene:redefineInsert>
	
	
	<c:set var="gruppo" value="${param.gruppoSelezionato}" />
	<c:choose>
		<c:when test="${param.gruppoSelezionato eq '2'}">
			<c:set var="sezDati" value="Dati requisito"/>
		</c:when>
		<c:otherwise>
			<c:set var="sezDati" value="Dati documento"/>
		</c:otherwise>
	</c:choose>
	
	
	<gene:redefineInsert name="corpo">
	
		<gene:formScheda entita="ARCHDOCG" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreARCHDOCG">
			<gene:campoScheda campo="CODARCH" obbligatorio="true" visibile="false" value="DOC01"/>
			<gene:campoScheda campo="NUMDOCG" obbligatorio="true" visibile="false" />
			<gene:campoScheda>
				<td colspan="2">
					<b>Caratteristiche della gara</b>
				</td>
			</gene:campoScheda>
			<gene:campoScheda campo="TIPOGARA" />
			<gene:campoScheda campo="TIPOPROC" />
			<gene:campoScheda campo="GRUPPO" visibile="false" obbligatorio="true" />
			<gene:campoScheda campo="CRITLIC" />
			<gene:campoScheda campo="LIMINF" />
			<gene:campoScheda campo="LIMSUP" />
			<gene:campoScheda campo="GARTEL" />
			<gene:campoScheda>
				<td colspan="2">
					<b>${sezDati}</b>
				</td>
			</gene:campoScheda>
			<gene:campoScheda campo="BUSTA" visibile="${gruppo eq 3 or datiRiga.ARCHDOCG_GRUPPO eq 3}" obbligatorio="true"/>
			<gene:campoScheda campo="REQCAP" visibile="${(gruppo eq 3 or datiRiga.ARCHDOCG_GRUPPO eq 3) }"/>
			<gene:campoScheda campo="TIPODOC" visibile="${gruppo eq 3 or datiRiga.ARCHDOCG_GRUPPO eq 3}"/>
			<gene:campoScheda campo="DESCRIZIONE" obbligatorio="true"/>
			<gene:campoScheda campo="CONTESTOVAL" visibile="${gruppo eq 3 or datiRiga.ARCHDOCG_GRUPPO eq 3}"/>
			<gene:campoScheda campo="OBBLIGATORIO" visibile="${gruppo eq 3 or datiRiga.ARCHDOCG_GRUPPO eq 3}"/>
			<gene:campoScheda campo="MODFIRMA" visibile="${gruppo eq 3 or datiRiga.ARCHDOCG_GRUPPO eq 3}"/>
			<gene:campoScheda campo="ALLMAIL" visibile="${gruppo eq 6 or datiRiga.ARCHDOCG_GRUPPO eq 6}"/>
			<gene:campoScheda campo="IDPRG" visibile="false" />
			<gene:campoScheda campo="IDDOCDG" visibile="false" />
			<gene:campoScheda campo="IDPRG" visibile="false" entita="W_DOCDIG" where="W_DOCDIG.IDPRG=ARCHDOCG.IDPRG AND W_DOCDIG.IDDOCDIG=ARCHDOCG.IDDOCDG" />
			<gene:campoScheda campo="IDDOCDIG" visibile="false" entita="W_DOCDIG" where="W_DOCDIG.IDPRG=ARCHDOCG.IDPRG AND W_DOCDIG.IDDOCDIG=ARCHDOCG.IDDOCDG" />
			<gene:campoScheda campo="DIGDESDOC" visibile="false" entita="W_DOCDIG" where="W_DOCDIG.IDPRG=ARCHDOCG.IDPRG AND W_DOCDIG.IDDOCDIG=ARCHDOCG.IDDOCDG" />
			<gene:campoScheda campo="DIGNOMDOC" entita="W_DOCDIG" visibile="${!(gruppo eq 2 or datiRiga.ARCHDOCG_GRUPPO eq 2)}" modificabile="false" 
				where="W_DOCDIG.IDPRG=ARCHDOCG.IDPRG AND W_DOCDIG.IDDOCDIG=ARCHDOCG.IDDOCDG" href="javascript:visualizzaFileAllegato('${datiRiga.ARCHDOCG_IDPRG}','${datiRiga.ARCHDOCG_IDDOCDG}','${datiRiga.W_DOCDIG_DIGNOMDOC}');" />
			<gene:campoScheda title="Nome file" visibile = '${!(gruppo eq 2 or datiRiga.ARCHDOCG_GRUPPO eq 2) and (modo eq "MODIFICA" or modo eq "NUOVO")}'>
					<input type="file" name="selezioneFile" id="selezioneFile" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" onchange='javascript:scegliFile();'/>
			</gene:campoScheda>
			<gene:campoScheda campo="IDSTAMPA" visibile="${gruppo eq '1' or gruppo eq '4' or gruppo eq '6' or datiRiga.ARCHDOCG_GRUPPO eq '1' or datiRiga.ARCHDOCG_GRUPPO eq '4' or datiRiga.ARCHDOCG_GRUPPO eq '6'}"/>
			<gene:campoScheda>	
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
		</gene:formScheda>
	</gene:redefineInsert>
	<gene:javaScript>
		document.forms[0].encoding="multipart/form-data";
		
		function visualizzaFileAllegato(idprg,iddocdig,dignomdoc) {
			var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
			document.location.href=href+"?idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc)+"&"+csrfToken;
		}
		
		function scegliFile() {
			var selezioneFile = document.getElementById("selezioneFile").value;
			var lunghezza_stringa=selezioneFile.length;
			var posizione_barra=selezioneFile.lastIndexOf("\\");
			var nome=selezioneFile.substring(posizione_barra+1,lunghezza_stringa).toUpperCase();
			if(nome.length>100){
				alert("Il nome del file non può superare i 100 caratteri!");
				document.getElementById("selFile").value="";
				setValue("W_DOCDIG_DIGNOMDOC","");
			}else{
				setValue("W_DOCDIG_DIGNOMDOC",nome);
			}
			
		}
		

		document.forms[0].action += "?gruppoSelezionato=${param.gruppoSelezionato}";
		
		function schedaNuovoDocArchivio() {
			schedaNuovo();
		}
	
		<c:if test="${modo eq 'NUOVO'}">
			$("#ARCHDOCG_GRUPPO").val("${param.gruppoSelezionato}");
		</c:if>
		
		
		var busta = $("#ARCHDOCG_BUSTA").val(); 
		if (busta == 1 || busta == 4) {
			$("#rowARCHDOCG_REQCAP").show();
		} else {
		   	$("#rowARCHDOCG_REQCAP").hide();
		   	$("#ARCHDOCG_REQCAP").val('');
		}

		$('#ARCHDOCG_BUSTA').change(function() {
			var busta = $("#ARCHDOCG_BUSTA option:selected").val(); 
			if (busta == 1 || busta == 4) {
				$("#rowARCHDOCG_REQCAP").show();
			
			} else {
			   	$("#rowARCHDOCG_REQCAP").hide();
			   	$("#ARCHDOCG_REQCAP").val('');
			}
		});
		
	</gene:javaScript>
</gene:template>
