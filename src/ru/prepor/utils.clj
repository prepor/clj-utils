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
  `(safe-res (async/<! ~ch)))

(defmacro <?? [ch]
  `(safe-res (async/<!! ~ch)))

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
