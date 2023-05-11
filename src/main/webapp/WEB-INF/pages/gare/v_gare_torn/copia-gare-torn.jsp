<%
	/*
   * Created on: 17.20 09/10/2008
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
			Maschera per la copia di una gara a lotto unico o una gara a lotti
			Parametri:
				param.key	Chiave del codice lavoro sorgente
					
			Creato da:	Luca.Giacomazzo
	 */
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<fmt:setBundle basename="AliceResources" />

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>



<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<c:set var="msgChiaveErrore">	
	<fmt:message key="match.tags.template.dettaglio.campoChiave.messaggio">
		<fmt:param value="$"/>
	</fmt:message>
</c:set>
<c:set var="msgChiaveErrore" value='${fn:replace(msgChiaveErrore, "\\\\", "")}' />
<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />

<c:choose>
	<c:when test='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.PRECED") }'>
		<c:set var="isCodificaAutomatica" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.IsCodificaAutomaticaFunction", pageContext, "GARE", "PRECED")}'/>
	</c:when>
	<c:otherwise>
		<c:set var="isCodificaAutomatica" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.IsCodificaAutomaticaFunction", pageContext, "TORN", "CODGAR")}'/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.key}'>
		<c:set var="chiave" value="${param.key}" />
	</c:when>
	<c:otherwise>
		<c:set var="chiave" value="${chiave}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${fn:containsIgnoreCase(chiave, "CODGAR")}'>
		<c:set var="copiaDa" value="listaTornate" />
		<c:set var="codiceSorgente" value='${gene:getValCampo(chiave,"CODGAR")}' />
		<c:set var="garaLottoUnico" value='${fn:startsWith(codiceSorgente, "$")}' />
		<c:set var="isProceduraTelematica" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsProceduraTelematicaFunction", pageContext, codiceSorgente)}' />
	</c:when>
	<c:otherwise>
		<c:set var="copiaDa" value="listaLotti" />
		<c:set var="codiceSorgente" value='${gene:getValCampo(chiave,"NGARA")}' />
		<c:set var="garaLottoUnico" value='false' />
		<c:set var="isProceduraTelematica" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsProceduraTelematicaGaraFunction", pageContext, codiceSorgente)}' />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${garaLottoUnico}" >
		<c:set var="codiceSorgenteVisual" value="${fn:substringAfter(codiceSorgente, '$')}" />
	</c:when>
	<c:otherwise>
		<c:set var="codiceSorgenteVisual" value="${codiceSorgente}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.tipoGara}'>
		<c:set var="tipoDiGara" value="${param.tipoGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipoDiGara" value="${TIPOGARA}" />
	</c:otherwise>
</c:choose>

<%/*Gestione della copia gara anche per le gare a lotti con offerta unica(genereGara=3)*/ %>
<c:choose>
	<c:when test='${not empty param.genereGara}'>
		<c:set var="genereDiGara" value="${param.genereGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="genereDiGara" value="${GENEREGARA}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.tipgarTornata}'>
		<c:set var="tipgarTornata" value="${param.tipgarTornata}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipgarTornata" value="${tipgarTornata}" />
	</c:otherwise>
</c:choose>

<c:set var="bloccoOliamm" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteGaraOLIAMMFunction", pageContext, codiceSorgenteVisual)}' />

<c:set var="tipgarOriginale" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTIPGARGFunction", pageContext, codiceSorgenteVisual)}'/>

<c:if test='${bloccoOliamm eq "true" }'>
	<gene:redefineInsert name="buttons">
		<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;
	</gene:redefineInsert>
</c:if>

