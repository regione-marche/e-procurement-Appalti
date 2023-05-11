<%
/*
 * Created on 18-lug-2011
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA TROVA CONCORREMTI AUR 
 // CONTENENTE LA SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<script type="text/javascript">


	function initPagina(){
	}

	function avviaRicercaConcorrentiAUR(){
		document.trovaConcorrentiAURForm.metodo.value="trovaDitta";
		document.trovaConcorrentiAURForm.submit();
	}

	function nuovaRicerca(){
		document.trovaConcorrentiAURForm.reset();
	}
	  
</script>
