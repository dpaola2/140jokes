(ns jokes.core
    (:use 
        compojure.core 
        ring.adapter.jetty 
        hiccup.core
        jokes.styles
        jokes.elements
        jokes.data)
    (:require 
        [compojure.route :as route]
        redis
        digest))

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
        (text-bigquote "The main reason Santa is so jolly is because he knows where all the bad girls live.")
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
    (str params))

(defroutes main-routes
    (GET "/" [] main-handler)
    (GET "/submit" [] form-handler)
    (POST "/submit" {params :params} 
        (submit-handler params))
    (route/not-found not-found-handler))

(defn new-key [story] (digest/md5 story))

(defn -main [& args] 
    (run-jetty main-routes {:port 8080}))