<c:choose>
	<c:when test="${empty RISULTATO}">
		<c:choose>
			<c:when test='${copiaDa eq "listaTornate" and garaLottoUnico}'>
				<gene:setString name="titoloMaschera" value="Copia gara a lotto unico ${codiceSorgenteVisual}"/>
			</c:when>
			<c:when test='${copiaDa eq "listaTornate" and (not garaLottoUnico) and genereDiGara ne "3"}'>
				<gene:setString name="titoloMaschera" value="Copia gara divisa in lotti con plichi distinti ${codiceSorgenteVisual}"/>
			</c:when>
			<c:when test='${copiaDa eq "listaTornate" and (not garaLottoUnico) and genereDiGara eq "3"}'>
				<gene:setString name="titoloMaschera" value="Copia gara divisa in lotti con plico unico ${codiceSorgenteVisual} "/>
			</c:when>
			<c:when test='${copiaDa eq "listaLotti" and (not garaLottoUnico)}'>
				<gene:setString name="titoloMaschera" value="Copia lotto di gara ${codiceSorgenteVisual}"/>
			</c:when>			
		</c:choose>
	</c:when>
	<c:when test='${RISULTATO eq "OK"}'>
		<c:choose>
			<c:when test='${(copiaDa eq "listaTornate" and garaLottoUnico) }'>
				<gene:setString name="titoloMaschera" value="Copia gara a lotto unico ${codiceSorgenteVisual} completata"/>
			</c:when>
			<c:when test='${copiaDa eq "listaTornate" and (not garaLottoUnico) and genereDiGara ne "3"}'>
				<gene:setString name="titoloMaschera" value="Copia gara divisa in lotti con plichi distinti ${codiceSorgenteVisual} completata"/>
			</c:when>
			<c:when test='${copiaDa eq "listaTornate" and (not garaLottoUnico) and genereDiGara eq "3"}'>
				<gene:setString name="titoloMaschera" value="Copia gara divisa in lotti con plico unico ${codiceSorgenteVisual} completata"/>
			</c:when>
			<c:when test='${copiaDa eq "listaLotti" and (not garaLottoUnico)}'>
				<gene:setString name="titoloMaschera" value="Copia lotto di gara ${codiceSorgenteVisual} completata"/>
			</c:when>			
		</c:choose>

		<gene:redefineInsert name="buttons">

			<c:choose>
				<c:when test='${isCodificaAutomatica eq "true"}' >
					<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:refeshEChiudi();">&nbsp;
				</c:when>
				<c:otherwise>
					<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:annulla();">&nbsp;
				</c:otherwise>
			</c:choose>
			
			<c:if test='${bloccoOliamm eq "true" }'>
				
			</c:if>
			
		</gene:redefineInsert>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test='${copiaDa eq "listaTornate" and garaLottoUnico}'>
				<gene:setString name="titoloMaschera" value="Copia gara a lotto unico ${codiceSorgenteVisual}"/>
			</c:when>
			<c:when test='${copiaDa eq "listaTornate" and garaLottoUnico and genereDiGara ne "3"}'>
				<gene:setString name="titoloMaschera" value="Copia gara divisa in lotti con offerte distinte ${codiceSorgenteVisual}"/>
			</c:when>
			<c:when test='${copiaDa eq "listaTornate" and garaLottoUnico and genereDiGara eq "3"}'>
				<gene:setString name="titoloMaschera" value="Copia gara divisa in lotti con offerta unica ${codiceSorgenteVisual}"/>
			</c:when>
			<c:when test='${copiaDa eq "listaLotti" and (not garaLottoUnico)}'>
				<gene:setString name="titoloMaschera" value="Copia lotto di gara ${codiceSorgenteVisual}"/>
			</c:when>			
		</c:choose>
	</c:otherwise>
</c:choose>

<gene:redefineInsert name="corpo">

