<%--

/*
 * Created on: 12-apr-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

Finestra che visualizza la conferma di eliminazione della categoria
--%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="id" value='${gene:getValCampo(param.chiaveRiga, "CAISIM")}'/>


<gene:template file="popup-message-template.jsp">
	<gene:setString name="titoloMaschera" value='Eliminazione categoria' />
	<c:set var="isPadre"
		value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.CheckIsCategoriaPadreFunction",pageContext,id)}' />
	
	<c:set var="isCategoriaAdoperata" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.CheckIsCategoriaAdoperataInPGPLFunction",  pageContext,id,"No")}' />
	
	<gene:redefineInsert name="corpo">
		<br>
		<c:choose>
			<c:when test="${isCategoriaAdoperata == 'true' }">
				La categoria <b><c:out value="${id}"/></b> non può essere eliminata in quanto risulta essere referenziata in entità dell'applicativo
			</c:when>
			<c:when test="${isPadre}">
				La categoria <c:out value="${id}"/> &egrave; padre di altre categorie.<br/>
				<b>L'eliminazione di tale categoria comporta anche l'eliminazione di tutte le categorie figlie</b>.<br/>
				Prosegui ugualmente l'eliminazione?
			</c:when>
			<c:otherwise>
				Stai per eliminare la categoria <b><c:out value="${id}"/></b>.<br/>
				Confermi l'eliminazione?
			</c:otherwise>
		</c:choose>
		<br>
		<br>
	</gene:redefineInsert>
	<c:if test="${isCategoriaAdoperata == 'true' }" >
		<gene:redefineInsert name="buttons">
				<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;
		</gene:redefineInsert>
	</c:if>
	<gene:javaScript>
	function conferma(){
		opener.confermaEliminaCategoria();
	}
	function annulla(){
		window.close();
	}
	</gene:javaScript>
</gene:template>
