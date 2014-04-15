package co.cc.dynamicdev.dynamicbanplus;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class DNSBL
{
    private static String[] RECORD_TYPES = { "A", "TXT" };
    private DirContext context;
    private List<String> lookupServices = new ArrayList<String>();
    
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public DNSBL() throws NamingException {
        StringBuilder dnsServers = new StringBuilder("");        
        List nameservers = sun.net.dns.ResolverConfiguration.open().nameservers();
        for(Object dns : nameservers) {
            dnsServers.append("dns://").append(dns).append(" ");
        }
        
        Hashtable settings = new Hashtable();
        settings.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");       
        settings.put("com.sun.jndi.dns.timeout.initial", "4000");
        settings.put("com.sun.jndi.dns.timeout.retries", "1");
        settings.put(Context.PROVIDER_URL, dnsServers.toString());
        context = new InitialDirContext(settings);
    }
    
    public void addService(String service) {
        lookupServices.add(service);
    }
    
    public void clearServices() {
    	lookupServices.clear();
    }
    
    public boolean isBlacklisted(String ip) {
        String[] parts = ip.split("\\.");
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            buffer.insert(0, '.');
            buffer.insert(0, parts[i]);
        }
        ip = buffer.toString();
        
        String lookupHost;
        for (String service : lookupServices) {
            lookupHost = ip + service;
            try {
                Attributes attributes = context.getAttributes(lookupHost, RECORD_TYPES);
                Attribute attribute = attributes.get("TXT");
                if (attribute != null) {
                    return true;
                }
            }
            catch (NameNotFoundException e) {
            }
            catch (NamingException e) {
            }
        }
		return false;
    }
}