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

<div style="width:97%;">

<gene:template file="popup-template.jsp">
	
	<c:set var="idprg" value="${param.idprg}" />
	<c:set var="idcom" value="${param.idcom}" />
	<c:set var="idconfi" value="${param.idconfi}" />
	<c:set var="comkey1" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetCOMKEYGaraLottoFunction",pageContext,idprg,idcom)}' />
	<c:set var="filtroGaraLotto1" value="DITG.NGARA5 = '${comkey1}' AND DITG.DITTAO NOT IN (select DESCODSOG from w_invcomdes where IDPRG = '${idprg}' and IDCOM = ${idcom} AND DESCODSOG is not null) and RTOFFERTA is null" />
	<c:set var="filtroGaraLotto2" value="DITG.NGARA5 = '${comkey1}'  and RTOFFERTA is null" />
	
	<c:set var="chiaveGara" value="GARE.NGARA=T:${comkey1}" />
	<c:set var="genereGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction",  pageContext,chiaveGara)}' />
	
	<c:set var="dbms" value="${gene:callFunction('it.eldasoft.gene.tags.utils.functions.GetTipoDBFunction', pageContext)}" />
		
	<c:if test="${genereGara eq '10' || genereGara eq '20'}">
		<c:set var="isVisibleDataScadenzaIscriz" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.isVisibleDataScadenzaIscrizFunction",pageContext,comkey1)}' />
		<c:set var="valiscr" value='${valiscr}' />
	</c:if>
	
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
	
	<gene:setString name="titoloMaschera" value="Ricerca destinatari da ditte ${testoTipoGara }" />
		
	<gene:redefineInsert name="corpo">
		<gene:formTrova entita="DITG"  gestisciProtezioni="true"
			lista="geneweb/w_invcomdes/w_invcomdes-lista-ditte-popup.jsp"
			gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.trova.gestori.RicercaDitteW_INVCOMDESGestoreTrova">
			<gene:campoTrova campo="NOMIMO" />
			<gene:campoTrova campo="CFIMP" entita="IMPR" where="DITG.DITTAO = IMPR.CODIMP"/>
			<gene:campoTrova campo="PIVIMP" entita="IMPR" where="DITG.DITTAO = IMPR.CODIMP"/>
			<c:choose>
				<c:when test="${genereGara ne '10' and genereGara ne '20'}">
					<gene:campoTrova campo="INVGAR" />
					<gene:campoTrova campo="INVOFF" />
					<gene:campoTrova title="Ditta esclusa ?" campo="AMMGAR" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoSiNoInvertito"/>
					<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.DITG.AMMGAR") && gene:checkProt(pageContext, "COLS.VIS.GARE.DITG.FASGAR")}'>
						<gene:fnJavaScriptTrova funzione="gestioneAMMGAR('#Campo5#')" elencocampi="Campo5" esegui="true" />
					</c:if>
					
					<gene:campoTrova campo="FASGAR" />
				</c:when>
				<c:otherwise>
					<gene:campoTrova campo="CAISIM" title="Codice categoria d'iscrizione" entita="V_ISCRIZCAT_CLASSI" where="DITG.NGARA5 = V_ISCRIZCAT_CLASSI.NGARA and DITG.DITTAO = V_ISCRIZCAT_CLASSI.CODIMP  "/>
					<gene:campoTrova campo="DESCAT1" title="Descrizione categoria d'iscrizione" entita="V_ISCRIZCAT_CLASSI" where="DITG.NGARA5 = V_ISCRIZCAT_CLASSI.NGARA and DITG.DITTAO = V_ISCRIZCAT_CLASSI.CODIMP  "/>
					<gene:campoTrova campo="TIPLAVG" entita="V_ISCRIZCAT_CLASSI" where="DITG.NGARA5 = V_ISCRIZCAT_CLASSI.NGARA and DITG.DITTAO = V_ISCRIZCAT_CLASSI.CODIMP  "/>
					<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.ISCRIZCAT.SUPNUMCLASS")}'>
 						<gene:campoTrova campo="NUMCLASS" title="Classifica categoria d'iscrizione" entita="V_ISCRIZCAT_CLASSI" where="DITG.NGARA5 = V_ISCRIZCAT_CLASSI.NGARA and DITG.DITTAO = V_ISCRIZCAT_CLASSI.CODIMP  " gestore="it.eldasoft.gene.tags.gestori.decoratori.GestoreCampoClassificaCategoriaRicerca"/>
						<gene:fnJavaScriptTrova funzione="gestioneNumcla('#Campo6#')" elencocampi="Campo6" esegui="true" />
 					</c:if>
					<gene:campoTrova campo="ABILITAZ" />
					<gene:campoTrova campo="DRICIND" title="Data domanda iscrizione"/>
					<c:set var="chiaveTmp" value="GARE.NGARA=T:${comkey1 }" />
					<c:set var="tipologia" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGarealboFunction", pageContext, chiaveTmp)}' scope="request"/>
					<c:if test="${tipologia ne 3 }">
						<gene:campoTrova campo="DSCAD" />
						<gene:campoTrova campo="STRIN" />
					</c:if>
					<c:if test='${isVisibleDataScadenzaIscriz eq "true"}'>
						<c:choose>
							<c:when test='${dbms eq "POS"}'>
								<gene:campoTrova title="Data scadenza iscrizione" computed="true" campo="${gene:getDBFunction(pageContext,'isnull','DSCAD;DRICIND')}::date + ${valiscr } - 1" definizione="D;0;;DATA_ELDA"/>
							</c:when>
							<c:otherwise>
								<gene:campoTrova title="Data scadenza iscrizione" computed="true" campo="${gene:getDBFunction(pageContext,'isnull','DSCAD;DRICIND')} + ${valiscr } - 1" definizione="D;0;;DATA_ELDA"/>
							</c:otherwise>
						</c:choose>
					</c:if>
					<gene:campoTrova campo="ESTIMP" title="Sorteggiata per verifica documenti?"/>
					<gene:campoTrova campo="DSORTEV" title="Data sorteggio"/>
				</c:otherwise>
			</c:choose>
			

			<tr>
				<td class="etichetta-dato"/>
				<td class="operatore-trova"/>
				<td class="valore-dato-trova"/>
			</tr>
			<tr>
				<td class="etichetta-dato">Visualizza solo ditte non ancora inserite tra i destinatari?</td>
				<td class="operatore-trova"/>
				<td class="valore-dato-trova">
					<select id="CampoFitt" name="CampoFitt" title="Visualizza ditte non inserite tra i destinatari">
						<option value="1" selected="selected">Si</option>
						<option value="2" >No</option>
					</select>
				</td>
			</tr>
			
			<input type="hidden" name="idconfi" value="${idconfi}" />
			<input type="hidden" name="idprg" value="${idprg}" />
			<input type="hidden" name="idcom" value="${idcom}" />
			<input type="hidden" name="comkey1" value="${comkey1}" />
			<input type="hidden" name="genereGara" value="${genereGara}" />
			
				
		</gene:formTrova>
		
		<gene:javaScript>
			<c:if test="${genereGara eq '10' or genereGara eq '20'}">
				showObj("rowCampo5", false);
			</c:if>

			function gestioneAMMGAR(ammgar){
				document.getElementById("rowCampo6").style.display = (ammgar=='2' ? '':'none');
			}

			function gestioneNumcla(numcla){
				var index = document.getElementById("Campo6").selectedIndex;
				var tipoAppalto = document.getElementById("Campo6").options[index].text.substr(0,1);
	
				setValue("Campo5",tipoAppalto);
			}
		</gene:javaScript>
  	</gene:redefineInsert>
</gene:template>
</div>


