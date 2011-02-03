(ns jokes.core
    (:use 
        compojure.core 
        ring.adapter.jetty 
        hiccup.core
        jokes.styles
        jokes.elements
        :reload-all)
    (:require 
        [compojure.route :as route]
        compojure.response
        redis
        digest
        :reload-all))

(defn new-key [story] (digest/md5 story))

(defn get-story [] 
    (redis/with-server {:host "127.0.0.1" :port 6379 :db 0}
        (redis/get (redis/randomkey))))

(defn put-story [story] 
    (redis/with-server {:host "127.0.01" :port 6379 :db 0}
        (redis/set (new-key story) (str story))))

(defn base-template [title content footer]
    (html
        [:head
            [:meta meta-tags]
            [:title title]]
        [:body {:style body-style}
            [:div {:style title-style} title]
            [:div {:style content-style} content]
            [:div {:style footer-style} footer]]))

(def main-handler 
    (base-template 
        "140jokes" 
        (text-bigquote (get-story))
        footer-text))

(def form-handler 
    (base-template
        "140jokes"
        submit-form
        footer-text))

(def not-found-handler 
    (base-template
        "140jokes"
        (text-noquote "404 Not found")
        footer-text))

(defn submit-handler [params]
    (try 
        (put-story (params "story"))
        (catch Exception e (base-template "140jokes" (text-noquote e) footer-text))))

(defroutes main-routes
    (GET "/" [] main-handler)
    (GET "/submit" [] form-handler)
    (POST "/submit" {params :params} 
        (submit-handler params))
    (route/not-found not-found-handler))


(defn -main [& args] 
    (run-jetty main-routes {:port 8080}))
