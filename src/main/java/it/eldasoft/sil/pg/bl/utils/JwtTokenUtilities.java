package it.eldasoft.sil.pg.bl.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;

public class JwtTokenUtilities {

  public static final String PROP_JWTKEY                                    = "it.maggioli.eldasoft.wslogin.jwtKey";

	/**
	 * Viene generato il token mettendo il claim "data"
	 * @param subject
	 * @param expirationInMillis
	 * @param jwtSecretKey
	 * @param data
	 * @return the generated token
	 */
	public static String generateToken(String subject, long expirationInMillis, String jwtSecretKey, Map<String, String> data) {
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("data", data);
		return generateTokenWithClaims(subject,expirationInMillis,jwtSecretKey,claims);
	}

  	/**
      * Viene generato il token adoperando i claim forniti da parametro
      * @param subject
      * @param expirationInMillis
      * @param jwtSecretKey
      * @param data
      * @return the generated token
      */
     public static String generateTokenWithClaims(String subject, long expirationInMillis, String jwtSecretKey, Map<String, Object> claims) {
         return Jwts.builder().setClaims(claims ).setSubject(subject)
                 .setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(expirationInMillis))
                 .signWith(Keys.hmacShaKeyFor(jwtSecretKey.getBytes()),SignatureAlgorithm.HS512).compact();
     }

	/**
	 * Questo metodo permette di decifrare un {@link Jwt} token
	 * @param jwt la stringa che rappresenta un token jwt
	 * @param signingKey la chiave in plain string con cui Ã¨ stato firmato il token
	 * @return il JSON del body del token jwt
	 * @throws ExpiredJwtException
	 * @throws MalformedJwtException
	 * @throws SignatureException
	 * @throws IllegalArgumentException
	 */
	public static Jwt<?, ?> parseJwt(String jwt,String signingKey) throws ExpiredJwtException, MalformedJwtException, IllegalArgumentException {
		Jwt<?, ?> tk = Jwts.parserBuilder().setSigningKey(signingKey.getBytes()).build().parse(jwt);
		return tk;
	}

	/**
	 * Questo metodo restituisce il body di un token
	 * @param token
	 * @return il body {@link Jwt#getBody()}
	 */
	public static Object getBodyFromJwt(Jwt<?,?> token) {
		return token.getBody();
	}

	/**
	 * Il metodo decifra la chiave segreta per la generazione del token JWT.
	 * La chiave privata cifrata è salvata in db
	 * @return String
	 * @throws GestoreException
	 */
	public static String getChiaveSegretaJwtChiaro() throws GestoreException {
	  String chiaveSegretaJwt = ConfigManager.getValore(PROP_JWTKEY);

	  String chiaveSegretaJwtChiaro = null;
	  if (chiaveSegretaJwt != null && chiaveSegretaJwt.trim().length() > 0) {
	    ICriptazioneByte passwordICriptazioneByte = null;
	    try {
	      passwordICriptazioneByte = FactoryCriptazioneByte.getInstance(
	          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), chiaveSegretaJwt.getBytes(),
	          ICriptazioneByte.FORMATO_DATO_CIFRATO);
	    } catch (CriptazioneException e) {
	      throw new GestoreException("Errore nella decifrazione della chiave privata per la generazione del token JWT",null,e);
	    }
	    chiaveSegretaJwtChiaro = new String(passwordICriptazioneByte.getDatoNonCifrato());
	  }else{
	    chiaveSegretaJwtChiaro = "";
	  }

	  return chiaveSegretaJwtChiaro;
	}
}
