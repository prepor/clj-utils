(ns ru.prepor.utils
  (:require [clojure.core.async :as async]
            [clojure.tools.logging :as log]))

(defn throwable?
  [v]
  (instance? Throwable v))

(defn safe-res
  [res]
  (if (throwable? res)
    (throw res)
    res))

(defmacro <? [ch]
  `(when-let [sexp-res# ~ch]
     (safe-res (async/<! sexp-res#))))

(defmacro <?? [ch]
  `(when-let [sexp-res# ~ch]
     (safe-res (async/<!! sexp-res#))))

(defmacro multi-<? [ch]
  `(when-let [sexp-res# ~ch]
     (doall (map safe-res (async/<! sexp-res#)))))

(defmacro multi-<?? [ch]
  `(when-let [sexp-res# ~ch]
     (doall (map safe-res (async/<!! sexp-res#)))))

(defn channel?
  [v]
  (instance? clojure.core.async.impl.channels.ManyToManyChannel v))

(defmacro maybe-<!
  [ch-or-res]
  `(let [res# ~ch-or-res]
     (if (channel? res#)
       (async/<! res#)
       res#)))

(defmacro maybe-<!!
  [ch-or-res]
  `(let [res# ~ch-or-res]
     (if (channel? res#)
       (async/<!! res#)
       res#)))

(defmacro maybe-<?
  [ch-or-res]
  `(let [res# ~ch-or-res]
     (if (channel? res#)
       (async/<? res#)
       res#)))

(defmacro maybe-<??
  [ch-or-res]
  `(let [res# ~ch-or-res]
     (if (channel? res#)
       (async/<?? res#)
       res#)))

(defmacro safe
  [& body]
  `(try
     ~@body
     (catch Throwable e#
       (log/error e#)
       e#)))

(defmacro safe-go
  [& body]
  `(async/go (safe ~@body)))

(defmacro safe-thread
  [& body]
  `(async/thread (safe ~@body)))
