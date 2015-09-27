(ns test.core
  (:use-macros [crate.def-macros :only [defpartial]])
  (:require-macros [enfocus.macros :as em])
  (:require [crate.core :as crate]
            [enfocus.core :as enfocus]
            [dommy.template :as dommy]
            [hipo.core :as hipo]
            [clojure.string :as str]
            [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]))

(enable-console-print!)

(def template [:div {:class "div-class"
                     :style "background-color: #EB7260; color: white;"} [:h1 "My app!"]])

(defpartial crate-template [] template)

(dommy/deftemplate dommy-template [] template)

(defonce app-state (atom {:libs [{:lib-name "crate"
                                  :lib-href "https://github.com/ibdknox/crate"
                                  :tests [{:func crate/html
                                           :func-name "html"
                                           :func-type "simple"}
                                          {:func crate-template
                                           :func-name "defpartial"
                                           :func-type "template"}]}
                                 {:lib-name "enfocus"
                                  :lib-href "https://github.com/ckirkendall/enfocus"
                                  :tests [{:func enfocus/html
                                           :func-name "html"
                                           :func-type "simple"}]}
                                 {:lib-name "dommy.template"
                                  :lib-href "https://github.com/immoh/dommy.template"
                                  :tests [{:func #(dommy/node %)
                                           :func-name "node"
                                           :func-type "simple"}
                                          {:func dommy-template
                                           :func-name "deftemplate"
                                           :func-type "template"}]}
                                 {:lib-name "hipo"
                                  :lib-href "https://github.com/jeluard/hipo"
                                  :tests [{:func #(hipo/create %)
                                           :func-name "create"
                                           :func-type "simple"}]}]}))

(defn measure-time [func func-type app]
  (let [res (cond
             (= func-type "simple") (str (re-find #"\d+" (with-out-str (time (dotimes [n 10000] (func template))))) " мс.")
             (= func-type "template") (str (re-find #"\d+" (with-out-str (time (dotimes [n 10000] (func))))) " мс."))]
    (om/update! app  :elapsed-time res)))

(defn result-view [elapsed-time owner]
  (reify
    om/IRender
    (render [_]
            (dom/div {:class "result"} (str "Результат: " elapsed-time)))))

(defn test-view [{:keys [func func-name func-type elapsed-time] :as app} owner]
  (reify
    om/IRender
    (render [_]
            (dom/div {:class "test"}
                     (if (= func-type "simple")
                       (dom/div nil (str "DOM-node создается функцией: " func-name "."))
                       (dom/div nil (str "Шаблон создается функцией: " func-name ".")))
                     (dom/div nil
                              (str "Создание 10000 div")
                              (dom/button {:on-click #(measure-time func func-type app)} "Старт"))
                     (om/build result-view elapsed-time)))))

(defn lib-view [{:keys [lib-name lib-href tests] :as app} owner]
  (reify
    om/IRender
    (render [_]
            (dom/div {:class "lib"}
                     (dom/a {:href lib-href} lib-name)
                     (dom/div {:class "tests"}
                              (om/build-all test-view tests))))))

(defn content-view [app owner]
  (reify
    om/IRender
    (render [_]
            (dom/div nil
                     (om/build-all lib-view (:libs app))))))

(om/root content-view app-state {:target (. js/document (getElementById "content"))})