<c:choose>
	<c:when test='${bloccoOliamm eq "true"}' >
		<br>
		&nbsp;Non &egrave; possibile procedere con la copia perchè la gara selezionata &egrave; collegata a OLIAMM.
		<br>
		<br>
	</c:when>
	<c:when test='${RISULTATO eq "OK" and isCodificaAutomatica eq "true"}' >
		<p>
			<br>
				<c:choose>
					<c:when test='${copiaDa eq "listaTornate" and garaLottoUnico and COPIA_COME_LOTTO eq 1}'>
						&nbsp;Al nuovo lotto di gara &egrave; stato assegnato il codice <b>${numeroGara}</b>
					</c:when>
					<c:when test='${copiaDa eq "listaTornate" and garaLottoUnico and COPIA_COME_LOTTO ne 1}'>
						&nbsp;Alla nuova gara &egrave; stato assegnato il codice <b>${fn:replace(codiceGara, "$", "")}</b>
					</c:when>
					<c:when test='${(copiaDa eq "listaTornate" and not garaLottoUnico)}'>
						&nbsp;Alla nuova gara &egrave; stato assegnato il codice <b>${fn:replace(codiceGara, "$", "")}</b>
					</c:when>
					<c:when test='${copiaDa eq "listaLotti" and COPIA_COME_LOTTO ne 1}'>
						&nbsp;Alla nuova gara &egrave; stato assegnato il codice <b>${fn:replace(codiceGara, "$", "")}</b>
					</c:when>
					<c:when test='${copiaDa eq "listaLotti" and COPIA_COME_LOTTO eq 1}'>
						&nbsp;Al nuovo lotto di gara &egrave; stato assegnato il codice <b>${numeroGara}</b>
					</c:when>
				</c:choose>
			<br>
			<br>
		</p>
	</c:when>
	<c:otherwise>

		<c:set var="modo" value="NUOVO" scope="request" />
		<gene:formScheda entita="V_GARE_TORN" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreCopiaGara" >
			<gene:campoScheda campo="TIPO_COPIA" title="Copia gara da ${copiaDa}" modificabile="false" value='${copiaDa}' definizione="T30" campoFittizio="true" visibile="false" />
			<gene:campoScheda campo="TIPOGARA" title="Tipo di gara" modificabile="false" value='${tipoDiGara}' definizione="N1" campoFittizio="true" visibile="false" />
			<gene:campoScheda campo="GENEREGARA" title="Genere di gara" modificabile="false" value='${genereDiGara}' definizione="N1" campoFittizio="true" visibile="false" />
			<c:choose>
				<c:when test='${copiaDa eq "listaTornate" and garaLottoUnico}' >
					<gene:campoScheda campo="GARA_LOTTO_UNICO" title="E' una gara a lotto unico?" modificabile="false" campoFittizio="true" visibile="false" definizione="N1;0;;SN" value="1" />
				</c:when>
				<c:when test='${copiaDa eq "listaTornate" and not garaLottoUnico}' >
					<gene:campoScheda campo="GARA_LOTTO_UNICO" title="E' una gara a lotto unico?" modificabile="false" campoFittizio="true" visibile="false" definizione="N1;0;;SN" value="2" />
				</c:when>
			</c:choose>
			<gene:campoScheda campo="SORGENTE" title="Codice gara sorgente" modificabile="false" value='${codiceSorgente}' definizione="T20;1" campoFittizio="true" visibile="false" />
	
			<c:choose>
				<c:when test='${copiaDa eq "listaTornate" and not garaLottoUnico}' >
					<gene:campoScheda campo="DESTINAZIONE" title="Codice nuova gara" defaultValue="" definizione="T20;1" campoFittizio="true" obbligatorio='${isCodificaAutomatica eq "false"}' visibile='${isCodificaAutomatica eq "false"}' >
						<gene:checkCampoScheda funzione='"##"!="#SORGENTE#"' messaggio="Specificare un codice diverso da quello della gara sorgente" obbligatorio='${isCodificaAutomatica eq "false"}' />
						<gene:checkCampoScheda funzione='"##".indexOf("$") < 0' obbligatorio="false" messaggio='${msgChiaveErrore}' />
					</gene:campoScheda>
					<gene:campoScheda campo="COPIA_LOTTI" title="Copia lotti della gara?" defaultValue="1" definizione="N1;0;;SN" campoFittizio="true" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSiNoSenzaNull" modificabile='${genereDiGara ne "3"}'/>
					<gene:campoScheda campo="PREFISSO_CODICE_LOTTI" title="Prefisso codice lotti" definizione="T17;0" campoFittizio="true" visibile='${isCodificaAutomatica eq "false"}' >
						<gne:checkCampoScheda funzione='"##".indexOf("$") < 0' obbligatorio="false" messaggio='${msgChiaveErrore}' />
					</gene:campoScheda>
					<gene:fnJavaScriptScheda funzione='mostraRiga("PREFISSO_CODICE_LOTTI", "#COPIA_LOTTI#")' elencocampi="COPIA_LOTTI" esegui="true" />
					<gene:fnJavaScriptScheda funzione='setPrefissoCodiceLotti("#DESTINAZIONE#")' elencocampi="DESTINAZIONE" esegui="false" />
				</c:when>
				<c:when test='${copiaDa eq "listaTornate" and garaLottoUnico}'>
					<gene:campoScheda campo="COPIA_COME_LOTTO" title="Copia come lotto di gara?" defaultValue="0" visibile='false' definizione="N1;0;;SN" campoFittizio="true" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSiNoSenzaNull" />
					<gene:campoScheda campo="DESTINAZIONE1" title="Codice nuovo lotto" defaultValue="" definizione="T20;1" campoFittizio="true" obbligatorio='${isCodificaAutomatica eq "false"}' visibile='${isCodificaAutomatica eq "false"}' >
						<gene:checkCampoScheda funzione='"$"+"##"!="#SORGENTE#"' messaggio="Specificare un codice diverso da quello della gara sorgente" obbligatorio='${isCodificaAutomatica eq "false"}' />
						<gene:checkCampoScheda funzione='"##".indexOf("$") < 0' obbligatorio="false" messaggio='${msgChiaveErrore}' />
					</gene:campoScheda>
					<gene:campoScheda campo="DESTINAZIONE2" title="Codice nuova gara" defaultValue="" definizione="T20;1" campoFittizio="true" obbligatorio='${isCodificaAutomatica eq "false"}' visibile='${isCodificaAutomatica eq "false"}' > 
						<gene:checkCampoScheda funzione='"$"+"##"!="#SORGENTE#"' messaggio="Specificare un codice diverso da quello della gara sorgente" obbligatorio='${isCodificaAutomatica eq "false"}' />
						<gene:checkCampoScheda funzione='"##".indexOf("$") < 0' obbligatorio="false" messaggio='${msgChiaveErrore}' />
					</gene:campoScheda>
					<gene:campoScheda campo="DESTINAZIONE" title="Codice nuova gara" defaultValue="" definizione="T20;1" campoFittizio="true" obbligatorio="false" visibile="false" />
					<gene:archivio titolo="Gare divise in lotti"
						lista='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GARE.V_GARE_TORN"), "gare/v_gare_torn/lista-gareAlotti-popup.jsp", "")}'
						scheda=""
						schedaPopUp=""
						campi="V_GARE_TORN.CODICE;TORN.TIPGAR"
						functionId="gareALotti"
						parametriWhere="N:${tipoGara}"
						chiave=""
						formName="formGareALotti"
						inseribile="false">
						<gene:campoScheda campo="CODICE_GARA" title="Codice gara di destinazione" definizione="T21;0" campoFittizio="true" modificabile="true" obbligatorio='${isCodificaAutomatica eq "false"}' />
						<gene:campoScheda campo="TIPGAR" definizione="N7" campoFittizio="true"  visibile="false"/>
					</gene:archivio>
					<gene:fnJavaScriptScheda funzione='mostraRiga("CODICE_GARA", "#COPIA_COME_LOTTO#")' elencocampi="COPIA_COME_LOTTO" esegui="true" />
					<gene:fnJavaScriptScheda funzione='visualizzaDestinazione()'  elencocampi="COPIA_COME_LOTTO" esegui="true" />
				</c:when>
				<c:when test='${copiaDa eq "listaLotti" and not garaLottoUnico}'>
					<gene:campoScheda campo="COPIA_COME_LOTTO" title="Copia come lotto di gara?" defaultValue="1" definizione="N1;0;;SN" campoFittizio="true" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSiNoSenzaNull" modificabile='false'/>
					<gene:campoScheda campo="DESTINAZIONE1" title="Codice nuovo lotto" defaultValue="" definizione="T20;1" campoFittizio="true" obbligatorio='${isCodificaAutomatica eq "false"}' visibile='${isCodificaAutomatica eq "false"}' > 
						<gene:checkCampoScheda funzione='"##"!="#SORGENTE#"' messaggio="Specificare un codice diverso da quello del lotto sorgente" obbligatorio='${isCodificaAutomatica eq "false"}' />
						<gene:checkCampoScheda funzione='"##".indexOf("$") < 0' obbligatorio="false" messaggio='${msgChiaveErrore}' />
					</gene:campoScheda>
					<gene:campoScheda campo="DESTINAZIONE2" title="Codice nuova gara" defaultValue="" definizione="T20;1" campoFittizio="true" obbligatorio='${isCodificaAutomatica eq "false"}' visibile='${isCodificaAutomatica eq "false"}' > 
						<gene:checkCampoScheda funzione='"$"+"##"!="#SORGENTE#"' messaggio="Specificare un codice diverso da quello del lotto sorgente" obbligatorio='${isCodificaAutomatica eq "false"}' />
						<gene:checkCampoScheda funzione='"##".indexOf("$") < 0' obbligatorio="false" messaggio='${msgChiaveErrore}' />
					</gene:campoScheda>
					<gene:campoScheda campo="DESTINAZIONE" title="Codice nuova gara" defaultValue="" definizione="T20;1" campoFittizio="true" obbligatorio="false" visibile="false" >
						<gene:checkCampoScheda funzione='"##".indexOf("$") < 0' obbligatorio="false" messaggio='${msgChiaveErrore}' />
					</gene:campoScheda>
				
				<c:choose>
					<c:when test='${genereDiGara eq "3" || (copiaDa eq "listaLotti" and not garaLottoUnico)}' >
						<gene:campoScheda campo="CODICE_GARA" title="Codice gara di destinazione" definizione="T21;0" campoFittizio="true" modificabile="false" value="${param.codgar}" obbligatorio="true"/>
					</c:when>
					<c:when test="${abilitazioneGare eq 'U' and not param.garaSorgenteModificabile}" >
						<gene:archivio titolo="Gare divise in lotti"
							lista='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GARE.V_GARE_TORN"), "gare/v_gare_torn/lista-gareAlotti-popup.jsp", "")}'
							scheda=""
							schedaPopUp=""
							campi="V_GARE_TORN.CODICE;TORN.TIPGAR"
							functionId="gareALotti"
							parametriWhere="N:${tipoGara}"
							chiave=""
							formName="formGareALotti"
							inseribile="false">
							<gene:campoScheda campo="CODICE_GARA" title="Codice gara di destinazione" definizione="T21;0" campoFittizio="true" modificabile="true" value="" obbligatorio="true"/>
							<gene:campoScheda campo="TIPGAR" definizione="N7" campoFittizio="true"  visibile="false" value='${tipgarTornata}'/>
						</gene:archivio>
					</c:when>
					<c:otherwise>
						<gene:archivio titolo="Gare divise in lotti"
							lista='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GARE.V_GARE_TORN"), "gare/v_gare_torn/lista-gareAlotti-popup.jsp", "")}'
							scheda=""
							schedaPopUp=""
							campi="V_GARE_TORN.CODICE;TORN.TIPGAR"
							functionId="gareALotti"
							parametriWhere="N:${tipoGara}"
							chiave=""
							formName="formGareALotti"
							inseribile="false">
							<gene:campoScheda campo="CODICE_GARA" title="Codice gara di destinazione" definizione="T21;0" campoFittizio="true" modificabile="true" value="${param.codgar}" obbligatorio="true"/>
							<gene:campoScheda campo="TIPGAR" definizione="N7" campoFittizio="true"  visibile="false" value='${tipgarTornata}'/>
						</gene:archivio>
					</c:otherwise>
				</c:choose>
	
					<gene:fnJavaScriptScheda funzione='mostraCopiaOfferte("CODICE_GARA", "#COPIA_COME_LOTTO#")' elencocampi="COPIA_COME_LOTTO" esegui="false" />
					<gene:fnJavaScriptScheda funzione='visualizzaDestinazione()'  elencocampi="COPIA_COME_LOTTO" esegui="true" />
				</c:when>
			</c:choose>
	
			<c:if test='${copiaDa eq "listaLotti" or garaLottoUnico}'>
				<gene:campoScheda campo="CODICE_LOTTO"   title="Lotto"   definizione="T10;0" campoFittizio="true" obbligatorio="true"/>
			</c:if>
			
			<c:choose>
				<c:when test="${isProceduraTelematica == 'true'}">
				<c:choose>
					<c:when test="${genereDiGara eq '3' && copiaDa eq 'listaLotti'}">
						<c:set var="valoreCopiaDitte" value="1"/>
					</c:when>
					<c:otherwise>
						<c:set var="valoreCopiaDitte" value="0"/>
					</c:otherwise>
				</c:choose>
				<gene:campoScheda campo="COPIA_DITTE" title="Copia ditte?" defaultValue="${valoreCopiaDitte }" modificabile="false" definizione="N1;0;;SN" campoFittizio="true" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSiNoSenzaNull" />
				<gene:campoScheda campo="COPIA_OFFERTE" title="Copia offerte delle ditte?" defaultValue="0" modificabile="false" definizione="N1;0;;SN" campoFittizio="true" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSiNoSenzaNull" />
				</c:when>
				<c:otherwise>
					<gene:campoScheda campo="COPIA_DITTE"   title="Copia ditte?"   defaultValue="1" definizione="N1;0;;SN" campoFittizio="true" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSiNoSenzaNull" modificabile='${not(genereDiGara eq "3" and copiaDa eq "listaLotti")}'/>
					<gene:campoScheda campo="COPIA_OFFERTE" title="Copia offerte delle ditte?" defaultValue="0" definizione="N1;0;;SN" campoFittizio="true" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSiNoSenzaNull" />
				</c:otherwise>
			</c:choose>
			
			<c:choose>
				<c:when test='${copiaDa eq "listaTornate" and not garaLottoUnico}' >
					<gene:fnJavaScriptScheda funzione='mostraCopiaDitteOfferte("#COPIA_LOTTI#")' elencocampi="COPIA_LOTTI" esegui="false" />
					<gene:fnJavaScriptScheda funzione='mostraCopiaOfferte(null, "#COPIA_DITTE#")' elencocampi="COPIA_DITTE" esegui="false" />
				</c:when>
				<c:when test='${copiaDa eq "listaTornate" and garaLottoUnico}' >
					<gene:fnJavaScriptScheda funzione='mostraCopiaOfferte(null, "#COPIA_DITTE#")' elencocampi="COPIA_DITTE" esegui="false" />
					<gene:fnJavaScriptScheda funzione='mostraCodiceLotto("#COPIA_COME_LOTTO#")' elencocampi="COPIA_COME_LOTTO" esegui="true" />
				</c:when>
				<c:when test='${copiaDa eq "listaLotti" and not garaLottoUnico}' >
					<gene:fnJavaScriptScheda funzione='mostraCopiaOfferte("#COPIA_COME_LOTTO#", "#COPIA_DITTE#")' elencocampi="COPIA_COME_LOTTO" esegui="false" />
					<gene:fnJavaScriptScheda funzione='mostraCopiaOfferte(null, "#COPIA_DITTE#")' elencocampi="COPIA_DITTE" esegui="false" />
					<gene:fnJavaScriptScheda funzione='mostraCodiceLotto("#COPIA_COME_LOTTO#")' elencocampi="COPIA_COME_LOTTO" esegui="false" />
				</c:when>
			</c:choose>
						
			<gene:campoScheda campo="COPIA_DOCUMENTAZIONE"   title="Copia documentazione di gara?"   defaultValue="0" definizione="N1;0;;SN" campoFittizio="true" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSiNoSenzaNull" visibile='${not(genereDiGara eq "3" and copiaDa eq "listaLotti")}'/>
			<c:choose>
				<c:when test='${(gene:checkProt(pageContext, "PAGE.VIS.GARE.TORN-scheda.PUBBANDO") || gene:checkProt(pageContext, "PAGE.VIS.GARE.GARE-scheda.PUBBANDO")) && gene:checkProt(pageContext, "PAGE.VIS.GARE.GARE-scheda.PUBESITO")}'>
					<c:set var="titoloTemini" value="Copia termini di gara e pubblicazioni?"/>
				</c:when>
				<c:otherwise>
					<c:set var="titoloTemini" value="Copia termini di gara?"/>
				</c:otherwise>
			</c:choose>
			<gene:campoScheda campo="COPIA_TERMINI"   title="${titoloTemini }" defaultValue="0" definizione="N1;0;;SN" campoFittizio="true" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSiNoSenzaNull" visibile='${not(genereDiGara eq "3" and copiaDa eq "listaLotti")}'/>
			<gene:campoScheda campo="COPIA_SCADENZARIO"   title="Copia scadenzario?"   defaultValue="0" definizione="N1;0;;SN" campoFittizio="true" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSiNoSenzaNull" visibile='${fn:contains(listaOpzioniDisponibili, "OP128#") and not(genereDiGara eq "3" and copiaDa eq "listaLotti")}'/>
		
			<gene:fnJavaScriptScheda funzione='aggiornaDaCopiaOfferte("#COPIA_OFFERTE#")' elencocampi="COPIA_OFFERTE" esegui="false" />
			
			<c:if test='${fn:contains(listaOpzioniDisponibili, "OP128#")}'>
				<gene:fnJavaScriptScheda funzione='mostraCopiaScadenzario("#COPIA_COME_LOTTO#")' elencocampi="COPIA_COME_LOTTO" esegui="false" />
			</c:if>
			
			<input type="hidden" name="tipgarTornata" id="tipgarTornata" value="${tipgarTornata}" />
			<input type="hidden" name="chiave" id="chiave" value="${chiave}" />
			<input type="hidden" name="bloccoOliamm" id="bloccoOliamm" value="${bloccoOliamm}" />
		</gene:formScheda>

	</c:otherwise>
