(ns jokes.core
    (:use 
        compojure.core 
        ring.adapter.jetty 
        ring.middleware.params
        hiccup.core
        jokes.styles
        jokes.elements)
    (:require 
        [compojure.route :as route]
        redis
        digest))

(defmacro redis-connect [form] 
    `(redis/with-server ~{:host "127.0.0.1" :port 6379 :db 0} ~form))

(defn validate-story [story] 
    (if (<= (count story) 140) true (throw (new Exception "Your story is too long."))))

(defn new-key [story] (digest/md5 story))

(defn del-key [key] (redis/del key))

(defn get-story [] 
    (try
        (redis/get (redis/randomkey))
        (catch Exception e (str e))))

(defn put-story [story] 
    (validate-story story)
    (redis/rpush "new" story))

(defn moderate [save-old]
    (if save-old 
        (let [story (redis/get "current")]
            (redis/set (new-key story) story)))
    (let [story (redis/rpop "new")]
        (let [result (redis/set "current" story)]
            (if (= "OK" result) story (result)))))

(defn base-template [title content footer]
    (html
        [:head
            [:meta meta-tags]
            [:title title]]
        [:body {:style body-style}
            [:div {:style title-style} title]
            [:div {:style content-style} content]
            [:div {:style footer-style} footer]]))

(defn main-handler [{params :params}]
    (base-template 
        "140jokes" 
        (text-bigquote (redis-connect (get-story)))
        footer-text))

(defn form-handler [{params :params}]
    (base-template
        "140jokes"
        submit-form
        footer-text))

(def not-found-handler 
    (base-template
        "140jokes"
        (text-noquote "404 Not found")
        footer-text))

(defn submit-handler [{params :params}]
    (try 
        (do
            (redis-connect (put-story (params "story")))
            (main-handler))
        (catch Exception e 
            (base-template 
                "140jokes" 
                (text-noquote e) 
                footer-text))))

(defn moderate-handler [{params :params}] 
    (base-template
        "140jokes"
        (text-bigquote (if (params "save-old") (redis-connect (moderate true)) (redis-connect (moderate false))))
        footer-text))

(defn print-handler [{params :params}] (str params))

(defroutes main-routes
        (GET "/" [] main-handler)
        (GET "/submit" [] form-handler)
        (POST "/submit" [] submit-handler)
        (GET "/moderate" [] moderate-handler)
        (route/not-found not-found-handler))


(defn -main [& args] 
    (run-jetty (wrap-params main-routes) {:port 8080}))
