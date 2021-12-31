<%
/*
 * Created on: 22/05/2018
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
		Finestra per impostare le condizioni di filtro per la ricerca da archivio dei destinatari da inserire
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div style="width:97%;">

<gene:template file="popup-template.jsp">
	
	<c:choose>
		<c:when test='${not empty param.idprg}'>
			<c:set var="idprg" value="${param.idprg}" />
		</c:when>
		<c:otherwise>
			<c:set var="idprg" value="${idprg}" />
		</c:otherwise>
	</c:choose>

	<c:choose>
		<c:when test='${not empty param.idcom}'>
			<c:set var="idcom" value="${param.idcom}" />
		</c:when>
		<c:otherwise>
			<c:set var="idcom" value="${idcom}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${not empty param.filtroRadio}'>
			<c:set var="filtroRadio" value="${param.filtroRadio}" />
		</c:when>
		<c:otherwise>
			<c:set var="filtroRadio" value="${filtroRadio}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${not empty param.idconfi}'>
			<c:set var="idconfi" value="${param.idconfi}" />
		</c:when>
		<c:otherwise>
			<c:set var="idconfi" value="${idconfi}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test="${filtroRadio eq '1'}">
			<c:set var="msgSelect" value="Visualizza imprese non inserite tra i destinatari"/>
			<c:set var="msgEtichetta" value="Visualizza solo imprese non ancora inserite tra i destinatari?"/>
			<c:set var="filtroGaraLotto1" value="IMPR.CODIMP NOT IN (select DESCODSOG from w_invcomdes where IDPRG = '${idprg}' and IDCOM = ${idcom} AND DESCODENT='IMPR' AND DESCODSOG is not null) and (IMPR.TIPIMP is null or (IMPR.TIPIMP <> 3 and IMPR.TIPIMP <> 10))" />
			<c:set var="filtroGaraLotto2" value="IMPR.TIPIMP is null or (IMPR.TIPIMP <> 3 and IMPR.TIPIMP <> 10)" />
			<c:set var="entitaRicerca" value="IMPR" />
		</c:when>
		<c:when test="${filtroRadio eq '2'}">
			<c:set var="msgSelect" value="Visualizza tecnici non inseriti tra i destinatari"/>
			<c:set var="msgEtichetta" value="Visualizza solo tecnici non ancora inseriti tra i destinatari?"/>
			<c:set var="filtroGaraLotto1" value="TECNI.CODTEC NOT IN (select DESCODSOG from w_invcomdes where IDPRG = '${idprg}' and IDCOM = ${idcom} AND DESCODENT='TECNI' AND DESCODSOG is not null)" />
			<c:set var="filtroGaraLotto2" value="" />
			<c:set var="entitaRicerca" value="TECNI" />
		</c:when>
		<c:otherwise>
			<c:set var="msgSelect" value="Visualizza utenti/RUP non inseriti tra i destinatari"/>
			<c:set var="msgEtichetta" value="Visualizza solo utenti/RUP non ancora inseriti tra i destinatari?"/>
			<c:set var="filtroGaraLotto1" value="and TECNI.CODTEC NOT IN (select DESCODSOG from w_invcomdes where IDPRG = '${idprg}' and IDCOM = ${idcom} AND DESCODENT='TECNI' AND DESCODSOG is not null)" />
			<c:set var="filtroGaraLotto2" value="" />
			<c:set var="joinUsrsys" value=" exists (select *  from USRSYS where USRSYS.SYSCF = TECNI.CFTEC) " />
			<c:set var="entitaRicerca" value="TECNI" />
		</c:otherwise>
	</c:choose>
	
	<gene:setString name="titoloMaschera" value="Ricerca destinatari da anagrafica" />
			
	<gene:redefineInsert name="corpo">
		<gene:formTrova entita="${entitaRicerca }" filtro="${joinUsrsys} ${filtroGaraLotto1}" gestisciProtezioni="true"
			lista="geneweb/w_invcomdes/w_invcomdes-lista-destinatariAnagrafica-popup.jsp">
			
			<tr>
				<td colspan="3">
					Ricerca in archivio:
					<input type="radio" value="1" id="radioImpresa" name="filtroAnagrafica" <c:if test='${filtroRadio eq "1"}'>checked="checked"</c:if> onclick="javascript:cambiaEntita(1);" />Imprese
					&nbsp;
					<input type="radio" value="2" id="radioTecnici" name="filtroAnagrafica" <c:if test='${filtroRadio eq "2"}'>checked="checked"</c:if> onclick="javascript:cambiaEntita(2);"/>Tecnici
					<c:if test="${! empty sessionScope.uffint}">
						&nbsp;
						<input type="radio" value="3" id="radioUtentiRup" name="filtroAnagrafica" <c:if test='${filtroRadio eq "3"}'>checked="checked"</c:if> onclick="javascript:cambiaEntita(3);"/>Utenti/RUP delle Amministrazioni aderenti
					</c:if>
					<br>
					<br>
				</td>
			
			<c:choose>
				<c:when test="${filtroRadio eq '1'}">
					<gene:campoTrova campo="CODIMP" />
					<gene:campoTrova campo="NOMEST" />
					<gene:campoTrova campo="CFIMP" />
					<gene:campoTrova campo="PIVIMP" />
					<gene:campoTrova campo="TIPIMP" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTipoImpresaNoRaggruppamento"/>
					<gene:campoTrova campo="ISMPMI" />
					<gene:campoTrova campo="EMAIIP" />
					<gene:campoTrova campo="EMAI2IP" />
				</c:when>
				<c:when test="${filtroRadio eq '2'}">
					<gene:campoTrova campo="CODTEC" />
					<gene:campoTrova campo="NOMTEC" />
					<gene:campoTrova campo="CFTEC" />
					<gene:campoTrova campo="PIVATEC" />
					<gene:campoTrova campo="EMATEC" />
					<gene:campoTrova campo="EMA2TEC" />
				</c:when>
				<c:otherwise>
					<gene:campoTrova campo="SYSUTE" entita="USRSYS" where="USRSYS.SYSCF = TECNI.CFTEC" />
					<gene:campoTrova campo="SYSCF" entita="USRSYS" where="USRSYS.SYSCF = TECNI.CFTEC" title="Codice fiscale utente"/>
					<gene:campoTrova campo="CODEIN" entita="USR_EIN" from="USRSYS" where="USR_EIN.SYSCON = USRSYS.SYSCON AND USRSYS.SYSCF = TECNI.CFTEC" title="Codice amministrazione"/>
					<gene:campoTrova campo="NOMEIN" entita="UFFINT" from="USRSYS,USR_EIN" where="UFFINT.CODEIN = USR_EIN.CODEIN AND USR_EIN.SYSCON = USRSYS.SYSCON AND USRSYS.SYSCF = TECNI.CFTEC"/>
					<gene:campoTrova campo="CFEIN" entita="UFFINT" from="USRSYS,USR_EIN" where="UFFINT.CODEIN = USR_EIN.CODEIN AND USR_EIN.SYSCON = USRSYS.SYSCON AND USRSYS.SYSCF = TECNI.CFTEC" title="Codice fiscale amministrazione"/>
					<gene:campoTrova campo="EMATEC" />
					<gene:campoTrova campo="EMA2TEC" />
				</c:otherwise>
			</c:choose>
						

			<tr>
				<td class="etichetta-dato"/>
				<td class="operatore-trova"/>
				<td class="valore-dato-trova"/>
			</tr>
			<tr>
				<td class="etichetta-dato">${msgEtichetta }</td>
				<td class="operatore-trova"/>
				<td class="valore-dato-trova">
					<select id="CampoFitt" name="CampoFitt" title="${msgSelect }" onchange="javascript:impostaFiltro(this.options[this.selectedIndex].value);"   >
						<option value="1" selected="selected">Si</option>
						<option value="2" >No</option>
					</select>
				</td>
			</tr>
			
			<input type="hidden" name="idprg" value="${idprg}" />
			<input type="hidden" name="idcom" value="${idcom}" />
			<input type="hidden" name="idconfi" value="${idconfi}" />
			<input type="hidden" name="filtroRadio" value="${filtroRadio}" />
			<input type="hidden" name="entitaRicerca" value="${entitaRicerca}" />
						
				
		</gene:formTrova>
		
		<gene:javaScript>
			function cambiaEntita(valore){
				if(valore==1){
					document.trova.filtroRadio.value="1";
				}else if(valore==2){
					document.trova.filtroRadio.value="2";
				}else{
					document.trova.filtroRadio.value="3";
				}
				trovaClear();		
			}
			
			function impostaFiltro(valore){
				
				var filtro1 = "${filtroGaraLotto1}";
				var filtro2 = "${filtroGaraLotto2}";
				if(valore==1)
					document.forms[0].filtro.value = filtro1;
				else
					document.forms[0].filtro.value = filtro2;
				
			}
			
		</gene:javaScript>
		
		
  	</gene:redefineInsert>
	
</gene:template>

</div>