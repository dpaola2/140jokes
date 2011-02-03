(ns jokes.elements 
    (:use
        hiccup.core
        hiccup.form-helpers
        hiccup.page-helpers
        jokes.styles))

(def meta-tags
    [:meta {
        :http-equiv "Content-type"
        :content "text/html; charset=UTF-8"}])

(def open-quote 
    [:span 
        {:style open-quote-style} 
        "&#8220;"])

(def close-quote 
    [:span 
        {:style close-quote-style}
        "&#8221;"])

(defn text-bigquote [text] 
    (list
        open-quote
        [:span 
            {:style text-style}
            text]
        close-quote))

(defn text-noquote [text] 
    [:span {:style text-style} text])

(def submit-form 
    (form-to [:post "/submit"] 
        (list
            (label {:style label-style} "story" "Enter your joke:")
            (text-area {:style text-field-style} "story")
            (submit-button {:style submit-style} "Submit"))))

(def footer-text
    (list
        "Created by Dave Paola"
        " | "
        (link-to "/submit" "Submit")))
