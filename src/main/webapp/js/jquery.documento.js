/*
*	Gestione della lettura degli elementi documentali
*
*/


$(window).ready(function (){

	/*
	 * Attesa all'avvio della pagina
	 */
	_wait();
  
	/*
	 * Avvio all'apertura della maschera del popolamento 
	 * della lista degli elementi documentali
	 */
	setTimeout(function(){
		_getWSDocumento();
	}, 800);

	/*
	 * Evento per la gestione della maschera di inserimento
	 */
	
	$('#comins').click(function() {
		document.formwsdmcomins.submit();
    });
	
});




