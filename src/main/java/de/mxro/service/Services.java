package de.mxro.service;

import delight.async.callbacks.SimpleCallback;
import delight.async.callbacks.ValueCallback;
import delight.concurrency.Concurrency;
import delight.functional.Success;

import de.mxro.service.callbacks.ShutdownCallback;
import de.mxro.service.internal.OperationCounterImpl;
import de.mxro.service.internal.ServiceRegistryImpl;
import de.mxro.service.internal.ShutdownHelperImpl;
import de.mxro.service.utils.OperationCounter;
import de.mxro.service.utils.ShutdownHelper;

public class Services {

    /**
     * <p>
     * A simple counter for how many operations a service is processing at any
     * point in time.
     * 
     * @return A new operation counter instance.
     */
    public static final OperationCounter createOperationCounter(final Concurrency con) {
        return new OperationCounterImpl(con);
    }

    /**
     * <p>
     * A helper to make shutdown operations safer.
     * 
     * @param operationCounter
     * @return
     */
    public static final ShutdownHelper createShutdownHelper(final OperationCounter operationCounter,
            final Concurrency con) {
        return new ShutdownHelperImpl(operationCounter, con);
    }

    public static ServiceRegistry create() {
        return new ServiceRegistryImpl();
    }

    public final static ShutdownCallback asShutdownCallback(final SimpleCallback callback) {
        return new ShutdownCallback() {

            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }
        };
    }

    public final static ShutdownCallback asShutdownCallback(final ValueCallback<Success> callback) {
        return new ShutdownCallback() {

            @Override
            public void onSuccess() {
                callback.onSuccess(Success.INSTANCE);
            }

            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }
        };
    }

    public final static ValueCallback<Success> asValueCallback(final ShutdownCallback callback) {
        return new ValueCallback<Success>() {

            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }

            @Override
            public void onSuccess(final Success value) {
                callback.onSuccess();
            }
        };
    }

}
