package de.mxro.service;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;

public interface ServiceRegistry {

	/**
	 * <p>Seeks first match in registry of service which provides the provided
	 * interface of type clazz (e.g. service can be cast to clazz).
	 * <p>Subscribe as a user to this service
	 * 
	 * @param clazz
	 * @return
	 */
	public <InterfaceType> void subscribe(Class<InterfaceType> clazz, ValueCallback<InterfaceType> callback);

	/**
	 * Release a subscription for this service.
	 * @param service
	 */
	public void unsubscribe(Object service, SimpleCallback callback);
	
	
	public void register(Service service);
	

	
}
