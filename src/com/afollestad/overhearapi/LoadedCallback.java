package com.afollestad.overhearapi;

public interface LoadedCallback<T> {

	public abstract void onLoaded(T result);
}
