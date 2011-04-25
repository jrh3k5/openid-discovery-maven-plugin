package com.google.code.openid.mojo;

import java.util.regex.Pattern;

/**
 * Definition of a service that is served as a service in a discovery document.
 * 
 * @author jrh3k5
 * 
 */

// TODO: Javadoc
public class DiscoveredService {
    private Pattern hostRegex;
    private String uri;
    private String type;
    private Integer priority;
    private String localId;
    
    public void setHostRegex(String hostRegex) {
        if(hostRegex == null)
            throw new IllegalArgumentException("Host regular expression cannot be null.");
        
        this.hostRegex = Pattern.compile(hostRegex);
    }
    
    public Pattern getHostRegex() {
        if(hostRegex == null)
            throw new IllegalStateException("Host regular expression has not yet been set prior to retrieval.");
        
        return hostRegex;
    }
    
    public void setUri(String uri) {
        if(uri == null)
            throw new IllegalArgumentException("URI cannot be null.");
        
        this.uri = uri;
    }
    
    public String getUri() {
        if(uri == null)
            throw new IllegalStateException("URI has not yet been set prior to retrieval.");
        
        return uri;
    }
    
    public void setType(String type) {
        if(type == null)
            throw new IllegalArgumentException("Type cannot be null.");
        
        this.type = type;
    }
    
    public String getType() {
        if(type == null)
            throw new IllegalStateException("Type has not yet been set prior to retrieval.");
        
        return type;
    }
    
    public void setPriority(int priority) {
        this.priority = Integer.valueOf(priority);
    }
    
    public Integer getPriority() {
        return priority;
    }
    
    public void setLocalId(String localId) {
        if(localId == null)
            throw new IllegalArgumentException("Local ID cannot be null.");
        
        this.localId = localId;
    }
    
    public String getLocalId() {
        return localId;
    }
}
