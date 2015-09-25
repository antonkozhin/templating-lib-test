(ns test.core
  (:use-macros [crate.def-macros :only [defpartial]])
  (:require-macros [enfocus.macros :as em])
  (:require [crate.core :as crate]
            [hiccups.runtime :as hiccupsrt]
            [enfocus.core :as enfocus]
            [dommy.template :as dommy]
            [hipo.core :as hipo]
            [clojure.string :as str]))

(enable-console-print!)

(def template [:div {:class "div-class"
                     :style "background-color: #EB7260; color: white;"} [:h1 "My app!"]])

(defn elapsed-time1 [func]
  (str (re-find #"\d+" (with-out-str (time (dotimes [n 10000] (func template))))) " мс."))

(defn elapsed-time2 [func]
  (str (re-find #"\d+" (with-out-str (time (dotimes [n 10000] (func template))))) " мс."))

(defn crate-test [lib-name lib-href func func-name & {:keys [template-func template-func-name]}]
  (let [title (func [:section nil
                     [:a {:href "https://github.com/ibdknox/crate"} lib-name]
                     [:div nil (str "DOM-node создается функцией: ") func-name]
                     [:div nil (str "Создание 10000 div: " (elapsed-time1 func))]
                     (when (and template-func template-func-name)
                       [:div nil (str "Шаблон создается функцией: ") template-func-name])
                     (when (and template-func template-func-name)
                       [:div nil (str "Создание 10000 div: ") (elapsed-time2 template-func)])])
        elem (if template-func
               (template-func)
               (func template))]
    (.appendChild (.getElementById js/document "container") title)
    (.appendChild (.getElementById js/document "container") elem)))

(defpartial crate-template [] template)

(crate-test "crate"
            "https://github.com/ibdknox/crate"
            crate/html "html"
            :template-func crate-template
            :template-func-name "defpartial")

(defn enfocus-template [] (enfocus/html template))

(crate-test "enfocus"
            "https://github.com/ckirkendall/enfocus"
            enfocus/html
            "html"
            :template-func enfocus-template
            :template-func-name "нет, используем html")

(dommy/deftemplate dommy-template [] template)

(crate-test "dommy.template"
            "https://github.com/immoh/dommy.template"
            #(dommy/node %) "node"
            :template-func dommy-template
            :template-func-name "deftemplate")

(defn hipo-template [] (hipo/create template))

(crate-test "hipo"
            "https://github.com/jeluard/hipo"
            #(hipo/create %)
            "create"
            :template-func hipo-template
            :template-func-name "нет, используем create")




