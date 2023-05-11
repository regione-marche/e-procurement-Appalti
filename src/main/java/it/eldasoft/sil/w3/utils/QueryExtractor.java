/*
 * Created on 02/dic/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.w3.utils;

import java.io.InputStream;
import org.dom4j.Document;
import org.dom4j.Node; 
/**
 * Classe che consente l'interrogazione del db mediante query inserite in un file xml
 * 
 * @author Eliseo Marini
 * 
 */

public class QueryExtractor {
	
	
	public QueryExtractor(){}

	/**
	 * Metodo che ritorna la query presente in un file xml
	 * 
	 * @fileXML rappresenta il nome del file xml (esclusa l'estensione) contenente i metodi e le query
	 * 
	 * @nomeMetodo rappresenta il nome del metodo che deve essere preso in considerazione nel file xml
	 * 
	 * @nomeQuery rappresenta il nome della query che deve essere eseguita
	 */
	public String getQuery(String fileXML,String nomeMetodo,String nomeQuery) 
	{
		String query="";
		String percorsoXML="it/eldasoft/sil/w3/utils/common/"+fileXML+".xml";
		try{
			InputStream inSQL = this.getClass().getClassLoader().getResourceAsStream(percorsoXML);
			XmlParser pars= new XmlParser(); 
			Document docSQL = pars.parse(inSQL);
			String selectNode = "//Classe/metodo[@nome='"+nomeMetodo+"']/query[@nome='"+nomeQuery+"']";
			Node nodeQuery = docSQL.selectSingleNode(selectNode);
			query=nodeQuery.getText();
			return query;
		}
		catch(Exception ex)
		{
			System.out.println("Errore in QueryExtractor.getQuey, caused by : " + ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}
	
	public String getQuery(String nomeMetodo,String nomeQuery) 
    {
        String query="";
        String percorsoXML="it/eldasoft/sil/w3/utils/common/W3Query.xml";
        try{
            InputStream inSQL = this.getClass().getClassLoader().getResourceAsStream(percorsoXML);
            XmlParser pars= new XmlParser(); 
            Document docSQL = pars.parse(inSQL);
            String selectNode = "//Classe/metodo[@nome='"+nomeMetodo+"']/query[@nome='"+nomeQuery+"']";
            Node nodeQuery = docSQL.selectSingleNode(selectNode);
            query=nodeQuery.getText();
            return query;
        }
        catch(Exception ex)
        {
            System.out.println("Errore in QueryExtractor.getQuey, caused by : " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }
	
}
