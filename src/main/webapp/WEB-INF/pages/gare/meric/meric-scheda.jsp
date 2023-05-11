<%/*
   * Created on 20-05-2014
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
<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="MERIC-scheda">
	<c:set var="id" value='${gene:getValCampo(key,"ID")}' scope="request"/>
	<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction",pageContext,"MERIC")}' />
	<gene:redefineInsert name="corpo">
		<c:set var="isValutazioneProdottiAttivata" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsValutazioneProdottiAttivataFunction", pageContext, id)}' />
		<gene:formPagine gestisciProtezioni="true" >
			<gene:pagina title="Dati generali" idProtezioni="DATIGEN">
				<jsp:include page="meric-datigen.jsp" />
			</gene:pagina>
			<gene:pagina title="Catalogo" idProtezioni="ARTICOLIALBERO">
				<jsp:include page="meric-pg-articoli-albero.jsp" />
			</gene:pagina>
			<gene:pagina title="Carrello articoli" idProtezioni="ARTICOLICARRELLO">
				<jsp:include page="meric-pg-articoli-carrello.jsp" />
			</gene:pagina>
			<gene:pagina title="Valutazione prodotti" idProtezioni="PRODOTTIVALUTAZIONE" selezionabile="${isValutazioneProdottiAttivata eq 'true'}">
				<jsp:include page="meric-pg-prodotti-valutazione.jsp" />
			</gene:pagina>
			<gene:pagina title="Ordini" idProtezioni="ORDINI">
				<jsp:include page="meric-pg-Ordini.jsp" />
			</gene:pagina>						
		</gene:formPagine>
		
		<gene:javaScript>
			function valutazioneProdotti(id,tipo) {
				openPopUpCustom("href=gare/meric/meric-popup-valutazione.jsp?id=" + id + "&tipo=" + tipo, "valutazioneprodotti", 500, 300, "yes", "yes");
			}
		
			function annullaValutazioneProdotti(id) {
				openPopUpCustom("href=gare/meric/meric-popup-annulla-valutazione.jsp?id=" + id, "annullavalutazioneprodotti", 500, 300, "yes", "yes");
			}
		</gene:javaScript>
		
	</gene:redefineInsert>
</gene:template>