</c:choose>

</gene:redefineInsert>

<gene:javaScript>
<c:choose>
	<c:when test='${copiaDa eq "listaTornate"}'>
	
		function mostraCopiaDitteOfferte(copiaLotti){
			if(copiaLotti == "0"){
				mostraCopiaOfferte("0", null);
			} else {
				mostraCopiaOfferte("1", "1");
			}
		}

		function mostraCopiaOfferte(copiaLotti, copiaDitte){
			if(copiaLotti != null){
				if(copiaLotti == "0"){
					setValue("COPIA_OFFERTE", "0");
					showObj("rowCOPIA_OFFERTE", false);
					setValue("COPIA_DITTE", "0");
					showObj("rowCOPIA_DITTE", false);
				} else {
					if(copiaDitte == "0"){
						setValue("COPIA_DITTE", "0");
						showObj("rowCOPIA_DITTE", false);					
					} else {
						document.getElementById("COPIA_DITTE").value = "1";
						showObj("rowCOPIA_DITTE", true);
						showObj("rowCOPIA_OFFERTE", true);
						setValue("COPIA_OFFERTE", "0");
					}
				}	
			} else {
				if(copiaDitte == "0"){
					setValue("COPIA_OFFERTE", "0");
					showObj("rowCOPIA_OFFERTE", false);
				} else {
					setValue("COPIA_OFFERTE", "0");
					showObj("rowCOPIA_OFFERTE", true);
				}
			}
		}
	</c:when>
	<c:otherwise>
		function mostraCopiaOfferte(copiaComeLotto, copiaDitte){
			if(copiaComeLotto != null){
				if(copiaComeLotto == "0"){
					setValue("CODICE_GARA", "");
					showObj("rowCODICE_GARA", false);
				} else {
					showObj("rowCODICE_GARA", true);
				}
			} else {
				if(copiaDitte == "0"){
					setValue("COPIA_OFFERTE", "0");
					showObj("rowCOPIA_OFFERTE", false);
				} else {
					setValue("COPIA_OFFERTE", "0");
					showObj("rowCOPIA_OFFERTE", true);
				}
			}
		}
		
		
	</c:otherwise>
