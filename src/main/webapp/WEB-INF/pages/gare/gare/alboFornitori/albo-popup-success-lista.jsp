
<%
	/*
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

<c:choose>
	<c:when test='${not empty RISULTATO and RISULTATO eq "OK"}' >
		<script type="text/javascript">
		window.opener.historyReload();
		window.close();
		</script>
	</c:when>
	<c:otherwise>
<div style="width:97%;">

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.numeroFaseAttiva}'>
		<c:set var="numeroFaseAttiva" value="${param.numeroFaseAttiva}" />
	</c:when>
	<c:otherwise>
		<c:set var="numeroFaseAttiva" value="${numeroFaseAttiva}" />
	</c:otherwise>
</c:choose>

<gene:template file="popup-template.jsp">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />

	<gene:setString name="titoloMaschera" value='Lista fornitori albo'/>
	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		<table class="lista">
			<tr>
				<td>
					<gene:formLista  
						tableclass="datilista" pagesize="1000"
						gestisciProtezioni="false"
						sortColumn="4"
						varName="risultatoListaFornitori">
						<c:set var="link" value="javascript:selezionaFornitoreAlbo('${datiRiga.OBJ1}');" />
						
						<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50" >
							<c:if test="${currentRow >= 0}" >
								<gene:PopUp variableJs="jvarRow${currentRow}" >
									<gene:PopUpItem title="Seleziona fornitore" href="${link}"/>
								</gene:PopUp>
							</c:if>
						<c:if test="${datiRiga.OBJ1 ne 'Non disponibile'}">
							<input type="checkbox" name="keys" value="${datiRiga.OBJ1}" />
						</c:if>
							
						</gene:campoLista>
						
						
						<gene:campoLista ordinabile="false" title="Codice Maggioli" campo="C01" definizione="T10;0" />
						<c:choose>
						    <c:when test="${datiRiga.OBJ1 eq 'Non disponibile'}">
								<c:set var="visualizzaLink" value='false'/>						    
						    </c:when>
						    <c:otherwise>
						        <c:set var="visualizzaLink" value='true'/>
						    </c:otherwise>
						</c:choose>
						<gene:campoLista ordinabile="false" title="Codice SAP" campo="C02" definizione="T4;0" />
						<gene:campoLista ordinabile="false" title="Descrizione" campo="C03" definizione="T15;0" 
							href="${gene:if(visualizzaLink, link, '')}"/>
						<gene:campoLista ordinabile="false" title="Codice Fiscale" campo="C04" definizione="T16;0" />
						<gene:campoLista ordinabile="false" title="Partita IVA" campo="C05" definizione="T11;0" />
						<gene:campoLista ordinabile="false" title="ECC" campo="C06" definizione="T16;0" />							
						<gene:campoLista ordinabile="false" title="Comune" campo="C07" definizione="T16;0" />
						<gene:campoLista ordinabile="false" title="Provincia" campo="C08" definizione="T16;0" />
					</gene:formLista>
				</td>
			</tr>
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<c:if test="${!empty risultatoListaFornitori}">
						<INPUT type="button"  class="bottone-azione" value='Conferma' title='Conferma' onclick="javascript:conferma();">&nbsp;
					</c:if>
					<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;&nbsp;
				</td>
			</tr>
		</table>
	</gene:redefineInsert>

	<gene:javaScript>
		function chiudi(){
			window.close();
		}
		
		function conferma() {
				var ngara = "${ngara}";
				var numeroFaseAttiva = "${numeroFaseAttiva}";
				var garaLottiConOffertaUnica = "${garaLottiConOffertaUnica}";
				var categoria = "${categoria}";
				var qsl = "${qsl}";
				document.forms[0].action="${pageContext.request.contextPath}/pg/InserimentoFornitoreAlbo.do?"+csrfToken+"&ngara="+ngara+"&garaLottiConOffertaUnica="+garaLottiConOffertaUnica+"&numeroFaseAttiva="+numeroFaseAttiva+"&categoria="+categoria+"&qsl="+qsl+"&codStazioneAppaltante=${param.codStazioneAppaltante }";
 				bloccaRichiesteServer();
				document.forms[0].submit();
		}
		
	
		function selezionaFornitoreAlbo(codiceFornitore) {
			var ngara = "${ngara}";
			var numeroFaseAttiva = "${numeroFaseAttiva}";
			var categoria = "${categoria}";
			var qsl = "${qsl}";
			var href="${contextPath}/pg/InserimentoFornitoreAlbo.do?"+csrfToken+"&codiceFornitore="+codiceFornitore+"&ngara="+ngara+"&garaLottiConOffertaUnica=${param.garaLottiConOffertaUnica}&numeroFaseAttiva="+numeroFaseAttiva+"&categoria="+categoria+"&qsl="+qsl+"&codStazioneAppaltante=${param.codStazioneAppaltante }";
			document.location.href=href;
			window.opener.historyReload();
			window.close();
		}
	
	</gene:javaScript>	
</gene:template>

</div>

</c:otherwise>
</c:choose>
