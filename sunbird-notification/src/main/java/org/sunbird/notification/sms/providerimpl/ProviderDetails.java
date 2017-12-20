package org.sunbird.notification.sms.providerimpl;

import java.io.Serializable;
import java.util.List;

import org.sunbird.notification.sms.Sms;
/**
 * 
 * @author Manzarul
 *
 */
public class ProviderDetails implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6602089097922616775L;
	private String sender;
    private String route;
    private String country;
    private List<Sms> sms;

    public ProviderDetails(String sender, String route, String country, List<Sms> sms) {
        this.sender = sender;
        this.route = route;
        this.country = country;
        this.sms = sms;
    }
}