</c:choose>
	
	function mostraCodiceLotto(copiaComeLotto){
			if(copiaComeLotto == '1'){
				showObj("rowCODICE_LOTTO", true);
			}else{
				setValue("CODICE_LOTTO", "");
				showObj("rowCODICE_LOTTO", false);
			}
		}
	
	function mostraRiga(idRiga, valore){
		var idTmpRiga = "row" + idRiga;
		var rigaVisualizzata = isObjShow(idTmpRiga);
		if(rigaVisualizzata){
			if(valore != "1"){
				showObj(idTmpRiga, false);
			}
		} else {
			if(valore == "1"){
				showObj(idTmpRiga, true);
				// Ripristino dei valori di default
				if(idRiga.indexOf("COPIA") >= 0)
					setValue(idRiga, 1);
			<c:if test='${not garaLottoUnico}'>
				else if(idRiga.indexOf("PREFISSO_CODICE_LOTTI") >= 0)
					setPrefissoCodiceLotti(getValue("DESTINAZIONE"));
			</c:if>
			}
		}
	}

<c:if test='${(copiaDa eq "listaTornate" and garaLottoUnico) or (copiaDa eq "listaLotti" and not garaLottoUnico)}' >
	function visualizzaDestinazione(){
		if(getValue("COPIA_COME_LOTTO") == "1"){
			showObj("rowDESTINAZIONE1", true);
			showObj("rowDESTINAZIONE2", false);
			showObj("rowCODICE_GARA", true);
		} else {
			showObj("rowDESTINAZIONE2", true);
			showObj("rowDESTINAZIONE1", false);
			showObj("rowCODICE_GARA", false);
		}
	}
