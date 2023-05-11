<%/*
       * Created on 02-Dec-2013
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

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="id" value='${gene:getValCampo(key,"ID")}' scope="request" />
		
			<gene:formScheda entita="COMMALBO" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreCOMMALBO" >
				
				<gene:campoScheda campo="ID" visibile="false" />
				<gene:campoScheda campo="DESCRIZIONE" />
				<gene:campoScheda campo="NOTE" />
				<gene:campoScheda campo="PERCRESET" />
				
				
				<c:if test="${modo eq 'NUOVO'  || modo eq 'MODIFICA' || (modo eq 'VISUALIZZA')}"> 
				</c:if>
				
				<gene:campoScheda>
					<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
				</gene:campoScheda>
				<gene:redefineInsert name="pulsanteNuovo" />
				<gene:redefineInsert name="schedaNuovo" />
								
			</gene:formScheda>
		
	
		
		<gene:javaScript>

		
		</gene:javaScript>
		
	



