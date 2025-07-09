package it.eldasoft.sil.pg.bl.tasks;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.eldasoft.gene.bl.SqlManager;

public class NominativiPerMetaXml {
	
	private Map<String, Map<String,String>> nominativi;
	private SqlManager sqlManager;
	
	public NominativiPerMetaXml(SqlManager sqlManager) {
		nominativi = new HashMap<String,Map<String,String>>();
		this.sqlManager = sqlManager;
	}//NominativiPerMetaXml
	
	public Map<String,String> getNominativo(String codgar, boolean isStipula) throws SQLException{
		Map<String,String> infoNominativo = nominativi.get(codgar);
		String query;
		
		if (infoNominativo==null) {
			  // devo recuperare le info dal db...
	  		  if (isStipula) {
	  			  query = "select u.nomein, u.cfein "
	  			  		+ "from  V_GARE_STIPULA  gs "
	  			  		+ "join  uffint u on u.codein  = gs.cenint "
	  			  		+ "where 1=1 "
	  			  		+ "  and gs.codstipula = ? ";
	  		  } else {
				   query =  "select u.nomein, u.cfein "
				  		  + "from  torn   t "
				  		  + "     ,uffint u "
				  		  + "where 1=1 "
				  		  + "  and t.codgar = ? "
				  		  + "  and u.codein = t.cenint ";
	  		  }//if 
	  		  							 
			  List<Object> dati = this.sqlManager.getListVector(query, new Object[]{codgar});
			  if (dati.size()>0) {
				  String NOMEIN = SqlManager.getValueFromVectorParam(dati.get(0), 0).getStringValue();
				  String CFEIN = SqlManager.getValueFromVectorParam(dati.get(0), 1).getStringValue();	
				  
				  Map<String,String> nominativo = new HashMap<String,String>();
				  nominativo.put("nomein", NOMEIN);
				  nominativo.put("cfein", CFEIN);
				  
				  nominativi.put(codgar, nominativo);
				  
				  infoNominativo = nominativo;
				 					
			  }//if
			
		}//if
		
		return infoNominativo;
	}
	

}//NominativiPerMetaXml