</c:if>

<c:if test='${copiaDa eq "listaTornate" and not garaLottoUnico}'>
	function setPrefissoCodiceLotti(prefisso){
		if(prefisso.length <= 17)
			setValue("PREFISSO_CODICE_LOTTI", prefisso);
		else
			setValue("PREFISSO_CODICE_LOTTI", prefisso.substr(0,17));
	}
</c:if>


	function mostraCopiaScadenzario(copiaComeLotto){
		var copiaDa="${copiaDa }";
		var visualizza= false;
		if((copiaDa == "listaLotti" && copiaComeLotto=="1") || (copiaDa != "listaLotti" && copiaComeLotto!="1") )
			visualizza=true;
		//if(copiaComeLotto!="1"){
		if(visualizza){
			showObj("rowCOPIA_SCADENZARIO", true);
		}else{
			setValue("COPIA_SCADENZARIO", "0");
			showObj("rowCOPIA_SCADENZARIO", false);
		}
	}

	function refeshEChiudi(){
		window.opener.historyReload();
		window.close;
	}

	function annulla(){
		window.close();
	}
	
	try {
		document.forms[0].jspPathTo.value="gare/v_gare_torn/copia-gare-torn.jsp";
	} catch(e) {
	}
	if(document.getElementById("CODICE_GARA"))
		document.getElementById("CODICE_GARA").disabled = true;
	
	
	function conferma(){
	
	<c:if test='${(copiaDa eq "listaTornate" and garaLottoUnico)}'>
		var copiaComeLotto = getValue("COPIA_COME_LOTTO");
		var tipgar = getValue("TIPGAR");
		var tipgarOriginale = "${tipgarOriginale }";
		var codiceDestinazione = getValue("CODICE_GARA");
		
		if(tipgar != tipgarOriginale && copiaComeLotto == "1" && codiceDestinazione != null && codiceDestinazione != ""){
			var conferma = confirm("Il tipo procedura del lotto da copiare è diverso da quello della gara di destinazione.\nCon la copia verrà pertanto modificato e allineato a quest'ultima.\nVuoi procedere ugualmente?");
			if(!conferma)
				return;
		
		}
	</c:if>
	
	<c:if test='${(copiaDa eq "listaTornate" and garaLottoUnico) or (copiaDa eq "listaLotti" and not garaLottoUnico)}' >
		if(getValue("COPIA_COME_LOTTO") == "1"){
			setValue("DESTINAZIONE", getValue("DESTINAZIONE1"));
		} else {
			setValue("DESTINAZIONE", getValue("DESTINAZIONE2"));
		}
	</c:if>
	
	
		if(document.getElementById("CODICE_GARA") && document.getElementById("CODICE_GARA").disabled)
			document.getElementById("CODICE_GARA").disabled = false;

		// Queste due righe sono state copiate per 
		document.forms[0].metodo.value="update";
		document.forms[0].key.value="${chiave}";
		
		var esitoSubmit = schedaConfermaPopUp();

		if(!esitoSubmit && getValue("COPIA_COME_LOTTO") == "1")
			document.getElementById("CODICE_GARA").disabled = true;
		
	}

