(set-env!
  :resource-paths #{"src"}
  :dependencies '[[tailrecursion/boot-jetty                 "0.1.3"]
                  [clj-http                                 "2.0.0"           :scope "test"]
                  [org.clojure/clojure                      "1.7.0"           :scope "provided"]
                  [hoplon/boot-hoplon                       "0.1.9"           :scope "test" ]
                  [adzerk/boot-reload                       "0.4.2"           :scope "test" ]
                  [adzerk/boot-cljs                         "1.7.170-3"       :scope "test" ]
                  [org.clojure/clojurescript                "1.7.170"]
                  [hoplon/castra                            "3.0.0-alpha3"]
                  [ch.qos.logback/logback-classic           "1.1.3"]
                  [ring                                     "1.4.0"]
                  [jumblerg/ring.middleware.cors            "1.0.1"]
                  [adzerk/env                               "0.2.0"]
                  [cheshire                                 "5.5.0"]
                  [hoplon                                   "6.0.0-alpha11"]
                  [compojure                                "1.3.4"]
                  [liberator                                "0.13"]])

(require
  '[adzerk.env :as env]
  '[adzerk.boot-cljs                       :refer [cljs]]
  '[adzerk.boot-reload                     :refer [reload]]
  '[hoplon.boot-hoplon                     :refer [hoplon]]
  '[tailrecursion.boot-jetty               :refer [serve]])

(task-options!
  serve       {:port            8000}
  web         {:serve           'server.api/app})


(deftask dev
  "dev task using boot-http"
  []

  (comp
    (watch)
    (hoplon :pretty-print true)
    (reload)
    (cljs   :source-map true)
    (web)
    (serve)
    (speak)
    ))

