/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eldasoft.sil.pg.bl;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;

import java.io.ByteArrayInputStream;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;

import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.OpenMode;
import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FilePermission;
import net.schmizz.sshj.xfer.InMemorySourceFile;
import net.schmizz.sshj.xfer.LocalDestFile;


/**
 *
 * @author Enrico
 */
/**
 * 
 */
public class SFTPManager {
  
    public static final String URL = "cos.sftp.url";
    public static final String LOGIN = "cos.sftp.login";
    public static final String PASSWORD = "cos.sftp.password";
    public static final String PORT = "cos.sftp.port";

    protected SSHClient ssh = new SSHClient();
    public SFTPClient sftp = null;
    public void connect(String host, String username, String password)
            throws IOException {
            ssh.addHostKeyVerifier(new PromiscuousVerifier());
            int port = Integer.parseInt(ConfigManager.getValore(PORT));
            ssh.connect(host, port);
        
        try {
            ssh.authPassword(username, password);
            sftp = ssh.newSFTPClient();
            
        } finally {
            //ssh.disconnect();
        }
    }
    
    public void connect() throws IOException, CriptazioneException
    {
      String password = ConfigManager.getValore(PASSWORD);
      ICriptazioneByte icb = FactoryCriptazioneByte.getInstance(ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
          password.getBytes(), it.eldasoft.utils.sicurezza.ICriptazioneByte.FORMATO_DATO_CIFRATO);
      String passwordDecoded = new String(icb.getDatoNonCifrato());
        
        connect(
                ConfigManager.getValore(URL),
                ConfigManager.getValore(LOGIN),
                passwordDecoded
        );
    }
    
    public void disconnect() throws IOException
    {
        sftp.close();
    }
    public void CreateDirs(String dirs) throws IOException
    {
        sftp.mkdirs(dirs);
    }
    
    public void existsDirs(String dirs) throws IOException, GestoreException{
      FileAttributes fadirs= sftp.statExistence(dirs);
      if(fadirs == null){
        throw new GestoreException("Impossibile trovare il path indicato:" + dirs, null);
      }
    }
    
    public void WriteAppend(byte[] bytes, String path) throws IOException{

        String filePath = path;
        long size = sftp.size(filePath);
        RemoteFile rf = sftp.open(filePath, new HashSet<OpenMode>(Arrays.asList(OpenMode.WRITE,OpenMode.APPEND)));
        rf.write(size, bytes, 0, bytes.length);
   }
    
    public void Put(byte[] bytes, String target) throws IOException
    {
        sftp.put(GetFile(bytes), target);
    }
    
    public void Get(String file, LocalDestFile target) throws IOException
    {
        sftp.get(file, target);
    }
    
    public void Rename(String oldpath, String newpath) throws IOException
    {
        sftp.rename(oldpath, newpath);
    }
    
    public void Remove(String path) throws IOException
    {
        sftp.rm(path);
    }
    
    public List<String> GetFiles(String path) throws IOException{
        ArrayList<String> retval = new ArrayList<String>();
        List<RemoteResourceInfo> lista = sftp.ls(path);
        for (int i = 0; i < lista.size(); i++) {
            RemoteResourceInfo rri = lista.get(i);
            retval.add(rri.getName());
        }
        return retval;
    }
    
    protected InMemorySourceFile GetFile(final byte[] data) throws IOException {
        return new InMemorySourceFile() {
            
            public String getName() {
                return "file";
            }
            
            public long getLength() {
                return data.length;
            }
            
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(data);
            }
        };
    }
}
