<%/*
   * Created on 26-11-2013
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

<form id="richiestainserimentoprotocollo">
	<div id="mascheraSceltaQform" title="Selezione modalita' inserimento documenti richiesti ai concorrenti" style="display:none;">
	<table class="sceltaQform">
		<tr style="font:11px Verdana, Arial, Helvetica, sans-serif"> 
			<td colspan="2">
				<br>
				<b>
				Documenti richiesti ai concorrenti
				</b>
				<br><br>
				Selezionare la modalità con cui si vuole dettagliare la documentazione richiesta ai concorrenti per la <span id="testoTipoBusta"></span>:
				<br><br>
			</td>				
		</tr>
		<tr style="font:11px Verdana, Arial, Helvetica, sans-serif">
			<td colspan="2">
				<input type="radio" value="1" name="tipoInserimento" id="radioQuestionario" />&nbsp;Configurazione guidata dei documenti mediante Q-form
				<br><br>
				<input type="radio" value="2" name="tipoInserimento" id="radioNormale" />&nbsp;Inserimento diretto dei documenti richiesti
				<br><br>
				<br> 	
			</td>				
		</tr>
	</table>
	</div>
	<input type="hidden" name="gestioneQuestionariPreq" id="gestioneQuestionariPreq" value="${gestioneQuestionariPreq}" />
	<input type="hidden" name="gestioneQuestionariAmm" id="gestioneQuestionariAmm" value="${gestioneQuestionariAmm}" />
	<input type="hidden" name="gestioneQuestionariTec" id="gestioneQuestionariTec" value="${gestioneQuestionariTec}" />
	<input type="hidden" name="gestioneQuestionariEco" id="gestioneQuestionariEco" value="${gestioneQuestionariEco}" />
	<input type="hidden" name="gestioneQuestionariIscriz" id="gestioneQuestionariIscriz" value="${gestioneQuestionariIscriz}" />
	<input type="hidden" name="noModaleDocumentiQform" id="noModaleDocumentiQform" value="${noModaleDocumentiQform}" />
	<input type="hidden" name="obbligoformularioPreq" id="obbligoformularioPreq" value="${obbligoformularioPreq}" />
	<input type="hidden" name="obbligoformularioAmm" id="obbligoformularioAmm" value="${obbligoformularioAmm}" />
	
</form>