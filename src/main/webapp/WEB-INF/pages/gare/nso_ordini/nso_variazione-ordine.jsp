<%
			/*
       * Created on: 26/5/2020
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
					Maschera per la copia/variazione di un ordine
						
				Creato da:
					Cristian Febas
			*/
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<gene:template file="popup-message-template.jsp">
	<c:set var="idOrdine" value='${gene:getValCampo(param.key,"ID")}' />
	<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GestioneDatiNsoFunction", pageContext, idOrdine)}'/>
	<c:choose>
		<c:when test="${empty RISULTATO}">
				<gene:setString name="titoloMaschera" value="Variazione ordine NSO ${idOrdine} - requestScope.codiceOrdine"/>
		</c:when>
		<c:when test='${RISULTATO eq "OK"}'>
				<gene:setString name="titoloMaschera" value="Variazione dell'ordine ${idOrdine} - requestScope.codiceOrdine eseguita correttamente"/>
			<c:if test="${not empty RISULTATO}" >
				<gene:redefineInsert name="buttons" >
					<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla()">&nbsp;
				</gene:redefineInsert>"
			</c:if>	
		</c:when>
		<c:otherwise>
					<gene:setString name="titoloMaschera" value="Variazione ordine NSO ${idOrdine} - requestScope.codiceOrdine"/>
		</c:otherwise>
	</c:choose>
	
	<gene:redefineInsert name="corpo">
	
		<c:set var="modo" value="NUOVO" scope="request" />
		<gene:formScheda entita="NSO_ORDINI" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreVariazioneOrdineNso" >
			<gene:campoScheda campo="ORIGINE" title="Codice ordine origine" modificabile="false" value='${idOrdine}' definizione="N12;1" campoFittizio="true" visibile="false" />
			<gene:campoScheda>
			<c:choose>
			<c:when test="${empty RISULTATO}">
				<td colspan="2">
					Verrà effettuata la variazione dell'ordine corrente: questo comporterà la generazione di un ordine sostitutivo e l'archiviazione nello storico dell'ordine originario<br>
					L'ordine sostitutivo generato acquisirà un nuovo codice Ordine e sarà possibile identificarne l'origine tramite il campo ordine collegato<br>
					Lo storico dell'ordine risulta consultabile all'interno della scheda dell'ordine. <br><br>&nbsp;
				</td>
			</c:when>
			<c:otherwise>
				<td colspan="2">
				</td>
			</c:otherwise>
			</c:choose>
			
			</gene:campoScheda>
		</gene:formScheda>
  </gene:redefineInsert>
	<gene:javaScript>
	function annulla(){
		window.close();
	}
	
	document.forms[0].jspPathTo.value="gare/nso_ordini/nso_variazione-ordine.jsp";
	
	function conferma(){
		schedaConferma();
	}
	
	<c:if test='${not empty RISULTATO and RISULTATO eq "OK"}' >
		opener.historyReload();
		window.close();
	</c:if>
	
	

	</gene:javaScript>
</gene:template>