package de.mxro.service.internal;

import de.mxro.service.utils.OperationCounter;
import de.mxro.service.utils.ShutdownHelper;
import delight.async.callbacks.SimpleCallback;
import delight.concurrency.Concurrency;
import delight.concurrency.wrappers.SimpleAtomicBoolean;
import delight.concurrency.wrappers.SimpleAtomicInteger;
import delight.simplelog.Log;

public final class ShutdownHelperImpl implements ShutdownHelper {

	private final OperationCounter operationCounter;

	private final SimpleAtomicInteger shutdownAttempts;
	private final SimpleAtomicBoolean isShutdown;
	private final SimpleAtomicBoolean isShuttingDown;
	private final Concurrency con;
	private final String serviceName;

	private final static int DEFAULT_DELAY = 100;
	private final static int MAX_ATTEMPTS = 300;

	@Override
	public boolean isShutdown() {
		return isShutdown.get();
	}

	@Override
	public boolean isShuttingDown() {
		return isShuttingDown.get();
	}

	@Override
	public void shutdown(final SimpleCallback callback) {
		assert !this.isShutdown() : "Cannot shut down already shut down server.";
		assert !this.isShuttingDown() : "Cannot shut down server which is already shutting down.";

		this.isShuttingDown.set(true);

		performShutdown(callback);
	}

	private final void performShutdown(final SimpleCallback callback) {
		if (operationCounter.count() == 0) {
			this.isShutdown.set(true);
			callback.onSuccess();

			return;
		}

		con.newTimer().scheduleOnce(DEFAULT_DELAY, new Runnable() {

			@Override
			public void run() {

				final int attempts = shutdownAttempts.incrementAndGet();

				if (attempts > MAX_ATTEMPTS) {
					// callback.onFailure(new Exception("Service could not be shut down in timeout
					// [" + serviceName
					// + "]. Outstanding operations: " + operationCounter.count()));
					// return;

					Log.warn("Service could not be shut down in timeout [" + serviceName + "]. Outstanding operations: "
							+ operationCounter.count());
				}

				performShutdown(callback);
			}
		});
	}

	public ShutdownHelperImpl(final String serviceName, final OperationCounter operationCounter,
			final Concurrency con) {
		super();
		this.operationCounter = operationCounter;
		this.con = con;
		this.shutdownAttempts = con.newAtomicInteger(0);
		this.isShutdown = con.newAtomicBoolean(false);
		this.isShuttingDown = con.newAtomicBoolean(false);
		this.serviceName = serviceName;
	}

}