<c:choose>
	<c:when test='${not empty RISULTATO and RISULTATO eq "OK" and isCodificaAutomatica eq "false"}' >
		opener.historyReload();
		window.close();
	</c:when>
	<c:when test='${not empty RISULTATO and RISULTATO eq "OK" and isCodificaAutomatica eq "true"}' >
	</c:when>
	<c:otherwise>
		<c:if test="${not empty GARA_LOTTO_UNICO}">
			setValue("GARA_LOTTO_UNICO", ${GARA_LOTTO_UNICO});
		</c:if>
		<c:if test="${not empty PREFISSO_CODICE_LOTTI}">
			setValue("PREFISSO_CODICE_LOTTI", "${PREFISSO_CODICE_LOTTI}");
		</c:if>
		<c:if test="${not empty COPIA_COME_LOTTO}">
			setValue("COPIA_COME_LOTTO", ${COPIA_COME_LOTTO});
		</c:if>
		<c:if test="${not empty COPIA_LOTTI}">
			setValue("COPIA_LOTTI", ${COPIA_LOTTI});
			if(0 == ${COPIA_LOTTI})
				visualizzaDestinazione();
		</c:if>
		<c:if test="${not empty COPIA_OFFERTE}">
			setValue("COPIA_OFFERTE", ${COPIA_OFFERTE});
		</c:if>
		<c:if test="${not empty COPIA_DITTE}">
			setValue("COPIA_DITTE", ${COPIA_DITTE});
		</c:if>
		<c:if test="${not empty SORGENTE}">
			setValue("SORGENTE", "${SORGENTE}");
		</c:if>
		<c:if test="${not empty DESTINAZIONE}">
			setValue("DESTINAZIONE", "${DESTINAZIONE}");
		</c:if>
		<c:if test="${not empty CODICE_GARA}">
			setValue("CODICE_GARA", "${CODICE_GARA}");
		</c:if>
		<c:if test="${not empty CODICE_LOTTO}">
			setValue("CODICE_LOTTO", "${CODICE_LOTTO}");
		</c:if>
		<c:if test="${not empty TIPGAR}">
			setValue("TIPGAR", "${TIPGAR}");
		</c:if>
		<c:if test="${not empty COPIA_DOCUMENTAZIONE}">
			setValue("COPIA_DOCUMENTAZIONE", "${COPIA_DOCUMENTAZIONE}");
		</c:if>
		<c:if test="${not empty COPIA_TERMINI}">
			setValue("COPIA_TERMINI", "${COPIA_TERMINI}");
		</c:if>
	</c:otherwise>
</c:choose>
	
		
	function aggiornaDaCopiaOfferte(copiaOfferte){
		if(copiaOfferte==1){
			setValue("COPIA_DOCUMENTAZIONE", copiaOfferte);
			setValue("COPIA_TERMINI", copiaOfferte);
		}
	}
	
	</gene:javaScript>
</gene:template>
</div>