<%
/*
 * Created on: 22/04/2021
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

<gene:template file="popup-message-template.jsp">
	
	<c:choose>
		<c:when test='${not empty param.discid_p}'>
			<c:set var="discid_p" value="${param.discid_p}" />
		</c:when>
		<c:otherwise>
			<c:set var="discid_p" value="${discid_p}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${not empty param.disckey1}'>
			<c:set var="disckey1" value="${param.disckey1}" />
		</c:when>
		<c:otherwise>
			<c:set var="disckey1" value="${disckey1}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
	<c:when test='${not empty param.entitaParent}'>
		<c:set var="entitaParent" value="${param.entitaParent}" />
	</c:when>
	<c:otherwise>
		<c:set var="entitaParent" value="${entitaParent}" />
	</c:otherwise>
	</c:choose>
	
	<c:choose>
	<c:when test='${entitaParent eq "GARE" or entitaParent eq "TORN"}'>
		<c:set var="wherekey" value="G_PERMESSI.CODGAR = '${disckey1}' OR G_PERMESSI.CODGAR = '$${disckey1}'" />
	</c:when>
	<c:otherwise>
		<c:set var="wherekey" value="G_PERMESSI.IDSTIPULA = '${disckey1}'" />
	</c:otherwise>
	</c:choose>
	
	<c:set var="where" value="(${wherekey})
	AND NOT EXISTS (SELECT * FROM W_DISCDEST WHERE G_PERMESSI.SYSCON=W_DISCDEST.DESTID AND W_DISCDEST.DISCID_P=${discid_p} AND W_DISCDEST.DISCID=-1)"/>

	<gene:setString name="titoloMaschera" value='Selezione dei destinatari delle notifiche' />
		
	<gene:redefineInsert name="corpo">
	
		<gene:set name="titoloMenuPec">
			<a href='javascript:selezionaTutti(document.forms[0].keysPec);' Title='Seleziona tutti'> <img src='${pageContext.request.contextPath}/img/ico_check.gif' height='15' width='15' alt='Seleziona tutti'></a>
			&nbsp;
			<a href='javascript:deselezionaTutti(document.forms[0].keysPec);' Title='Deseleziona tutti'><img src='${pageContext.request.contextPath}/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona tutti'></a>
		</gene:set>
		<gene:set name="titoloMenuEmail">
			<a href='javascript:selezionaTutti(document.forms[0].keysEmail);' Title='Seleziona tutti'> <img src='${pageContext.request.contextPath}/img/ico_check.gif' height='15' width='15' alt='Seleziona tutti'></a>
			&nbsp;
			<a href='javascript:deselezionaTutti(document.forms[0].keysEmail);' Title='Deseleziona tutti'><img src='${pageContext.request.contextPath}/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona tutti'></a>
		</gene:set>
		
		<br>
		Selezionare dalla lista sottostante gli utenti destinatari delle notifiche.
		<br>
		<br>	
	
		<gene:formLista entita="G_PERMESSI" pagesize="20" where="${where}" sortColumn="4" tableclass="datilista" gestisciProtezioni="true">
			
			<c:set var="destid" value="${fn:escapeXml(datiRiga.G_PERMESSI_SYSCON)}"/>
			<c:set var="destname" value="${fn:escapeXml(datiRiga.USRSYS_SYSUTE)}"/>
			<c:set var="destmail" value="${fn:escapeXml(datiRiga.USRSYS_EMAIL)}"/>

			<gene:campoLista title="Email<br><center>${titoloMenuEmail}</center>" width="50">
				<c:if test="${currentRow >= 0 && !empty datiRiga.USRSYS_EMAIL}">
					<input type="checkbox" name="keysEmail" class="email" value="${destid};${destname};${destmail}" />
				</c:if>
			</gene:campoLista>
			
			<gene:campoLista campo="NUMPER" visibile="false"/>
			<gene:campoLista campo="SYSCON" visibile="false"/>
			<gene:campoLista campo="CODGAR" visibile="false"/>
			<gene:campoLista entita="USRSYS" campo="SYSUTE" where="G_PERMESSI.SYSCON = USRSYS.SYSCON" ordinabile="false" />
			<gene:campoLista entita="USRSYS" campo="EMAIL" where="G_PERMESSI.SYSCON = USRSYS.SYSCON" ordinabile="false" />
			
			<input type="hidden" name="discid_p" value="${discid_p}" />
			
			<gene:redefineInsert name="buttons">
				<INPUT type="button" class="bottone-azione" value="Aggiungi destinatari selezionati" title="Aggiungi destinatari selezionati" onclick="javascript:aggiungi()">&nbsp;
				<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi()">&nbsp;
			</gene:redefineInsert>
			
		</gene:formLista>

  	</gene:redefineInsert>

	<gene:javaScript>

		function aggiungi(){
			var numeroOggettiEmail = contaCheckSelezionati(document.forms[0].keysEmail);
			if (numeroOggettiEmail == 0) {
	      		alert("Selezionare almeno un destinatario dalla lista");
	      	} else {
	      		document.forms[0].action="${pageContext.request.contextPath}/pg/AggiungiDestinatariNotifiche.do?"+csrfToken;
 				bloccaRichiesteServer();
				document.forms[0].submit();
 			}
		}
		
		function chiudi(){
			window.close();
		}
	</gene:javaScript>
	
	</gene:template>
</div>
